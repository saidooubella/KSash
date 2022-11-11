package sash.evaluate.values.primitives

import sash.evaluate.values.Value
import sash.types.BaseType
import sash.types.BooleanType

internal class BooleanValue private constructor(internal val value: Boolean) : Value() {

    override val type: BaseType get() = BooleanType

    internal fun logicalAnd(other: BooleanValue): Value = invoke(value && other.value)

    internal fun logicalOr(other: BooleanValue): Value = invoke(value || other.value)

    internal fun logicalInvert(): Value = invoke(!value)

    override fun notEquals(other: Value): BooleanValue = when (other) {
        is BooleanValue -> invoke(value != other.value)
        else -> True
    }

    override fun equals(other: Value): BooleanValue = when (other) {
        is BooleanValue -> invoke(value == other.value)
        else -> False
    }

    override fun hashCode(): Int = value.hashCode()

    override fun equals(other: Any?): Boolean = other is BooleanValue && value == other.value

    override fun toString(): String = value.toString()

    companion object {

        internal val False = BooleanValue(false)
        internal val True = BooleanValue(true)

        internal operator fun invoke(value: Boolean) = if (value) True else False
    }
}