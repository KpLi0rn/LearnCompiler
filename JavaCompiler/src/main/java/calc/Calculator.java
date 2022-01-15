package calc;

import lexer.SimpleLexer;
import lexer.Token;
import lexer.TokenReader;
import lexer.TokenType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Collections;

@SuppressWarnings("all")
public class Calculator {
    private HashMap<String, Integer> variables = new HashMap<String, Integer>();

    public static void main(String[] args) throws Exception {
        String code = "2*3+4+5*5";
        Calculator calculator = new Calculator();
        calculator.evaluate(code);
    }

    /**
     * 利用词法分析器来处理传入的代码，处理成 tokens
     * 然后调用 addition 表达式来进行处理
     * 加法表达式 => 乘法表达式 => 基础表达式
     * 这些表达式都是通过深度遍历进行递归处理，直到找到终结符，即元素不可再拆分
     */

    public void evaluate(String code) throws Exception {
        AstNode node = parse(code);
        dumpAST(node,"");
        evaluate(node,"");
    }

    public AstNode parse(String code) throws Exception {
        SimpleLexer simpleLexer = new SimpleLexer();
        TokenReader tokens = simpleLexer.tokenize(code);
        AstNode node = prog(tokens);
        return node;
    }

    public AstNode prog(TokenReader tokens) throws Exception {
        // 创建根节点并调用 加法表达式
        SimpleAstNode bootNode = new SimpleAstNode(AstNodeType.Programm,"Calculator");
        SimpleAstNode child = additive(tokens);
        if (child != null){
            bootNode.addChildren(child);
        }
        return bootNode;
    }

    // 递归下降法
    public int evaluate(AstNode node,String indent) throws Exception{
        // 求最终结果
        int result = 0;
        AstNodeType type = node.getType();
        System.out.println(indent + "Calculating: " + type);
        switch (type){
            case Programm:
                for(AstNode child:node.getChildren()){
                    result = evaluate(child,indent + "\t");
                }
                break;
            case Identifier:
                String varName = node.getText();
                if (variables.containsKey(varName)) {
                    Integer value = variables.get(varName);
                    if (value != null) {
                        result = value.intValue();
                    } else {
                        throw new Exception("variable " + varName + " has not been set any value");
                    }
                }
                else{
                    throw new Exception("unknown variable: " + varName);
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
            case IntLiteral:
                result = Integer.valueOf(node.getText()).intValue();
                break;
//            case Identifier:
//                result = Integer.valueOf(node.getText()).intValue();
//                break;
            default:
        }
        System.out.println(indent + "Result: " + result);
        return result;
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
     * 右递归，左递归换一下 additive 和 multiplicative 然后需要换一下判断条件 但是这样会导致死循环，即不断调用自身，
     */

//    public SimpleAstNode additive(TokenReader tokens) throws Exception{
//        // multiplicative + add  , multiplicative 到最后为 int ，相当于 int + addexpression => 右递归
//        SimpleAstNode child1 = multiplicative(tokens);
//        SimpleAstNode node = child1; // 乘法子树
//        Token token = tokens.peek();
//        if (token != null && child1 != null){
//            if (token.getType() == TokenType.Plus) { // 如果是 + 号
//                token = tokens.read(); // 对加号进行消耗
//                SimpleAstNode child2 = additive(tokens); //
//                if (child2 != null){
//                    node = new SimpleAstNode(AstNodeType.Additive, token.getText()); // 创建 * 的节点，反之返回树，学到了
//                    node.addChildren(child1);
//                    node.addChildren(child2);
//                }else {
//                    throw new Exception("invalid multiplicative expression, expecting the right part.");
//                }
//            }
//        }
//        return node;
//    }

    /**
     * 乘法表达式
     * @param tokens
     * @return
     */
    public SimpleAstNode multiplicative(TokenReader tokens) throws Exception{
        // 1*2*3
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


    /**
     * 创建了一个 Ast节点，属性：类型、数值、父节点、子节点、添加节点
     */
//    class SimpleAstNode implements AstNode{
//
//        private AstNodeType type = null;
//        private String text = null;
//        private AstNode parent = null; // 只有一个父节点
//        private List<AstNode> childrens = new ArrayList<>();
//        private List<AstNode> readOnlyChildrens = Collections.unmodifiableList(childrens);  // 创建一个可读的列表
//
//
//        public SimpleAstNode(AstNodeType type, String text) {
//            this.type = type;
//            this.text = text;
//        }
//
//        @Override
//        public AstNode getParent() {
//            return parent;
//        }
//
//        @Override
//        public List<AstNode> getChildren() {
//            return readOnlyChildrens;
//        }
//
//        @Override
//        public AstNodeType getType() {
//            return type;
//        }
//
//        @Override
//        public String getText() {
//            return text;
//        }
//
//        public void addChildren(SimpleAstNode child){
//            childrens.add(child); // 我之前为什么要多此一举...
//            child.parent = this;
//        }
//    }

    public void dumpAST(AstNode node, String indent) {
        System.out.println(indent + node.getType() + " " + node.getText());
        for (AstNode child : node.getChildren()) {
            dumpAST(child, indent + "\t");
        }
    }
}
