package sash.types

internal abstract class BaseType {
    internal abstract val name: String
    internal abstract fun assignableTo(that: BaseType): Boolean
    internal abstract fun castableTo(that: BaseType): Boolean
    abstract override fun equals(other: Any?): Boolean
    abstract override fun hashCode(): Int
    final override fun toString(): String = name
}
