package playscript;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * 利用 visitor 来计算结果
 * 那么分析反正也不是一朝一夕的... 其实就是针对树的 各个节点来进行整理，放到对应的变量表里
 */

@SuppressWarnings("all")
public class ASTEvaluator extends PlayScriptBaseVisitor<Object>{

    AnnotatedTree at;

    public ASTEvaluator(AnnotatedTree at) {
        this.at = at;
    }

    protected boolean traceStackFrame = false;

    protected boolean traceFunctionCall = false;

    private Stack<StackFrame> stack = new Stack<StackFrame>();

    private void pushStack(StackFrame frame) {
        if (stack.size() > 0) {
            //从栈顶到栈底依次查找
            for (int i = stack.size()-1; i>0; i--){
                StackFrame f = stack.get(i);
                // 同级
                if (f.scope.enclosingScope == frame.scope.enclosingScope){
                    frame.parentFrame = f.parentFrame;
                    break;
                }
                // 建立父子关系
                else if (f.scope == frame.scope.enclosingScope){
                    frame.parentFrame = f;
                    break;
                }
            }
            if (frame.parentFrame == null){
                frame.parentFrame = stack.peek();
            }
        }

        stack.push(frame);

        if (traceStackFrame){
            dumpStackFrame();
        }
    }

    private void popStack(){
        stack.pop();
    }

    private void dumpStackFrame(){
        System.out.println("\nStack Frames ----------------");
        for (StackFrame frame : stack){
            System.out.println(frame);
        }
        System.out.println("-----------------------------\n");
    }

    /**
     * 主要是来存放变量的数值
     */
    private final class MyLValue implements LValue {
        private Variable variable;
        private PlayObject valueContainer; // 被用来保护本地变量

        public MyLValue(PlayObject valueContainer, Variable variable) {
            this.valueContainer = valueContainer;
            this.variable = variable;
        }

        @Override
        public Object getValue() {
            //对于this或super关键字，直接返回这个对象，应该是ClassObject

            return valueContainer.getValue(variable);
        }

        @Override
        public void setValue(Object value) {
            valueContainer.setValue(variable, value);

            //如果variable是函数型变量，那改变functionObject.receiver
            if (value instanceof FunctionObject){
                ((FunctionObject) value).receiver = (Variable)variable;
            }
        }

        @Override
        public Variable getVariable() {
            return variable;
        }

        @Override
        public String toString() {
            return "LValue of " + variable.name + " : " + getValue();
        }

        @Override
        public PlayObject getValueContainer() {
            return valueContainer;
        }
    }

    // 这里没有找到变量
    public LValue getLValue(Variable variable) {
        StackFrame f = stack.peek();

        PlayObject valueContainer = null;
        while (f != null) {
            if (f.scope.containsSymbol(variable)) { //对于对象来说，会查找所有父类的属性
                valueContainer = f.object;
                break;
            }
            f = f.parentFrame;
        }

        //通过正常的作用域找不到，就从闭包里找
        //原理：PlayObject中可能有一些变量，其作用域跟StackFrame.scope是不同的。
        if (valueContainer == null){
            f = stack.peek();
            while (f != null) {
                if (f.contains(variable)) {
                    valueContainer = f.object;
                    break;
                }
                f = f.parentFrame;
            }
        }

        MyLValue lvalue = new MyLValue(valueContainer, variable);

        return lvalue;
    }

