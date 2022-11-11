package sash.parser.nodes

import sash.lexer.token.Token
import sash.span.Span
import sash.span.Spannable
import sash.span.plus
import sash.tools.MutableSeparatedList
import sash.tools.SeparatedList

internal sealed class ExpressionNode : Spannable

internal data class IndexedExpressionNode(
    val target: ExpressionNode,
    val open: Token,
    val index: ExpressionNode,
    val close: Token
) : ExpressionNode() {
    override val span: Span = target + close
}

internal data class TernaryExpressionNode(
    val condition: ExpressionNode,
    val question: Token,
    val ifExpression: ExpressionNode,
    val colon: Token,
    val elseExpression: ExpressionNode
) : ExpressionNode() {
    override val span: Span = condition + elseExpression
}

internal data class ListValueExpressionNode(
    val open: Token,
    val values: MutableSeparatedList<ExpressionNode, Token>,
    val close: Token
) : ExpressionNode() {
    override val span: Span = open + close
}

internal data class MapValueExpressionNode(
    val open: Token,
    val values: MutableSeparatedList<MapExpression, Token>,
    val close: Token,
    val type: MapValueType
) : ExpressionNode() {
    override val span: Span = open + close
}

internal data class TupleValueExpressionNode(
    val open: Token,
    val values: MutableSeparatedList<ExpressionNode, Token>,
    val close: Token
) : ExpressionNode() {
    override val span: Span = open + close
}

internal data class RecordInitExpressionNode(
    val keyword: Token,
    val identifier: Token,
    val open: Token,
    val args: SeparatedList<ExpressionNode, Token>,
    val close: Token
) : ExpressionNode() {
    override val span: Span = keyword + close
}

internal data class TypeCastExpressionNode(
    val target: ExpressionNode,
    val operation: Token,
    val valueType: ValueType
) : ExpressionNode() {
    override val span: Span = target + valueType
}

internal data class LiteralExpressionNode(
    val token: Token,
    val value: Any,
    override val span: Span
) : ExpressionNode()

internal data class NoneExpressionNode(
    val token: Token
) : ExpressionNode() {
    override val span: Span = token.span
}

internal data class SelfExpressionNode(
    val token: Token
) : ExpressionNode() {
    override val span: Span = token.span
}

internal data class ContinueExpressionNode(
    val keyword: Token
) : ExpressionNode() {
    override val span: Span = keyword.span
}

internal data class BreakExpressionNode(
    val keyword: Token
) : ExpressionNode() {
    override val span: Span = keyword.span
}

internal data class AssignmentExpressionNode(
    val target: ExpressionNode,
    val equal: Token,
    val expression: ExpressionNode
) : ExpressionNode() {
    override val span: Span = target + expression
}

internal data class VariableExpressionNode(
    val identifier: Token
) : ExpressionNode() {
    override val span: Span = identifier.span
}

internal data class UnaryOperationExpressionNode(
    val operation: Token,
    val operand: ExpressionNode
) : ExpressionNode() {
    override val span: Span = operation + operand
}

internal data class BinaryOperationExpressionNode(
    val left: ExpressionNode,
    val operation: Token,
    val right: ExpressionNode
) : ExpressionNode() {
    override val span: Span = left + right
}

internal data class ParenthesisedExpressionNode(
    val leftParent: Token,
    val expression: ExpressionNode,
    val rightParent: Token
) : ExpressionNode() {
    override val span: Span = leftParent + rightParent
}

internal data class CallExpressionNode(
    val target: ExpressionNode,
    val open: Token,
    val args: SeparatedList<ExpressionNode, Token>,
    val close: Token
) : ExpressionNode() {
    override val span: Span = target + close
}

internal data class FunctionExpressionNode(
    val keyword: Token,
    val open: Token,
    val params: SeparatedList<ParamClause, Token>,
    val close: Token,
    val type: TypeClause?,
    val block: BlockStatementNode
) : ExpressionNode() {
    override val span: Span = keyword + block
}

internal data class ReturnExpressionNode(
    val keyword: Token,
    val value: ExpressionNode?
) : ExpressionNode() {
    override val span: Span = when (value) {
        null -> keyword.span
        else -> keyword + value
    }
}

internal data class PanicExpressionNode(
    val keyword: Token,
    val message: ExpressionNode
) : ExpressionNode() {
    override val span: Span = keyword + message
}

internal data class TryExpressionNode(
    val keyword: Token,
    val expression: ExpressionNode
) : ExpressionNode() {
    override val span: Span = keyword + expression
}

internal data class GetExpressionNode(
    val target: ExpressionNode,
    val dot: Token,
    val identifier: Token
) : ExpressionNode() {
    override val span: Span = target + identifier
}
