package sash.evaluate.values.indexed

import sash.evaluate.values.Value
import sash.evaluate.values.asInt
import sash.evaluate.values.primitives.BooleanValue
import sash.evaluate.values.primitives.NoneValue
import sash.tools.zipAll
import sash.types.BaseType

internal class TupleValue(
    private val values: List<Value>,
    override val type: BaseType
) : IndexedValue() {

    override fun get(index: Value) = values[index.asInt()]

    override fun set(index: Value, value: Value): Value = throw UnsupportedOperationException()

    override fun add(value: Value) = throw UnsupportedOperationException()

    override fun size(): Int = throw UnsupportedOperationException()

    override fun notEquals(other: Value): BooleanValue = when (other) {
        is TupleValue -> BooleanValue(values.zipAll(other.values) { l, r -> l.notEquals(r).value })
        else -> BooleanValue.True
    }

    override fun equals(other: Value): BooleanValue = when (other) {
        is TupleValue -> BooleanValue(values.zipAll(other.values) { l, r -> l.equals(r).value })
        else -> BooleanValue.False
    }

    override fun hashCode(): Int = values.hashCode()

    override fun equals(other: Any?): Boolean = other is TupleValue && values == other.values

    override fun toString(): String = values.joinToString(prefix = "(", postfix = ")")
}