    // 运算
    private Object add(Object obj1, Object obj2, Type targetType) {
        Object rtn = null;
        if (targetType == PrimitiveType.String) {
            rtn = String.valueOf(obj1) + String.valueOf(obj2);
        } else if (targetType == PrimitiveType.Integer) {
            rtn = ((Number) obj1).intValue() + ((Number) obj2).intValue();
        } else if (targetType == PrimitiveType.Float) {
            rtn = ((Number) obj1).floatValue() + ((Number) obj2).floatValue();
        } else if (targetType == PrimitiveType.Long) {
            rtn = ((Number) obj1).longValue() + ((Number) obj2).longValue();
        } else if (targetType == PrimitiveType.Double) {
            rtn = ((Number) obj1).doubleValue() + ((Number) obj2).doubleValue();
        } else if (targetType == PrimitiveType.Short) {
            rtn = ((Number) obj1).shortValue() + ((Number) obj2).shortValue();
        }
        else {
            System.out.println("unsupported add operation");
        }

        return rtn;
    }
    private Object minus(Object obj1, Object obj2, Type targetType) {
        Object rtn = null;
        if (targetType == PrimitiveType.Integer) {
            rtn = ((Number) obj1).intValue() - ((Number) obj2).intValue();
        } else if (targetType == PrimitiveType.Float) {
            rtn = ((Number) obj1).floatValue() - ((Number) obj2).floatValue();
        } else if (targetType == PrimitiveType.Long) {
            rtn = ((Number) obj1).longValue() - ((Number) obj2).longValue();
        } else if (targetType == PrimitiveType.Double) {
            rtn = ((Number) obj1).doubleValue() - ((Number) obj2).doubleValue();
        } else if (targetType == PrimitiveType.Short) {
            rtn = ((Number) obj1).shortValue() - ((Number) obj2).shortValue();
        }

        return rtn;
    }

    private Object mul(Object obj1, Object obj2, Type targetType) {
        Object rtn = null;
        if (targetType == PrimitiveType.Integer) {
            rtn = ((Number) obj1).intValue() * ((Number) obj2).intValue();
        } else if (targetType == PrimitiveType.Float) {
            rtn = ((Number) obj1).floatValue() * ((Number) obj2).floatValue();
        } else if (targetType == PrimitiveType.Long) {
            rtn = ((Number) obj1).longValue() * ((Number) obj2).longValue();
        } else if (targetType == PrimitiveType.Double) {
            rtn = ((Number) obj1).doubleValue() * ((Number) obj2).doubleValue();
        } else if (targetType == PrimitiveType.Short) {
            rtn = ((Number) obj1).shortValue() * ((Number) obj2).shortValue();
        }

        return rtn;
    }

    private Object div(Object obj1, Object obj2, Type targetType) {
        Object rtn = null;
        if (targetType == PrimitiveType.Integer) {
            rtn = ((Number) obj1).intValue() / ((Number) obj2).intValue();
        } else if (targetType == PrimitiveType.Float) {
            rtn = ((Number) obj1).floatValue() / ((Number) obj2).floatValue();
        } else if (targetType == PrimitiveType.Long) {
            rtn = ((Number) obj1).longValue() / ((Number) obj2).longValue();
        } else if (targetType == PrimitiveType.Double) {
            rtn = ((Number) obj1).doubleValue() / ((Number) obj2).doubleValue();
        } else if (targetType == PrimitiveType.Short) {
            rtn = ((Number) obj1).shortValue() / ((Number) obj2).shortValue();
        }

        return rtn;
    }

    private Boolean EQ(Object obj1, Object obj2, Type targetType) {
        Boolean rtn = null;
        if (targetType == PrimitiveType.Integer) {
            rtn = ((Number) obj1).intValue() == ((Number) obj2).intValue();
        } else if (targetType == PrimitiveType.Float) {
            rtn = ((Number) obj1).floatValue() == ((Number) obj2).floatValue();
        } else if (targetType == PrimitiveType.Long) {
            rtn = ((Number) obj1).longValue() == ((Number) obj2).longValue();
        } else if (targetType == PrimitiveType.Double) {
            rtn = ((Number) obj1).doubleValue() == ((Number) obj2).doubleValue();
        } else if (targetType == PrimitiveType.Short) {
            rtn = ((Number) obj1).shortValue() == ((Number) obj2).shortValue();
        }
        //对于对象实例、函数，直接比较对象引用
        else {
            rtn = obj1 == obj2;
        }

        return rtn;
    }

