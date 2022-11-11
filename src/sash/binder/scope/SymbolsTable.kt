package sash.binder.scope

import sash.symbols.MethodSymbol
import sash.symbols.RecordSymbol
import sash.symbols.Symbol
import sash.types.BaseType

internal class SymbolsTable private constructor(private var scope: SymbolsScope) {

    internal fun pushScope() {
        scope = SymbolsScope.wrap(scope)
    }

    internal fun popScope() {
        scope = scope.apply { clear() }.run { requireParent() }
    }

    // ----------------------------------

    internal fun putMethod(symbol: MethodSymbol) = scope.putMethod(symbol)

    internal fun putRecord(record: RecordSymbol) = scope.putRecord(record)

    internal fun putSymbol(symbol: Symbol) = scope.putSymbol(symbol)

    internal fun putType(type: BaseType) = scope.putType(type)

    // ----------------------------------

    internal fun getMethod(receiver: BaseType, name: String): List<MethodsMap>? {

        val list = mutableListOf<MethodsMap>()

        var scope: SymbolsScope? = scope
        while (scope != null) {
            list += scope.getMethod(receiver, name)
            scope = scope.parent
        }

        return list.takeIf { it.isNotEmpty() }
    }

    internal fun getRecord(name: String): RecordSymbol? = scope.get { getRecord(name) }

    internal fun getSymbol(name: String): Symbol? = scope.get { getSymbol(name) }

    internal fun getType(name: String): BaseType? = scope.get { getType(name) }

    // ----------------------------------

    internal fun hasMethod(receiver: BaseType, name: String): Boolean = scope.hasMethod(receiver, name)

    internal fun hasRecord(name: String): Boolean = scope.hasRecord(name)

    internal fun hasSymbol(name: String): Boolean = scope.hasSymbol(name)

    // ----------------------------------

    companion object {
        internal fun create(): SymbolsTable = SymbolsTable(SymbolsScope.create())
    }
}

private typealias MethodsMap = MutableMap.MutableEntry<BaseType, MutableMap<String, MethodSymbol>>

private inline fun <V : Any> SymbolsScope.get(action: SymbolsScope.() -> V?): V? {
    var scope: SymbolsScope? = this
    while (scope != null) {
        scope.action()?.let { return it }
        scope = scope.parent
    }
    return null
}

internal inline fun <R> SymbolsTable.scoped(action: () -> R): R {
    try {
        pushScope()
        return action()
    } finally {
        popScope()
    }
}
