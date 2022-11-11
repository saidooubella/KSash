@file:Suppress("NOTHING_TO_INLINE")

package sash.binder

import sash.binder.nodes.*
import sash.binder.scope.SymbolsTable
import sash.binder.scope.scoped
import sash.errors.ErrorsReporter
import sash.lexer.token.Token
import sash.parser.nodes.*
import sash.span.Span
import sash.span.plus
import sash.symbols.*
import sash.tools.*
import sash.types.*
import kotlin.math.min

internal class Binder(private val program: ProgramNode, private val errors: ErrorsReporter) {

    private val symbolsTable: SymbolsTable = SymbolsTable.create()
    private val scopeTracker: ScopeTracker = ScopeTracker()

    internal fun createProgramNode(): ProgramBindNode {
        val statements = program.statements.map { it.bind() }
        checkReturnPaths(statements, UnitType)
        return ProgramBindNode(statements)
    }

    private fun StatementNode.bind(): StatementBindNode {
        return when (this) {
            is ExpressionStatementNode -> bindExpressionStatement()
            is VariableStatementNode -> bindVariableStatement()
            is FunctionStatementNode -> bindFunctionStatement()
            is DoWhileStatementNode -> bindDoWhileStatement()
            is MethodStatementNode -> bindMethodStatement()
            is RecordStatementNode -> bindRecordStatement()
            is BlockStatementNode -> bindBlockStatement()
            is DeferStatementNode -> bindDeferStatement()
            is WhileStatementNode -> bindWhileStatement()
            is IfStatementNode -> bindIfStatement()
        }
    }

    private fun DeferStatementNode.bindDeferStatement(): StatementBindNode {
        return DeferStatementBindNode(scopeTracker.deferScope { statement.bind() }, span)
    }

    private fun RecordStatementNode.bindRecordStatement(): StatementBindNode {

        val name = identifier.text
        val type = RecordType(name)

        val hasSymbol = symbolsTable.hasRecord(name)
        if (hasSymbol) errors.reportAlreadyExistentSymbol(identifier.span, name)
        else if (name.isNotEmpty()) symbolsTable.putType(type)

        val fields = bindRecordFields(fields)
        val record = RecordSymbol(fields, type)

        if (name.isNotEmpty()) symbolsTable.putRecord(record)

        return RecordStatementBindNode(record, span)
    }

    private fun bindRecordFields(recordFields: List<RecordField>): List<FieldSymbol> {

        val fields = mutableListOf<FieldSymbol>()
        val fieldsLookup = hashSetOf<String>()

        recordFields.forEach { field ->
            when (field) {
                is RecordField.NormalField -> {
                    val identifier = field.identifier.text
                    if (identifier.isEmpty()) return@forEach
                    val type = field.type.type.bindSymbolType()
                    if (!fieldsLookup.add(identifier))
                        errors.reportAlreadyUsedFieldName(field.span, identifier)
                    fields.add(FieldSymbol(identifier, type))
                }
            }
        }

        return fields
    }

    private fun FunctionStatementNode.bindFunctionStatement(): StatementBindNode {

        val hasSymbol = symbolsTable.hasSymbol(identifier.text)
        if (hasSymbol) errors.reportAlreadyExistentSymbol(identifier.span, identifier.text)

        val params = bindParamClauses(params)

        val returnType = type?.type?.bindSymbolType() ?: UnitType
        val type = FunctionType(params.map { it.type }, returnType)
        val function = FunctionSymbol(identifier.text, type, params)

        if (!hasSymbol && identifier.text.isNotEmpty()) symbolsTable.putSymbol(function)

        val block = scopeTracker.funScope {
            symbolsTable.scoped {
                params.forEach(symbolsTable::putSymbol)
                block.bindBlockStatement()
            }
        }

        if (!checkReturnPaths(block, returnType)) {
            errors.reportRequireReturnValue(close.span, returnType)
        }

        return FunctionStatementBindNode(function, block, span)
    }

