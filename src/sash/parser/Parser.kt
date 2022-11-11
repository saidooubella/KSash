@file:Suppress("NOTHING_TO_INLINE")

package sash.parser

import sash.errors.ErrorsReporter
import sash.input.Input
import sash.lexer.token.Token
import sash.lexer.token.TokenType
import sash.parser.nodes.*
import sash.span.Spannable
import sash.tools.SeparatedList
import sash.tools.allIndexed
import sash.tools.separatedListOf
import java.io.Closeable

internal class Parser(
    private val lexer: Input<Token, Token>,
    private val errors: ErrorsReporter
) : Closeable {

    private var errorsFlag = true

    internal fun createProgramNode(): ProgramNode {

        val statements = mutableListOf<StatementNode>()

        while (!lexer.isFinished) avoidLooping {
            val statement = declaration()
            statements += statement
        }

        return ProgramNode(statements)
    }

    private fun declaration(): StatementNode {

        if (match(TokenType.FunKeyword))
            return functionStatement()

        if (match(TokenType.RecordKeyword))
            return recordStatement()

        if (match(TokenType.LetKeyword))
            return variableStatement(true)

        if (match(TokenType.DefKeyword))
            return variableStatement(false)

        return statement()
    }

    private fun recordStatement(): StatementNode {
        val keyword = lexer.consume()
        val identifier = consume(TokenType.Identifier, "identifier")
        val open = consume(TokenType.OpenCurly, "{")
        val fields = recordFields()
        val close = consume(TokenType.CloseCurly, "}")
        return RecordStatementNode(keyword, identifier, open, fields, close)
    }

    private fun recordFields(): List<RecordField> {
        val fields = mutableListOf<RecordField>()
        while (!lexer.isFinished && !match(TokenType.CloseCurly)) avoidLooping {
            val identifier = consume(TokenType.Identifier, "identifier")
            val type = typeClause()
            fields += RecordField.NormalField(identifier, type)
        }
        return fields
    }

    private fun functionStatement(): StatementNode {
        val keyword = lexer.consume()
        val isNextReceiver = isNextReceiver()
        val receiver = if (isNextReceiver) valueType() else null
        val dot = if (isNextReceiver) consume(TokenType.Dot, ".") else null
        val identifier = consume(TokenType.Identifier, "identifier")
        val open = consume(TokenType.OpenParentheses, "(")
        val params = parameters()
        val close = consume(TokenType.CloseParentheses, ")")
        val type = optionalTypeClause()
        val block = statementsBlock()
        if (receiver != null && dot != null)
            return MethodStatementNode(keyword, receiver, dot, identifier, open, params, close, type, block)
        return FunctionStatementNode(keyword, identifier, open, params, close, type, block)
    }

    private fun isNextReceiver(): Boolean {

        if (lexer.isFinished)
            return false

        if (lexer.current.type == TokenType.Identifier)
            return isEndSeparator(1)

        if (lexer.current.type == TokenType.OpenParentheses)
            return isEndSeparator(offsetTo(TokenType.OpenParentheses, TokenType.CloseParentheses))

        return false
    }

    private fun offsetTo(open: TokenType, close: TokenType, startOffset: Int = 1): Int {

        var parents = 1
        var offset = startOffset

        while (lexer.peek(offset).type != TokenType.EndOfFile && parents > 0) {
            @Suppress("NON_EXHAUSTIVE_WHEN")
            when (lexer.peek(offset++).type) {
                close -> parents--
                open -> parents++
            }
        }

        return offset
    }

    private fun isEndSeparator(offset: Int): Boolean {
        return lexer.peek(offset).type == TokenType.Dot
    }

    private fun parameters(): SeparatedList<ParamClause, Token> {
        val parameters = separatedListOf<ParamClause, Token>()
        while (!lexer.isFinished && !match(TokenType.CloseParentheses)) avoidLooping {
            parameters.addElement(paramClause())
            if (!match(TokenType.CloseParentheses))
                parameters.addSeparator(consume(TokenType.Comma, ","))
        }
        return parameters
    }

    private fun paramClause(): ParamClause {
        val identifier = consume(TokenType.Identifier, "identifier")
        val type = typeClause()
        return ParamClause(identifier, type)
    }

    private fun variableStatement(readOnly: Boolean): VariableStatementNode {
        val keyword = lexer.consume()
        val identifier = consume(TokenType.Identifier, "identifier")
        val type = optionalTypeClause()
        val equal = consume(TokenType.Equal, "=")
        val value = expression()
        return VariableStatementNode(keyword, identifier, type, readOnly, equal, value)
    }

    private fun statement(): StatementNode {

        if (match(TokenType.IfKeyword))
            return ifStatement()

        if (match(TokenType.WhileKeyword))
            return whileStatement()

        if (match(TokenType.DoKeyword))
            return doWhileStatement()

        if (match(TokenType.DeferKeyword))
            return deferStatement()

        if (match(TokenType.OpenCurly))
            return statementsBlock()

        return ExpressionStatementNode(expression())
    }

    private fun deferStatement(): StatementNode {
        val keyword = lexer.consume()
        val statement = statement()
        return DeferStatementNode(keyword, statement)
    }

    private fun doWhileStatement(): DoWhileStatementNode {
        val doKeyword = lexer.consume()
        val block = statement()
        val whileKeyword = consume(TokenType.WhileKeyword, "while")
        val open = consume(TokenType.OpenParentheses, "(")
        val condition = expression()
        val close = consume(TokenType.CloseParentheses, ")")
        return DoWhileStatementNode(doKeyword, block, whileKeyword, open, condition, close)
    }

    private fun whileStatement(): WhileStatementNode {
        val keyword = lexer.consume()
        val open = consume(TokenType.OpenParentheses, "(")
        val condition = expression()
        val close = consume(TokenType.CloseParentheses, ")")
        val block = statement()
        return WhileStatementNode(keyword, open, condition, close, block)
    }

    private fun ifStatement(): IfStatementNode {
        val keyword = lexer.consume()
        val open = consume(TokenType.OpenParentheses, "(")
        val condition = expression()
        val close = consume(TokenType.CloseParentheses, ")")
        val block = statement()
        val elseClause = optionalElseClause()
        return IfStatementNode(keyword, open, condition, close, block, elseClause)
    }

    private fun optionalElseClause(): ElseClause? {
        if (!match(TokenType.ElseKeyword)) return null
        val keyword = consume(TokenType.ElseKeyword, "else")
        val block = statement()
        return ElseClause(keyword, block)
    }

    private fun statementsBlock(): BlockStatementNode {
        val open = consume(TokenType.OpenCurly, "{")
        val statements = mutableListOf<StatementNode>()
        while (!lexer.isFinished && !match(TokenType.CloseCurly))
            avoidLooping { statements += declaration() }
        val close = consume(TokenType.CloseCurly, "}")
        return BlockStatementNode(open, statements, close)
    }

    private fun optionalTypeClause(): TypeClause? {
        return if (!match(TokenType.Colon)) null else typeClause()
    }

    private fun typeClause(): TypeClause {
        val colon = lexer.consume()
        val type = valueType()
        return TypeClause(colon, type)
    }

    private fun valueType(): ValueType {
        return unionType()
    }

    private fun unionType(): ValueType {

        val valueType = postfixType()

        if (match(TokenType.Pipe)) {

            val types = separatedListOf<ValueType, Token>()
            types.addElement(valueType)

            while (!lexer.isFinished && match(TokenType.Pipe)) avoidLooping {
                types.addSeparator(lexer.consume())
                types.addElement(postfixType())
            }

            return ValueType.Union(types)
        }

        return valueType
    }

    private fun postfixType(): ValueType {

        var keyValue = primaryType()

        while (!lexer.isFinished) {
            keyValue = if (
                matchAll(TokenType.OpenSquare, TokenType.CloseSquare) &&
                keyValue areTied lexer.current &&
                lexer.current areTied lexer.peek(1)
            ) {
                val open = lexer.consume()
                val close = lexer.consume()
                ValueType.List(keyValue, open, close)
            } else if (match(TokenType.OpenCurly) && keyValue areTied lexer.current) {
                val open = lexer.consume()
                if (match(TokenType.CloseCurly) && open areTied lexer.current) {
                    val close = lexer.consume()
                    ValueType.Set(keyValue, open, close)
                } else {
                    val valueType = valueType()
                    val close = consume(TokenType.CloseCurly, "}")
                    ValueType.Map(keyValue, open, valueType, close)
                }
            } else break
        }

        return keyValue
    }

    private fun primaryType(): ValueType {

        if (match(TokenType.OpenParentheses)) {

            val open = lexer.consume()
            val types = valueTypes()
            val close = consume(TokenType.CloseParentheses, ")")

            if (match(TokenType.RightArrow) || types.isEmpty()) {
                val arrow = consume(TokenType.RightArrow, "->")
                val returnType = valueType()
                return ValueType.Function(open, types, close, arrow, returnType)
            }

            if (types.sumSize != 1) {
                return ValueType.Tuple(open, types, close)
            }

            return ValueType.Parenthesised(open, types.getElement(0), close)
        }

        return ValueType.Normal(consume(TokenType.Identifier, "type"))
    }

    private fun valueTypes(): SeparatedList<ValueType, Token> {
        val types = separatedListOf<ValueType, Token>()
        while (!lexer.isFinished && !match(TokenType.CloseParentheses)) avoidLooping {
            types.addElement(valueType())
            if (!match(TokenType.CloseParentheses))
                types.addSeparator(consume(TokenType.Comma, ","))
        }
        return types
    }

    private fun expression(): ExpressionNode {
        return assignment()
    }

    private fun assignment(): ExpressionNode {

        val target = ternary()

        if (match(TokenType.Equal)) {
            val operation = lexer.consume()
            val expression = assignment()
            return AssignmentExpressionNode(target, operation, expression)
        }

        return target
    }

    private fun ternary(): ExpressionNode {

        val condition = disjunction()

        if (match(TokenType.Question)) {
            val question = consume(TokenType.Question, "?")
            val ifExpression = ternary()
            val colon = consume(TokenType.Colon, ":")
            val elseExpression = ternary()
            return TernaryExpressionNode(condition, question, ifExpression, colon, elseExpression)
        }

        return condition
    }

    private fun disjunction(): ExpressionNode {
        return operation(TokenType.PipePipe) { conjunction() }
    }

    private fun conjunction(): ExpressionNode {
        return operation(TokenType.AmpersandAmpersand) { equality() }
    }

    private fun equality(): ExpressionNode {
        return operation(TokenType.EqualEqual, TokenType.BangEqual) { comparison() }
    }

    private fun comparison(): ExpressionNode {
        return operation(
            TokenType.GreaterThan,
            TokenType.GreaterThanEqual,
            TokenType.LessThan,
            TokenType.LessThanEqual
        ) { additive() }
    }

    private fun additive(): ExpressionNode {
        return operation(TokenType.Plus, TokenType.Minus) { multiplicative() }
    }

    private fun multiplicative(): ExpressionNode {
        return operation(TokenType.Star, TokenType.Slash) { typeCasting() }
    }

    private fun typeCasting(): ExpressionNode {
        var left = unary()
        while (!lexer.isFinished && match(TokenType.AsKeyword)) {
            val operation = lexer.consume()
            val valueType = valueType()
            left = TypeCastExpressionNode(left, operation, valueType)
        }
        return left
    }

    private fun unary(): ExpressionNode {

        if (matchAny(TokenType.Plus, TokenType.Minus, TokenType.Bang)) {
            val op = lexer.consume()
            val operand = unary()
            return UnaryOperationExpressionNode(op, operand)
        }

        return postfix()
    }

    private fun postfix(): ExpressionNode {
        var left = primary()
        while (!lexer.isFinished && left atSameLine lexer.current) {
            left = if (
                match(TokenType.OpenParentheses) ||
                match(TokenType.LessThan) && lexer.current areTied lexer.peek(1)
            ) {
                callExpression(left)
            } else if (match(TokenType.OpenSquare)) {
                indexedExpression(left)
            } else if (match(TokenType.Dot)) {
                getExpression(left)
            } else break
        }
        return left
    }

    private fun getExpression(left: ExpressionNode): GetExpressionNode {
        val dot = lexer.consume()
        val identifier = consume(TokenType.Identifier, "identifier")
        return GetExpressionNode(left, dot, identifier)
    }

    private fun indexedExpression(left: ExpressionNode): IndexedExpressionNode {
        val open = lexer.consume()
        val index = expression()
        val close = consume(TokenType.CloseSquare, "]")
        return IndexedExpressionNode(left, open, index, close)
    }

    private fun callExpression(left: ExpressionNode): CallExpressionNode {
        val open = lexer.consume()
        val args = arguments()
        val close = consume(TokenType.CloseParentheses, ")")
        return CallExpressionNode(left, open, args, close)
    }

    private fun arguments(): SeparatedList<ExpressionNode, Token> {
        val arguments = separatedListOf<ExpressionNode, Token>()
        while (!lexer.isFinished && !match(TokenType.CloseParentheses)) avoidLooping {
            arguments.addElement(expression())
            if (!match(TokenType.CloseParentheses))
                arguments.addSeparator(consume(TokenType.Comma, ","))
        }
        return arguments
    }

    private fun primary(): ExpressionNode {

        if (matchAny(
                TokenType.String, TokenType.Character,
                TokenType.Integer, TokenType.Float,
                TokenType.Long, TokenType.Double
            )
        ) {
            val literal = lexer.consume()
            return LiteralExpressionNode(literal, literal.requireValue(), literal.span)
        }

        if (matchAny(TokenType.TrueKeyword, TokenType.FalseKeyword)) {
            val boolean = lexer.consume()
            val value = boolean.type == TokenType.TrueKeyword
            return LiteralExpressionNode(boolean, value, boolean.span)
        }

        if (match(TokenType.FunKeyword)) {
            val keyword = lexer.consume()
            val open = consume(TokenType.OpenParentheses, "(")
            val params = parameters()
            val close = consume(TokenType.CloseParentheses, ")")
            val type = optionalTypeClause()
            val block = statementsBlock()
            return FunctionExpressionNode(keyword, open, params, close, type, block)
        }

        if (match(TokenType.NewKeyword)) {
            val keyword = lexer.consume()
            val identifier = consume(TokenType.Identifier, "identifier")
            val open = consume(TokenType.OpenParentheses, "(")
            val args = arguments()
            val close = consume(TokenType.CloseParentheses, ")")
            return RecordInitExpressionNode(keyword, identifier, open, args, close)
        }

        if (match(TokenType.ReturnKeyword)) {
            val keyword = lexer.consume()
            val value = returnValue(keyword)
            return ReturnExpressionNode(keyword, value)
        }

        if (match(TokenType.PanicKeyword)) {
            val keyword = lexer.consume()
            val message = expression()
            return PanicExpressionNode(keyword, message)
        }

        if (match(TokenType.TryKeyword)) {
            val keyword = lexer.consume()
            val expression = expression()
            return TryExpressionNode(keyword, expression)
        }

        if (match(TokenType.NoneKeyword)) {
            return NoneExpressionNode(lexer.consume())
        }

        if (match(TokenType.SelfKeyword)) {
            return SelfExpressionNode(lexer.consume())
        }

        if (match(TokenType.ContinueKeyword)) {
            return ContinueExpressionNode(lexer.consume())
        }

        if (match(TokenType.BreakKeyword)) {
            return BreakExpressionNode(lexer.consume())
        }

        if (match(TokenType.OpenSquare)) {

            val open = lexer.consume()

            val values = separatedListOf<ExpressionNode, Token>()

            while (!lexer.isFinished && !match(TokenType.CloseSquare)) avoidLooping {
                values.addElement(expression())
                if (!match(TokenType.CloseSquare))
                    values.addSeparator(consume(TokenType.Comma, ","))
            }

            val close = consume(TokenType.CloseSquare, "]")

            return ListValueExpressionNode(open, values, close)
        }

        if (match(TokenType.OpenCurly)) {

            val open = lexer.consume()

            var type: MapValueType = MapValueType.Unknown
            val values = separatedListOf<MapExpression, Token>()

            while (!lexer.isFinished && !match(TokenType.CloseCurly)) avoidLooping {

                val expression = expression()

                if (type == MapValueType.Unknown) {
                    type = if (match(TokenType.Colon)) MapValueType.Map else MapValueType.Set
                }

                if (type == MapValueType.Map) {
                    val colon = consume(TokenType.Colon, ":")
                    val value = expression()
                    values.addElement(MapExpression.KeyValue(expression, colon, value))
                } else if (type == MapValueType.Set) {
                    values.addElement(MapExpression.ValueOnly(expression))
                }

                if (!match(TokenType.CloseCurly)) {
                    values.addSeparator(consume(TokenType.Comma, ","))
                }
            }

            val close = consume(TokenType.CloseCurly, "}")

            return MapValueExpressionNode(open, values, close, type)
        }

        if (match(TokenType.OpenParentheses)) {

            val open = lexer.consume()
            val expr = expression()

            if (match(TokenType.CloseParentheses)) {
                val close = lexer.consume()
                return ParenthesisedExpressionNode(open, expr, close)
            }

            val values = separatedListOf<ExpressionNode, Token>().apply {
                addElement(expr)
                addSeparator(consume(TokenType.Comma, ","))
            }

            while (!lexer.isFinished && !match(TokenType.CloseParentheses)) avoidLooping {
                values.addElement(expression())
                if (!match(TokenType.CloseParentheses))
                    values.addSeparator(consume(TokenType.Comma, ","))
            }

            val close = consume(TokenType.CloseParentheses, ")")

            return TupleValueExpressionNode(open, values, close)
        }

        return VariableExpressionNode(consume(TokenType.Identifier, "expression"))
    }

    private fun returnValue(token: Token): ExpressionNode? {
        val isValid = !lexer.isFinished && token atSameLine lexer.current && when (lexer.current.type) {
            TokenType.Plus -> true
            TokenType.Bang -> true
            TokenType.Minus -> true
            TokenType.NewKeyword -> true
            TokenType.OpenCurly -> true
            TokenType.OpenSquare -> true
            TokenType.OpenParentheses -> true
            TokenType.FunKeyword -> true
            TokenType.SelfKeyword -> true
            TokenType.NoneKeyword -> true
            TokenType.TrueKeyword -> true
            TokenType.FalseKeyword -> true
            TokenType.TryKeyword -> true
            TokenType.Identifier -> true
            TokenType.ReturnKeyword -> true
            TokenType.PanicKeyword -> true
            TokenType.BreakKeyword -> true
            TokenType.ContinueKeyword -> true
            TokenType.Long -> true
            TokenType.Float -> true
            TokenType.String -> true
            TokenType.Integer -> true
            TokenType.Double -> true
            TokenType.Character -> true
            // #################################
            TokenType.Whitespace -> false
            TokenType.LineBreak -> false
            TokenType.LineComment -> false
            TokenType.BlockComment -> false
            TokenType.IllegalChar -> false
            TokenType.Slash -> false
            TokenType.Star -> false
            TokenType.CloseParentheses -> false
            TokenType.PipePipe -> false
            TokenType.AmpersandAmpersand -> false
            TokenType.EqualEqual -> false
            TokenType.BangEqual -> false
            TokenType.GreaterThanEqual -> false
            TokenType.LessThanEqual -> false
            TokenType.GreaterThan -> false
            TokenType.LessThan -> false
            TokenType.Equal -> false
            TokenType.Colon -> false
            TokenType.CloseCurly -> false
            TokenType.Comma -> false
            TokenType.RightArrow -> false
            TokenType.Question -> false
            TokenType.Dot -> false
            TokenType.CloseSquare -> false
            TokenType.Pipe -> false
            TokenType.LetKeyword -> false
            TokenType.DefKeyword -> false
            TokenType.IfKeyword -> false
            TokenType.ElseKeyword -> false
            TokenType.WhileKeyword -> false
            TokenType.DoKeyword -> false
            TokenType.AsKeyword -> false
            TokenType.RecordKeyword -> false
            TokenType.DeferKeyword -> false
            TokenType.EndOfFile -> false
        }
        return if (isValid) expression() else null
    }

    private fun consume(type: TokenType, expected: String): Token {

        if (match(type)) {
            if (!errorsFlag) errorsFlag = true
            return lexer.consume()
        }

        val token = lexer.current

        if (errorsFlag) {
            errors.reportUnexpectedToken(token, token.text, expected)
            errorsFlag = false
        }

        return Token("", type, null, token.span, listOf(), listOf())
    }

    private inline fun matchAll(vararg types: TokenType): Boolean {
        return !lexer.isFinished && types.allIndexed { index, token -> lexer.peek(index).type == token }
    }

    private inline fun matchAny(vararg types: TokenType): Boolean {
        return !lexer.isFinished && types.any { lexer.current.type == it }
    }

    private inline fun match(type: TokenType): Boolean {
        return !lexer.isFinished && lexer.current.type == type
    }

    private inline fun operation(vararg types: TokenType, next: () -> ExpressionNode): ExpressionNode {
        var left = next()
        while (!lexer.isFinished && matchAny(*types)) {
            val operation = lexer.consume()
            left = BinaryOperationExpressionNode(left, operation, next())
        }
        return left
    }

    private inline fun avoidLooping(block: () -> Unit) {
        val current = lexer.current
        block()
        if (current === lexer.current)
            lexer.advance()
        errorsFlag = true
    }

    override fun close() = lexer.close()
}

private inline infix fun Spannable.areTied(that: Spannable): Boolean {
    return span.end == that.span.start
}

private inline infix fun Spannable.atSameLine(that: Spannable): Boolean {
    return span.start.line == that.span.start.line
}
