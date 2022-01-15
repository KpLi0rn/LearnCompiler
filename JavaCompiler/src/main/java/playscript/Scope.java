package playscript;

import java.util.LinkedList;
import java.util.List;

/**
 * 抽象类，命名空间
 * 命名空间中是需要存储变量的
 * 作用域其实就是块
 */
public abstract class Scope extends Symbol{

    protected List<Symbol> symbols = new LinkedList<>(); // 块中存储这所有的符号

    /**
     * 向scope中添加符号，同时设置好该符号的enclosingScope
     * @param symbol
     */
    protected void addSymbol(Symbol symbol){
        symbols.add(symbol);
        symbol.enclosingScope = this;
    }
}
