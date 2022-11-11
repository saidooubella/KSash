package sash.types

import sash.tools.zipAll

internal class FunctionType(internal val params: List<BaseType>, internal val returnType: BaseType) : BaseType() {

    override val name: String = params.joinToString(prefix = "(", postfix = ")") + " -> " + returnType

    override fun assignableTo(that: BaseType): Boolean = when (that) {
        is UnionType -> that.types.any { assignableTo(it) }
        is FunctionType -> params.zipAll(that.params) { l, r -> l.assignableTo(r) }
                && returnType.assignableTo(that.returnType)
        else -> that is ErrorType || that is AnyType
    }

    override fun castableTo(that: BaseType): Boolean = TODO("not implemented")

    override fun equals(other: Any?): Boolean = other is FunctionType && name == other.name
            && params == other.params && returnType == other.returnType

    internal operator fun component1() = params
    internal operator fun component2() = returnType

    override fun hashCode(): Int {
        var result = 1
        result = 31 * result + returnType.hashCode()
        result = 31 * result + params.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }
}
