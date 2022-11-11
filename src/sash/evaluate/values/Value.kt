package sash.evaluate.values

import sash.evaluate.values.callables.CallableValue
import sash.evaluate.values.callables.MethodBuilder
import sash.evaluate.values.indexed.IndexedValue
import sash.evaluate.values.primitives.BooleanValue
import sash.evaluate.values.primitives.IntValue
import sash.evaluate.values.primitives.NumberValue
import sash.evaluate.values.indexed.StringValue
import sash.evaluate.values.records.RecordBuilder
import sash.evaluate.values.records.RecordValue
import sash.types.BaseType

internal abstract class Value {
    internal abstract val type: BaseType
    internal abstract fun notEquals(other: Value): BooleanValue
    internal abstract fun equals(other: Value): BooleanValue
    abstract override fun hashCode(): Int
    abstract override fun equals(other: Any?): Boolean
    abstract override fun toString(): String
}

internal fun Value.asRecordBuilder() = this as RecordBuilder
internal fun Value.asMethodBuilder() = this as MethodBuilder

internal fun Value.asCallable() = this as CallableValue
internal fun Value.asIndexed() = this as IndexedValue

internal fun Value.asBoolean() = this as BooleanValue
internal fun Value.asNumber() = this as NumberValue
internal fun Value.asString() = this as StringValue
internal fun Value.asRecord() = this as RecordValue

internal fun Value.asInt() = (this as IntValue).value
