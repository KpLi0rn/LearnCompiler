package playscript;


import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Stack;

/**
 * 类似监听的感觉,可以在进入节点和退出节点的过程中做事情
 * 这里不是每个节点都会创建对应的作用域的 ，这里是我们自己处理的，例如我们的表达式 block function 这种
 * enterFunctionDeclaration ClassDeclaration Class 等等
 */
@SuppressWarnings("all")
public class TypeAndScopeScanner extends PlayScriptBaseListener{

    private AnnotatedTree at = null;
    // 临时变量
    private Stack<Scope> scopeStack = new Stack<>();

    public TypeAndScopeScanner(AnnotatedTree ast){
        this.at = ast;
    }

    /**
     * 需要根据 node 取出作用域
     */
    public void pushScope(ParserRuleContext ctx, Scope scope){
        // node2Scope 属性将 ast 树上的节点和对应的作用域建立了联系
        at.node2Scope.put(ctx,scope);
        // 作用域对应的 ast 上的节点，暂时不知道干嘛用的
        scope.ctx = ctx;
        scopeStack.push(scope); // 压入当前堆栈中
    }

    /**
     * 扫描结束将 scopeStack 全部进行弹出
     */
    public void popScope(){
        scopeStack.pop();
    }


    public Scope currentScope(){
        if (scopeStack.size() > 0){
            return scopeStack.peek();
        }else {
            return null;
        }
    }
    /**
     * 程序刚开始的话就设置命名空间例如 package
     * 将当前的作用域与 AST 结合起来
     * 入口在这里,代表进入树结构,当进入 Prog 节点的时候可以触发
     * 同理离开的时候也会触发 exitProg
     * @param ctx
     */
    @Override
    public void enterProg(PlayScriptParser.ProgContext ctx) {
        // 这里是这个属于的作用域的范围，这里赋值应该是 null ，创建命名空间
        NameSpace scope = new NameSpace("", currentScope(), ctx); // 所属作用域
        at.nameSpace = scope; // 设置树的命名结构
        pushScope(ctx,scope); // 压入
    }

    /**
     * 扫描结束退出
     * 退出 Prog 节点
     * @param ctx
     */
    @Override
    public void exitProg(PlayScriptParser.ProgContext ctx) {
        popScope();
    }

    /**
     * 针对 Block Function Class 依次进行处理
     */
    @Override
    public void enterBlock(PlayScriptParser.BlockContext ctx) {
        // 如果父类不是函数体的话就创建，不加这个的话要创建两个
        if (!(ctx.parent instanceof PlayScriptParser.FunctionBodyContext)){
            BlockScope scope = new BlockScope();
            // 不懂这个添加的意义
//            currentScope().addSymbol(scope); // ? 为什么要这么做，在当前作用域下添加作用域？添加
            pushScope(ctx,scope);
        }
    }

    @Override
    public void exitBlock(PlayScriptParser.BlockContext ctx) {
        if (!(ctx.parent instanceof PlayScriptParser.FunctionBodyContext)) {
            popScope();
        }
    }

    /**
     * 进入 function
     * @param ctx
     */
    @Override
    public void enterFunctionDeclaration(PlayScriptParser.FunctionDeclarationContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        FunctionScope functionScope = new FunctionScope(name,currentScope(),ctx);
        at.types.add(functionScope);
        // ?
        currentScope().addSymbol(functionScope);
        pushScope(ctx,functionScope);
    }

    /**
     * 离开 function
     * @param ctx
     */
    @Override
    public void exitFunctionDeclaration(PlayScriptParser.FunctionDeclarationContext ctx) {
        popScope();
    }



    /**
     * 类待会在做有点小麻烦先把 func 做好
     * 进入 class 创建对应的 class 作用域 然后压到栈中
     * @param ctx
     */
//    @Override
//    public void exitClassDeclaration(PlayScriptParser.ClassDeclarationContext ctx) {
//        popScope();
//    }
//
//    /**
//     * 离开 class
//     * @param ctx
//     */
//    @Override
//    public void enterClassDeclaration(PlayScriptParser.ClassDeclarationContext ctx) {
//        String name = ctx.IDENTIFIER().getText();
//        ClassScope classScope = new ClassScope(name,ctx);
//
//        /**
//         * 先不管下面这些代码 尼玛创建好作用域就应该直接压入到栈
//         *         at.types.add(theClass);
//         *
//         *         if (at.lookupClass(currentScope(), idName) != null) {
//         *             at.log("duplicate class name:" + idName, ctx); // 只是报警，但仍然继续解析
//         *         }
//         */
//        //
//        pushScope(ctx,classScope);
//    }



    // 这个应该是处理 for 语句的
//    /**
//     * 进入代码片段
//     * @param ctx
//     */
//    @Override
//    public void enterStatement(PlayScriptParser.StatementContext ctx) {
//        super.enterStatement(ctx);
//    }
//
//    /**
//     * 离开代码片段
//     * @param ctx
//     */
//    @Override
//    public void exitStatement(PlayScriptParser.StatementContext ctx) {
//        super.exitStatement(ctx);
//    }
}
