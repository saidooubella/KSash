package sash.types

internal class RecordType(override val name: String) : BaseType() {

    override fun assignableTo(that: BaseType): Boolean = when (that) {
        is UnionType -> that.types.any { assignableTo(it) }
        is RecordType -> name == that.name
        else -> that is ErrorType || that is AnyType
    }

    override fun castableTo(that: BaseType): Boolean = TODO("not implemented")

    override fun equals(other: Any?): Boolean =
        other is RecordType && name == other.name

    override fun hashCode(): Int = 31 + name.hashCode()
}