    private Object GE(Object obj1, Object obj2, Type targetType) {
        Object rtn = null;
        if (targetType == PrimitiveType.Integer) {
            rtn = ((Number) obj1).intValue() >= ((Number) obj2).intValue();
        } else if (targetType == PrimitiveType.Float) {
            rtn = ((Number) obj1).floatValue() >= ((Number) obj2).floatValue();
        } else if (targetType == PrimitiveType.Long) {
            rtn = ((Number) obj1).longValue() >= ((Number) obj2).longValue();
        } else if (targetType == PrimitiveType.Double) {
            rtn = ((Number) obj1).doubleValue() >= ((Number) obj2).doubleValue();
        } else if (targetType == PrimitiveType.Short) {
            rtn = ((Number) obj1).shortValue() >= ((Number) obj2).shortValue();
        }

        return rtn;
    }

    private Object GT(Object obj1, Object obj2, Type targetType) {
        Object rtn = null;
        if (targetType == PrimitiveType.Integer) {
            rtn = ((Number) obj1).intValue() > ((Number) obj2).intValue();
        } else if (targetType == PrimitiveType.Float) {
            rtn = ((Number) obj1).floatValue() > ((Number) obj2).floatValue();
        } else if (targetType == PrimitiveType.Long) {
            rtn = ((Number) obj1).longValue() > ((Number) obj2).longValue();
        } else if (targetType == PrimitiveType.Double) {
            rtn = ((Number) obj1).doubleValue() > ((Number) obj2).doubleValue();
        } else if (targetType == PrimitiveType.Short) {
            rtn = ((Number) obj1).shortValue() > ((Number) obj2).shortValue();
        }

        return rtn;
    }

    private Object LE(Object obj1, Object obj2, Type targetType) {
        Object rtn = null;
        if (targetType == PrimitiveType.Integer) {
            rtn = ((Number) obj1).intValue() <= ((Number) obj2).intValue();
        } else if (targetType == PrimitiveType.Float) {
            rtn = ((Number) obj1).floatValue() <= ((Number) obj2).floatValue();
        } else if (targetType == PrimitiveType.Long) {
            rtn = ((Number) obj1).longValue() <= ((Number) obj2).longValue();
        } else if (targetType == PrimitiveType.Double) {
            rtn = ((Number) obj1).doubleValue() <= ((Number) obj2).doubleValue();
        } else if (targetType == PrimitiveType.Short) {
            rtn = ((Number) obj1).shortValue() <= ((Number) obj2).shortValue();
        }

        return rtn;
    }

    private Object LT(Object obj1, Object obj2, Type targetType) {
        Object rtn = null;
        if (targetType == PrimitiveType.Integer) {
            rtn = ((Number) obj1).intValue() < ((Number) obj2).intValue();
        } else if (targetType == PrimitiveType.Float) {
            rtn = ((Number) obj1).floatValue() < ((Number) obj2).floatValue();
        } else if (targetType == PrimitiveType.Long) {
            rtn = ((Number) obj1).longValue() < ((Number) obj2).longValue();
        } else if (targetType == PrimitiveType.Double) {
            rtn = ((Number) obj1).doubleValue() < ((Number) obj2).doubleValue();
        } else if (targetType == PrimitiveType.Short) {
            rtn = ((Number) obj1).shortValue() < ((Number) obj2).shortValue();
        }

        return rtn;
    }


    // visit 各个节点
    public Object visitBlock(PlayScriptParser.BlockContext ctx) {
        // 从节点中取出对应的 block 作用域
        BlockScope blockScope = (BlockScope) at.node2Scope.get(ctx);
        // 把作用域压入到栈中
        if(blockScope != null){
            // 创建一个栈帧 然后进行压入
            StackFrame stackFrame = new StackFrame(blockScope);
            pushStack(stackFrame);
        }
        // 处理块中的结构
        Object rtn = visitBlockStatements(ctx.blockStatements());

        if (blockScope !=null) {
            popStack();
        }

        return rtn;

    }

