package sash.parser.nodes

import sash.lexer.token.Token
import sash.span.Span
import sash.span.Spannable
import sash.span.plus

internal sealed class RecordField : Spannable {
    internal data class NormalField(
        internal val identifier: Token,
        internal val type: TypeClause
    ) : RecordField() {
        override val span: Span = identifier + type
    }
}
