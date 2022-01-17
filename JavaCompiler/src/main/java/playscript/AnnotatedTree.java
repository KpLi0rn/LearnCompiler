package playscript;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 注释树
 * 语义分析的结果都放在这里。跟AST的节点建立关联。包括：
 * 1.类型信息，包括基本类型和用户自定义类型；
 * 2.变量和函数调用的消解；
 * 3.作用域Scope。在Scope中包含了该作用域的所有符号。Variable、Function、Class等都是符号。
 * 在这里放置的数据是需要和 ast 树产生关联
 */
public class AnnotatedTree {

    protected ParseTree ast = null;

    // 命名空间
    NameSpace nameSpace = null;  //全局命名空间

    // 解析出来的所有类型，包括类和函数，以后还可以包括数组和枚举。类的方法也作为单独的要素放进去。
    protected List<Type> types = new LinkedList<Type>();


    // 存放 ast 对应的 变量名的 地方，并不存储变量，只是存储变量名
    protected Map<ParserRuleContext, Symbol> symbolOfNode = new HashMap<>();

    // AST节点对应的Scope，如for、函数调用会启动新的Scope，每个 节点对应的 作用域
    protected Map<ParserRuleContext, Scope> node2Scope = new HashMap<>();

    // 每个节点解析出来的节点
    protected Map<ParserRuleContext, Type> typeOfNode = new HashMap<>();


    public Scope enclosingScopeOfNode(ParserRuleContext node){
        Scope rtn = null;
        ParserRuleContext parent = node.getParent();
        // 如果父节点不为 null
        if (parent != null){
            rtn = node2Scope.get(parent);
            if (rtn == null){
                rtn = enclosingScopeOfNode(parent);
            }
        }
        return rtn;
    }

    /**
     * 递归从作用域中进行寻找，如果当前作用域中找不到那么就到上级作用域进行寻找
     * @param scope
     * @param idName
     * @return
     */
    public Variable lookupVariable(Scope scope,String idName){
        Variable variable = scope.getVariable(idName);
        if (variable == null && scope.enclosingScope != null){
            variable = lookupVariable(scope.enclosingScope,idName);
        }
        return variable;
    }

    /**
     * 从函数作用域中寻找函数
     */
    public FunctionScope lookupFunction(Scope scope, String idName, List<Type> paramTypes){
        FunctionScope rtn = scope.getFunction(idName, paramTypes);

        if (rtn == null && scope.enclosingScope != null) {
            rtn = lookupFunction(scope.enclosingScope, idName, paramTypes);
        }
        return rtn;
    }

    protected FunctionScope lookupFunction(Scope scope, String name){
        FunctionScope rtn = null;

        rtn = getFunctionOnlyByName(scope, name);


        if (rtn == null && scope.enclosingScope != null){
            rtn = lookupFunction(scope.enclosingScope,name);
        }
        return rtn;
    }


    private FunctionScope getFunctionOnlyByName(Scope scope, String name){
        for (Symbol s : scope.symbols){
            if (s instanceof FunctionScope && s.name.equals(name)){
                return  (FunctionScope)s;
            }
        }
        return  null;
    }







}
