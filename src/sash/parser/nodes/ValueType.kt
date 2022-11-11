package sash.parser.nodes

import sash.lexer.token.Token
import sash.span.Span
import sash.span.Spannable
import sash.span.plus
import sash.tools.SeparatedList
import sash.tools.first
import sash.tools.last

internal sealed class ValueType : Spannable {

    internal data class Normal(
        internal val type: Token
    ) : ValueType() {
        override val span: Span = type.span
    }

    internal data class Parenthesised(
        internal val open: Token,
        internal val type: ValueType,
        internal val close: Token
    ) : ValueType() {
        override val span: Span = open + close
    }

    internal data class Function(
        internal val open: Token,
        internal val types: SeparatedList<ValueType, Token>,
        internal val close: Token,
        internal val arrow: Token,
        internal val returnType: ValueType
    ) : ValueType() {
        override val span: Span = open + returnType
    }

    internal data class Tuple(
        internal val open: Token,
        internal val types: SeparatedList<ValueType, Token>,
        internal val close: Token
    ) : ValueType() {
        override val span: Span = open + close
    }

    internal data class List(
        internal val type: ValueType,
        internal val open: Token,
        internal val close: Token
    ) : ValueType() {
        override val span: Span = type + close
    }

    internal data class Set(
        internal val type: ValueType,
        internal val open: Token,
        internal val close: Token
    ) : ValueType() {
        override val span: Span = type + close
    }

    internal data class Map(
        internal val keyType: ValueType,
        internal val open: Token,
        internal val valueType: ValueType,
        internal val close: Token
    ) : ValueType() {
        override val span: Span = keyType + close
    }

    internal data class Union(
        internal val types: SeparatedList<ValueType, Token>
    ) : ValueType() {
        override val span: Span = types.first() + types.last()
    }
}
