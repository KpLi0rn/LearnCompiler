package playscript;

import java.util.LinkedList;
import java.util.List;

/**
 * 抽象类，命名空间
 * 命名空间中是需要存储变量的
 * 作用域其实就是块
 */
public abstract class Scope extends Symbol{

    // 相当于符号表，所有变量都会存储在这里

    protected List<Symbol> symbols = new LinkedList<>(); // 块中存储这所有的符号

    /**
     * 向scope中添加符号，同时设置好该符号的enclosingScope
     * @param symbol
     */
    protected void addSymbol(Symbol symbol){
        symbols.add(symbol);
        symbol.enclosingScope = this;
    }

    protected Variable getVariable(String idName){
        return getVariable(this,idName);
    }

    /**
     * 写代码别忘了目的，这里主要是为了从 scope 中找到对应名字的变量
     * @param scope
     * @param name
     * @return
     */
    protected static Variable getVariable(Scope scope, String name){
        for (Symbol symbol: scope.symbols){
            if (name.equals(symbol.name) && symbol instanceof Variable ){
                return (Variable) symbol;
            }
        }
        return null;
    }

    protected FunctionScope getFunction(String name, List<Type> paramTypes){
        return getFunction(this,name,paramTypes);
    }

    /**
     * 从 symbols 中寻找 function 然后将形参和实参进行匹配如果匹配上了就返回 function
     * @param scope
     * @param name
     * @param paramTypes
     * @return
     */
    protected static FunctionScope getFunction(Scope scope, String name,List<Type> paramTypes){
        FunctionScope rtn = null;
        for (Symbol symbol: scope.symbols){
            if (name.equals(symbol.name) && symbol instanceof FunctionScope ){
                FunctionScope function = (FunctionScope) symbol;
                if (function.matchParameterTypes(paramTypes)){
                    rtn = function;
                    break;
                }
            }
        }
        return rtn;
    }

    protected boolean containsSymbol(Symbol symbol){
        return symbols.contains(symbol);
    }
}
