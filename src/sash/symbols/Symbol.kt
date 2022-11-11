package sash.symbols

import sash.types.BaseType
import sash.types.FunctionType
import sash.types.RecordType

internal abstract class Symbol {
    internal abstract val name: String
    internal abstract val type: BaseType
}

internal data class VariableSymbol(
    override val name: String,
    override val type: BaseType,
    internal val readOnly: Boolean
) : Symbol()

internal data class ParameterSymbol(
    override val name: String,
    override val type: BaseType
) : Symbol()

internal data class FunctionSymbol(
    override val name: String,
    override val type: FunctionType,
    internal val parameters: List<ParameterSymbol>
) : Symbol()

internal data class MethodSymbol(
    override val name: String,
    internal val receiver: ReceiverSymbol,
    internal val parameters: List<ParameterSymbol>,
    override val type: FunctionType
) : Symbol()

internal data class FieldSymbol(
    override val name: String,
    override val type: BaseType
) : Symbol()

internal data class ReceiverSymbol(
    override val type: BaseType
) : Symbol() {
    override val name: String = "self"
}

internal data class RecordSymbol(
    internal val fields: List<FieldSymbol>,
    override val type: RecordType
) : Symbol() {
    override val name: String = type.name
}
