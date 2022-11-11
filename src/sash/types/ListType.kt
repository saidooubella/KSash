package sash.types

internal class ListType private constructor(internal val type: BaseType) : BaseType() {

    override val name: String = if (type is FunctionType) "($type)[]" else "$type[]"

    override fun assignableTo(that: BaseType): Boolean = when (that) {
        is UnionType -> that.types.any { assignableTo(it) }
        is ListType -> type.assignableTo(that.type)
        else -> that is ErrorType || that is AnyType
    }

    override fun castableTo(that: BaseType): Boolean = TODO("not implemented")

    override fun equals(other: Any?): Boolean =
        other is ListType && name == other.name && type == other.type

    override fun hashCode(): Int {
        var result = 1
        result = 31 * result + type.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }

    companion object {

        private val cache = mapOf(
            BooleanType to ListType(BooleanType),
            NothingType to ListType(NothingType),
            DoubleType to ListType(DoubleType),
            StringType to ListType(StringType),
            ErrorType to ListType(ErrorType),
            FloatType to ListType(FloatType),
            LongType to ListType(LongType),
            UnitType to ListType(UnitType),
            AnyType to ListType(AnyType),
            IntType to ListType(IntType)
        )

        internal operator fun invoke(type: BaseType) = cache[type] ?: ListType(type)
    }
}