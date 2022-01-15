package playscript;

import org.antlr.v4.runtime.ParserRuleContext;

/**
 * 扫描 ast 树的时候需要创建对应的作用域
 * 类作用域，由于是写自己的编译器。。。 所以类的从属关系也要加进去
 * 类的作用域，先简单设置一下具体后面再看，他这是尼玛是完全体 一口气吃不成胖子
 */
public class ClassScope extends Scope{

    private ClassScope parentClass = null; // 父类

    /**
     * this 指向自己的变量
     */

    /**
     * 不允许有相同的类名
     * @param name
     * @param ctx
     */
    public ClassScope(String name, ParserRuleContext ctx){
        this.name = name;
        this.ctx = ctx; // ast 关联的节点
    }


}
