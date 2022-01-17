package playscript;

/**
 * 先把函数和变量做出来，现在加入类的话有可能会容易乱
 * 主要是对变量和函数的一个处理
 */
public class TypeResolver extends PlayScriptBaseListener{

    private AnnotatedTree at = null;
    // 是否需要将变量添加到符号表
    private boolean enterLocalVariable = false;


    public TypeResolver(AnnotatedTree at) {
        this.at = at;
    }

    public TypeResolver(AnnotatedTree at,boolean enterLocalVariable){
        this.at = at;
        this.enterLocalVariable = enterLocalVariable;
    }


    // int b = 10 离开变量定义片段
    @Override
    public void exitVariableDeclarators(PlayScriptParser.VariableDeclaratorsContext ctx) {
        Scope scope = at.enclosingScopeOfNode(ctx);

        //Aaaaaaaaaaayou同学请看这里。
        if (enterLocalVariable ){
            // 设置变量类型
            Type type = (Type) at.typeOfNode.get(ctx.typeType());

            for (PlayScriptParser.VariableDeclaratorContext child : ctx.variableDeclarator()) {
                Variable variable = (Variable) at.symbolOfNode.get(child.variableDeclaratorId());
                variable.type = type;
            }
        }
    }


    /**
     * 对变量进行解析
     * @param ctx
     */
    @Override
    public void exitPrimitiveType(PlayScriptParser.PrimitiveTypeContext ctx) {
        Type type = null;

        if (ctx.BOOLEAN() != null) {
            type = PrimitiveType.Boolean;
        } else if (ctx.INT() != null) {
            type = PrimitiveType.Integer;
        } else if (ctx.LONG() != null) {
            type = PrimitiveType.Long;
        } else if (ctx.FLOAT() != null) {
            type = PrimitiveType.Float;
        } else if (ctx.DOUBLE() != null) {
            type = PrimitiveType.Double;
        } else if (ctx.BYTE() != null) {
            type = PrimitiveType.Byte;
        } else if (ctx.SHORT() != null) {
            type = PrimitiveType.Short;
        } else if (ctx.CHAR() != null) {
            type = PrimitiveType.Char;
        }else if (ctx.STRING() != null) {
            type = PrimitiveType.String;
        }

        at.typeOfNode.put(ctx,type);
    }

    /**
     * 设置函数返回值
     */
    @Override
    public void exitFunctionDeclaration(PlayScriptParser.FunctionDeclarationContext ctx) {
        FunctionScope function = (FunctionScope) at.node2Scope.get(ctx);

        // 对作用域进行赋值，将返回值添加到函数作用域中
        if (ctx.typeTypeOrVoid() != null){
            function.returnType = at.typeOfNode.get(ctx.typeTypeOrVoid());
        }

        /**
         * 函数查重
         */


    }

    /**
     * 这里就是把变量名都放到了 symbolOfNode
     * @param ctx
     */
    @Override
    public void enterVariableDeclaratorId(PlayScriptParser.VariableDeclaratorIdContext ctx) {
        String idName = ctx.IDENTIFIER().getText();
        // 获取上一个节点的作用域，寻找当前变量应该在的作用域
        // 每个变量都需要知道自己属于哪个作用域
        Scope scope = at.enclosingScopeOfNode(ctx);

        /**
         * 如果当前节点的父节点是 FormalParameterContext 那么说明当前节点的变量是我们所需要的
         */
        if (ctx.parent instanceof PlayScriptParser.FormalParameterContext || enterLocalVariable){
            // 我们就需要去创建一个参数变量
            Variable variable = new Variable(idName,scope,ctx);
            // 暂时还不知道这个 addSymbol 是做什么的
            scope.addSymbol(variable);
            // 这一步才是最关键的就是把变量放到作用域里
            at.symbolOfNode.put(ctx,variable);
        }
    }


    /**
     * 主要解析条件中的 int a 部分
     * func void demo(int a){
     *
     * }
     * 有点理解了，每个作用域都通过一个类对象来存储，将一些信息都添加到类对象中
     */
    @Override
    public void exitFormalParameter(PlayScriptParser.FormalParameterContext ctx) {
        Type type = at.typeOfNode.get(ctx.typeType());
        // 根据变量名去寻找
        Variable variable = (Variable) at.symbolOfNode.get(ctx.variableDeclaratorId());
        variable.type = type;

        // 寻找当前节点的上一个节点的作用域，说白了就是看当前这个是在哪个作用域下面的
        Scope scope = at.enclosingScopeOfNode(ctx);
        // 如果是在 函数作用域下的话就添加
        if (scope instanceof FunctionScope){
            ((FunctionScope) scope).parameters.add(variable);
        }
    }


    /**
     * exit 相当于就是当前节点已经遍历完了，所以这时已经可以知道这个树节点是否是void了
     * @param ctx
     */
    @Override
    public void exitTypeTypeOrVoid(PlayScriptParser.TypeTypeOrVoidContext ctx) {
        if (ctx.VOID() != null){
            at.typeOfNode.put(ctx,VoidType.instance());
            // 如果不为 null 那么就根据原来的进行获取
        }else if (ctx.typeType() != null){
            at.typeOfNode.put(ctx,at.typeOfNode.get(ctx.typeType()));
        }
    }


    // 为什么要把下级的属性注册到本级？ 有点不知道所以然
    @Override
    public void exitTypeType(PlayScriptParser.TypeTypeContext ctx) {
        // 冒泡，将下级的属性标注在本级
        if (ctx.classOrInterfaceType() != null) {
            Type type = (Type) at.typeOfNode.get(ctx.classOrInterfaceType());
            at.typeOfNode.put(ctx, type);
        } else if (ctx.functionType() != null) {
            Type type = (Type) at.typeOfNode.get(ctx.functionType());
            at.typeOfNode.put(ctx, type);
        } else if (ctx.primitiveType() != null) {
            Type type = (Type) at.typeOfNode.get(ctx.primitiveType());
            at.typeOfNode.put(ctx, type);
        }

    }




}
