package playscript;

public class PlayScript {
    public static void main(String[] args) {
        String script = "int age = 44; { int i = 10; age+i;}";

        /**
         * 传入 script 对 String 进行解析，并进行词法语法分析，语法规则根据 PlayScript.g4 来建立对应的 Ast 语法树
         * tree 为生成的 AST 语法树
         */
        AnnotatedTree tree = null;
        PlayScriptCompiler compiler = new PlayScriptCompiler();
        /**
         * 当前的语法树把 token 加到了对应的位置,并进行扫描
         * 我目前支队 block 进行了扫描，将对应的节点和作用域存放到了 map 中，通过 antlr 的 listener 来对节点遍历进行监听
         * listener 当进入节点和离开节点的时候都允许我们做一些操作
         *
         */
        tree = compiler.Compile(script);

//        compiler.DumpAst(tree);
        /**
         * 作用域的问题
         * 目的：
         * 在遍历 AST 树过程中拿到节点之后就对应的获取对应的作用域，如果变量存在当前节点对应的作用域那么就直接从作用域中进行获取
         * 如果当前节点获取不到那么就到上一个作用域中进行获取，同时当函数执行完成之后我们需要对作用域进行销毁，所以我们需要维护下面的结构
         * 1. 数据结构要为栈，这样能做到作用域销毁,这里第一阶段只是把 scope 和 节点对应起来，首先进行扫描，把节点和 Scope 的关系建立起来（扫描完成了）
         * 2. 需要利用map（ HashMap<node节点,作用域> ）来建立对应关系，即获取到 Bock 节点，那么就从 map 中获取到对应的 Scope
         *    同时需要有多个 map 因为有各种类型的 Node 节点(加法表达式、函数表达式、一般表达式等等...)
         * 3. Scope 中存在着当前作用域下的所有变量
         * 4. Symbol 需要定义变量
         * 应该是先扫描一遍，根据 ast 上的节点创建对应的 作用域或者命名空间 然后再添加进去
         */

        // 在实际调用首先可以获取对应节点，接下来进行变量的cunhuc
        /**
         * 扫描之后将所有的 node 和 对应的作用域都存到了 tree 中
         * 接下来我觉得就是要把函数中的变量都存到作用域里面，例如函数中的参数都放入到函数都作用域中
         */




    }
}