    /**
     * BlockStatements 下有多个 BlockStatement
     * @param ctx
     * @return
     */
    @Override // 处理 block 块中的代码
    public Object visitBlockStatements(PlayScriptParser.BlockStatementsContext ctx) {
        Object rtn = null;
        for (PlayScriptParser.BlockStatementContext child : ctx.blockStatement()) { //遍历子节点
            rtn = visitBlockStatement(child);

            //如果返回的是break，那么不执行下面的语句
            if (rtn instanceof BreakObject){
                break;
            }

            else if (rtn instanceof ReturnObject){
                break;
            }
        }
        return rtn;
    }

    // 这里是不是缺了一个 function，
    @Override
    public Object visitBlockStatement(PlayScriptParser.BlockStatementContext ctx) {
        Object rtn = null;
        // 如果是表达式
        if (ctx.variableDeclarators() != null) {
            rtn = visitVariableDeclarators(ctx.variableDeclarators());
        } else if (ctx.statement() != null) {
            rtn = visitStatement(ctx.statement());
        }
        return rtn;
    }

    /**
     * tmd 变量定义没加
     * @param ctx
     * @return
     */
    @Override
    public Object visitVariableDeclarators(PlayScriptParser.VariableDeclaratorsContext ctx) { // 处理变量表达式
        Object rtn = null;
        // Integer typeType = (Integer)visitTypeType(ctx.typeType()); //后面要利用这个类型信息
        for (PlayScriptParser.VariableDeclaratorContext child : ctx.variableDeclarator()) {
            rtn = visitVariableDeclarator(child);
        }
        return rtn;
    }

    /**
     * 变量赋值过程中的设置
     * @param ctx
     * @return
     */
    @Override
    public Object visitVariableDeclarator(PlayScriptParser.VariableDeclaratorContext ctx) {
        Object rtn = null;
        LValue lValue = (LValue) visitVariableDeclaratorId(ctx.variableDeclaratorId());
        if (ctx.variableInitializer() != null) {
            rtn = visitVariableInitializer(ctx.variableInitializer());
            if (rtn instanceof LValue) {
                rtn = ((LValue) rtn).getValue();
            }
            lValue.setValue(rtn);
        }
        return rtn;
    }

    // 程序的开头
    @Override
    public Object visitProg(PlayScriptParser.ProgContext ctx) {
        Object rtn = null;
        pushStack(new StackFrame((BlockScope) at.node2Scope.get(ctx)));

        rtn = visitBlockStatements(ctx.blockStatements());

        popStack();

        return rtn;
    }

    @Override
    public Object visitFunctionCall(PlayScriptParser.FunctionCallContext ctx) {
        Object rtn = null;
        // 调用函数的地方
        Symbol symbol = at.symbolOfNode.get(ctx);

        FunctionObject functionObject = getFuntionObject(ctx);
        FunctionScope functionScope = functionObject.function;

        List<Object> paramValues = calcParamValues(ctx);

        if (traceFunctionCall){
            System.out.println("\n>>FunctionCall : " + ctx.getText());
        }

        rtn = functionCall(functionObject, paramValues);

        return rtn;

    }


    private FunctionObject getFuntionObject(PlayScriptParser.FunctionCallContext ctx){
        if (ctx.IDENTIFIER() == null) return null;

        FunctionScope function = null;
        FunctionObject functionObject = null;

        Symbol symbol = at.symbolOfNode.get(ctx);
        //函数类型的变量
        if (symbol instanceof Variable) {
            Variable variable = (Variable) symbol;
            LValue lValue = getLValue(variable);
            Object value = lValue.getValue();
            if (value instanceof FunctionObject) {
                functionObject = (FunctionObject) value;
                function = functionObject.function;
            }
        }
        //普通函数
        else if (symbol instanceof FunctionScope) {
            function = (FunctionScope) symbol;
        }
        //报错
        else {
            String functionName = ctx.IDENTIFIER().getText();  //这是调用时的名称，不一定是真正的函数名，还可能是函数类型的变量名
            return null;
        }

        if (functionObject == null) {
            functionObject = new FunctionObject(function);
        }

        return functionObject;
    }

