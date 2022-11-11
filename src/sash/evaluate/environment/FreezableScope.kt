package sash.evaluate.environment

import sash.evaluate.values.Value
import sash.evaluate.values.asIndexed
import sash.evaluate.values.asString
import sash.evaluate.values.callables.CallableValue
import sash.evaluate.values.callables.MethodBuilder
import sash.evaluate.values.indexed.StringValue
import sash.evaluate.values.primitives.*
import sash.symbols.BuiltIn
import sash.symbols.FunctionSymbol
import sash.symbols.MethodSymbol
import sash.symbols.Symbol
import sash.tools.putValue
import sash.types.BaseType
import sash.types.ListType
import sash.types.SetType

internal class FreezableScope private constructor(
    private val symbols: MutableMap<Symbol, SymbolValue>,
    private val parent: FreezableScope?
) {

    internal fun put(symbol: Symbol, value: Value) = symbols.putValue(symbol, SymbolValue(value))

    internal fun change(symbol: Symbol, value: Value) = run { valueOf(symbol).value = value }

    internal fun get(symbol: Symbol): Value = valueOf(symbol).value

    internal fun parent(): FreezableScope = parent ?: throw IllegalStateException("Parent scope not available")

    internal fun freeze(): FreezableScope = FreezableScope(HashMap(symbols), parent)

    private fun valueOf(symbol: Symbol): SymbolValue {
        var scope: FreezableScope? = this
        while (scope != null) {
            val value = scope.symbols[symbol]
            if (value != null) return value
            scope = scope.parent
        }
        throw IllegalStateException("'${symbol.name}' not found")
    }

    companion object {

        private val GlobalScope = FreezableScope(HashMap<Symbol, SymbolValue>().apply {
            put(BuiltIn.PRINT_LN, function(BuiltIn.PRINT_LN) { args ->
                println(args[0])
                UnitValue
            })
            put(BuiltIn.PRINT, function(BuiltIn.PRINT) { args ->
                print(args[0])
                UnitValue
            })
            put(BuiltIn.ADD, function(BuiltIn.ADD) { args ->

                val indexed = args[0].asIndexed()
                val value = args[1]

                val type = when (val type = indexed.type) {
                    is SetType -> type.type
                    is ListType -> type.type
                    else -> throw IllegalStateException()
                }

                if (!value.type.assignableTo(type))
                    throw IllegalStateException("a ${value.type} cannot be added to ${indexed.type}")

                indexed.add(value)
                UnitValue
            })
            put(BuiltIn.STRING, function(BuiltIn.STRING) { args ->
                StringValue(args[0].toString())
            })
            put(BuiltIn.TYPE_OF, function(BuiltIn.TYPE_OF) { args ->
                StringValue(args[0].type.toString())
            })
            put(BuiltIn.INPUT, function(BuiltIn.INPUT) {
                StringValue(readLine() ?: "")
            })
            put(BuiltIn.TIME, function(BuiltIn.TIME) {
                LongValue(System.currentTimeMillis())
            })
            put(BuiltIn.LENGTH, function(BuiltIn.LENGTH) { args ->
                IntValue(args[0].asIndexed().size())
            })
            put(BuiltIn.INT, method(BuiltIn.INT) { receiver, _ ->
                receiver.asString().value.toIntOrNull()?.let(::IntValue) ?: NoneValue
            })
            put(BuiltIn.LONG, method(BuiltIn.LONG) { receiver, _ ->
                receiver.asString().value.toLongOrNull()?.let(::LongValue) ?: NoneValue
            })
            put(BuiltIn.FLOAT, method(BuiltIn.FLOAT) { receiver, _ ->
                receiver.asString().value.toFloatOrNull()?.let(::FloatValue) ?: NoneValue
            })
            put(BuiltIn.DOUBLE, method(BuiltIn.DOUBLE) { receiver, _ ->
                receiver.asString().value.toDoubleOrNull()?.let(::DoubleValue) ?: NoneValue
            })
        }, null)

        internal fun create(): FreezableScope = FreezableScope.wrap(FreezableScope.GlobalScope)

        internal fun wrap(scope: FreezableScope): FreezableScope = FreezableScope(HashMap(), scope)
    }
}

private data class SymbolValue(var value: Value)

private fun function(
    function: FunctionSymbol,
    block: (List<Value>) -> Value
) = SymbolValue(object : CallableValue() {
    override val type: BaseType = function.type
    override fun invoke(args: List<Value>): Value = block(args)
    override fun equals(other: Any?): Boolean = this === other
    override fun hashCode(): Int = System.identityHashCode(this)
    override fun toString(): String = type.name
    override fun notEquals(other: Value) = BooleanValue(other == NoneValue)
    override fun equals(other: Value) = BooleanValue(other != NoneValue)
})

private fun method(
    method: MethodSymbol,
    block: (Value, List<Value>) -> Value
) = SymbolValue(object : MethodBuilder() {
    override val type: BaseType = method.type
    override fun toString(): String = type.name
    override fun equals(other: Any?): Boolean = this === other
    override fun hashCode(): Int = System.identityHashCode(this)
    override fun notEquals(other: Value) = BooleanValue(other == NoneValue)
    override fun equals(other: Value) = BooleanValue(other != NoneValue)
    override fun build(receiver: Value): CallableValue = object : CallableValue() {
        override val type: BaseType = method.type
        override fun invoke(args: List<Value>): Value = block(receiver, args)
        override fun equals(other: Any?): Boolean = this === other
        override fun hashCode(): Int = System.identityHashCode(this)
        override fun toString(): String = type.name
        override fun notEquals(other: Value) = BooleanValue(other == NoneValue)
        override fun equals(other: Value) = BooleanValue(other != NoneValue)
    }
})