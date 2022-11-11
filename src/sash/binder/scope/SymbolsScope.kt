@file:Suppress("MoveLambdaOutsideParentheses")

package sash.binder.scope

import sash.symbols.BuiltIn
import sash.symbols.MethodSymbol
import sash.symbols.RecordSymbol
import sash.symbols.Symbol
import sash.tools.putValue
import sash.types.*

internal class SymbolsScope private constructor(internal val parent: SymbolsScope?) {

    private val methods = HashMap<BaseType, MutableMap<String, MethodSymbol>>()
    private val records = HashMap<String, RecordSymbol>()
    private val symbols = HashMap<String, Symbol>()
    private val types = HashMap<String, BaseType>()

    // ----------------------------------

    internal fun putSymbol(symbol: Symbol) = symbols.putValue(symbol.name, symbol)

    internal fun putRecord(record: RecordSymbol) = records.putValue(record.name, record)

    internal fun putType(type: BaseType) = types.putValue(type.name, type)

    internal fun putMethod(method: MethodSymbol) =
        methods.getOrPut(method.receiver.type, { hashMapOf() }).putValue(method.name, method)

    // ----------------------------------

    internal fun getSymbol(name: String): Symbol? = symbols[name]

    internal fun getRecord(name: String): RecordSymbol? = records[name]

    internal fun getType(name: String): BaseType? = types[name]

    internal fun getMethod(receiver: BaseType, name: String) = methods.entries.filter {
        receiver.assignableTo(it.key) && name in it.value
    }

    // ----------------------------------

    internal fun hasSymbol(name: String): Boolean = name in symbols

    internal fun hasRecord(name: String): Boolean = name in records

    internal fun hasMethod(receiver: BaseType, name: String): Boolean = methods[receiver]?.containsKey(name) ?: false

    // ----------------------------------

    internal fun requireParent(): SymbolsScope = parent ?: throw IllegalStateException("Parent scope not available")

    internal fun clear() {
        methods.values.forEach { it.clear() }
        methods.clear()
        symbols.clear()
        types.clear()
    }

    companion object {

        private val GLOBAL_SCOPE = SymbolsScope(null).apply {
            putSymbol(BuiltIn.PRINT_LN)
            putSymbol(BuiltIn.TYPE_OF)
            putSymbol(BuiltIn.ADD)
            putSymbol(BuiltIn.STRING)
            putSymbol(BuiltIn.INPUT)
            putSymbol(BuiltIn.PRINT)
            putSymbol(BuiltIn.TIME)
            putSymbol(BuiltIn.LENGTH)
            putMethod(BuiltIn.DOUBLE)
            putMethod(BuiltIn.FLOAT)
            putMethod(BuiltIn.LONG)
            putMethod(BuiltIn.INT)
            putType(BooleanType)
            putType(NothingType)
            putType(DoubleType)
            putType(StringType)
            putType(FloatType)
            putType(NoneType)
            putType(LongType)
            putType(UnitType)
            putType(AnyType)
            putType(IntType)
        }

        internal fun create(): SymbolsScope = SymbolsScope(GLOBAL_SCOPE)

        internal fun wrap(parent: SymbolsScope): SymbolsScope = SymbolsScope(parent)
    }
}