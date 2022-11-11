package sash.parser.nodes

import sash.lexer.token.Token
import sash.span.Span
import sash.span.Spannable
import sash.span.plus

internal sealed class MapExpression : Spannable {

    internal data class ValueOnly(
        internal val value: ExpressionNode
    ) : MapExpression() {
        override val span: Span = value.span
    }

    internal data class KeyValue(
        internal val key: ExpressionNode,
        internal val colon: Token,
        internal val value: ExpressionNode
    ) : MapExpression(){
        override val span: Span = key + value
    }
}

internal sealed class MapValueType {
    internal object Unknown : MapValueType()
    internal object Map : MapValueType()
    internal object Set : MapValueType()
}