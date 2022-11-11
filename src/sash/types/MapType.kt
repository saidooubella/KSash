package sash.types

internal class MapType(internal val key: BaseType, internal val value: BaseType) : BaseType() {

    override val name: String = if (key is FunctionType) "($key){$value}" else "$key{$value}"

    override fun assignableTo(that: BaseType): Boolean = when (that) {
        is MapType -> key.assignableTo(that.key) && value.assignableTo(that.value)
        is UnionType -> that.types.any { assignableTo(it) }
        else -> that is ErrorType || that is AnyType
    }

    override fun castableTo(that: BaseType): Boolean = TODO("not implemented")

    override fun equals(other: Any?): Boolean =
        other is MapType && name == other.name && key == other.key && value == other.value

    override fun hashCode(): Int {
        var result = 1
        result = 31 * result + value.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + key.hashCode()
        return result
    }
}