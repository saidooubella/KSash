package sash.evaluate.values.callables

import sash.evaluate.values.Value
import sash.types.BaseType

internal abstract class CallableValue : Value() {
    internal abstract fun invoke(args: List<Value>): Value
}