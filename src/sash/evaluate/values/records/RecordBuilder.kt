package sash.evaluate.values.records

import sash.evaluate.values.Value
import sash.evaluate.values.primitives.BooleanValue
import sash.evaluate.values.primitives.NoneValue
import sash.symbols.FieldSymbol
import sash.symbols.RecordSymbol
import sash.types.BaseType

internal class RecordBuilder(private val record: RecordSymbol) : Value() {

    override val type: BaseType = record.type

    internal fun build(args: List<Value>): RecordValue {
        val fields = record.fields
        val values = hashMapOf<FieldSymbol, Value>()
        for (index in fields.indices)
            values[fields[index]] = args[index]
        return RecordValue(values, record.type)
    }

    override fun notEquals(other: Value): BooleanValue = BooleanValue(other == NoneValue)

    override fun equals(other: Value): BooleanValue = BooleanValue(other != NoneValue)

    override fun hashCode(): Int = System.identityHashCode(this)

    override fun equals(other: Any?): Boolean = this === other

    override fun toString(): String = type.name
}