    private fun MethodStatementNode.bindMethodStatement(): StatementBindNode {

        val receiverType = receiver.bindSymbolType()

        val hasSymbol = symbolsTable.hasMethod(receiverType, identifier.text)
        if (hasSymbol) errors.reportAlreadyExistentSymbol(identifier.span, identifier.text)

        val params = bindParamClauses(params)

        val returnType = type?.type?.bindSymbolType() ?: UnitType
        val type = FunctionType(params.map { it.type }, returnType)
        val receiver = ReceiverSymbol(receiverType)
        val method = MethodSymbol(identifier.text, receiver, params, type)

        if (!hasSymbol && identifier.text.isNotEmpty()) {
            symbolsTable.putMethod(method)
        }

        val block = scopeTracker.funScope {
            symbolsTable.scoped {
                symbolsTable.putSymbol(receiver)
                params.forEach(symbolsTable::putSymbol)
                block.bindBlockStatement()
            }
        }

        if (!checkReturnPaths(block, returnType)) {
            errors.reportRequireReturnValue(close.span, returnType)
        }

        return MethodStatementBindNode(method, block, span)
    }

    private fun bindParamClauses(paramClauses: SeparatedList<ParamClause, Token>): List<ParameterSymbol> {
        val parameters = mutableListOf<ParameterSymbol>()
        val paramsLookup = hashSetOf<String>()
        paramClauses.forEach { paramClause ->
            val identifier = paramClause.identifier.text
            if (identifier.isEmpty()) return@forEach
            val type = paramClause.type.type.bindSymbolType()
            if (!paramsLookup.add(identifier))
                errors.reportAlreadyUsedParamName(paramClause.span, identifier)
            parameters.add(ParameterSymbol(identifier, type))
        }
        return parameters
    }

    private fun DoWhileStatementNode.bindDoWhileStatement(): StatementBindNode {

        val condition = condition.bind(BindingContext(BooleanType))
        val block = scopeTracker.loopScope { block.bind() }

        if (!condition.isError() && condition.type != BooleanType)
            errors.reportInvalidConditionType(condition.span)

        return DoWhileStatementBindNode(block, condition, span)
    }

    private fun WhileStatementNode.bindWhileStatement(): StatementBindNode {

        val condition = condition.bind(BindingContext(BooleanType))
        val block = scopeTracker.loopScope { block.bind() }

        if (!condition.isError() && condition.type != BooleanType)
            errors.reportInvalidConditionType(condition.span)

        return WhileStatementBindNode(condition, block, span)
    }

    private fun IfStatementNode.bindIfStatement(): StatementBindNode {

        val condition = condition.bind(BindingContext(BooleanType))
        val ifBlock = block.bind()
        val elseBlock = elseClause?.block?.bind()

        if (!condition.isError() && condition.type != BooleanType)
            errors.reportInvalidConditionType(condition.span)

        return IfStatementBindNode(condition, ifBlock, elseBlock, span)
    }

    private fun BlockStatementNode.bindBlockStatement(): BlockStatementBindNode {
        symbolsTable.scoped {
            val statements = statements.map { it.bind() }
            return BlockStatementBindNode(statements, span)
        }
    }

    private fun VariableStatementNode.bindVariableStatement(): VariableStatementBindNode {

        val hasSymbol = symbolsTable.hasSymbol(identifier.text)

        if (hasSymbol) errors.reportAlreadyExistentSymbol(identifier.span, identifier.text)

        val explicitType = type?.type?.bindSymbolType()
        val value = value.bind(explicitType?.let { BindingContext(it) } ?: BindingContext.Empty)
        val type = explicitType ?: value.type
        val variable = VariableSymbol(identifier.text, type, readOnly)

        if (!hasSymbol && identifier.text.isNotEmpty()) symbolsTable.putSymbol(variable)

        if (value.type == NothingType)
            errors.reportUnreachedStatement(keyword + equal)
        else if (!value.type.assignableTo(type))
            errors.reportWrongAssignment(value.span, value.type, type)

        return VariableStatementBindNode(variable, value, span)
    }

    private fun ExpressionStatementNode.bindExpressionStatement(): StatementBindNode {
        val expression = expression.bind(BindingContext.Empty)
        if (!expression.isValidStatement)
            errors.reportIllegalExpressionStatement(span)
        return ExpressionStatementBindNode(expression)
    }

