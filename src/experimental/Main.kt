package experimental

import kotlin.reflect.KClass

fun main(args: Array<String>) {

    val symbols = SymbolTable().apply {
        storeRecord("Person")
        storeRecord("Builder")
    }

    symbols.retrieve("Person", RecordType::class) ?: error("Unexpected")
    symbols.retrieve("Person", RecordSymbol::class) ?: error("Unexpected")
    symbols.retrieve("Builder", RecordType::class) ?: error("Unexpected")
    symbols.retrieve("Builder", RecordSymbol::class) ?: error("Unexpected")
}

data class SymbolTable(private val store: MutableMap<String, Symbol> = HashMap()) {

    fun store(symbol: Symbol) = store.set(key(symbol.name, symbol::class), symbol)

    fun <T : Symbol> retrieve(name: String, clazz: KClass<T>): T? {
        @Suppress("UNCHECKED_CAST") return store[key(name, clazz)] as T?
    }

    private companion object {
        fun <T : Symbol> key(name: String, clazz: KClass<T>) = "$name:${clazz.qualifiedName}"
    }
}

fun SymbolTable.storeRecord(name: String) {
    store(RecordType(name))    // declaration
    store(RecordSymbol(name))  // definition
}

sealed class Symbol {
    abstract val name: String
}

sealed class Type : Symbol()

data class RecordSymbol(override val name: String) : Symbol()
data class RecordType(override val name: String) : Type()