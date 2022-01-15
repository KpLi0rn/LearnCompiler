package playscript;

/**
 * 这里是要规定我们编写的语言所具有的类型
 * 编写的脚本语言比较简单，在我理解看来是结构
 * Class
 * Function
 * void
 *
 */
public interface Type {

    String getName();

    Scope getEnclosingScope();

    /**
     * 本类型是不是 is 目标类型。 也就是能否用来替换目标类型。
     * 以面向对象为例，子类 is 父类。子类可以出现在任何需要父类的地方。
     * @param type 目标类型
     * @return
     */
    boolean isType(Type type);

}
