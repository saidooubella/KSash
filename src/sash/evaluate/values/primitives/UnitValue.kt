package sash.evaluate.values.primitives

import sash.evaluate.values.Value
import sash.types.BaseType
import sash.types.UnitType

internal object UnitValue : Value() {

    override val type: BaseType get() = UnitType

    override fun notEquals(other: Value): BooleanValue = when (other) {
        is NoneValue -> BooleanValue.True
        else -> throw IllegalStateException()
    }

    override fun equals(other: Value): BooleanValue = when (other) {
        is NoneValue -> BooleanValue.False
        else -> throw IllegalStateException()
    }

    override fun hashCode(): Int = System.identityHashCode(this)

    override fun equals(other: Any?): Boolean = this === other

    override fun toString(): String = "unit"
}