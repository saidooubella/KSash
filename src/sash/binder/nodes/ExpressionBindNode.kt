package sash.binder.nodes

import sash.span.Span
import sash.span.Spannable
import sash.symbols.*
import sash.types.*

internal sealed class ExpressionBindNode : Spannable {
    internal abstract val type: BaseType
    internal abstract val isValidStatement: Boolean
    internal fun isError(): Boolean = type == ErrorType
}

internal data class SetValueExpressionBindNode(
    internal val values: List<ExpressionBindNode>,
    override val type: BaseType,
    override val span: Span
) : ExpressionBindNode() {
    override val isValidStatement: Boolean = false
}

internal data class MapValueExpressionBindNode(
    internal val values: List<Pair<ExpressionBindNode, ExpressionBindNode>>,
    override val type: BaseType,
    override val span: Span
) : ExpressionBindNode() {
    override val isValidStatement: Boolean = false
}

internal data class ListValueExpressionBindNode(
    internal val values: List<ExpressionBindNode>,
    override val type: BaseType,
    override val span: Span
) : ExpressionBindNode() {
    override val isValidStatement: Boolean = false
}

internal data class TupleValueExpressionBindNode(
    internal val values: List<ExpressionBindNode>,
    override val type: BaseType,
    override val span: Span
) : ExpressionBindNode() {
    override val isValidStatement: Boolean = false
}

internal data class TryExpressionBindNode(
    internal val expression: ExpressionBindNode,
    override val type: BaseType,
    override val span: Span
) : ExpressionBindNode() {
    override val isValidStatement: Boolean = true
}

internal data class PanicExpressionBindNode(
    internal val message: ExpressionBindNode,
    override val span: Span
) : ExpressionBindNode() {
    override val isValidStatement: Boolean = true
    override val type: BaseType = NothingType
}

internal data class SetIndexedExpressionBindNode(
    internal val target: ExpressionBindNode,
    internal val index: ExpressionBindNode,
    internal val value: ExpressionBindNode,
    override val type: BaseType,
    override val span: Span
) : ExpressionBindNode() {
    override val isValidStatement: Boolean = true
}

internal data class SetFieldExpressionBindNode(
    internal val target: ExpressionBindNode,
    internal val field: FieldSymbol,
    internal val value: ExpressionBindNode,
    override val type: BaseType,
    override val span: Span
) : ExpressionBindNode() {
    override val isValidStatement: Boolean = true
}

internal data class TernaryExpressionBindNode(
    internal val condition: ExpressionBindNode,
    internal val ifExpression: ExpressionBindNode,
    internal val elseExpression: ExpressionBindNode,
    override val type: BaseType,
    override val span: Span
) : ExpressionBindNode() {
    override val isValidStatement: Boolean = false
}

internal data class GetFieldExpressionBindNode(
    internal val target: ExpressionBindNode,
    internal val field: FieldSymbol,
    override val span: Span
) : ExpressionBindNode() {
    override val isValidStatement: Boolean = false
    override val type: BaseType = field.type
}

internal data class GetMethodExpressionBindNode(
    internal val target: ExpressionBindNode,
    internal val method: MethodSymbol,
    override val span: Span
) : ExpressionBindNode() {
    override val isValidStatement: Boolean = false
    override val type: BaseType = method.type
}

internal data class TypeCastExpressionBindNode(
    internal val target: ExpressionBindNode,
    override val type: BaseType,
    override val span: Span
) : ExpressionBindNode() {
    override val isValidStatement: Boolean = false
}

internal data class NoneExpressionBindNode(
    override val span: Span
) : ExpressionBindNode() {
    override val type: BaseType = NoneType
    override val isValidStatement: Boolean = false
}

internal data class FunctionExpressionBindNode(
    internal val parameters: List<ParameterSymbol>,
    internal val block: BlockStatementBindNode,
    override val type: FunctionType,
    override val span: Span
) : ExpressionBindNode() {
    override val isValidStatement = false
}

internal data class ReturnExpressionBindNode(
    internal val value: ExpressionBindNode?,
    override val span: Span
) : ExpressionBindNode() {
    override val type: BaseType = NothingType
    override val isValidStatement: Boolean = true
}

internal data class CallExpressionBindNode(
    internal val target: ExpressionBindNode,
    internal val arguments: List<ExpressionBindNode>,
    override val type: BaseType,
    override val span: Span
) : ExpressionBindNode() {
    override val isValidStatement: Boolean = true
}

internal data class IndexedExpressionBindNode(
    internal val target: ExpressionBindNode,
    internal val index: ExpressionBindNode,
    override val type: BaseType,
    override val span: Span
) : ExpressionBindNode() {
    override val isValidStatement: Boolean = true
}

internal data class RecordInitExpressionBindNode(
    internal val record: RecordSymbol,
    internal val arguments: List<ExpressionBindNode>,
    override val type: BaseType,
    override val span: Span
) : ExpressionBindNode() {
    override val isValidStatement: Boolean = false
}

internal data class ContinueExpressionBindNode(
    override val span: Span
) : ExpressionBindNode() {
    override val type: BaseType = NothingType
    override val isValidStatement: Boolean = true
}

internal data class BreakExpressionBindNode(
    override val span: Span
) : ExpressionBindNode() {
    override val type: BaseType = NothingType
    override val isValidStatement: Boolean = true
}

internal data class AssignmentExpressionBindNode(
    internal val variable: VariableSymbol,
    internal val value: ExpressionBindNode,
    override val type: BaseType,
    override val span: Span
) : ExpressionBindNode() {
    override val isValidStatement: Boolean = true
}

internal data class VariableExpressionBindNode(
    internal val symbol: Symbol,
    override val span: Span
) : ExpressionBindNode() {
    override val type: BaseType = symbol.type
    override val isValidStatement: Boolean = false
}

internal data class BinaryOperationExpressionBindNode(
    internal val left: ExpressionBindNode,
    internal val operation: BinaryOperation,
    internal val right: ExpressionBindNode,
    override val type: BaseType,
    override val span: Span
) : ExpressionBindNode() {
    override val isValidStatement: Boolean = false
}

internal data class UnaryOperationExpressionBindNode(
    internal val operation: UnaryOperation,
    internal val operand: ExpressionBindNode,
    override val type: BaseType,
    override val span: Span
) : ExpressionBindNode() {
    override val isValidStatement: Boolean = false
}

internal data class ParenthesisedExpressionBindNode(
    internal val expression: ExpressionBindNode,
    override val span: Span
) : ExpressionBindNode() {
    override val type: BaseType = expression.type
    override val isValidStatement: Boolean = false
}

internal data class LiteralExpressionBindNode(
    internal val value: Any,
    override val type: BaseType,
    override val span: Span
) : ExpressionBindNode() {
    override val isValidStatement: Boolean = false
}

internal data class ErrorExpressionBindNode(
    override val span: Span
) : ExpressionBindNode() {
    override val type: BaseType = ErrorType
    override val isValidStatement: Boolean = true
}
