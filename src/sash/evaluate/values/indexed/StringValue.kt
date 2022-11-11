package sash.evaluate.values.indexed

import sash.evaluate.values.Value
import sash.evaluate.values.asInt
import sash.evaluate.values.primitives.BooleanValue
import sash.evaluate.values.primitives.CharValue
import sash.types.BaseType
import sash.types.StringType

internal class StringValue(internal val value: String) : IndexedValue() {

    override val type: BaseType = StringType

    internal fun concat(other: StringValue): StringValue =
        StringValue(value + other.value)

    override fun get(index: Value): Value = CharValue(value[index.asInt()])

    override fun set(index: Value, value: Value): Value = throw UnsupportedOperationException()

    override fun add(value: Value) = throw UnsupportedOperationException()

    override fun size(): Int = value.length

    override fun notEquals(other: Value): BooleanValue = when (other) {
        is StringValue -> BooleanValue(value != other.value)
        else -> BooleanValue.True
    }

    override fun equals(other: Value): BooleanValue = when (other) {
        is StringValue -> BooleanValue(value == other.value)
        else -> BooleanValue.False
    }

    override fun hashCode(): Int = value.hashCode()

    override fun equals(other: Any?): Boolean = other is StringValue && value == other.value

    override fun toString(): String = value
}