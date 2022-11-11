package sash.types

import sash.tools.zipAll

internal class TupleType(internal val types: List<BaseType>) : BaseType() {

    override val name: String = types.joinToString(prefix = "(", postfix = ")")

    override fun assignableTo(that: BaseType): Boolean = when (that) {
        is TupleType -> types.zipAll(that.types) { l, r -> l.assignableTo(r) }
        is UnionType -> that.types.any { assignableTo(it) }
        else -> that is ErrorType || that is AnyType
    }

    override fun castableTo(that: BaseType): Boolean = TODO("not implemented")

    override fun equals(other: Any?): Boolean =
        other is TupleType && name == other.name && types == other.types

    override fun hashCode(): Int {
        var result = 1
        result = 31 * result + types.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }
}
