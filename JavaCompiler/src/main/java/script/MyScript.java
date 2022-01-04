package script;

import calc.AstNode;
import calc.AstNodeType;
import lexer.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;


public class MyScript {

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

}
