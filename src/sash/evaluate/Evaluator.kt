package sash.evaluate

import sash.binder.nodes.*
import sash.evaluate.environment.Environment
import sash.evaluate.environment.copy
import sash.evaluate.environment.scoped
import sash.evaluate.values.*
import sash.evaluate.values.callables.FunctionValue
import sash.evaluate.values.callables.MethodValue
import sash.evaluate.values.indexed.*
import sash.evaluate.values.primitives.*
import sash.evaluate.values.records.RecordBuilder
import sash.types.*
import java.util.*

internal object Evaluator {

    internal fun evaluate(program: ProgramBindNode) = try {
        val environment = Environment.create()
        program.statements.evaluateStatementsList(environment)
    } catch (e: ReturnException) {}

    internal fun evaluate(node: StatementBindNode, environment: Environment) {
        return when (node) {
            is ExpressionStatementBindNode -> node.evaluateExpressionStatement(environment)
            is FunctionStatementBindNode -> node.evaluateFunctionStatement(environment)
            is VariableStatementBindNode -> node.evaluateVariableStatement(environment)
            is DoWhileStatementBindNode -> node.evaluateDoWhileStatement(environment)
            is MethodStatementBindNode -> node.evaluateMethodStatement(environment)
            is RecordStatementBindNode -> node.evaluateRecordStatement(environment)
            is BlockStatementBindNode -> node.evaluateBlockStatement(environment)
            is DeferStatementBindNode -> node.evaluateDeferStatement(environment)
            is WhileStatementBindNode -> node.evaluateWhileStatement(environment)
            is IfStatementBindNode -> node.evaluateIfStatement(environment)
        }
    }

    private fun DeferStatementBindNode.evaluateDeferStatement(environment: Environment) {
        evaluate(statement, environment)
    }

    private fun RecordStatementBindNode.evaluateRecordStatement(environment: Environment) {
        environment.put(record, RecordBuilder(record))
    }

    private fun MethodStatementBindNode.evaluateMethodStatement(environment: Environment) = environment.copy {
        environment.put(method, MethodValue(method.receiver, method.parameters, block, it, method.type))
    }

    private fun FunctionStatementBindNode.evaluateFunctionStatement(environment: Environment) = environment.copy {
        environment.put(function, FunctionValue(function.parameters, block, it, function.type))
    }

    private fun BlockStatementBindNode.evaluateBlockStatement(environment: Environment) {
        environment.scoped { statements.evaluateStatementsList(environment) }
    }

    private fun List<StatementBindNode>.evaluateStatementsList(environment: Environment) {
        val stack = ArrayDeque<StatementBindNode>()
        try {
            for (statement in this) {
                if (statement is DeferStatementBindNode)
                    stack.push(statement.statement)
                else evaluate(statement, environment)
            }
        } finally {
            while (stack.isNotEmpty()) evaluate(stack.pop(), environment)
        }
    }

    private fun ExpressionStatementBindNode.evaluateExpressionStatement(environment: Environment) {
        evaluate(expression, environment)
    }

    private fun VariableStatementBindNode.evaluateVariableStatement(environment: Environment) {
        environment.put(variable, evaluate(value, environment))
    }

    private fun WhileStatementBindNode.evaluateWhileStatement(environment: Environment) {
        while (evaluate(condition, environment).asBoolean().value) try {
            evaluate(block, environment)
        } catch (e: ContinueException) {
            continue
        } catch (e: BreakException) {
            break
        }
    }

    private fun DoWhileStatementBindNode.evaluateDoWhileStatement(environment: Environment) {
        do try {
            evaluate(block, environment)
        } catch (e: ContinueException) {
            continue
        } catch (e: BreakException) {
            break
        } while (evaluate(condition, environment).asBoolean().value)
    }

    private fun IfStatementBindNode.evaluateIfStatement(environment: Environment) {
        if (evaluate(condition, environment).asBoolean().value)
            evaluate(ifBlock, environment) else elseBlock?.let { evaluate(it, environment) }
    }

