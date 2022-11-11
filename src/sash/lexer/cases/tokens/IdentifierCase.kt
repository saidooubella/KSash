package sash.lexer.cases.tokens

import sash.errors.ErrorsReporter
import sash.input.CharInput
import sash.lexer.cases.CheckResult
import sash.lexer.cases.TokenCase
import sash.lexer.token.TokenInfo
import sash.lexer.token.TokenType
import sash.span.Span

internal object IdentifierCase : TokenCase {

    override fun perform(input: CharInput, reporter: ErrorsReporter, extra: Any?): TokenInfo {

        val builder = StringBuilder()
        val start = input.position()

        while (!input.isFinished && input.current.isIdentifierFull()) {
            builder.append(input.consume())
        }

        val end = input.position()
        val span = Span(start, end)
        val identifier = builder.toString()

        return TokenInfo(identifier, identifier.tokenType(), null, span)
    }

    override fun check(input: CharInput): CheckResult {
        return CheckResult(input.current.isIdentifier())
    }

    private val KEYWORDS: Map<String, TokenType> = mapOf(
        "continue" to TokenType.ContinueKeyword,
        "record" to TokenType.RecordKeyword,
        "return" to TokenType.ReturnKeyword,
        "break" to TokenType.BreakKeyword,
        "defer" to TokenType.DeferKeyword,
        "false" to TokenType.FalseKeyword,
        "panic" to TokenType.PanicKeyword,
        "while" to TokenType.WhileKeyword,
        "else" to TokenType.ElseKeyword,
        "none" to TokenType.NoneKeyword,
        "self" to TokenType.SelfKeyword,
        "true" to TokenType.TrueKeyword,
        "def" to TokenType.DefKeyword,
        "fun" to TokenType.FunKeyword,
        "let" to TokenType.LetKeyword,
        "new" to TokenType.NewKeyword,
        "try" to TokenType.TryKeyword,
        "as" to TokenType.AsKeyword,
        "do" to TokenType.DoKeyword,
        "if" to TokenType.IfKeyword
    )

    private fun String.tokenType() = KEYWORDS[this] ?: TokenType.Identifier

    private fun Char.isIdentifier() = isLetter() || this == '_' || this == '$'

    private fun Char.isIdentifierFull() = isIdentifier() || isDigit()
}