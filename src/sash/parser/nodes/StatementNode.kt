package sash.parser.nodes

import sash.lexer.token.Token
import sash.span.Span
import sash.span.Spannable
import sash.span.plus
import sash.tools.SeparatedList

internal sealed class StatementNode : Spannable

internal data class VariableStatementNode(
    internal val keyword: Token,
    internal val identifier: Token,
    internal val type: TypeClause?,
    internal val readOnly: Boolean,
    internal val equal: Token,
    internal val value: ExpressionNode
) : StatementNode() {
    override val span: Span = keyword + value
}

internal data class FunctionStatementNode(
    internal val keyword: Token,
    internal val identifier: Token,
    internal val open: Token,
    internal val params: SeparatedList<ParamClause, Token>,
    internal val close: Token,
    internal val type: TypeClause?,
    internal val block: BlockStatementNode
) : StatementNode() {
    override val span: Span = keyword + block
}

internal data class MethodStatementNode(
    internal val keyword: Token,
    internal val receiver: ValueType,
    internal val dot: Token,
    internal val identifier: Token,
    internal val open: Token,
    internal val params: SeparatedList<ParamClause, Token>,
    internal val close: Token,
    internal val type: TypeClause?,
    internal val block: BlockStatementNode
) : StatementNode() {
    override val span: Span = keyword + block
}

internal data class RecordStatementNode(
    internal val keyword: Token,
    internal val identifier: Token,
    internal val open: Token,
    internal val fields: List<RecordField>,
    internal val close: Token
) : StatementNode() {
    override val span: Span = keyword + close
}

internal data class IfStatementNode(
    internal val keyword: Token,
    internal val open: Token,
    internal val condition: ExpressionNode,
    internal val close: Token,
    internal val block: StatementNode,
    internal val elseClause: ElseClause?
) : StatementNode() {
    override val span: Span = keyword + (elseClause ?: block)
}

internal data class WhileStatementNode(
    internal val keyword: Token,
    internal val open: Token,
    internal val condition: ExpressionNode,
    internal val close: Token,
    internal val block: StatementNode
) : StatementNode() {
    override val span: Span = keyword + block
}

internal data class DoWhileStatementNode(
    internal val doKeyword: Token,
    internal val block: StatementNode,
    internal val whileKeyword: Token,
    internal val open: Token,
    internal val condition: ExpressionNode,
    internal val close: Token
) : StatementNode() {
    override val span: Span = doKeyword + close
}

internal data class DeferStatementNode(
    internal val keyword: Token,
    internal val statement: StatementNode
) : StatementNode() {
    override val span: Span = keyword + statement
}

internal data class BlockStatementNode(
    internal val open: Token,
    internal val statements: List<StatementNode>,
    internal val close: Token
) : StatementNode() {
    override val span: Span = open + close
}

internal data class ExpressionStatementNode(
    internal val expression: ExpressionNode
) : StatementNode() {
    override val span: Span = expression.span
}