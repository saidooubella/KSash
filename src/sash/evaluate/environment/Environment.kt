package sash.evaluate.environment

import sash.evaluate.values.Value
import sash.symbols.Symbol

internal class Environment private constructor(private var current: FreezableScope) {

    internal fun pushScope() = run { current = FreezableScope.wrap(current) }

    internal fun popScope() = run { current = current.parent() }

    internal fun freeze() = run { current = current.freeze() }

    internal fun put(symbol: Symbol, value: Value) = current.put(symbol, value)

    internal fun change(symbol: Symbol, value: Value) = current.change(symbol, value)

    internal fun get(symbol: Symbol): Value = current.get(symbol)

    internal fun copy() = Environment(current)

    companion object {
        internal fun create(): Environment = Environment(FreezableScope.create())
    }
}

internal inline fun <R> Environment.copy(action: (Environment) -> R): R {
    return action(copy()).also { freeze() }
}

internal inline fun <R> Environment.scoped(action: () -> R): R {
    return try { pushScope(); action() } finally { popScope() }
}