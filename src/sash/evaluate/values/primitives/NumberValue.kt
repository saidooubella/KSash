package sash.evaluate.values.primitives

import sash.evaluate.values.Value
import sash.types.*

internal sealed class NumberValue : Value() {
    internal abstract fun plus(other: NumberValue): NumberValue
    internal abstract fun minus(other: NumberValue): NumberValue
    internal abstract fun divide(other: NumberValue): NumberValue
    internal abstract fun multiply(other: NumberValue): NumberValue
    internal abstract fun greaterThanEqual(other: NumberValue): BooleanValue
    internal abstract fun lessThanEqual(other: NumberValue): BooleanValue
    internal abstract fun greaterThan(other: NumberValue): BooleanValue
    internal abstract fun lessThan(other: NumberValue): BooleanValue
    internal abstract fun invert(): NumberValue
}

internal class IntValue(internal val value: Int) : NumberValue() {

    override val type: BaseType get() = IntType

    override fun plus(other: NumberValue): NumberValue = when (other) {
        is IntValue -> IntValue(value + other.value)
        is DoubleValue -> DoubleValue(value + other.value)
        is FloatValue -> FloatValue(value + other.value)
        is LongValue -> LongValue(value + other.value)
    }

    override fun minus(other: NumberValue): NumberValue = when (other) {
        is IntValue -> IntValue(value - other.value)
        is DoubleValue -> DoubleValue(value - other.value)
        is FloatValue -> FloatValue(value - other.value)
        is LongValue -> LongValue(value - other.value)
    }

    override fun divide(other: NumberValue): NumberValue = when (other) {
        is IntValue -> IntValue(value / other.value)
        is DoubleValue -> DoubleValue(value / other.value)
        is FloatValue -> FloatValue(value / other.value)
        is LongValue -> LongValue(value / other.value)
    }

    override fun multiply(other: NumberValue): NumberValue = when (other) {
        is IntValue -> IntValue(value * other.value)
        is DoubleValue -> DoubleValue(value * other.value)
        is FloatValue -> FloatValue(value * other.value)
        is LongValue -> LongValue(value * other.value)
    }

    override fun greaterThanEqual(other: NumberValue): BooleanValue = when (other) {
        is DoubleValue -> BooleanValue(value >= other.value)
        is IntValue -> BooleanValue(value >= other.value)
        is FloatValue -> BooleanValue(value >= other.value)
        is LongValue -> BooleanValue(value >= other.value)
    }

    override fun lessThanEqual(other: NumberValue): BooleanValue = when (other) {
        is DoubleValue -> BooleanValue(value <= other.value)
        is IntValue -> BooleanValue(value <= other.value)
        is FloatValue -> BooleanValue(value <= other.value)
        is LongValue -> BooleanValue(value <= other.value)
    }

    override fun greaterThan(other: NumberValue): BooleanValue = when (other) {
        is DoubleValue -> BooleanValue(value > other.value)
        is IntValue -> BooleanValue(value > other.value)
        is FloatValue -> BooleanValue(value > other.value)
        is LongValue -> BooleanValue(value > other.value)
    }

    override fun lessThan(other: NumberValue): BooleanValue = when (other) {
        is DoubleValue -> BooleanValue(value < other.value)
        is IntValue -> BooleanValue(value < other.value)
        is FloatValue -> BooleanValue(value < other.value)
        is LongValue -> BooleanValue(value < other.value)
    }

    override fun notEquals(other: Value): BooleanValue = when (other) {
        is IntValue -> BooleanValue(value != other.value)
        else -> BooleanValue.True
    }

    override fun equals(other: Value): BooleanValue = when (other) {
        is IntValue -> BooleanValue(value == other.value)
        else -> BooleanValue.False
    }

    override fun invert(): NumberValue = IntValue(-value)

    override fun hashCode(): Int = value.hashCode()

    override fun equals(other: Any?): Boolean = other is IntValue && value == other.value

    override fun toString(): String = value.toString()
}

internal class DoubleValue(internal val value: Double) : NumberValue() {

    override val type: BaseType get() = DoubleType

    override fun plus(other: NumberValue): NumberValue = when (other) {
        is IntValue -> DoubleValue(value + other.value)
        is DoubleValue -> DoubleValue(value + other.value)
        is FloatValue -> DoubleValue(value + other.value)
        is LongValue -> DoubleValue(value + other.value)
    }

