package playscript;

public class PrimitiveType implements Type{

    private String name;

    /**
     * 基础类型的罗列，奥 相当于一个类一个类型吗
     * @param name
     */

    public static PrimitiveType Integer = new PrimitiveType("Integer");
    public static PrimitiveType Long = new PrimitiveType("Long");
    public static PrimitiveType Float = new PrimitiveType("Float");
    public static PrimitiveType Double = new PrimitiveType("Double");
    public static PrimitiveType Boolean = new PrimitiveType("Boolean");
    public static PrimitiveType Byte = new PrimitiveType("Byte");
    public static PrimitiveType Char = new PrimitiveType("Char");
    public static PrimitiveType Short = new PrimitiveType("Short");
    public static PrimitiveType String = new PrimitiveType("String"); //增加String为基础类型
    public static PrimitiveType Null = new PrimitiveType("Null");

    /**
     * 设置私有估计是不让外部添加基础属性
     * @param name
     */
    private PrimitiveType(String name){
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Scope getEnclosingScope() {
        return null;
    }

    /**
     * 判断类型是否一致
     * @param type 目标类型
     * @return
     */
    @Override
    public boolean isType(Type type) {
        return this == type;
    }
}
