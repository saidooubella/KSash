package sash.evaluate.values.indexed

import sash.evaluate.values.Value
import sash.evaluate.values.asInt
import sash.evaluate.values.primitives.BooleanValue
import sash.evaluate.values.primitives.NoneValue
import sash.tools.zipAll
import sash.types.BaseType

internal class ListValue(values: List<Value>, override val type: BaseType) : IndexedValue() {

    private val values: MutableList<Value> = values.toMutableList()

    override fun get(index: Value) = values[index.asInt()]

    override fun set(index: Value, value: Value): Value {
        values[index.asInt()] = value
        return value
    }

    override fun size(): Int = values.size

    override fun add(value: Value) = values.add(value).let { Unit }

    override fun notEquals(other: Value): BooleanValue = when (other) {
        is ListValue -> BooleanValue(values.zipAll(other.values) { l, r -> l.notEquals(r).value })
        else -> BooleanValue.True
    }

    override fun equals(other: Value): BooleanValue = when (other) {
        is ListValue -> BooleanValue(values.zipAll(other.values) { l, r -> l.equals(r).value })
        else -> BooleanValue.False
    }

    override fun hashCode(): Int = values.hashCode()

    override fun toString(): String = values.joinToString(prefix = "[", postfix = "]")

    override fun equals(other: Any?): Boolean = other is ListValue && values == other.values
}