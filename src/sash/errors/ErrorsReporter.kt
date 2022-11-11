package sash.errors

import sash.span.Spannable
import sash.symbols.MethodSymbol
import sash.types.BaseType

internal class ErrorsReporter {

    private val errors = mutableListOf<ErrorMessage>()

    internal val asErrorReports get() = ErrorReports(errors)

    private fun report(span: Spannable, message: String) {
        errors += ErrorMessage(span.span, message)
    }

    internal fun reportIllegalCharacter(span: Spannable, value: Char) {
        report(span, "Illegal character '$value'")
    }

    internal fun reportInvalidLiteral(span: Spannable, value: String) {
        report(span, "Invalid literal '$value'")
    }

    internal fun reportUnterminatedStringLiteral(span: Spannable) {
        report(span, "Unterminated string literal")
    }

    internal fun reportIllegalEscape(span: Spannable, esc: String) {
        report(span, "Illegal escape: $esc")
    }

    internal fun reportEmptyCharLiteral(span: Spannable) {
        report(span, "Empty character literal")
    }

    internal fun reportTooManyCharacterInCharLiteral(span: Spannable) {
        report(span, "Too many characters in a character literal")
    }

    internal fun reportUnterminatedCharLiteral(span: Spannable) {
        report(span, "Unterminated character literal")
    }

    internal fun reportUnterminatedBlockComment(span: Spannable) {
        report(span, "Unterminated block comment")
    }

    internal fun reportUnexpectedToken(span: Spannable, actual: String, expected: String) {
        report(span, "Unexpected '$actual', expected '$expected'")
    }

    internal fun reportInvalidUnaryOperation(span: Spannable, operation: String, type: BaseType) {
        report(span, "The '$operation' operator cannot be applied to '$type'")
    }

    internal fun reportInvalidBinaryOperation(span: Spannable, operation: String, left: BaseType, right: BaseType) {
        report(span, "The '$operation' operator cannot be applied to '$left' and '$right'")
    }

    internal fun reportIllegalExpressionStatement(span: Spannable) {
        report(span, "This isn't a valid statement")
    }

    internal fun reportUnknownSymbol(span: Spannable, name: String) {
        report(span, "Unknown symbol '$name'")
    }

    internal fun reportAlreadyExistentSymbol(span: Spannable, name: String) {
        report(span, "Already existent symbol '$name'")
    }

    internal fun reportWrongAssignment(span: Spannable, valueType: BaseType, targetType: BaseType) {
        report(span, "You can't assign a value of type '$valueType' to '$targetType'")
    }

    internal fun reportReadOnlyVariableAssignment(span: Spannable, name: String) {
        report(span, "'$name' is final and it cannot be modified")
    }

    internal fun reportInvalidAssignmentTarget(span: Spannable) {
        report(span, "This isn't a valid assignment target")
    }

    internal fun reportInvalidConditionType(span: Spannable) {
        report(span, "A condition must be of type 'boolean'")
    }

    internal fun reportJumpThroughFunction(span: Spannable, name: String) {
        report(span, "'$name' cannot transfer control out of a 'function' statement")
    }

    internal fun reportJumpThroughDefer(span: Spannable, name: String) {
        report(span, "'$name' cannot transfer control out of a 'defer' statement")
    }

    internal fun reportOutOfLoopScope(span: Spannable, name: String) {
        report(span, "'$name' must be in a 'while' or 'do..while' statement")
    }

    internal fun reportAlreadyUsedParamName(span: Spannable, name: String) {
        report(span, "There already is a parameter named '$name'")
    }

    internal fun reportAlreadyUsedFieldName(span: Spannable, name: String) {
        report(span, "There already is a field named '$name'")
    }

    internal fun reportUndefinedType(span: Spannable, name: String) {
        report(span, "Undefined type '$name'")
    }

    internal fun reportInvalidInvokableTarget(span: Spannable) {
        report(span, "This isn't a valid calling target")
    }

    internal fun reportInvalidIndexedTarget(span: Spannable) {
        report(span, "This isn't an indexed target")
    }

    internal fun reportUnexpectedArgsSize(span: Spannable, expected: Int, actual: Int) {
        report(span, "Unexpected arguments size: expected: '$expected', actual: '$actual'")
    }

    internal fun reportInvalidArgumentType(span: Spannable, expected: BaseType, actual: BaseType) {
        report(span, "Unexpected argument type: expected: '$expected', actual: '$actual'")
    }

    internal fun reportUnreachedStatement(span: Spannable) {
        report(span, "Unreached statement")
    }

    internal fun reportWrongReturnValueType(span: Spannable, expected: BaseType, actual: BaseType) {
        report(span, "A value of type '$actual' cannot be return by a function of type '$expected'")
    }

    internal fun reportMissingReturnValue(span: Spannable, type: BaseType) {
        report(span, "This return expression must provide a value of type '$type'")
    }

    internal fun reportRequireReturnValue(span: Spannable, type: BaseType) {
        report(span, "This function require a return expression of type '$type'")
    }

    internal fun reportInvalidPanicMessageType(span: Spannable) {
        report(span, "Panic's message must be of type 'string'")
    }

    internal fun reportSelfReferenceNotFound(span: Spannable) {
        report(span, "There is no 'self' reference available in this scope")
    }

    internal fun reportIndexWrongType(span: Spannable, expected: BaseType) {
        report(span, "This index type must be of type '$expected'")
    }

    internal fun reportWrongTupleIndexFormat(span: Spannable) {
        report(span, "A tuple index must be a constant int literal")
    }

    internal fun reportIndexOutOfBounds(span: Spannable) {
        report(span, "This index is out of bounds")
    }

    internal fun reportCannotInferType(span: Spannable) {
        report(span, "Cannot infer type for this expression, it must be specified it explicitly")
    }

    internal fun reportUnexpectedValueType(span: Spannable, expected: BaseType, actual: BaseType) {
        report(span, "Expected value of type '$expected', but got a value of type '$actual'")
    }

    internal fun reportTypeCastCanNeverSucceed(span: Spannable) {
        report(span, "This cast can never succeed")
    }

    internal fun reportOverloadResolutionAmbiguity(
        span: Spannable,
        methodsList: List<MutableMap.MutableEntry<BaseType, MutableMap<String, MethodSymbol>>>
    ) {
        report(span, "Overload resolution ambiguity. All these functions match:" + methodsList
            .flatMap { it.value.entries }.map { it.value }.joinToString(
                prefix = "\n\t- ",
                separator = "\n\t- ",
                transform = {
                    "fun (${it.receiver.type.name}).${it.name}" + it.type.params.joinToString(
                        prefix = "(",
                        postfix = ")"
                    ) + ": " + it.type.returnType
                }
            ))
    }
}
