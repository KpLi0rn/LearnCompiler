package playscript;

public class PlayScript {
    public static void main(String[] args) {
        String script = "int b=10; int myfunc(int a) {int c=3;return a+b+c;} myfunc(2);";
        /**
         * 函数内变量的问题，函数内部，函数外部的没有正常进行添加
         */
        script = "int b=8;int myfunc(int a) {int c=2; return a+b+c;} myfunc(2);";
        // c 没有获取到 没有添加进去
//        script = "int myfunc(int a) {int c=3;return a+c;} myfunc(2);";
//        script = "int myfunc(int a) {return a+3;} myfunc(2);";


        AnnotatedTree tree = null;
        PlayScriptCompiler compiler = new PlayScriptCompiler();

        tree = compiler.Compile(script);
        Object result = compiler.Execute(tree);
        System.out.println(result);





    }
}