    private fun ExpressionNode.bind(context: BindingContext): ExpressionBindNode {
        return when (this) {
            is BinaryOperationExpressionNode -> bindBinaryOperationExpression(context)
            is UnaryOperationExpressionNode -> bindUnaryOperationExpression(context)
            is ParenthesisedExpressionNode -> bindParenthesisedExpression(context)
            is AssignmentExpressionNode -> bindAssignmentExpression(context)
            is RecordInitExpressionNode -> bindRecordInitExpression(context)
            is TupleValueExpressionNode -> bindTupleValueExpression(context)
            is ListValueExpressionNode -> bindListValueExpression(context)
            is ContinueExpressionNode -> bindContinueExpression()
            is FunctionExpressionNode -> bindFunctionExpression()
            is MapValueExpressionNode -> bindMapValueExpression(context)
            is TypeCastExpressionNode -> bindTypeCastExpression(context)
            is VariableExpressionNode -> bindVariableExpression()
            is IndexedExpressionNode -> bindIndexedExpression(context)
            is LiteralExpressionNode -> bindLiteralExpression()
            is TernaryExpressionNode -> bindTernaryExpression(context)
            is ReturnExpressionNode -> bindReturnExpression(context)
            is BreakExpressionNode -> bindBreakExpression()
            is PanicExpressionNode -> bindPanicExpression(context)
            is SelfExpressionNode -> bindSelfExpression()
            is CallExpressionNode -> bindCallExpression(context)
            is NoneExpressionNode -> bindNoneExpression()
            is GetExpressionNode -> bindGetExpression(context)
            is TryExpressionNode -> bindTryExpression(context)
        }
    }

    private fun MapValueExpressionNode.bindMapValueExpression(context: BindingContext): ExpressionBindNode {

        return when (type) {

            MapValueType.Unknown -> {
                when (val type = context.expectedType?.takeIf { it is SetType || it is MapType }) {
                    is SetType -> SetValueExpressionBindNode(listOf(), type, span)
                    is MapType -> MapValueExpressionBindNode(listOf(), type, span)
                    else -> ErrorExpressionBindNode(span).also { errors.reportCannotInferType(span) }
                }
            }

            MapValueType.Map -> {

                val baseType = context.expectedType as? MapType
                val keyContext = context.copy(expectedType = baseType?.key)
                val valueContext = context.copy(expectedType = baseType?.value)

                val values = values.map { node ->
                    val keyValue = node as MapExpression.KeyValue
                    keyValue.key.bind(keyContext).also {
                        if (baseType != null && !it.type.assignableTo(baseType.key)) {
                            errors.reportUnexpectedValueType(keyValue.span, baseType.key, it.type)
                        }
                    } to keyValue.value.bind(valueContext).also {
                        if (baseType != null && !it.type.assignableTo(baseType.value)) {
                            errors.reportUnexpectedValueType(keyValue.span, baseType.value, it.type)
                        }
                    }
                }

                val actualType = MapType(
                    baseType?.key ?: values.generalType({ first.type }, span),
                    baseType?.value ?: values.generalType({ second.type }, span)
                )

                MapValueExpressionBindNode(values, actualType, span)
            }

            MapValueType.Set -> {

                val baseType = (context.expectedType as? SetType)?.type
                val bindingContext = context.copy(expectedType = baseType)

                val values = values.map { node ->
                    val valueOnly = node as MapExpression.ValueOnly
                    valueOnly.value.bind(bindingContext).also {
                        if (baseType != null && !it.type.assignableTo(baseType)) {
                            errors.reportUnexpectedValueType(valueOnly.span, baseType, it.type)
                        }
                    }
                }

                val actualType = SetType(baseType ?: values.generalType({ type }, span))

                SetValueExpressionBindNode(values, actualType, span)
            }
        }
    }

    private fun ListValueExpressionNode.bindListValueExpression(context: BindingContext): ExpressionBindNode {

        val baseType = (context.expectedType as? ListType)?.type
        val bindingContext = context.copy(expectedType = baseType)

        val values = values.map { node ->
            node.bind(bindingContext).also {
                if (baseType != null && !it.type.assignableTo(baseType)) {
                    errors.reportUnexpectedValueType(node.span, baseType, it.type)
                }
            }
        }

        val actualType = ListType(baseType ?: values.generalType({ type }, span))

        return ListValueExpressionBindNode(values, actualType, span)
    }