    private List<Object> calcParamValues(PlayScriptParser.FunctionCallContext ctx){
        List<Object> paramValues = new LinkedList<Object>();
        if (ctx.expressionList() != null) {
            for (PlayScriptParser.ExpressionContext exp : ctx.expressionList().expression()) {
                Object value = visitExpression(exp);
                if (value instanceof LValue) {
                    value = ((LValue) value).getValue();
                }
                paramValues.add(value);
            }
        }
        return paramValues;
    }

    private Object functionCall(FunctionObject functionObject, List<Object> paramValues){
        Object rtn = null;

        //添加函数的栈桢
        StackFrame functionFrame = new StackFrame(functionObject);
        pushStack(functionFrame);

        // 给参数赋值，这些值进入functionFrame
        PlayScriptParser.FunctionDeclarationContext functionCode = (PlayScriptParser.FunctionDeclarationContext) functionObject.function.ctx;
        if (functionCode.formalParameters().formalParameterList() != null) {
            for (int i = 0; i < functionCode.formalParameters().formalParameterList().formalParameter().size(); i++) {
                PlayScriptParser.FormalParameterContext param = functionCode.formalParameters().formalParameterList().formalParameter(i);
                LValue lValue = (LValue) visitVariableDeclaratorId(param.variableDeclaratorId());
                lValue.setValue(paramValues.get(i));
            }
        }

        // 调用函数（方法）体
        rtn = visitFunctionDeclaration(functionCode);

        // 弹出StackFrame
        popStack(); //函数的栈桢

        //如果由一个return语句返回，真实返回值会被封装在一个ReturnObject里。
        if (rtn instanceof ReturnObject){
            rtn = ((ReturnObject)rtn).returnValue;
        }

        return rtn;
    }

    @Override
    public Object visitFunctionDeclaration(PlayScriptParser.FunctionDeclarationContext ctx) {
        return visitFunctionBody(ctx.functionBody());
    }

    @Override
    public Object visitFunctionBody(PlayScriptParser.FunctionBodyContext ctx) {
        Object rtn = null;
        if (ctx.block() != null) {
            rtn = visitBlock(ctx.block());
        }
        return rtn;
    }

    @Override
    public Object visitClassBody(PlayScriptParser.ClassBodyContext ctx) {
        Object rtn = null;
        for (PlayScriptParser.ClassBodyDeclarationContext child : ctx.classBodyDeclaration()) {
            rtn = visitClassBodyDeclaration(child);
        }
        return rtn;
    }

    @Override
    public Object visitClassBodyDeclaration(PlayScriptParser.ClassBodyDeclarationContext ctx) {
        Object rtn = null;
        if (ctx.memberDeclaration() != null) {
            rtn = visitMemberDeclaration(ctx.memberDeclaration());
        }
        return rtn;
    }

    @Override
    public Object visitMemberDeclaration(PlayScriptParser.MemberDeclarationContext ctx) {
        Object rtn = null;
        if (ctx.fieldDeclaration() != null) {
            rtn = visitFieldDeclaration(ctx.fieldDeclaration());
        }
        return rtn;
    }

    @Override
    public Object visitFieldDeclaration(PlayScriptParser.FieldDeclarationContext ctx) {
        Object rtn = null;
        if (ctx.variableDeclarators() != null) {
            rtn = visitVariableDeclarators(ctx.variableDeclarators());
        }
        return rtn;
    }