    internal fun evaluate(node: ExpressionBindNode, environment: Environment): Value {
        return when (node) {
            is ErrorExpressionBindNode -> throw IllegalStateException()
            is ContinueExpressionBindNode -> throw ContinueException()
            is BreakExpressionBindNode -> throw BreakException()
            is NoneExpressionBindNode -> NoneValue
            is BinaryOperationExpressionBindNode -> node.evaluateBinaryOperationExpression(environment)
            is UnaryOperationExpressionBindNode -> node.evaluateUnaryOperationExpression(environment)
            is ParenthesisedExpressionBindNode -> node.evaluateParenthesisedExpression(environment)
            is AssignmentExpressionBindNode -> node.evaluateAssignmentExpression(environment)
            is RecordInitExpressionBindNode -> node.evaluateRecordInitExpression(environment)
            is SetIndexedExpressionBindNode -> node.evaluateSetIndexedExpression(environment)
            is TupleValueExpressionBindNode -> node.evaluateTupleValueExpression(environment)
            is ListValueExpressionBindNode -> node.evaluateListValueExpression(environment)
            is GetMethodExpressionBindNode -> node.evaluateGetMethodExpression(environment)
            is SetFieldExpressionBindNode -> node.evaluateSetFieldExpression(environment)
            is TypeCastExpressionBindNode -> node.evaluateTypeCastExpression(environment)
            is SetValueExpressionBindNode -> node.evaluateSetValueExpression(environment)
            is MapValueExpressionBindNode -> node.evaluateMapValueExpression(environment)
            is FunctionExpressionBindNode -> node.evaluateFunctionExpression(environment)
            is VariableExpressionBindNode -> node.evaluateVariableExpression(environment)
            is GetFieldExpressionBindNode -> node.evaluateGetFieldExpression(environment)
            is LiteralExpressionBindNode -> node.evaluateLiteralExpression()
            is IndexedExpressionBindNode -> node.evaluateIndexedExpression(environment)
            is TernaryExpressionBindNode -> node.evaluateTernaryExpression(environment)
            is ReturnExpressionBindNode -> node.evaluateReturnExpression(environment)
            is PanicExpressionBindNode -> node.evaluatePanicExpression(environment)
            is CallExpressionBindNode -> node.evaluateCallExpression(environment)
            is TryExpressionBindNode -> node.evaluateTryExpression(environment)
        }
    }

    private fun MapValueExpressionBindNode.evaluateMapValueExpression(environment: Environment): Value {
        return MapValue(values.map { evaluate(it.first, environment) to evaluate(it.second, environment) }, type)
    }

    private fun SetValueExpressionBindNode.evaluateSetValueExpression(environment: Environment): Value {
        return SetValue(values.map { evaluate(it, environment) }, type)
    }

    private fun SetIndexedExpressionBindNode.evaluateSetIndexedExpression(environment: Environment): Value {
        return evaluate(target, environment).asIndexed().set(evaluate(index, environment), evaluate(value, environment))
    }

    private fun PanicExpressionBindNode.evaluatePanicExpression(environment: Environment): Value {
        throw PanicException(evaluate(message, environment))
    }

    private fun IndexedExpressionBindNode.evaluateIndexedExpression(environment: Environment): Value {
        return evaluate(target, environment).asIndexed().get(evaluate(index, environment))
    }

    private fun TupleValueExpressionBindNode.evaluateTupleValueExpression(environment: Environment): Value {
        return TupleValue(values.map { evaluate(it, environment) }, type)
    }

    private fun ListValueExpressionBindNode.evaluateListValueExpression(environment: Environment): Value {
        return ListValue(values.map { evaluate(it, environment) }, type)
    }

    private fun TryExpressionBindNode.evaluateTryExpression(environment: Environment): Value {
        return try { evaluate(expression, environment) } catch (panic: PanicException) { NoneValue }
    }

    private fun SetFieldExpressionBindNode.evaluateSetFieldExpression(environment: Environment): Value {
        return evaluate(value, environment).also { evaluate(target, environment).asRecord().set(field, it) }
    }

    private fun GetFieldExpressionBindNode.evaluateGetFieldExpression(environment: Environment): Value {
        return evaluate(target, environment).asRecord().get(field)
    }

    private fun GetMethodExpressionBindNode.evaluateGetMethodExpression(environment: Environment): Value {
        return environment.get(method).asMethodBuilder().build(evaluate(target, environment))
    }

