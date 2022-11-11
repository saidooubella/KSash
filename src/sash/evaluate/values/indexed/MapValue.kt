package sash.evaluate.values.indexed

import sash.evaluate.values.Value
import sash.evaluate.values.primitives.BooleanValue
import sash.evaluate.values.primitives.NoneValue
import sash.tools.toMutableMap
import sash.types.BaseType

internal class MapValue(values: List<Pair<Value, Value>>, override val type: BaseType) : IndexedValue() {

    private val values: MutableMap<Value, Value> = values.toMutableMap()

    override fun get(index: Value): Value = values[index] ?: NoneValue

    override fun set(index: Value, value: Value): Value {
        values[index] = value
        return value
    }

    override fun add(value: Value) = throw UnsupportedOperationException()

    override fun size(): Int = values.size

    override fun notEquals(other: Value): BooleanValue = BooleanValue.True

    override fun equals(other: Value): BooleanValue = BooleanValue.False

    override fun hashCode(): Int = values.hashCode()

    override fun equals(other: Any?): Boolean = other is MapValue && values == other.values

    override fun toString(): String = values.toString()
}