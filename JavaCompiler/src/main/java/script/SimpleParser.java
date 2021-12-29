package script;

import calc.AstNode;
import calc.AstNodeType;
import calc.Calculator;
import calc.SimpleAstNode;
import lexer.*;

import java.util.HashMap;


@SuppressWarnings("all")
public class SimpleParser {

    private HashMap<String, Integer> variables = new HashMap<String, Integer>();
    private String varName = null;
    public static void main(String[] args) throws Exception {
        String code = "int num=1+2+3;num;";
        SimpleParser parser = new SimpleParser();
        parser.evaluate(code);

//        String code = "int num=2*3+4+5*6;";
//        SimpleParser parser = new SimpleParser();
//        parser.evaluate(code);

//        String code = "num=2*3+4+5*5;";
//        SimpleParser parser = new SimpleParser();
//        parser.evaluate(code);

    }

    public void evaluate(String code) throws Exception {
        AstNode node = parse(code);
        dumpAST(node,"");
        System.out.println("\n=================\n");
        evaluate(node,"");
    }

    /**
     * 针对表达式的入口
     * @param code
     * @return
     * @throws Exception
     */
    public AstNode parse(String code) throws Exception {
        SimpleLexer simpleLexer = new SimpleLexer();
        TokenReader tokens = simpleLexer.tokenize(code);
        AstNode node = prog(tokens);
        return node;
    }

    /**
     * 针对不同 token 进行特殊化的处理
     * @param tokens
     * @return
     * @throws Exception
     */
    public SimpleAstNode prog(TokenReader tokens) throws Exception {
        SimpleAstNode node = null;
        Token token = tokens.peek(); // 进行预读，我这里主要是两种场景，ID 和 关键字
        if (token != null){
            if (token.getType().equals(TokenType.Identifier)){        // 变量
                node = assignmentStatement(tokens);
            }else if (token.getType().equals(TokenType.Int)){        // int 关键字
                node = intDeclare(tokens);
            }else if (token.getType().equals(TokenType.IntLiteral)){ // 纯数字直接进行计算
                node = additive(tokens);
            }
        }
        return node;
    }

    /**
     * 需要处理变量赋值的问题
     * @param node
     * @param indent
     * @return
     * @throws Exception
     */

    public int evaluate(AstNode node,String indent) throws Exception{
        // 求最终结果
        int result = 0;
        AstNodeType type = node.getType();
//        System.out.println(indent + "Calculating: " + type);
        switch (type){
            case Identifier:
                varName = node.getText();
                if (variables.containsKey(varName)){ // 如果存在就从里面取出来
                    result = variables.get(varName);
                }else {
                    for(AstNode child:node.getChildren()){
                        result = evaluate(child,indent + "\t");
                    }
                }
                break;
            case Additive:
                AstNode child1 = node.getChildren().get(0);
                // 不断进行递归求解
                int value1 = evaluate(child1,indent + "\t"); // 计算当前节点下的所有值的和
                AstNode child2 = node.getChildren().get(1);
                int value2 = evaluate(child2,indent + "\t");
                if (node.getText().equals("+")){ // 递归最后运算对逻辑
                    result = value1+value2;
                }
                break;
            case Multiplicative:
                child1 = node.getChildren().get(0);
                // 不断进行递归求解
                value1 = evaluate(child1,indent + "\t");
                child2 = node.getChildren().get(1);
                value2 = evaluate(child2,indent + "\t");
                if (node.getText().equals("*")){ // 递归最后运算对逻辑
                    result = value1 * value2;
                }
                break;
            case AssignmentStmt:
                varName = node.getText();
                if (varName == null) {
                    throw new Exception("unknown variable: " + varName);
                }
                break;
            case IntLiteral:
//                varName = node.getText();
//                result = Integer.valueOf(varName).intValue();
//                variables.put(varName,result);
                if (varName == null){
                    result = Integer.valueOf(node.getText()).intValue();
//                    variables.put(varName,result);
                }else {
                    result = Integer.valueOf(node.getText()).intValue();
                    int value;
                    try {
                        value = variables.get(varName);
                    }catch (NullPointerException e){
                        value = 0;
                    }
                    value = value+result;
                    variables.put(varName,value);
                }
                break;
            default:
        }
//        System.out.println(indent + "Result: " + result);
        return result;
    }