    private fun TernaryExpressionBindNode.evaluateTernaryExpression(environment: Environment): Value {
        val value = if (evaluate(condition, environment).asBoolean().value) ifExpression else elseExpression
        return evaluate(value, environment)
    }

    private fun TypeCastExpressionBindNode.evaluateTypeCastExpression(environment: Environment): Value {
        val value = evaluate(target, environment)
        return value.takeIf { it.type.assignableTo(type) }
            ?: throw PanicException(StringValue("CastError: ${value.type} cannot be cast to $type"))
    }

    private fun FunctionExpressionBindNode.evaluateFunctionExpression(environment: Environment): Value {
        return environment.copy { FunctionValue(parameters, block, it, type) }
    }

    private fun ReturnExpressionBindNode.evaluateReturnExpression(environment: Environment): Value {
        throw ReturnException(value?.let { evaluate(it, environment) } ?: UnitValue)
    }

    private fun RecordInitExpressionBindNode.evaluateRecordInitExpression(environment: Environment): Value {
        return environment.get(record).asRecordBuilder().build(arguments.map { evaluate(it, environment) })
    }

    private fun CallExpressionBindNode.evaluateCallExpression(environment: Environment): Value {
        return evaluate(target, environment).asCallable().invoke(arguments.map { evaluate(it, environment) })
    }

    private fun BinaryOperationExpressionBindNode.evaluateBinaryOperationExpression(environment: Environment): Value {

        val left = evaluate(left, environment)
        val right = evaluate(right, environment)

        return when (operation) {
            BinaryOperation.Equals -> left.equals(right)
            BinaryOperation.NotEquals -> left.notEquals(right)
            BinaryOperation.GreaterThanEqual -> left.asNumber().greaterThanEqual(right.asNumber())
            BinaryOperation.LessThanEqual -> left.asNumber().lessThanEqual(right.asNumber())
            BinaryOperation.Multiplication -> left.asNumber().multiply(right.asNumber())
            BinaryOperation.GreaterThan -> left.asNumber().greaterThan(right.asNumber())
            BinaryOperation.Subtraction -> left.asNumber().minus(right.asNumber())
            BinaryOperation.LessThan -> left.asNumber().lessThan(right.asNumber())
            BinaryOperation.Division -> left.asNumber().divide(validateNotZero(right.asNumber()))
            BinaryOperation.Addition -> left.asNumber().plus(right.asNumber())
            BinaryOperation.LogicalAnd -> left.asBoolean().logicalAnd(right.asBoolean())
            BinaryOperation.LogicalOr -> left.asBoolean().logicalOr(right.asBoolean())
            BinaryOperation.Concat -> left.asString().concat(right.asString())
        }
    }

    private fun validateNotZero(right: NumberValue): NumberValue {
        return right.takeIf { right is IntValue && right.value != 0 || right is LongValue && right.value != 0L }
            ?: throw PanicException(StringValue("Division by zero"))
    }

    private fun UnaryOperationExpressionBindNode.evaluateUnaryOperationExpression(environment: Environment): Value {
        val operand = evaluate(operand, environment)
        return when (operation) {
            UnaryOperation.LogicalNegation -> operand.asBoolean().logicalInvert()
            UnaryOperation.Negation -> operand.asNumber().invert()
            UnaryOperation.Identity -> operand
        }
    }

    private fun ParenthesisedExpressionBindNode.evaluateParenthesisedExpression(environment: Environment): Value {
        return evaluate(expression, environment)
    }

    private fun AssignmentExpressionBindNode.evaluateAssignmentExpression(environment: Environment): Value {
        return evaluate(value, environment).also { environment.change(variable, it) }
    }

    private fun VariableExpressionBindNode.evaluateVariableExpression(environment: Environment): Value {
        return environment.get(symbol)
    }

    private fun LiteralExpressionBindNode.evaluateLiteralExpression(): Value {
        return when (type as? Primitive) {
            ErrorType, NothingType, AnyType, UnitType, null -> throw IllegalStateException()
            BooleanType -> BooleanValue(value as Boolean)
            DoubleType -> DoubleValue(value as Double)
            StringType -> StringValue(value as String)
            FloatType -> FloatValue(value as Float)
            LongType -> LongValue(value as Long)
            CharType -> CharValue(value as Char)
            IntType -> IntValue(value as Int)
        }
    }
}