    private fun TernaryExpressionNode.bindTernaryExpression(context: BindingContext): ExpressionBindNode {

        val condition = condition.bind(context)
        val ifExpression = ifExpression.bind(context)
        val elseExpression = elseExpression.bind(context)

        if (!condition.isError() && condition.type != BooleanType)
            errors.reportInvalidConditionType(condition.span)

        val ifExprType = ifExpression.type
        val elseExprType = elseExpression.type

        val type = when {
            ifExprType == elseExprType -> ifExprType
            ifExprType.isNoneable() || elseExprType.isNoneable() -> BuiltIn.ANY_NONE_UNION
            else -> AnyType
        }

        return TernaryExpressionBindNode(condition, ifExpression, elseExpression, type, span)
    }

    private fun TupleValueExpressionNode.bindTupleValueExpression(context: BindingContext): ExpressionBindNode {
        val values = values.map { it.bind(context) }
        val type = TupleType(values.map { it.type })
        return TupleValueExpressionBindNode(values, type, span)
    }

    private fun TryExpressionNode.bindTryExpression(context: BindingContext): ExpressionBindNode {
        val expression = expression.bind(context)
        val type = validateUnreachableValue(expression.type, UnionType(listOf(expression.type, NoneType)), keyword.span)
        return TryExpressionBindNode(expression, type, span)
    }

    private fun VariableExpressionNode.bindVariableExpression(): ExpressionBindNode {

        if (identifier.text.isEmpty()) return ErrorExpressionBindNode(span)

        symbolsTable.getSymbol("self")?.let { self ->
            val target = VariableExpressionBindNode(self, span)
            bindGetExpression(target, identifier, false, span)?.let { return it }
        }

        val symbol = symbolsTable.getSymbol(identifier.text) ?: run {
            errors.reportUnknownSymbol(span, identifier.text)
            return ErrorExpressionBindNode(span)
        }

        return VariableExpressionBindNode(symbol, span)
    }

    private fun GetExpressionNode.bindGetExpression(context: BindingContext): ExpressionBindNode {
        val target = target.bind(context).nullIfError() ?: return ErrorExpressionBindNode(span)
        if (identifier.text.isEmpty()) return ErrorExpressionBindNode(span)
        return bindGetExpression(target, identifier, true, span) ?: ErrorExpressionBindNode(span)
    }

    private fun bindGetExpression(
        target: ExpressionBindNode,
        identifier: Token,
        reportAbsence: Boolean,
        span: Span
    ): ExpressionBindNode? {

        val receiverType = target.type

        if (receiverType is RecordType) {
            val record = symbolsTable.getRecord(receiverType.name) as RecordSymbol
            record.fields.find { it.name == identifier.text }?.let { field ->
                return GetFieldExpressionBindNode(target, field, span)
            }
        }

        val methodsList = symbolsTable.getMethod(receiverType, identifier.text) ?: run {
            if (reportAbsence) errors.reportUnknownSymbol(identifier.span, identifier.text)
            return null
        }

        val method = when {
            methodsList.size == 1 -> methodsList.first().value[identifier.text]
            else -> methodsList.find { receiverType == it.key }?.value?.get(identifier.text)
        } ?: run {
            errors.reportOverloadResolutionAmbiguity(target.span, methodsList)
            return null
        }

        return GetMethodExpressionBindNode(target, method, span)
    }

    private fun TypeCastExpressionNode.bindTypeCastExpression(context: BindingContext): ExpressionBindNode {

        val target = target.bind(context)
        val targetType = valueType.bindSymbolType()

        if (!target.type.castableTo(targetType))
            errors.reportTypeCastCanNeverSucceed(span)

        return TypeCastExpressionBindNode(target, targetType, span)
    }

    private fun FunctionExpressionNode.bindFunctionExpression(): ExpressionBindNode {

        val params = bindParamClauses(params)
        val block = scopeTracker.funScope {
            symbolsTable.scoped {
                params.forEach(symbolsTable::putSymbol)
                block.bindBlockStatement()
            }
        }

        val returnType = type?.type?.bindSymbolType() ?: UnitType
        val type = FunctionType(params.map { it.type }, returnType)

        if (!checkReturnPaths(block, returnType)) {
            errors.reportRequireReturnValue(close.span, returnType)
        }

        return FunctionExpressionBindNode(params, block, type, span)
    }

