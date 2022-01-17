package playscript;

import org.antlr.v4.runtime.ParserRuleContext;

/**
 * int num = 111;
 */
public class Variable extends Symbol {

    protected Type type;

    /**
     * 缺省值
     */
    protected Object defaultValue = null;

    protected Variable(String name, Scope enclosingScope, ParserRuleContext ctx) {
        this.name = name;
        this.enclosingScope = enclosingScope;
        this.ctx = ctx;
    }

    @Override
    public String toString(){
        return "Variable " + name + " -> "+ type;
    }

}
