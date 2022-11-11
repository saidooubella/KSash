package sash.types

internal class UnionType(internal val types: List<BaseType>) : BaseType() {

    override val name: String = types.joinToString(separator = " | ")

    override fun assignableTo(that: BaseType): Boolean =
        that is ErrorType || types.all { type -> type.assignableTo(that) }

    override fun castableTo(that: BaseType): Boolean = TODO("not implemented")

    override fun equals(other: Any?): Boolean =
        other is UnionType && name == other.name && types == other.types

    override fun hashCode(): Int {
        var result = 1
        result = 31 * result + types.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }
}