    private fun PanicExpressionNode.bindPanicExpression(context: BindingContext): ExpressionBindNode {

        val message = message.bind(context)

        if (!message.isError() && message.type != StringType)
            errors.reportInvalidPanicMessageType(message.span)

        return PanicExpressionBindNode(message, span)
    }

    private fun IndexedExpressionNode.bindIndexedExpression(context: BindingContext): ExpressionBindNode {

        val target = target.bind(context)
        val index = index.bind(context)
        val targetType = target.type

        if (targetType is StringType) {
            if (!index.isError() && index.type != IntType) errors.reportIndexWrongType(index.span, IntType)
            return IndexedExpressionBindNode(target, index, CharType, span)
        }

        if (targetType is ListType) {
            if (!index.isError() && index.type != IntType) errors.reportIndexWrongType(index.span, IntType)
            return IndexedExpressionBindNode(target, index, targetType.type, span)
        }

        if (targetType is MapType) {
            if (!index.type.assignableTo(targetType.key))
                errors.reportIndexWrongType(index.span, targetType.key)
            val type = UnionType(listOf(targetType.value, NoneType))
            return IndexedExpressionBindNode(target, index, type, span)
        }

        if (targetType is TupleType) {

            val type = if (index is LiteralExpressionBindNode && index.type == IntType) {
                val result = index.value as Int
                if (result !in targetType.types.indices) {
                    errors.reportIndexOutOfBounds(index.span)
                    ErrorType
                } else targetType.types[result]
            } else {
                errors.reportWrongTupleIndexFormat(index.span)
                ErrorType
            }

            return IndexedExpressionBindNode(target, index, type, span)
        }

        if (!target.isError()) errors.reportInvalidIndexedTarget(target.span)

        return ErrorExpressionBindNode(span)
    }

    private fun CallExpressionNode.bindCallExpression(context: BindingContext): ExpressionBindNode {

        val target = target.bind(context)
        val targetType = target.type

        if (targetType is FunctionType) {
            val (params, returnType) = targetType
            val arguments = validateArguments(context, params, args, open + close)
            val type = validateUnreachedStatement(arguments, returnType, span)
            return CallExpressionBindNode(target, arguments, type, span)
        }

        args.forEach { it.bind(context.copy(expectedType = null)) }
        if (!target.isError()) errors.reportInvalidInvokableTarget(target.span)

        return ErrorExpressionBindNode(span)
    }

    private fun RecordInitExpressionNode.bindRecordInitExpression(context: BindingContext): ExpressionBindNode {

        if (identifier.text.isEmpty()) {
            args.forEach { it.bind(context.copy(expectedType = null)) }
            return ErrorExpressionBindNode(span)
        }

        val record = symbolsTable.getRecord(identifier.text) ?: run {
            args.forEach { it.bind(context.copy(expectedType = null)) }
            errors.reportUnknownSymbol(span, identifier.text)
            return ErrorExpressionBindNode(span)
        }

        val fieldTypes = record.fields.map { it.type }

        val arguments = validateArguments(context, fieldTypes, args, open + close)
        val type = validateUnreachedStatement(arguments, record.type, span)

        return RecordInitExpressionBindNode(record, arguments, type, span)
    }

    private fun validateUnreachedStatement(
        arguments: List<ExpressionBindNode>,
        resultType: BaseType,
        span: Span
    ): BaseType {
        val it = arguments.find { it.type == NothingType }?.span ?: return resultType
        errors.reportUnreachedStatement(Span(span.start, it.start))
        errors.reportUnreachedStatement(Span(it.end, span.end))
        return NothingType
    }