    /**
     * 处理变量赋值情况
     *  num = 3*5;
     *  num
     * @param tokens
     * @return
     * @throws Exception
     */
    public SimpleAstNode assignmentStatement(TokenReader tokens) throws Exception{
        SimpleAstNode node = null;
        SimpleToken token = (SimpleToken) tokens.peek(); // 首先进行预读
        if (token != null && token.getType().equals(TokenType.Identifier)){
            tokens.read(); // 对变量进行消耗
            node = new SimpleAstNode(AstNodeType.Identifier,token.getText());
            token = (SimpleToken) tokens.peek(); // 首先进行预读
            if (token != null && token.getType().equals(TokenType.Assignment)){ // 判断是否是等号情况
                // 回朔
                tokens.read(); // 对等号进行消耗
//                token = (SimpleToken) tokens.peek(); // 直接对等号后面对表达
                SimpleAstNode child = additive(tokens);
                if (child != null){
                    node.addChildren(child);
                    token = (SimpleToken) tokens.peek(); // 首先进行预读
                    if (token != null && token.getType().equals(TokenType.SemiColon)){ // 如果最后一个是分号的话
                        tokens.read();
                    }else{
                        // 抛出报错，需要 ； 结尾
                        throw new Exception("invalid statement, expecting semicolon");
                    }
                }
            }else {
                tokens.unread();
//                node = null; // 这里不注释的话，单独输入 num 获取变量的时候就会返回 null 导致获取不到 node
            }
        }
        return  node;
    }

    /**
     * 处理 int num=1+1+1;的情况
     * @param tokens
     * @return
     * @throws Exception
     */
    public SimpleAstNode intDeclare(TokenReader tokens) throws Exception{
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
                    SimpleAstNode child = additive(tokens);
                    if (child != null) {
                        node.addChildren(child);
                        token = (SimpleToken) tokens.peek();
                        if (token != null && token.getType().equals(TokenType.SemiColon)) {
                            tokens.read();
                        } else {
                            throw new Exception("invalid statement, expecting semicolon");
                        }
                    }
                    // 个人感觉这里也需要添加回朔
//                    }else {
//                        node = null;
//                        tokens.setPosition(position); // 对初始位置进行回朔
//                    }
                }else {
                    tokens.unread();
                    node = null;
                }
            }
        }
        return node;
    }


    /**
     * 加法表达式, 这样就变成左递归了... 有点牛逼，左递归
     * add = multiplicative (+multiplicative)*
     * @param tokens
     * @return
     */
    public SimpleAstNode additive(TokenReader tokens) throws Exception{
        SimpleAstNode child1 = multiplicative(tokens);
        SimpleAstNode node = child1; // 乘法子树
        if (child1 != null){
            while(true){
                Token token = tokens.peek();
                if (token!=null && token.getType() == TokenType.Plus){ // +
                    token = tokens.read(); // 对加号进行消耗
                    SimpleAstNode child2 = multiplicative(tokens); //
                    node = new SimpleAstNode(AstNodeType.Additive,token.getText()); // 创建 * 的节点，反之返回树，学到了
                    node.addChildren(child1);
                    node.addChildren(child2);
                    child1 = node; // child1 在while中就依靠这个来变化 明白了 往节点里不停的加 +

                }else {
                    break;
                }
            }

        }
        return node;
    }

    /**
     * 乘法表达式
     * @param tokens
     * @return
     */
    public SimpleAstNode multiplicative(TokenReader tokens) throws Exception{
        SimpleAstNode child1 = primary(tokens); // 1
        SimpleAstNode node = child1; // 递归的思想，如果读到最里面了 就返回 int，
        Token token = tokens.peek();
        if (token != null && child1 != null){
            if (token.getType() == TokenType.Star){
                token = tokens.read(); // 对 * 进行消耗，此时 token 为 *
                SimpleAstNode child2 = multiplicative(tokens);
                if (child2 != null){
                    node = new SimpleAstNode(AstNodeType.Multiplicative,token.getText()); // 创建 * 的节点，反之返回树，学到了
                    node.addChildren(child1);
                    node.addChildren(child2);
                } else {
                    throw new Exception("invalid multiplicative expression, expecting the right part.");
                }

            }
        }
        return node;
    }

    /**
     * 基础表达式
     * @param tokens
     * @return
     */
    public SimpleAstNode primary(TokenReader tokens){
        SimpleAstNode node = null;
        Token token = tokens.peek(); // 对 token 进行一次预读
        if (token != null){
            // 如果是 int 类型
            if (token.getType() == TokenType.IntLiteral){
                token = tokens.read(); // 要对 token 进行消耗推进
                return new SimpleAstNode(AstNodeType.IntLiteral,token.getText());
            }else if (token.getType() == TokenType.Identifier){
                token = tokens.read();
                return new SimpleAstNode(AstNodeType.Identifier,token.getText());
            }
        }
        return node;
    }

    public void dumpAST(AstNode node, String indent) {
        System.out.println(indent + node.getType() + " " + node.getText());
        for (AstNode child : node.getChildren()) {
            dumpAST(child, indent + "\t");
        }
    }
}
