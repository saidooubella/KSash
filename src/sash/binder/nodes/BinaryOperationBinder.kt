package sash.binder.nodes

import sash.lexer.token.TokenType
import sash.tools.zipAll
import sash.types.*

@Suppress("FunctionName")
internal fun BinaryOperationBinder(left: BaseType, operationToken: TokenType, right: BaseType): BinaryOperationValue? {
    return MAP_OPERATIONS[BinaryOperationKey(left, operationToken, right)]
        ?: LIST_OPERATIONS.firstOrNull { it.check(left, operationToken, right) }?.value
}

private val MAP_OPERATIONS = mapOf(
    //////////////////////////////////////////////////

    BinaryOperationKey(IntType, TokenType.Plus, IntType) to
            BinaryOperationValue(BinaryOperation.Addition, IntType),
    BinaryOperationKey(IntType, TokenType.Plus, FloatType) to
            BinaryOperationValue(BinaryOperation.Addition, FloatType),
    BinaryOperationKey(IntType, TokenType.Plus, LongType) to
            BinaryOperationValue(BinaryOperation.Addition, LongType),
    BinaryOperationKey(IntType, TokenType.Plus, DoubleType) to
            BinaryOperationValue(BinaryOperation.Addition, DoubleType),

    BinaryOperationKey(FloatType, TokenType.Plus, IntType) to
            BinaryOperationValue(BinaryOperation.Addition, FloatType),
    BinaryOperationKey(FloatType, TokenType.Plus, FloatType) to
            BinaryOperationValue(BinaryOperation.Addition, FloatType),
    BinaryOperationKey(FloatType, TokenType.Plus, LongType) to
            BinaryOperationValue(BinaryOperation.Addition, LongType),
    BinaryOperationKey(FloatType, TokenType.Plus, DoubleType) to
            BinaryOperationValue(BinaryOperation.Addition, DoubleType),

    BinaryOperationKey(LongType, TokenType.Plus, IntType) to
            BinaryOperationValue(BinaryOperation.Addition, LongType),
    BinaryOperationKey(LongType, TokenType.Plus, FloatType) to
            BinaryOperationValue(BinaryOperation.Addition, LongType),
    BinaryOperationKey(LongType, TokenType.Plus, LongType) to
            BinaryOperationValue(BinaryOperation.Addition, LongType),
    BinaryOperationKey(LongType, TokenType.Plus, DoubleType) to
            BinaryOperationValue(BinaryOperation.Addition, DoubleType),

    BinaryOperationKey(DoubleType, TokenType.Plus, IntType) to
            BinaryOperationValue(BinaryOperation.Addition, DoubleType),
    BinaryOperationKey(DoubleType, TokenType.Plus, FloatType) to
            BinaryOperationValue(BinaryOperation.Addition, DoubleType),
    BinaryOperationKey(DoubleType, TokenType.Plus, LongType) to
            BinaryOperationValue(BinaryOperation.Addition, DoubleType),
    BinaryOperationKey(DoubleType, TokenType.Plus, DoubleType) to
            BinaryOperationValue(BinaryOperation.Addition, DoubleType),

    //////////////////////////////////////////////////

    BinaryOperationKey(IntType, TokenType.Minus, IntType) to
            BinaryOperationValue(BinaryOperation.Subtraction, IntType),
    BinaryOperationKey(IntType, TokenType.Minus, FloatType) to
            BinaryOperationValue(BinaryOperation.Subtraction, FloatType),
    BinaryOperationKey(IntType, TokenType.Minus, LongType) to
            BinaryOperationValue(BinaryOperation.Subtraction, LongType),
    BinaryOperationKey(IntType, TokenType.Minus, DoubleType) to
            BinaryOperationValue(BinaryOperation.Subtraction, DoubleType),

    BinaryOperationKey(FloatType, TokenType.Minus, IntType) to
            BinaryOperationValue(BinaryOperation.Subtraction, FloatType),
    BinaryOperationKey(FloatType, TokenType.Minus, FloatType) to
            BinaryOperationValue(BinaryOperation.Subtraction, FloatType),
    BinaryOperationKey(FloatType, TokenType.Minus, LongType) to
            BinaryOperationValue(BinaryOperation.Subtraction, LongType),
    BinaryOperationKey(FloatType, TokenType.Minus, DoubleType) to
            BinaryOperationValue(BinaryOperation.Subtraction, DoubleType),

    BinaryOperationKey(LongType, TokenType.Minus, IntType) to
            BinaryOperationValue(BinaryOperation.Subtraction, LongType),
    BinaryOperationKey(LongType, TokenType.Minus, FloatType) to
            BinaryOperationValue(BinaryOperation.Subtraction, LongType),
    BinaryOperationKey(LongType, TokenType.Minus, LongType) to
            BinaryOperationValue(BinaryOperation.Subtraction, LongType),
    BinaryOperationKey(LongType, TokenType.Minus, DoubleType) to
            BinaryOperationValue(BinaryOperation.Subtraction, DoubleType),

    BinaryOperationKey(DoubleType, TokenType.Minus, IntType) to
            BinaryOperationValue(BinaryOperation.Subtraction, DoubleType),
    BinaryOperationKey(DoubleType, TokenType.Minus, FloatType) to
            BinaryOperationValue(BinaryOperation.Subtraction, DoubleType),
    BinaryOperationKey(DoubleType, TokenType.Minus, LongType) to
            BinaryOperationValue(BinaryOperation.Subtraction, DoubleType),
    BinaryOperationKey(DoubleType, TokenType.Minus, DoubleType) to
            BinaryOperationValue(BinaryOperation.Subtraction, DoubleType),

    //////////////////////////////////////////////////

    BinaryOperationKey(IntType, TokenType.Slash, IntType) to
            BinaryOperationValue(BinaryOperation.Division, IntType),
    BinaryOperationKey(IntType, TokenType.Slash, FloatType) to
            BinaryOperationValue(BinaryOperation.Division, FloatType),
    BinaryOperationKey(IntType, TokenType.Slash, LongType) to
            BinaryOperationValue(BinaryOperation.Division, LongType),
    BinaryOperationKey(IntType, TokenType.Slash, DoubleType) to
            BinaryOperationValue(BinaryOperation.Division, DoubleType),

    BinaryOperationKey(FloatType, TokenType.Slash, IntType) to
            BinaryOperationValue(BinaryOperation.Division, FloatType),
    BinaryOperationKey(FloatType, TokenType.Slash, FloatType) to
            BinaryOperationValue(BinaryOperation.Division, FloatType),
    BinaryOperationKey(FloatType, TokenType.Slash, LongType) to
            BinaryOperationValue(BinaryOperation.Division, LongType),
    BinaryOperationKey(FloatType, TokenType.Slash, DoubleType) to
            BinaryOperationValue(BinaryOperation.Division, DoubleType),

    BinaryOperationKey(LongType, TokenType.Slash, IntType) to
            BinaryOperationValue(BinaryOperation.Division, LongType),
    BinaryOperationKey(LongType, TokenType.Slash, FloatType) to
            BinaryOperationValue(BinaryOperation.Division, LongType),
    BinaryOperationKey(LongType, TokenType.Slash, LongType) to
            BinaryOperationValue(BinaryOperation.Division, LongType),
    BinaryOperationKey(LongType, TokenType.Slash, DoubleType) to
            BinaryOperationValue(BinaryOperation.Division, DoubleType),

    BinaryOperationKey(DoubleType, TokenType.Slash, IntType) to
            BinaryOperationValue(BinaryOperation.Division, DoubleType),
    BinaryOperationKey(DoubleType, TokenType.Slash, FloatType) to
            BinaryOperationValue(BinaryOperation.Division, DoubleType),
    BinaryOperationKey(DoubleType, TokenType.Slash, LongType) to
            BinaryOperationValue(BinaryOperation.Division, DoubleType),
    BinaryOperationKey(DoubleType, TokenType.Slash, DoubleType) to
            BinaryOperationValue(BinaryOperation.Division, DoubleType),

    //////////////////////////////////////////////////

    BinaryOperationKey(IntType, TokenType.Star, IntType) to
            BinaryOperationValue(BinaryOperation.Multiplication, IntType),
    BinaryOperationKey(IntType, TokenType.Star, FloatType) to
            BinaryOperationValue(BinaryOperation.Multiplication, FloatType),
    BinaryOperationKey(IntType, TokenType.Star, LongType) to
            BinaryOperationValue(BinaryOperation.Multiplication, LongType),
    BinaryOperationKey(IntType, TokenType.Star, DoubleType) to
            BinaryOperationValue(BinaryOperation.Multiplication, DoubleType),

    BinaryOperationKey(FloatType, TokenType.Star, IntType) to
            BinaryOperationValue(BinaryOperation.Multiplication, FloatType),
    BinaryOperationKey(FloatType, TokenType.Star, FloatType) to
            BinaryOperationValue(BinaryOperation.Multiplication, FloatType),
    BinaryOperationKey(FloatType, TokenType.Star, LongType) to
            BinaryOperationValue(BinaryOperation.Multiplication, LongType),
    BinaryOperationKey(FloatType, TokenType.Star, DoubleType) to
            BinaryOperationValue(BinaryOperation.Multiplication, DoubleType),

    BinaryOperationKey(LongType, TokenType.Star, IntType) to
            BinaryOperationValue(BinaryOperation.Multiplication, LongType),
    BinaryOperationKey(LongType, TokenType.Star, FloatType) to
            BinaryOperationValue(BinaryOperation.Multiplication, LongType),
    BinaryOperationKey(LongType, TokenType.Star, LongType) to
            BinaryOperationValue(BinaryOperation.Multiplication, LongType),
    BinaryOperationKey(LongType, TokenType.Star, DoubleType) to
            BinaryOperationValue(BinaryOperation.Multiplication, DoubleType),

    BinaryOperationKey(DoubleType, TokenType.Star, IntType) to
            BinaryOperationValue(BinaryOperation.Multiplication, DoubleType),
    BinaryOperationKey(DoubleType, TokenType.Star, FloatType) to
            BinaryOperationValue(BinaryOperation.Multiplication, DoubleType),
    BinaryOperationKey(DoubleType, TokenType.Star, LongType) to
            BinaryOperationValue(BinaryOperation.Multiplication, DoubleType),
    BinaryOperationKey(DoubleType, TokenType.Star, DoubleType) to
            BinaryOperationValue(BinaryOperation.Multiplication, DoubleType),

    //////////////////////////////////////////////////

    BinaryOperationKey(IntType, TokenType.GreaterThanEqual, IntType) to
            BinaryOperationValue(BinaryOperation.GreaterThanEqual, BooleanType),
    BinaryOperationKey(IntType, TokenType.GreaterThanEqual, FloatType) to
            BinaryOperationValue(BinaryOperation.GreaterThanEqual, BooleanType),
    BinaryOperationKey(IntType, TokenType.GreaterThanEqual, LongType) to
            BinaryOperationValue(BinaryOperation.GreaterThanEqual, BooleanType),
    BinaryOperationKey(IntType, TokenType.GreaterThanEqual, DoubleType) to
            BinaryOperationValue(BinaryOperation.GreaterThanEqual, BooleanType),

    BinaryOperationKey(FloatType, TokenType.GreaterThanEqual, IntType) to
            BinaryOperationValue(BinaryOperation.GreaterThanEqual, BooleanType),
    BinaryOperationKey(FloatType, TokenType.GreaterThanEqual, FloatType) to
            BinaryOperationValue(BinaryOperation.GreaterThanEqual, BooleanType),
    BinaryOperationKey(FloatType, TokenType.GreaterThanEqual, LongType) to
            BinaryOperationValue(BinaryOperation.GreaterThanEqual, BooleanType),
    BinaryOperationKey(FloatType, TokenType.GreaterThanEqual, DoubleType) to
            BinaryOperationValue(BinaryOperation.GreaterThanEqual, BooleanType),

    BinaryOperationKey(LongType, TokenType.GreaterThanEqual, IntType) to
            BinaryOperationValue(BinaryOperation.GreaterThanEqual, BooleanType),
    BinaryOperationKey(LongType, TokenType.GreaterThanEqual, FloatType) to
            BinaryOperationValue(BinaryOperation.GreaterThanEqual, BooleanType),
    BinaryOperationKey(LongType, TokenType.GreaterThanEqual, LongType) to
            BinaryOperationValue(BinaryOperation.GreaterThanEqual, BooleanType),
    BinaryOperationKey(LongType, TokenType.GreaterThanEqual, DoubleType) to
            BinaryOperationValue(BinaryOperation.GreaterThanEqual, BooleanType),

    BinaryOperationKey(DoubleType, TokenType.GreaterThanEqual, IntType) to
            BinaryOperationValue(BinaryOperation.GreaterThanEqual, BooleanType),
    BinaryOperationKey(DoubleType, TokenType.GreaterThanEqual, FloatType) to
            BinaryOperationValue(BinaryOperation.GreaterThanEqual, BooleanType),
    BinaryOperationKey(DoubleType, TokenType.GreaterThanEqual, LongType) to
            BinaryOperationValue(BinaryOperation.GreaterThanEqual, BooleanType),
    BinaryOperationKey(DoubleType, TokenType.GreaterThanEqual, DoubleType) to
            BinaryOperationValue(BinaryOperation.GreaterThanEqual, BooleanType),

    //////////////////////////////////////////////////

    BinaryOperationKey(IntType, TokenType.LessThanEqual, IntType) to
            BinaryOperationValue(BinaryOperation.LessThanEqual, BooleanType),
    BinaryOperationKey(IntType, TokenType.LessThanEqual, FloatType) to
            BinaryOperationValue(BinaryOperation.LessThanEqual, BooleanType),
    BinaryOperationKey(IntType, TokenType.LessThanEqual, LongType) to
            BinaryOperationValue(BinaryOperation.LessThanEqual, BooleanType),
    BinaryOperationKey(IntType, TokenType.LessThanEqual, DoubleType) to
            BinaryOperationValue(BinaryOperation.LessThanEqual, BooleanType),

    BinaryOperationKey(FloatType, TokenType.LessThanEqual, IntType) to
            BinaryOperationValue(BinaryOperation.LessThanEqual, BooleanType),
    BinaryOperationKey(FloatType, TokenType.LessThanEqual, FloatType) to
            BinaryOperationValue(BinaryOperation.LessThanEqual, BooleanType),
    BinaryOperationKey(FloatType, TokenType.LessThanEqual, LongType) to
            BinaryOperationValue(BinaryOperation.LessThanEqual, BooleanType),
    BinaryOperationKey(FloatType, TokenType.LessThanEqual, DoubleType) to
            BinaryOperationValue(BinaryOperation.LessThanEqual, BooleanType),

    BinaryOperationKey(LongType, TokenType.LessThanEqual, IntType) to
            BinaryOperationValue(BinaryOperation.LessThanEqual, BooleanType),
    BinaryOperationKey(LongType, TokenType.LessThanEqual, FloatType) to
            BinaryOperationValue(BinaryOperation.LessThanEqual, BooleanType),
    BinaryOperationKey(LongType, TokenType.LessThanEqual, LongType) to
            BinaryOperationValue(BinaryOperation.LessThanEqual, BooleanType),
    BinaryOperationKey(LongType, TokenType.LessThanEqual, DoubleType) to
            BinaryOperationValue(BinaryOperation.LessThanEqual, BooleanType),

    BinaryOperationKey(DoubleType, TokenType.LessThanEqual, IntType) to
            BinaryOperationValue(BinaryOperation.LessThanEqual, BooleanType),
    BinaryOperationKey(DoubleType, TokenType.LessThanEqual, FloatType) to
            BinaryOperationValue(BinaryOperation.LessThanEqual, BooleanType),
    BinaryOperationKey(DoubleType, TokenType.LessThanEqual, LongType) to
            BinaryOperationValue(BinaryOperation.LessThanEqual, BooleanType),
    BinaryOperationKey(DoubleType, TokenType.LessThanEqual, DoubleType) to
            BinaryOperationValue(BinaryOperation.LessThanEqual, BooleanType),

    //////////////////////////////////////////////////

    BinaryOperationKey(IntType, TokenType.GreaterThan, IntType) to
            BinaryOperationValue(BinaryOperation.GreaterThan, BooleanType),
    BinaryOperationKey(IntType, TokenType.GreaterThan, FloatType) to
            BinaryOperationValue(BinaryOperation.GreaterThan, BooleanType),
    BinaryOperationKey(IntType, TokenType.GreaterThan, LongType) to
            BinaryOperationValue(BinaryOperation.GreaterThan, BooleanType),
    BinaryOperationKey(IntType, TokenType.GreaterThan, DoubleType) to
            BinaryOperationValue(BinaryOperation.GreaterThan, BooleanType),

    BinaryOperationKey(FloatType, TokenType.GreaterThan, IntType) to
            BinaryOperationValue(BinaryOperation.GreaterThan, BooleanType),
    BinaryOperationKey(FloatType, TokenType.GreaterThan, FloatType) to
            BinaryOperationValue(BinaryOperation.GreaterThan, BooleanType),
    BinaryOperationKey(FloatType, TokenType.GreaterThan, LongType) to
            BinaryOperationValue(BinaryOperation.GreaterThan, BooleanType),
    BinaryOperationKey(FloatType, TokenType.GreaterThan, DoubleType) to
            BinaryOperationValue(BinaryOperation.GreaterThan, BooleanType),

    BinaryOperationKey(LongType, TokenType.GreaterThan, IntType) to
            BinaryOperationValue(BinaryOperation.GreaterThan, BooleanType),
    BinaryOperationKey(LongType, TokenType.GreaterThan, FloatType) to
            BinaryOperationValue(BinaryOperation.GreaterThan, BooleanType),
    BinaryOperationKey(LongType, TokenType.GreaterThan, LongType) to
            BinaryOperationValue(BinaryOperation.GreaterThan, BooleanType),
    BinaryOperationKey(LongType, TokenType.GreaterThan, DoubleType) to
            BinaryOperationValue(BinaryOperation.GreaterThan, BooleanType),

    BinaryOperationKey(DoubleType, TokenType.GreaterThan, IntType) to
            BinaryOperationValue(BinaryOperation.GreaterThan, BooleanType),
    BinaryOperationKey(DoubleType, TokenType.GreaterThan, FloatType) to
            BinaryOperationValue(BinaryOperation.GreaterThan, BooleanType),
    BinaryOperationKey(DoubleType, TokenType.GreaterThan, LongType) to
            BinaryOperationValue(BinaryOperation.GreaterThan, BooleanType),
    BinaryOperationKey(DoubleType, TokenType.GreaterThan, DoubleType) to
            BinaryOperationValue(BinaryOperation.GreaterThan, BooleanType),

    //////////////////////////////////////////////////

    BinaryOperationKey(IntType, TokenType.LessThan, IntType) to
            BinaryOperationValue(BinaryOperation.LessThan, BooleanType),
    BinaryOperationKey(IntType, TokenType.LessThan, FloatType) to
            BinaryOperationValue(BinaryOperation.LessThan, BooleanType),
    BinaryOperationKey(IntType, TokenType.LessThan, LongType) to
            BinaryOperationValue(BinaryOperation.LessThan, BooleanType),
    BinaryOperationKey(IntType, TokenType.LessThan, DoubleType) to
            BinaryOperationValue(BinaryOperation.LessThan, BooleanType),

    BinaryOperationKey(FloatType, TokenType.LessThan, IntType) to
            BinaryOperationValue(BinaryOperation.LessThan, BooleanType),
    BinaryOperationKey(FloatType, TokenType.LessThan, FloatType) to
            BinaryOperationValue(BinaryOperation.LessThan, BooleanType),
    BinaryOperationKey(FloatType, TokenType.LessThan, LongType) to
            BinaryOperationValue(BinaryOperation.LessThan, BooleanType),
    BinaryOperationKey(FloatType, TokenType.LessThan, DoubleType) to
            BinaryOperationValue(BinaryOperation.LessThan, BooleanType),

    BinaryOperationKey(LongType, TokenType.LessThan, IntType) to
            BinaryOperationValue(BinaryOperation.LessThan, BooleanType),
    BinaryOperationKey(LongType, TokenType.LessThan, FloatType) to
            BinaryOperationValue(BinaryOperation.LessThan, BooleanType),
    BinaryOperationKey(LongType, TokenType.LessThan, LongType) to
            BinaryOperationValue(BinaryOperation.LessThan, BooleanType),
    BinaryOperationKey(LongType, TokenType.LessThan, DoubleType) to
            BinaryOperationValue(BinaryOperation.LessThan, BooleanType),

    BinaryOperationKey(DoubleType, TokenType.LessThan, IntType) to
            BinaryOperationValue(BinaryOperation.LessThan, BooleanType),
    BinaryOperationKey(DoubleType, TokenType.LessThan, FloatType) to
            BinaryOperationValue(BinaryOperation.LessThan, BooleanType),
    BinaryOperationKey(DoubleType, TokenType.LessThan, LongType) to
            BinaryOperationValue(BinaryOperation.LessThan, BooleanType),
    BinaryOperationKey(DoubleType, TokenType.LessThan, DoubleType) to
            BinaryOperationValue(BinaryOperation.LessThan, BooleanType),

    //////////////////////////////////////////////////

    BinaryOperationKey(BooleanType, TokenType.PipePipe, BooleanType) to
            BinaryOperationValue(BinaryOperation.LogicalOr, BooleanType),
    BinaryOperationKey(BooleanType, TokenType.AmpersandAmpersand, BooleanType) to
            BinaryOperationValue(BinaryOperation.LogicalAnd, BooleanType),

    //////////////////////////////////////////////////

    BinaryOperationKey(IntType, TokenType.EqualEqual, IntType) to
            BinaryOperationValue(BinaryOperation.Equals, BooleanType),
    BinaryOperationKey(DoubleType, TokenType.EqualEqual, DoubleType) to
            BinaryOperationValue(BinaryOperation.Equals, BooleanType),
    BinaryOperationKey(BooleanType, TokenType.EqualEqual, BooleanType) to
            BinaryOperationValue(BinaryOperation.Equals, BooleanType),
    BinaryOperationKey(FloatType, TokenType.EqualEqual, FloatType) to
            BinaryOperationValue(BinaryOperation.Equals, BooleanType),
    BinaryOperationKey(LongType, TokenType.EqualEqual, LongType) to
            BinaryOperationValue(BinaryOperation.Equals, BooleanType),
    BinaryOperationKey(StringType, TokenType.EqualEqual, StringType) to
            BinaryOperationValue(BinaryOperation.Equals, BooleanType),
    BinaryOperationKey(CharType, TokenType.EqualEqual, CharType) to
            BinaryOperationValue(BinaryOperation.Equals, BooleanType),

    //////////////////////////////////////////////////

    BinaryOperationKey(IntType, TokenType.BangEqual, IntType) to
            BinaryOperationValue(BinaryOperation.NotEquals, BooleanType),
    BinaryOperationKey(DoubleType, TokenType.BangEqual, DoubleType) to
            BinaryOperationValue(BinaryOperation.NotEquals, BooleanType),
    BinaryOperationKey(BooleanType, TokenType.BangEqual, BooleanType) to
            BinaryOperationValue(BinaryOperation.NotEquals, BooleanType),
    BinaryOperationKey(FloatType, TokenType.BangEqual, FloatType) to
            BinaryOperationValue(BinaryOperation.NotEquals, BooleanType),
    BinaryOperationKey(LongType, TokenType.BangEqual, LongType) to
            BinaryOperationValue(BinaryOperation.NotEquals, BooleanType),
    BinaryOperationKey(StringType, TokenType.BangEqual, StringType) to
            BinaryOperationValue(BinaryOperation.NotEquals, BooleanType),
    BinaryOperationKey(CharType, TokenType.BangEqual, CharType) to
            BinaryOperationValue(BinaryOperation.NotEquals, BooleanType),

    //////////////////////////////////////////////////

    BinaryOperationKey(StringType, TokenType.Plus, StringType) to
        BinaryOperationValue(BinaryOperation.Concat, StringType)

    //////////////////////////////////////////////////
)

