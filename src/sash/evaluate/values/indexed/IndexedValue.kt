package sash.evaluate.values.indexed

import sash.evaluate.values.Value

internal abstract class IndexedValue : Value() {
    internal abstract fun get(index: Value): Value
    internal abstract fun set(index: Value, value: Value): Value
    internal abstract fun add(value: Value)
    internal abstract fun size(): Int
}
