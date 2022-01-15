package playscript;

import org.antlr.v4.runtime.ParserRuleContext;

/**
 * 块，但是由于名字都一样所以需要进行编号
 * 每个作用域都需要有自己的名字
 */
public class BlockScope extends Scope{
    private static int index = 1;

    public BlockScope(){
        this.name = "Block" + index++; // 放置 Scope 不重复
    }

    public BlockScope(Scope enclosingScope,ParserRuleContext ctx){
        this.name = "Block" + index++; // 放置 Scope 不重复
        this.enclosingScope = enclosingScope;
        this.ctx = ctx;
    }


}
