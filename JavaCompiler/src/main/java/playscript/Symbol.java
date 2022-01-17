package playscript;

import org.antlr.v4.runtime.ParserRuleContext;

// 符号，块、类、字符串、函数都称之为符号
public abstract class Symbol {

    protected String name = null;

    // 所属的作用域
    protected Scope enclosingScope = null;

    // 作用域所对应的 ast 上的节点
    protected ParserRuleContext ctx = null;

}
