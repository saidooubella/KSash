package sash.evaluate.values.callables

import sash.binder.nodes.BlockStatementBindNode
import sash.evaluate.Evaluator
import sash.evaluate.ReturnException
import sash.evaluate.environment.Environment
import sash.evaluate.environment.scoped
import sash.evaluate.values.Value
import sash.evaluate.values.primitives.BooleanValue
import sash.evaluate.values.primitives.NoneValue
import sash.evaluate.values.primitives.UnitValue
import sash.symbols.ParameterSymbol
import sash.types.FunctionType

internal class FunctionValue(
    private val parameters: List<ParameterSymbol>,
    private val block: BlockStatementBindNode,
    private val closure: Environment,
    override val type: FunctionType
) : CallableValue() {

    override fun invoke(args: List<Value>): Value {

        closure.scoped {
            try {
                args.forEachIndexed { index, arg -> closure.put(parameters[index], arg) }
                Evaluator.evaluate(block, closure)
            } catch (exception: ReturnException) {
                return exception.value
            }
        }

        return UnitValue
    }

    override fun notEquals(other: Value): BooleanValue = BooleanValue.True

    override fun equals(other: Value): BooleanValue = BooleanValue.False

    override fun equals(other: Any?): Boolean = this === other

    override fun hashCode(): Int = System.identityHashCode(this)

    override fun toString(): String = type.name
}