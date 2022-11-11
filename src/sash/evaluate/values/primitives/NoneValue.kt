package sash.evaluate.values.primitives

import sash.evaluate.values.Value
import sash.types.BaseType
import sash.types.NoneType

internal object NoneValue : Value() {

    override val type: BaseType get() = NoneType

    override fun notEquals(other: Value) = BooleanValue(other != NoneValue)

    override fun equals(other: Value) = BooleanValue(other == NoneValue)

    override fun hashCode(): Int = System.identityHashCode(this)

    override fun equals(other: Any?): Boolean = this === other

    override fun toString(): String = "none"
}