    private fun validateArguments(
        context: BindingContext,
        paramsTypes: List<BaseType>,
        args: SeparatedList<ExpressionNode, *>,
        span: Span
    ): List<ExpressionBindNode> {

        val arguments = args.mapIndexed { index, item ->
            item.bind(context.copy(expectedType = paramsTypes.getOrNull(index)))
        }

        if (paramsTypes.size != arguments.size)
            errors.reportUnexpectedArgsSize(span, paramsTypes.size, arguments.size)

        repeat(min(arguments.size, paramsTypes.size)) { index ->
            val argument = arguments[index]
            val paramType = paramsTypes[index]
            if (!argument.type.assignableTo(paramType)) {
                errors.reportInvalidArgumentType(argument.span, paramType, argument.type)
            }
        }

        return arguments
    }

    private fun BreakExpressionNode.bindBreakExpression(): ExpressionBindNode {

        when (scopeTracker.inLoopState()) {
            LoopState.InFunction -> errors.reportJumpThroughFunction(span, "break")
            LoopState.InDefer -> errors.reportJumpThroughDefer(span, "break")
            LoopState.Invalid -> errors.reportOutOfLoopScope(span, "break")
        }

        return BreakExpressionBindNode(span)
    }

    private fun ContinueExpressionNode.bindContinueExpression(): ExpressionBindNode {

        when (scopeTracker.inLoopState()) {
            LoopState.InFunction -> errors.reportJumpThroughFunction(span, "continue")
            LoopState.InDefer -> errors.reportJumpThroughDefer(span, "continue")
            LoopState.Invalid -> errors.reportOutOfLoopScope(span, "continue")
        }

        return ContinueExpressionBindNode(span)
    }

    private fun ReturnExpressionNode.bindReturnExpression(context: BindingContext): ExpressionBindNode {

        if (scopeTracker.inFunctionState() == FunctionState.InDefer)
            errors.reportJumpThroughDefer(span, "return")

        val value = value?.bind(context)?.apply {
            if (type == NothingType) errors.reportUnreachedStatement(keyword)
        }

        return ReturnExpressionBindNode(value, span)
    }

    private fun AssignmentExpressionNode.bindAssignmentExpression(context: BindingContext): ExpressionBindNode {

        val target = target.bind(context)

        if (target is VariableExpressionBindNode && target.symbol is VariableSymbol) {
            val (name, type, readOnly) = target.symbol
            if (readOnly) errors.reportReadOnlyVariableAssignment(target.span, name)
            val value = validateAssignmentValue(context, expression, type)
            val actualType = validateUnreachableValue(value.type, type, target + equal)
            return AssignmentExpressionBindNode(target.symbol, value, actualType, span)
        } else if (target is IndexedExpressionBindNode) {
            when (val targetType = target.target.type) {
                is ListType -> targetType.type
                is MapType -> targetType.value
                else -> null
            }?.let { type ->
                val value = validateAssignmentValue(context, expression, type)
                val actualType = validateUnreachableValue(value.type, type, target + equal)
                return SetIndexedExpressionBindNode(target.target, target.index, value, actualType, span)
            }
        } else if (target is GetFieldExpressionBindNode) {
            val value = validateAssignmentValue(context, expression, target.type)
            val actualType = validateUnreachableValue(value.type, target.field.type, target + equal)
            return SetFieldExpressionBindNode(target.target, target.field, value, actualType, span)
        }

        expression.bind(context.copy(expectedType = null))

        if (!target.isError())
            errors.reportInvalidAssignmentTarget(target.span)

        return ErrorExpressionBindNode(span)
    }

    private fun validateUnreachableValue(
        valueType: BaseType,
        resultType: BaseType,
        span: Span
    ): BaseType {
        val isUnreachable = valueType == NothingType
        if (isUnreachable) errors.reportUnreachedStatement(span)
        return if (isUnreachable) NothingType else resultType
    }

    private fun validateAssignmentValue(
        context: BindingContext,
        expression: ExpressionNode,
        type: BaseType
    ): ExpressionBindNode {
        return expression.bind(context.copy(expectedType = type)).also { value ->
            if (!value.type.assignableTo(type))
                errors.reportWrongAssignment(value.span, value.type, type)
        }
    }

    private fun SelfExpressionNode.bindSelfExpression(): ExpressionBindNode {

        val symbol = symbolsTable.getSymbol("self") ?: run {
            errors.reportSelfReferenceNotFound(span)
            return ErrorExpressionBindNode(span)
        }

        return VariableExpressionBindNode(symbol, span)
    }

