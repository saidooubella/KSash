package sash.symbols

import sash.types.*

internal object BuiltIn {

    internal val ANY_NONE_UNION = UnionType(listOf(AnyType, NoneType))

    internal val PRINT_LN = FunctionSymbol(
        "println", FunctionType(listOf(ANY_NONE_UNION), UnitType),
        listOf(ParameterSymbol("value", ANY_NONE_UNION))
    )

    internal val PRINT = FunctionSymbol(
        "print", FunctionType(listOf(ANY_NONE_UNION), UnitType),
        listOf(ParameterSymbol("value", ANY_NONE_UNION))
    )

    internal val STRING = FunctionSymbol(
        "string", FunctionType(listOf(ANY_NONE_UNION), StringType),
        listOf(ParameterSymbol("value", ANY_NONE_UNION))
    )

    internal val TYPE_OF = FunctionSymbol(
        "typeOf", FunctionType(listOf(ANY_NONE_UNION), StringType),
        listOf(ParameterSymbol("value", ANY_NONE_UNION))
    )

    internal val ADD = run {
        val listType = UnionType(listOf(SetType(ANY_NONE_UNION), ListType(ANY_NONE_UNION)))
        FunctionSymbol(
            "add", FunctionType(listOf(listType, ANY_NONE_UNION), UnitType),
            listOf(ParameterSymbol("list", listType), ParameterSymbol("value", ANY_NONE_UNION))
        )
    }

    internal val LENGTH = run {
        val collectionType = UnionType(
            listOf(
                ListType(ANY_NONE_UNION),
                SetType(ANY_NONE_UNION),
                MapType(ANY_NONE_UNION, ANY_NONE_UNION),
                StringType
            )
        )
        FunctionSymbol(
            "len", FunctionType(listOf(collectionType), IntType),
            listOf(ParameterSymbol("target", collectionType))
        )
    }

    internal val INPUT = FunctionSymbol("input", FunctionType(listOf(), StringType), listOf())

    internal val TIME = FunctionSymbol("time", FunctionType(listOf(), LongType), listOf())

    internal val DOUBLE = MethodSymbol(
        "double", ReceiverSymbol(StringType), listOf(),
        FunctionType(listOf(), UnionType(listOf(DoubleType, NoneType)))
    )

    internal val FLOAT = MethodSymbol(
        "float", ReceiverSymbol(StringType), listOf(),
        FunctionType(listOf(), UnionType(listOf(FloatType, NoneType)))
    )

    internal val LONG = MethodSymbol(
        "long", ReceiverSymbol(StringType), listOf(),
        FunctionType(listOf(), UnionType(listOf(LongType, NoneType)))
    )

    internal val INT = MethodSymbol(
        "int", ReceiverSymbol(StringType), listOf(),
        FunctionType(listOf(), UnionType(listOf(IntType, NoneType)))
    )
}