    /**
     * 表达式计算
     */
    @Override
    public Object visitExpression(PlayScriptParser.ExpressionContext ctx) {
        Object rtn = null;
        if (ctx.bop != null && ctx.expression().size() >= 2) {
            Object left = visitExpression(ctx.expression(0));
            Object right = visitExpression(ctx.expression(1));
            Object leftObject = left;
            Object rightObject = right;

            if (left instanceof LValue) {
                leftObject = ((LValue) left).getValue();}

            if (right instanceof LValue) {
                rightObject = ((LValue) right).getValue();
            }

            //本节点期待的数据类型
            Type type = at.typeOfNode.get(ctx);

            //左右两个子节点的类型
            Type type1 = at.typeOfNode.get(ctx.expression(0));
            Type type2 = at.typeOfNode.get(ctx.expression(1));

            switch (ctx.bop.getType()) {
                case PlayScriptParser.ADD:
                    rtn = add(leftObject, rightObject, type);
                    break;
                case PlayScriptParser.SUB:
                    rtn = minus(leftObject, rightObject, type);
                    break;
                case PlayScriptParser.MUL:
                    rtn = mul(leftObject, rightObject, type);
                    break;
                case PlayScriptParser.DIV:
                    rtn = div(leftObject, rightObject, type);
                    break;
                case PlayScriptParser.EQUAL:
                    rtn = EQ(leftObject, rightObject, PrimitiveType.getUpperType(type1, type2));
                    break;
                case PlayScriptParser.NOTEQUAL:
                    rtn = !EQ(leftObject, rightObject, PrimitiveType.getUpperType(type1, type2));
                    break;
                case PlayScriptParser.LE:
                    rtn = LE(leftObject, rightObject, PrimitiveType.getUpperType(type1, type2));
                    break;
                case PlayScriptParser.LT:
                    rtn = LT(leftObject, rightObject, PrimitiveType.getUpperType(type1, type2));
                    break;
                case PlayScriptParser.GE:
                    rtn = GE(leftObject, rightObject, PrimitiveType.getUpperType(type1, type2));
                    break;
                case PlayScriptParser.GT:
                    rtn = GT(leftObject, rightObject, PrimitiveType.getUpperType(type1, type2));
                    break;

                case PlayScriptParser.AND:
                    rtn = (Boolean) leftObject && (Boolean) rightObject;
                    break;
                case PlayScriptParser.OR:
                    rtn = (Boolean) leftObject || (Boolean) rightObject;
                    break;
                case PlayScriptParser.ASSIGN:
                    if (left instanceof LValue) {
                        //((LValue) left).setValue(right);
                        ((LValue) left).setValue(rightObject);
                        rtn = right;
                    } else {
                        System.out.println("Unsupported feature during assignment");
                    }
                    break;

                default:
                    break;
            }
        } else if (ctx.bop != null && ctx.bop.getType() == PlayScriptParser.DOT) {
            // 此语法是左递归的，算法体现这一点
            Object leftObject = visitExpression(ctx.expression(0));
            if (leftObject instanceof LValue) {
                Object value = ((LValue) leftObject).getValue();
            } else {
                System.out.println("Expecting an Object Reference");
            }

        } else if (ctx.primary() != null) {
            rtn = visitPrimary(ctx.primary());
        }

        // 后缀运算，例如：i++ 或 i--
        else if (ctx.postfix != null) {
            Object value = visitExpression(ctx.expression(0));
            LValue lValue = null;
            Type type = at.typeOfNode.get(ctx.expression(0));
            if (value instanceof LValue) {
                lValue = (LValue) value;
                value = lValue.getValue();
            }
            switch (ctx.postfix.getType()) {
                case PlayScriptParser.INC:
                    if (type == PrimitiveType.Integer) {
                        lValue.setValue((Integer) value + 1);
                    } else {
                        lValue.setValue((Long) value + 1);
                    }
                    rtn = value;
                    break;
                case PlayScriptParser.DEC:
                    if (type == PrimitiveType.Integer) {
                        lValue.setValue((Integer) value - 1);
                    } else {
                        lValue.setValue((long) value - 1);
                    }
                    rtn = value;
                    break;
                default:
                    break;
            }
        }

        //前缀操作，例如：++i 或 --i
        else if (ctx.prefix != null) {
            Object value = visitExpression(ctx.expression(0));
            LValue lValue = null;
            Type type = at.typeOfNode.get(ctx.expression(0));
            if (value instanceof LValue) {
                lValue = (LValue) value;
                value = lValue.getValue();
            }
            switch (ctx.prefix.getType()) {
                case PlayScriptParser.INC:
                    if (type == PrimitiveType.Integer) {
                        rtn = (Integer) value + 1;
                    } else {
                        rtn = (Long) value + 1;
                    }
                    lValue.setValue(rtn);
                    break;
                case PlayScriptParser.DEC:
                    if (type == PrimitiveType.Integer) {
                        rtn = (Integer) value - 1;
                    } else {
                        rtn = (Long) value - 1;
                    }
                    lValue.setValue(rtn);
                    break;
                //!符号，逻辑非运算
                case PlayScriptParser.BANG:
                    rtn = !((Boolean) value);
                    break;
                default:
                    break;
            }
        } else if (ctx.functionCall() != null) {// functionCall
            rtn = visitFunctionCall(ctx.functionCall());
        }
        return rtn;
    }

