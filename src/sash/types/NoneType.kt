package sash.types

internal object NoneType : BaseType() {

    override val name: String = "None"

    override fun assignableTo(that: BaseType): Boolean = when (that) {
        is UnionType -> that.types.any { assignableTo(it) }
        else -> that is ErrorType || that is NoneType
    }

    override fun castableTo(that: BaseType): Boolean = TODO("not implemented")

    override fun equals(other: Any?): Boolean = this === other

    override fun hashCode(): Int = 31 + name.hashCode()
}