    override fun minus(other: NumberValue): NumberValue = when (other) {
        is IntValue -> DoubleValue(value - other.value)
        is DoubleValue -> DoubleValue(value - other.value)
        is FloatValue -> DoubleValue(value - other.value)
        is LongValue -> DoubleValue(value - other.value)
    }

    override fun divide(other: NumberValue): NumberValue = when (other) {
        is IntValue -> DoubleValue(value / other.value)
        is DoubleValue -> DoubleValue(value / other.value)
        is FloatValue -> DoubleValue(value / other.value)
        is LongValue -> DoubleValue(value / other.value)
    }

    override fun multiply(other: NumberValue): NumberValue = when (other) {
        is IntValue -> DoubleValue(value * other.value)
        is DoubleValue -> DoubleValue(value * other.value)
        is FloatValue -> DoubleValue(value * other.value)
        is LongValue -> DoubleValue(value * other.value)
    }

    override fun greaterThanEqual(other: NumberValue): BooleanValue = when (other) {
        is DoubleValue -> BooleanValue(value >= other.value)
        is IntValue -> BooleanValue(value >= other.value)
        is FloatValue -> BooleanValue(value >= other.value)
        is LongValue -> BooleanValue(value >= other.value)
    }

    override fun lessThanEqual(other: NumberValue): BooleanValue = when (other) {
        is DoubleValue -> BooleanValue(value <= other.value)
        is IntValue -> BooleanValue(value <= other.value)
        is FloatValue -> BooleanValue(value <= other.value)
        is LongValue -> BooleanValue(value <= other.value)
    }

    override fun greaterThan(other: NumberValue): BooleanValue = when (other) {
        is DoubleValue -> BooleanValue(value > other.value)
        is IntValue -> BooleanValue(value > other.value)
        is FloatValue -> BooleanValue(value > other.value)
        is LongValue -> BooleanValue(value > other.value)
    }

    override fun lessThan(other: NumberValue): BooleanValue = when (other) {
        is DoubleValue -> BooleanValue(value < other.value)
        is IntValue -> BooleanValue(value < other.value)
        is FloatValue -> BooleanValue(value < other.value)
        is LongValue -> BooleanValue(value < other.value)
    }

    override fun notEquals(other: Value): BooleanValue = when (other) {
        is DoubleValue -> BooleanValue(value != other.value)
        else -> BooleanValue.True
    }

    override fun equals(other: Value): BooleanValue = when (other) {
        is DoubleValue -> BooleanValue(value == other.value)
        else -> BooleanValue.False
    }

    override fun invert(): NumberValue = DoubleValue(-value)

    override fun hashCode(): Int = value.hashCode()

    override fun equals(other: Any?): Boolean = other is DoubleValue && value == other.value

    override fun toString(): String = value.toString()
}

internal class FloatValue(internal val value: Float) : NumberValue() {

    override val type: BaseType get() = FloatType

    override fun plus(other: NumberValue): NumberValue = when (other) {
        is IntValue -> FloatValue(value + other.value)
        is DoubleValue -> DoubleValue(value + other.value)
        is FloatValue -> FloatValue(value + other.value)
        is LongValue -> FloatValue(value + other.value)
    }

    override fun minus(other: NumberValue): NumberValue = when (other) {
        is IntValue -> FloatValue(value - other.value)
        is DoubleValue -> DoubleValue(value - other.value)
        is FloatValue -> FloatValue(value - other.value)
        is LongValue -> FloatValue(value - other.value)
    }

    override fun divide(other: NumberValue): NumberValue = when (other) {
        is IntValue -> FloatValue(value / other.value)
        is DoubleValue -> DoubleValue(value / other.value)
        is FloatValue -> FloatValue(value / other.value)
        is LongValue -> FloatValue(value / other.value)
    }

    override fun multiply(other: NumberValue): NumberValue = when (other) {
        is IntValue -> FloatValue(value * other.value)
        is DoubleValue -> DoubleValue(value * other.value)
        is FloatValue -> FloatValue(value * other.value)
        is LongValue -> FloatValue(value * other.value)
    }

    override fun greaterThanEqual(other: NumberValue): BooleanValue = when (other) {
        is DoubleValue -> BooleanValue(value >= other.value)
        is IntValue -> BooleanValue(value >= other.value)
        is FloatValue -> BooleanValue(value >= other.value)
        is LongValue -> BooleanValue(value >= other.value)
    }