    private fun ParenthesisedExpressionNode.bindParenthesisedExpression(context: BindingContext): ExpressionBindNode {
        return ParenthesisedExpressionBindNode(expression.bind(context), span)
    }

    private fun BinaryOperationExpressionNode.bindBinaryOperationExpression(context: BindingContext): ExpressionBindNode {

        val left = left.bind(context).nullIfError()
        val right = right.bind(context).nullIfError()

        left ?: return ErrorExpressionBindNode(span)
        right ?: return ErrorExpressionBindNode(span)

        val binder = BinaryOperationBinder(left.type, operation.type, right.type) ?: run {
            errors.reportInvalidBinaryOperation(operation.span, operation.text, left.type, right.type)
            return ErrorExpressionBindNode(span)
        }

        return BinaryOperationExpressionBindNode(left, binder.operation, right, binder.operationType, span)
    }

    private fun UnaryOperationExpressionNode.bindUnaryOperationExpression(context: BindingContext): ExpressionBindNode {
        val value = operand.bind(context).nullIfError() ?: return ErrorExpressionBindNode(span)
        val binder = UnaryOperationBinder(operation.type, value.type) ?: run {
            errors.reportInvalidUnaryOperation(operation.span, operation.text, value.type)
            return ErrorExpressionBindNode(span)
        }
        return UnaryOperationExpressionBindNode(binder.operation, value, binder.operationType, span)
    }

    private fun NoneExpressionNode.bindNoneExpression(): ExpressionBindNode {
        return NoneExpressionBindNode(span)
    }

    private fun LiteralExpressionNode.bindLiteralExpression(): ExpressionBindNode {
        return LiteralExpressionBindNode(value, value.bindLiteralType(), span)
    }

    private fun ValueType.bindSymbolType(): BaseType {
        return when (this) {
            is ValueType.Function ->
                FunctionType(types.map { it.bindSymbolType() }, returnType.bindSymbolType())
            is ValueType.Tuple ->
                TupleType(types.map { it.bindSymbolType() })
            is ValueType.Union ->
                UnionType(types.map { it.bindSymbolType() })
            is ValueType.List ->
                ListType(type.bindSymbolType())
            is ValueType.Set ->
                SetType(type.bindSymbolType())
            is ValueType.Map ->
                MapType(keyType.bindSymbolType(), valueType.bindSymbolType())
            is ValueType.Parenthesised -> {
                var current = type
                while (current is ValueType.Parenthesised)
                    current = current.type
                current.bindSymbolType()
            }
            is ValueType.Normal -> when (type.text.isNotEmpty()) {
                true -> symbolsTable.getType(type.text)
                    ?: ErrorType.also { errors.reportUndefinedType(span, type.text) }
                else -> ErrorType
            }
        }
    }

    private fun checkReturnPaths(root: StatementBindNode, type: BaseType): Boolean {
        return checkReturnPathsInternal(root, type) || type == ErrorType || type == UnitType
    }

    private fun checkReturnPaths(statements: List<StatementBindNode>, type: BaseType): Boolean {
        return checkReturnPathsInternal(statements, type) || type == ErrorType || type == UnitType
    }

    private fun checkReturnPathsInternal(node: StatementBindNode, type: BaseType): Boolean {

        when (node) {
            is ExpressionStatementBindNode -> return checkReturnPathsInternal(node.expression, type)
            is VariableStatementBindNode -> return node.value.type == NothingType
            is DoWhileStatementBindNode -> checkReturnPathsInternal(node.block, type)
            is BlockStatementBindNode -> return checkReturnPathsInternal(node.statements, type)
            is DeferStatementBindNode -> checkReturnPathsInternal(node.statement, type)
            is WhileStatementBindNode -> checkReturnPathsInternal(node.block, type)
            is IfStatementBindNode -> {
                val ifResult = checkReturnPathsInternal(node.ifBlock, type)
                val elseResult = node.elseBlock?.let { checkReturnPathsInternal(it, type) } ?: false
                return ifResult && elseResult
            }
        }

        return false
    }

