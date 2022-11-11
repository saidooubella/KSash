package sash.binder.nodes

import sash.span.Span
import sash.span.Spannable
import sash.symbols.FunctionSymbol
import sash.symbols.MethodSymbol
import sash.symbols.RecordSymbol
import sash.symbols.VariableSymbol

internal sealed class StatementBindNode : Spannable

// -----------------------------------------------

internal sealed class DeclarationBindNode : StatementBindNode()

internal data class RecordStatementBindNode(
    internal val record: RecordSymbol,
    override val span: Span
) : DeclarationBindNode()

internal data class MethodStatementBindNode(
    internal val method: MethodSymbol,
    internal val block: BlockStatementBindNode,
    override val span: Span
) : DeclarationBindNode()

internal data class FunctionStatementBindNode(
    internal val function: FunctionSymbol,
    internal val block: BlockStatementBindNode,
    override val span: Span
) : DeclarationBindNode()

internal data class VariableStatementBindNode(
    internal val variable: VariableSymbol,
    internal val value: ExpressionBindNode,
    override val span: Span
) : DeclarationBindNode()

// -----------------------------------------------

internal data class DeferStatementBindNode(
    internal val statement: StatementBindNode,
    override val span: Span
) : StatementBindNode()

internal data class ExpressionStatementBindNode(
    internal val expression: ExpressionBindNode
) : StatementBindNode() {
    override val span: Span = expression.span
}

internal data class BlockStatementBindNode(
    internal val statements: List<StatementBindNode>,
    override val span: Span
) : StatementBindNode()

internal data class IfStatementBindNode(
    internal val condition: ExpressionBindNode,
    internal val ifBlock: StatementBindNode,
    internal val elseBlock: StatementBindNode?,
    override val span: Span
) : StatementBindNode()

internal data class WhileStatementBindNode(
    internal val condition: ExpressionBindNode,
    internal val block: StatementBindNode,
    override val span: Span
) : StatementBindNode()

internal data class DoWhileStatementBindNode(
    internal val block: StatementBindNode,
    internal val condition: ExpressionBindNode,
    override val span: Span
) : StatementBindNode()