    @Override
    public Object visitPrimary(PlayScriptParser.PrimaryContext ctx) {
        Object rtn = null;
        //字面量
        if (ctx.literal() != null) {
            rtn = visitLiteral(ctx.literal());
        }
        //变量
        else if (ctx.IDENTIFIER() != null) {
            Symbol symbol = at.symbolOfNode.get(ctx);
            if (symbol instanceof Variable) {
                rtn = getLValue((Variable) symbol);
            } else if (symbol instanceof FunctionScope) {
                FunctionObject obj = new FunctionObject((FunctionScope) symbol);
                rtn = obj;
            }
        }
        //括号括起来的表达式
        else if (ctx.expression() != null){
            rtn = visitExpression(ctx.expression());
        }
        //this

        return rtn;
    }

    @Override
    public Object visitLiteral(PlayScriptParser.LiteralContext ctx) {
        Object rtn = null;

        //整数
        if (ctx.integerLiteral() != null) {
            rtn = visitIntegerLiteral(ctx.integerLiteral());
        }

        //浮点数
        else if (ctx.floatLiteral() != null) {
            rtn = visitFloatLiteral(ctx.floatLiteral());
        }

        //布尔值
        else if (ctx.BOOL_LITERAL() != null) {
            if (ctx.BOOL_LITERAL().getText().equals("true")) {
                rtn = Boolean.TRUE;
            } else {
                rtn = Boolean.FALSE;
            }
        }

        //字符串
        else if (ctx.STRING_LITERAL() != null) {
            String withQuotationMark = ctx.STRING_LITERAL().getText();
            rtn = withQuotationMark.substring(1, withQuotationMark.length() - 1);
        }

        //单个的字符
        else if (ctx.CHAR_LITERAL() != null) {
            rtn = ctx.CHAR_LITERAL().getText().charAt(0);
        }

        //null字面量
        else if (ctx.NULL_LITERAL() != null) {
            rtn = NullObject.instance();
        }

        return rtn;
    }

    @Override
    public Object visitIntegerLiteral(PlayScriptParser.IntegerLiteralContext ctx) {
        Object rtn = null;
        if (ctx.DECIMAL_LITERAL() != null) {
            rtn = Integer.valueOf(ctx.DECIMAL_LITERAL().getText());
        }
        return rtn;
    }

    @Override
    public Object visitFloatLiteral(PlayScriptParser.FloatLiteralContext ctx) {
        return Float.valueOf(ctx.getText());
    }

    /**
     * 漏掉的
     * @param ctx
     * @return
     */
    @Override
    public Object visitVariableDeclaratorId(PlayScriptParser.VariableDeclaratorIdContext ctx) {
        Object rtn = null;
        Symbol symbol = at.symbolOfNode.get(ctx);
        rtn = getLValue((Variable) symbol);
        return rtn;
    }


