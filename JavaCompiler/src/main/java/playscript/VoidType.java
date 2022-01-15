package playscript;

public class VoidType implements Type{

    private static VoidType voidType = new VoidType();

    public static VoidType instance(){
        return voidType;
    }
    public VoidType(){

    }

    @Override
    public String getName() {
        return "void";
    }

    /**
     * 基础类型不需要有所属作用域
     * @return
     */
    @Override
    public Scope getEnclosingScope() {
        return null;
    }

    @Override
    public boolean isType(Type type) {
        return this==type;
    }
}
