package script;

import calc.AstNodeType;
import calc.Calculator;
import calc.SimpleAstNode;
import lexer.*;


public class MyScript {

    public static void main(String[] args) throws Exception{
        /**
         * 实现自己的脚本语言
         * int num = 2*3;
         * num+8;
         */
        SimpleLexer lexer = new SimpleLexer();
        String code = "int num = 2+3;";
        System.out.println("parse :" + code);
        SimpleTokenReader tokenReader = lexer.tokenize(code);
//        SimpleLexer.dump(tokenReader);
        MyScript script = new MyScript();
        SimpleAstNode node = script.assignmentStatement(tokenReader);

        Calculator calculator = new Calculator();
        calculator.dumpAST(node,"");
        calculator.evaluate(node,"");
    }

    /**
     * 解析赋值语句
     * @param tokens
     * @throws Exception
     */
    public SimpleAstNode assignmentStatement(SimpleTokenReader tokens) throws Exception{
        SimpleAstNode node = null;
        SimpleToken token = (SimpleToken) tokens.peek();
        if (token.getType().equals(TokenType.Int)){
            tokens.read(); // 消耗
            token = (SimpleToken) tokens.peek();
            if (token != null && token.getType().equals(TokenType.Identifier)){
                tokens.read();
                node = new SimpleAstNode(AstNodeType.Identifier,token.getText()); // num 为根节点
                token = (SimpleToken) tokens.peek();
                if (token != null && token.getType().equals(TokenType.Assignment)){
                    int position = tokens.getPosition();
                    tokens.read();
                    Calculator calculator = new Calculator();
                    SimpleAstNode child = calculator.additive(tokens);
                    if (child != null){
                        node.addChildren(child);
                        token = (SimpleToken) tokens.peek();
                        // 检测到分号语句结束
                        if (token != null && token.getType().equals(TokenType.SemiColon)){
                            tokens.read();
                        }else {
                            throw new Exception("invalid statement, expecting semicolon");
                        }
                    }else {
                        node = null;
                        tokens.setPosition(position); // 对初始位置进行回朔
                    }
                }
            }
        }
        return node;
    }
}
