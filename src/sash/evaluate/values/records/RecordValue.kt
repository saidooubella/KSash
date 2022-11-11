package sash.evaluate.values.records

import sash.evaluate.values.Value
import sash.evaluate.values.primitives.BooleanValue
import sash.evaluate.values.primitives.NoneValue
import sash.symbols.FieldSymbol
import sash.types.RecordType

internal class RecordValue(
    private val values: MutableMap<FieldSymbol, Value>,
    override val type: RecordType
) : Value() {

    internal fun set(field: FieldSymbol, value: Value) {
        values[field] = value
    }

    internal fun get(field: FieldSymbol): Value = values[field] ?: throw IllegalStateException()

    override fun notEquals(other: Value): BooleanValue = BooleanValue(other == NoneValue)

    override fun equals(other: Value): BooleanValue = BooleanValue(other != NoneValue)

    override fun hashCode(): Int = values.hashCode()

    override fun equals(other: Any?): Boolean = other is RecordValue && values == other.values

    override fun toString(): String = type.name
}