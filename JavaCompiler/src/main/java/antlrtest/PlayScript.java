package antlrtest;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class PlayScript {
    public static void main(String[] args) {
        String script = "2+6/3";

        // 词法分析
        PlayScriptLexer lexer = new PlayScriptLexer(CharStreams.fromString(script));
        CommonTokenStream tokens = new CommonTokenStream(lexer); // 返回 tokens ，可进行 token 的读写

        // 语法分析，将 tokens 传入到表达式，让表达式来进行计算
        PlayScriptParser parser = new PlayScriptParser(tokens); // 对语法进行解析
        ParseTree tree = parser.additiveExpression();   // 调用表达式 加法表达式，返回树

        // 遍历树节点进行运算
        //打印语法树
        System.out.println("The lisp style ast of : " + script);
        System.out.println(tree.toStringTree(parser)); // 打印树结构


        // antlr 能将 词法分析、语法分析、语法树都进行实现....

        //解释执行
        ASTEvaluator visitor = new ASTEvaluator();
        Integer result = visitor.visit(tree);
        System.out.println("\nValue of : " + script);
        System.out.println(result);
    }
}
