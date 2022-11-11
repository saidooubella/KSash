package sash.evaluate.values.indexed

import sash.evaluate.values.Value
import sash.evaluate.values.primitives.BooleanValue
import sash.evaluate.values.primitives.NoneValue
import sash.tools.toMutableSet
import sash.types.BaseType

internal class SetValue(values: List<Value>, override val type: BaseType) : IndexedValue() {

    private val values: MutableSet<Value> = values.toMutableSet()

    override fun get(index: Value): Value = throw UnsupportedOperationException()

    override fun set(index: Value, value: Value) = throw UnsupportedOperationException()

    override fun add(value: Value) = values.add(value).let { Unit }

    override fun size(): Int = values.size

    override fun notEquals(other: Value): BooleanValue = BooleanValue.True

    override fun equals(other: Value): BooleanValue = BooleanValue.False

    override fun hashCode(): Int = values.hashCode()

    override fun equals(other: Any?): Boolean = other is SetValue && values == other.values

    override fun toString(): String = values.joinToString(prefix = "{", postfix = "}")
}