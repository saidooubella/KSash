package sash.evaluate.values.callables

import sash.evaluate.values.Value

internal abstract class MethodBuilder : Value() {
    internal abstract fun build(receiver: Value): CallableValue
}