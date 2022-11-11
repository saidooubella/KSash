package sash.evaluate.values.primitives

import sash.evaluate.values.Value
import sash.types.BaseType
import sash.types.CharType

internal class CharValue(internal val value: Char) : Value() {

    override val type: BaseType = CharType

    override fun notEquals(other: Value): BooleanValue = when (other) {
        is CharValue -> BooleanValue(value != other.value)
        else -> BooleanValue.True
    }

    override fun equals(other: Value): BooleanValue = when (other) {
        is CharValue -> BooleanValue(value == other.value)
        else -> BooleanValue.False
    }

    override fun hashCode(): Int = value.hashCode()

    override fun equals(other: Any?): Boolean = other is CharValue && value == other.value

    override fun toString(): String = value.toString()
}