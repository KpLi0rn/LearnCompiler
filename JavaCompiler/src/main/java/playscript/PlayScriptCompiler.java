package playscript;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;


/**
 * 将词法 语法 语义 结合在一起
 */
public class PlayScriptCompiler {

    AnnotatedTree at = null;
    PlayScriptLexer lexer = null;
    PlayScriptParser parser = null;

    public AnnotatedTree Compile(String script) {
        at = new AnnotatedTree();
        /**
         * 词法解析
         */
        lexer = new PlayScriptLexer(CharStreams.fromString(script));
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        /**
         * 语法解析，将 Tokens 根据语法规则存放在树节点中
         * parser.prog() => prog 为顶节点
         */
        parser = new PlayScriptParser(tokens);
        at.ast = parser.prog();

        ParseTreeWalker walker = new ParseTreeWalker();

        /**
         * pass: 类型和作用域
         */
        TypeAndScopeScanner scopeScanner = new TypeAndScopeScanner(at);
        walker.walk(scopeScanner,at.ast);

        /**
         * pass: 解析变量函数声明，这里处理好了基本类型数据，函数传参数，函数返回值, 这里解析的有问题
         * 但是函数内部好像没有做处理
         */
        TypeResolver typeResolver = new TypeResolver(at);
        walker.walk(typeResolver, at.ast);

        /**
         * 这一部分是引用的消解，主要是确定变量类型，这一步没有确定变量类型
         */
        RefResolver resolver = new RefResolver(at);
        walker.walk(resolver, at.ast);

//        DumpAst(at);

        return at;
    }

    public void DumpAst(AnnotatedTree at){
        if (at != null) {
            System.out.println(at.ast.toStringTree());
        }
    }

    public Object Execute(AnnotatedTree at){
        ASTEvaluator visitor = new ASTEvaluator(at);
        Object result = visitor.visit(at.ast);
        return result;
    }

}
