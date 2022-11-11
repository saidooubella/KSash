package sash.types

internal sealed class Primitive(override val name: String) : BaseType() {

    final override fun assignableTo(that: BaseType): Boolean = when {
        that is UnionType -> that.types.any { assignableTo(it) }
        this is NothingType || this is ErrorType -> true
        that is ErrorType || that is AnyType -> true
        else -> this == that
    }

    final override fun castableTo(that: BaseType): Boolean = TODO("not implemented")

    final override fun equals(other: Any?) = other is Primitive && name == other.name

    override fun hashCode(): Int = 31 + name.hashCode()
}

internal object BooleanType : Primitive("Boolean")

internal object NothingType : Primitive("Nothing")

internal object DoubleType : Primitive("Double")

internal object StringType : Primitive("String")

internal object FloatType : Primitive("Float")

internal object CharType : Primitive("Char")

internal object LongType : Primitive("Long")

internal object UnitType : Primitive("Unit")

internal object AnyType : Primitive("Any")

internal object IntType : Primitive("Int")

internal object ErrorType : Primitive("???")
