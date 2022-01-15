package playscript;

import org.antlr.v4.runtime.ParserRuleContext;

public class NameSpace extends BlockScope{

    protected NameSpace(String name, Scope enclosingScope, ParserRuleContext ctx) {
        this.name = name;
        this.enclosingScope = enclosingScope;
        this.ctx = ctx;
    }

}