    /**
     * 非常关键的一个函数
     */

    @Override
    public Object visitStatement(PlayScriptParser.StatementContext ctx){
        Object rtn = null;
        if (ctx.statementExpression != null) {
            rtn = visitExpression(ctx.statementExpression);
        } else if (ctx.IF() != null) {
            Boolean condition = (Boolean) visitParExpression(ctx.parExpression());
            if (Boolean.TRUE == condition) {
                rtn = visitStatement(ctx.statement(0));
            } else if (ctx.ELSE() != null) {
                rtn = visitStatement(ctx.statement(1));
            }
        }

        //while循环
        else if (ctx.WHILE() != null) {
            if (ctx.parExpression().expression() != null && ctx.statement(0) != null) {

                while (true) {
                    //每次循环都要计算一下循环条件
                    Boolean condition = true;
                    Object value = visitExpression(ctx.parExpression().expression());
                    if (value instanceof LValue) {
                        condition = (Boolean) ((LValue) value).getValue();
                    } else {
                        condition = (Boolean) value;
                    }

                    if (condition) {
                        //执行while后面的语句
                        if (condition) {
                            rtn = visitStatement(ctx.statement(0));

                            //break
                            if (rtn instanceof BreakObject){
                                rtn = null;  //清除BreakObject，也就是只跳出一层循环
                                break;
                            }
                            //return
                            else if (rtn instanceof ReturnObject){
                                break;
                            }
                        }
                    }
                    else{
                        break;
                    }
                }
            }

        }

        //for循环
        else if (ctx.FOR() != null) {
            // 添加StackFrame
            BlockScope scope = (BlockScope) at.node2Scope.get(ctx);
            StackFrame frame = new StackFrame(scope);
            // frame.parentFrame = stack.peek();
            pushStack(frame);

            PlayScriptParser.ForControlContext forControl = ctx.forControl();
            if (forControl.enhancedForControl() != null) {

            } else {
                // 初始化部分执行一次
                if (forControl.forInit() != null) {
                    rtn = visitForInit(forControl.forInit());
                }

                while (true) {
                    Boolean condition = true; // 如果没有条件判断部分，意味着一直循环
                    if (forControl.expression() != null) {
                        Object value = visitExpression(forControl.expression());
                        if (value instanceof LValue) {
                            condition = (Boolean) ((LValue) value).getValue();
                        } else {
                            condition = (Boolean) value;
                        }
                    }

                    if (condition) {
                        // 执行for的语句体
                        rtn = visitStatement(ctx.statement(0));

                        //处理break
                        if (rtn instanceof BreakObject){
                            rtn = null;
                            break;
                        }
                        //return
                        else if (rtn instanceof ReturnObject){
                            break;
                        }

                        // 执行forUpdate，通常是“i++”这样的语句。这个执行顺序不能出错。
                        if (forControl.forUpdate != null) {
                            visitExpressionList(forControl.forUpdate);
                        }
                    } else {
                        break;
                    }
                }
            }

            // 去掉StackFrame
            popStack();
        }

        //block
        else if (ctx.blockLabel != null) {
            rtn = visitBlock(ctx.blockLabel);

        }

        //break语句
        else if (ctx.BREAK() != null) {
            rtn = BreakObject.instance();
        }

        //return语句
        else if (ctx.RETURN() != null) {
            if (ctx.expression() != null) {
                rtn = visitExpression(ctx.expression());

                //return语句应该不需要左值   //TODO 取左值的场景需要优化，目前都是取左值。
                if (rtn instanceof LValue){
                    rtn = ((LValue)rtn).getValue();
                }

            }

            //把真实的返回值封装在一个ReturnObject对象里，告诉visitBlockStatements停止执行下面的语句
            rtn = new ReturnObject(rtn);
        }
        return rtn;
    }
}
