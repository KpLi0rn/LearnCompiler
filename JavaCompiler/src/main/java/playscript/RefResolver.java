package playscript;

import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.LinkedList;
import java.util.List;

/**
 * 消解主要有几个步骤
 * 返回值消解？函数类型消解，函数体消解
 * 最终消解的结果就是把对应的节点和类型对应起来
 * 这里我们主要有两个要做的，变量的消解，函数的消解 ，在调用函数的时候需要知道函数定义的地方
 */
public class RefResolver extends PlayScriptBaseListener{

    private AnnotatedTree at;

    // 把本地变量添加到符号表，符号表其实就是一个存放变量名和对应变量地址的地方，
    ParseTreeWalker typeResolverWalker = new ParseTreeWalker();
    TypeResolver localVariableEnter = null;

    public RefResolver(AnnotatedTree at){
        this.at = at;
        this.localVariableEnter = new TypeResolver(at,true);
    }


    @Override
    public void enterVariableDeclarators(PlayScriptParser.VariableDeclaratorsContext ctx) {
        // 获取节点对应的作用域
        Scope scope = at.enclosingScopeOfNode(ctx);
        if (scope instanceof BlockScope || scope instanceof FunctionScope){
            typeResolverWalker.walk(localVariableEnter, ctx);
        }
    }


    /**
     * 变量的消解主要是根据变量名选择找到对应的类型
     * 上一步相当于只将变量取出来了吧，比价variable只知道变量名
     * 但是现在我们需要把变量和对应的类型对照起来
     * 这里就是确认变量类型的
     * @param ctx
     */
    @Override
    public void exitPrimary(PlayScriptParser.PrimaryContext ctx) {
        Scope scope = at.enclosingScopeOfNode(ctx);
        Type type = null;

        if (ctx.IDENTIFIER() != null){
            String idName = ctx.IDENTIFIER().getText();
            // 然后先从变量池中进行寻找
            Variable variable = at.lookupVariable(scope,idName);
            if (variable == null){
                FunctionScope function = at.lookupFunction(scope, idName);
                if (function != null) {
                    at.symbolOfNode.put(ctx, function);
                    type = function;
                }
            }else {
                //
                at.symbolOfNode.put(ctx, variable);
                type = variable.type;
            }
        }
        else if (ctx.literal() != null) {
            type = at.typeOfNode.get(ctx.literal());
        }
        //括号里的表达式
        else if (ctx.expression() != null) {
            type = at.typeOfNode.get(ctx.expression());
        }
        at.typeOfNode.put(ctx,type);
    }


    /**
     * 获取函数参数
     * @param ctx
     * @return
     */
    private List<Type> getParamTypes(PlayScriptParser.FunctionCallContext ctx){
        List<Type> paramTypes = new LinkedList<Type>();
        if (ctx.expressionList() != null) {
            for (PlayScriptParser.ExpressionContext exp : ctx.expressionList().expression()) {
                Type type = at.typeOfNode.get(exp);
                paramTypes.add(type);
            }
        }
        return paramTypes;
    }


    /**
     * 函数调用的变量消解...
     * @param ctx
     */
    @Override
    public void exitFunctionCall(PlayScriptParser.FunctionCallContext ctx) {
        String idName = ctx.IDENTIFIER().getText();
        List<Type> ParamTypes = getParamTypes(ctx);
        // 寻找对应的作用域
        Scope scope = at.enclosingScopeOfNode(ctx);
        FunctionScope functionScope = at.lookupFunction(scope,idName,ParamTypes);
        // 不为 null 说明找到了对应参数的函数，是从 symbol 中进行寻找的
        if (functionScope != null){
            at.symbolOfNode.put(ctx,functionScope);
            at.typeOfNode.put(ctx,functionScope.returnType);
        }
    }