private val LIST_OPERATIONS = listOf(
    NoneNonEqualityCase,
    NoneEqualityCase,
    TuplesNoneEqualityCase,
    TuplesEqualityCase
)

private interface BinaryOperationCase {
    val value: BinaryOperationValue
    fun check(left: BaseType, operationToken: TokenType, right: BaseType): Boolean
}

private object NoneEqualityCase : BinaryOperationCase {

    override val value: BinaryOperationValue =
        BinaryOperationValue(BinaryOperation.Equals, BooleanType)

    override fun check(left: BaseType, operationToken: TokenType, right: BaseType): Boolean =
        operationToken == TokenType.EqualEqual &&
                (left == NoneType && right != NoneType && NoneType.assignableTo(right) ||
                        left != NoneType && right == NoneType && NoneType.assignableTo(left))
}

private object NoneNonEqualityCase : BinaryOperationCase {

    override val value: BinaryOperationValue =
        BinaryOperationValue(BinaryOperation.NotEquals, BooleanType)

    override fun check(left: BaseType, operationToken: TokenType, right: BaseType): Boolean =
        operationToken == TokenType.BangEqual &&
                (left == NoneType && right != NoneType && NoneType.assignableTo(right) ||
                        left != NoneType && right == NoneType && NoneType.assignableTo(left))
}