    private fun checkReturnPathsInternal(statements: List<StatementBindNode>, type: BaseType): Boolean {
        var result = false
        val size = statements.size
        var i = 0
        while (i < size) {
            val statement = statements[i]
            result = result || checkReturnPathsInternal(statement, type)
            if (result && i < size - 1) break
            i++
        }
        for (index in i + 1 until size) {
            val statement = statements[index]
            checkReturnPathsInternal(statement, type)
            errors.reportUnreachedStatement(statement.span)
        }
        return result
    }

    private fun checkReturnPathsInternal(expression: ExpressionBindNode, type: BaseType): Boolean {
        return when (expression) {
            is RecordInitExpressionBindNode -> expression.arguments.any {
                it.type == NothingType || checkReturnPathsInternal(it, type)
            }
            is CallExpressionBindNode -> expression.arguments.any {
                it.type == NothingType || checkReturnPathsInternal(it, type)
            }
            is AssignmentExpressionBindNode -> expression.value.type == NothingType
            is SetIndexedExpressionBindNode -> expression.value.type == NothingType
            is SetFieldExpressionBindNode -> expression.value.type == NothingType
            is TryExpressionBindNode -> expression.expression.type == NothingType
            is ReturnExpressionBindNode -> {
                when (val value = expression.value) {
                    null -> if (type != UnitType && type != ErrorType)
                        errors.reportMissingReturnValue(expression.span, type)
                    else -> if (!value.type.assignableTo(type))
                        errors.reportWrongReturnValueType(expression.span, type, value.type)
                }
                true
            }
            else -> expression.type == NothingType
        }
    }

    private inline fun <T> List<T>.generalType(what: T.() -> BaseType, span: Span): BaseType {
        return firstOrNull()?.what()?.let { type ->
            if (any { it.what().assignableTo(type).not() }) {
                val isAnyNone = any { it.what().isNoneable() }
                if (isAnyNone) BuiltIn.ANY_NONE_UNION else AnyType
            } else type
        } ?: run { errors.reportCannotInferType(span); ErrorType }
    }
}

private inline fun ExpressionBindNode.nullIfError() = if (isError()) null else this

private inline fun BaseType.isNoneable() = NoneType.assignableTo(this)

private fun Any.bindLiteralType(): Primitive {
    return when (this) {
        is Boolean -> BooleanType
        is Double -> DoubleType
        is String -> StringType
        is Float -> FloatType
        is Char -> CharType
        is Long -> LongType
        is Int -> IntType
        else -> throw IllegalStateException()
    }
}

private data class BindingContext(
    internal val expectedType: BaseType? = null
) {
    companion object {
        internal val Empty = BindingContext()
    }
}

private sealed class FunctionState {
    internal object InDefer : FunctionState()
    internal object Valid : FunctionState()
}

private sealed class LoopState {
    internal object InFunction : LoopState()
    internal object InDefer : LoopState()
    internal object Invalid : LoopState()
    internal object Valid : LoopState()
}

private class ScopeTracker {

    internal val scopes = stackOf<Scope>()

    internal inline fun <R> deferScope(block: () -> R): R = try {
        scopes.push(Scope.Defer)
        block()
    } finally {
        scopes.pop()
    }

    internal inline fun <R> loopScope(block: () -> R): R = try {
        scopes.push(Scope.Loop)
        block()
    } finally {
        scopes.pop()
    }

    internal inline fun <R> funScope(block: () -> R): R = try {
        scopes.push(Scope.Function)
        block()
    } finally {
        scopes.pop()
    }

    internal fun inLoopState(): LoopState {
        if (inScope(Scope.Loop)) {
            when (scopes.peekOrNull()) {
                Scope.Function -> return LoopState.InFunction
                Scope.Defer -> return LoopState.InDefer
                Scope.Loop -> return LoopState.Valid
            }
        }
        return LoopState.Invalid
    }

    internal fun inFunctionState(): FunctionState {
        if (inScope(Scope.Function) && scopes.peekOrNull() == Scope.Defer)
            return FunctionState.InDefer
        return FunctionState.Valid
    }

    private fun inScope(scope: Scope): Boolean {
        scopes.forEachReversed {
            if (it == scope) return true
        }
        return false
    }

    private sealed class Scope {
        internal object Function : Scope()
        internal object Defer : Scope()
        internal object Loop : Scope()
    }
}
