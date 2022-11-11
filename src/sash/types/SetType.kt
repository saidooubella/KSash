package sash.types

internal class SetType private constructor(internal val type: BaseType) : BaseType() {

    override val name: String = if (type is FunctionType) "($type){}" else "$type{}"

    override fun assignableTo(that: BaseType): Boolean = when (that) {
        is UnionType -> that.types.any { assignableTo(it) }
        is SetType -> type.assignableTo(that.type)
        else -> that is ErrorType || that is AnyType
    }

    override fun castableTo(that: BaseType): Boolean = TODO("not implemented")

    override fun equals(other: Any?): Boolean =
        other is SetType && name == other.name && type == other.type

    override fun hashCode(): Int {
        var result = 1
        result = 31 * result + type.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }

    companion object {

        private val cache = mapOf(
            BooleanType to SetType(BooleanType),
            NothingType to SetType(NothingType),
            DoubleType to SetType(DoubleType),
            StringType to SetType(StringType),
            ErrorType to SetType(ErrorType),
            FloatType to SetType(FloatType),
            LongType to SetType(LongType),
            UnitType to SetType(UnitType),
            AnyType to SetType(AnyType),
            IntType to SetType(IntType)
        )

        internal operator fun invoke(type: BaseType) = cache[type] ?: SetType(type)
    }
}