    //根据字面量来推断类型
    @Override
    public void exitLiteral(PlayScriptParser.LiteralContext ctx) {
        if (ctx.BOOL_LITERAL() != null) {
            at.typeOfNode.put(ctx, PrimitiveType.Boolean);
        } else if (ctx.CHAR_LITERAL() != null) {
            at.typeOfNode.put(ctx, PrimitiveType.Char);
        } else if (ctx.NULL_LITERAL() != null) {
            at.typeOfNode.put(ctx, PrimitiveType.Null);
        } else if (ctx.STRING_LITERAL() != null) {
            at.typeOfNode.put(ctx, PrimitiveType.String);
        } else if (ctx.integerLiteral() != null) {
            at.typeOfNode.put(ctx, PrimitiveType.Integer);
        } else if (ctx.floatLiteral() != null) {
            at.typeOfNode.put(ctx, PrimitiveType.Float);
        }
    }


    /**
     * 这个函数没加导致后面类型找不到, 应该是这里导致类型没对上
     * @param ctx
     */
    @Override
    public void exitExpression(PlayScriptParser.ExpressionContext ctx) {
        Type type = null;

        //变量引用冒泡： 如果下级是一个变量，往上冒泡传递，以便在点符号表达式中使用
        //也包括This和Super的冒泡
        if (ctx.primary() != null) {
            Symbol symbol = at.symbolOfNode.get(ctx.primary());
            at.symbolOfNode.put(ctx, symbol);
        }

        //类型推断和综合
        if (ctx.primary() != null) {
            type = at.typeOfNode.get(ctx.primary());
        } else if (ctx.functionCall() != null) {
            type = at.typeOfNode.get(ctx.functionCall());
        } else if (ctx.bop != null && ctx.expression().size() >= 2) {
            Type type1 = at.typeOfNode.get(ctx.expression(0));
            Type type2 = at.typeOfNode.get(ctx.expression(1));

            switch (ctx.bop.getType()) {
                case PlayScriptParser.ADD:
                    if (type1 == PrimitiveType.String || type2 == PrimitiveType.String){
                        type = PrimitiveType.String;
                    }
                    else if (type1 instanceof PrimitiveType && type2 instanceof PrimitiveType){
                        //类型“向上”对齐，比如一个int和一个float，取float
                        type = PrimitiveType.getUpperType(type1,type2);
                    }
                    break;
                case PlayScriptParser.SUB:
                case PlayScriptParser.MUL:
                case PlayScriptParser.DIV:
                    if (type1 instanceof PrimitiveType && type2 instanceof PrimitiveType){
                        //类型“向上”对齐，比如一个int和一个float，取float
                        type = PrimitiveType.getUpperType(type1,type2);
                    }
                    break;
                case PlayScriptParser.EQUAL:
                case PlayScriptParser.NOTEQUAL:
                case PlayScriptParser.LE:
                case PlayScriptParser.LT:
                case PlayScriptParser.GE:
                case PlayScriptParser.GT:
                case PlayScriptParser.AND:
                case PlayScriptParser.OR:
                case PlayScriptParser.BANG:
                    type = PrimitiveType.Boolean;
                    break;
                case PlayScriptParser.ASSIGN:
                case PlayScriptParser.ADD_ASSIGN:
                case PlayScriptParser.SUB_ASSIGN:
                case PlayScriptParser.MUL_ASSIGN:
                case PlayScriptParser.DIV_ASSIGN:
                case PlayScriptParser.AND_ASSIGN:
                case PlayScriptParser.OR_ASSIGN:
                case PlayScriptParser.XOR_ASSIGN:
                case PlayScriptParser.MOD_ASSIGN:
                case PlayScriptParser.LSHIFT_ASSIGN:
                case PlayScriptParser.RSHIFT_ASSIGN:
                case PlayScriptParser.URSHIFT_ASSIGN:
                    type = type1;
                    break;
            }
        }

        //类型冒泡
        at.typeOfNode.put(ctx, type);

    }

    @Override
    public void exitVariableInitializer(PlayScriptParser.VariableInitializerContext ctx) {
        if (ctx.expression() != null){
            at.typeOfNode.put(ctx, at.typeOfNode.get(ctx.expression()));
        }
    }

}
