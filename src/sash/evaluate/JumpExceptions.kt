package sash.evaluate

import sash.evaluate.values.Value

internal class ReturnException(val value: Value) : Exception(null, null, false, false)

internal class PanicException(val value: Value) : Exception(value.toString(), null, false, false)

internal class ContinueException : Exception(null, null, false, false)

internal class BreakException : Exception(null, null, false, false)