    override fun lessThanEqual(other: NumberValue): BooleanValue = when (other) {
        is DoubleValue -> BooleanValue(value <= other.value)
        is IntValue -> BooleanValue(value <= other.value)
        is FloatValue -> BooleanValue(value <= other.value)
        is LongValue -> BooleanValue(value <= other.value)
    }

    override fun greaterThan(other: NumberValue): BooleanValue = when (other) {
        is DoubleValue -> BooleanValue(value > other.value)
        is IntValue -> BooleanValue(value > other.value)
        is FloatValue -> BooleanValue(value > other.value)
        is LongValue -> BooleanValue(value > other.value)
    }

    override fun lessThan(other: NumberValue): BooleanValue = when (other) {
        is DoubleValue -> BooleanValue(value < other.value)
        is IntValue -> BooleanValue(value < other.value)
        is FloatValue -> BooleanValue(value < other.value)
        is LongValue -> BooleanValue(value < other.value)
    }

    override fun notEquals(other: Value): BooleanValue = when (other) {
        is FloatValue -> BooleanValue(value != other.value)
        else -> BooleanValue.True
    }

    override fun equals(other: Value): BooleanValue = when (other) {
        is FloatValue -> BooleanValue(value == other.value)
        else -> BooleanValue.False
    }

    override fun invert(): NumberValue = FloatValue(-value)

    override fun hashCode(): Int = value.hashCode()

    override fun equals(other: Any?): Boolean = other is FloatValue && value == other.value

    override fun toString(): String = value.toString()
}

internal class LongValue(internal val value: Long) : NumberValue() {

    override val type: BaseType get() = LongType

    override fun plus(other: NumberValue): NumberValue = when (other) {
        is IntValue -> LongValue(value + other.value)
        is DoubleValue -> DoubleValue(value + other.value)
        is FloatValue -> FloatValue(value + other.value)
        is LongValue -> LongValue(value + other.value)
    }

    override fun minus(other: NumberValue): NumberValue = when (other) {
        is IntValue -> LongValue(value - other.value)
        is DoubleValue -> DoubleValue(value - other.value)
        is FloatValue -> FloatValue(value - other.value)
        is LongValue -> LongValue(value - other.value)
    }

    override fun divide(other: NumberValue): NumberValue = when (other) {
        is IntValue -> LongValue(value / other.value)
        is DoubleValue -> DoubleValue(value / other.value)
        is FloatValue -> FloatValue(value / other.value)
        is LongValue -> LongValue(value / other.value)
    }

    override fun multiply(other: NumberValue): NumberValue = when (other) {
        is IntValue -> LongValue(value * other.value)
        is DoubleValue -> DoubleValue(value * other.value)
        is FloatValue -> FloatValue(value * other.value)
        is LongValue -> LongValue(value * other.value)
    }

    override fun greaterThanEqual(other: NumberValue): BooleanValue = when (other) {
        is DoubleValue -> BooleanValue(value >= other.value)
        is IntValue -> BooleanValue(value >= other.value)
        is FloatValue -> BooleanValue(value >= other.value)
        is LongValue -> BooleanValue(value >= other.value)
    }

    override fun lessThanEqual(other: NumberValue): BooleanValue = when (other) {
        is DoubleValue -> BooleanValue(value <= other.value)
        is IntValue -> BooleanValue(value <= other.value)
        is FloatValue -> BooleanValue(value <= other.value)
        is LongValue -> BooleanValue(value <= other.value)
    }

    override fun greaterThan(other: NumberValue): BooleanValue = when (other) {
        is DoubleValue -> BooleanValue(value > other.value)
        is IntValue -> BooleanValue(value > other.value)
        is FloatValue -> BooleanValue(value > other.value)
        is LongValue -> BooleanValue(value > other.value)
    }

    override fun lessThan(other: NumberValue): BooleanValue = when (other) {
        is DoubleValue -> BooleanValue(value < other.value)
        is IntValue -> BooleanValue(value < other.value)
        is FloatValue -> BooleanValue(value < other.value)
        is LongValue -> BooleanValue(value < other.value)
    }

    override fun notEquals(other: Value): BooleanValue = when (other) {
        is LongValue -> BooleanValue(value != other.value)
        else -> BooleanValue.True
    }

    override fun equals(other: Value): BooleanValue = when (other) {
        is LongValue -> BooleanValue(value == other.value)
        else -> BooleanValue.False
    }

    override fun invert(): NumberValue = LongValue(-value)

    override fun hashCode(): Int = value.hashCode()

    override fun equals(other: Any?): Boolean = other is LongValue && value == other.value

    override fun toString(): String = value.toString()
}