private object TuplesNoneEqualityCase : BinaryOperationCase {

    override val value: BinaryOperationValue =
        BinaryOperationValue(BinaryOperation.NotEquals, BooleanType)

    override fun check(left: BaseType, operationToken: TokenType, right: BaseType): Boolean =
        operationToken == TokenType.BangEqual && left is TupleType && right is TupleType
                && left.types.zipAll(right.types) { leftType, rightType -> leftType == rightType }
}

private object TuplesEqualityCase : BinaryOperationCase {

    override val value: BinaryOperationValue =
        BinaryOperationValue(BinaryOperation.Equals, BooleanType)

    override fun check(left: BaseType, operationToken: TokenType, right: BaseType): Boolean =
        operationToken == TokenType.EqualEqual && left is TupleType && right is TupleType
                && left.types.zipAll(right.types) { leftType, rightType -> leftType == rightType }
}

private data class BinaryOperationKey(
    private val left: BaseType,
    private val operationToken: TokenType,
    private val right: BaseType
)

internal data class BinaryOperationValue(
    val operation: BinaryOperation,
    val operationType: BaseType
)

internal sealed class BinaryOperation {

    override fun toString(): String = when (this) {
        Addition, Concat -> "+"
        Subtraction -> "-"
        Multiplication -> "*"
        Division -> "/"
        LogicalOr -> "||"
        LogicalAnd -> "&&"
        Equals -> "=="
        NotEquals -> "!="
        GreaterThanEqual -> ">="
        LessThanEqual -> "<="
        GreaterThan -> ">"
        LessThan -> "<"
    }

    internal object Addition : BinaryOperation()
    internal object Subtraction : BinaryOperation()
    internal object Multiplication : BinaryOperation()
    internal object Division : BinaryOperation()
    internal object LogicalOr : BinaryOperation()
    internal object LogicalAnd : BinaryOperation()
    internal object Equals : BinaryOperation()
    internal object NotEquals : BinaryOperation()
    internal object Concat : BinaryOperation()
    internal object GreaterThanEqual : BinaryOperation()
    internal object LessThanEqual : BinaryOperation()
    internal object GreaterThan : BinaryOperation()
    internal object LessThan : BinaryOperation()
}
