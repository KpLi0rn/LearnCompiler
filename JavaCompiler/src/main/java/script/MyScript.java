package script;

import calc.AstNode;
import calc.AstNodeType;
import lexer.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;


public class MyScript {

    private HashMap<String,Integer> variables = new HashMap();

    public static void main(String[] args) throws Exception{
        System.out.println("Simple script language!");
        SimpleParser parser = new SimpleParser();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        StringBuilder scriptText = new StringBuilder();
        System.out.print("\n>");   //提示符

        while (true) {
            try {
                String line = reader.readLine().trim();
                if (line.equals("exit();")) {
                    System.out.println("good bye!");
                    break;
                }
                scriptText.append(line).append("\n");
                if (line.endsWith(";")) {
                    // 主要的两行
                    AstNode tree = parser.parse(scriptText.toString());

                    int result = parser.evaluate(tree, "");

                    System.out.println(result);

                    System.out.print("\n>");   //提示符

                    scriptText = new StringBuilder();
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e.getLocalizedMessage());
                System.out.print("\n>");   //提示符
                scriptText = new StringBuilder();
            }
        }
    }





//    public int evaluate(AstNode node, String indent) throws Exception{
//        int result = 0;
//        AstNodeType type = node.getType();
////        System.out.println(indent + "Calculating: " + type);
//        switch (type){
//            case Programm:
//                for(AstNode child:node.getChildren()){
//                    result = evaluate(child,indent + "\t");
//                }
//                break;
//            case Identifier:
//                String varName = node.getText();
//                if (variables.get(varName) != null){
//                    Integer value = variables.get(varName);
//                    if (value != null) {
//                        result = value;
//                    }else {
//                        throw new Exception("variable " + varName + " has not been set any value");
//                    }
//                }else {
//                    for(AstNode child:node.getChildren()){
//                        result = evaluate(child,indent + "\t");
//                    }
//                }
//                break;
//            case Additive:
//                AstNode child1 = node.getChildren().get(0);
//                // 不断进行递归求解
//                int value1 = evaluate(child1,indent + "\t"); // 计算当前节点下的所有值的和
//                AstNode child2 = node.getChildren().get(1);
//                int value2 = evaluate(child2,indent + "\t");
//                if (node.getText().equals("+")){ // 递归最后运算对逻辑
//                    result = value1+value2;
//                }
//                break;
//            case Multiplicative:
//                child1 = node.getChildren().get(0);
//                value1 = evaluate(child1,indent + "\t");
//                child2 = node.getChildren().get(1);
//                value2 = evaluate(child2,indent + "\t");
//                if (node.getText().equals("*")){ // 递归最后运算对逻辑
//                    result = value1 * value2;
//                }
//                break;
//            case IntLiteral:
//                result = Integer.valueOf(node.getText()).intValue();
//                break;
//            default:
//        }
////        System.out.println(indent + "Result: " + result);
//        return result;
//    }


}
