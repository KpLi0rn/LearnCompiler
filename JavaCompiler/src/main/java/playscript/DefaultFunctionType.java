package playscript;

import java.util.List;

public class DefaultFunctionType implements FunctionType{
    @Override
    public Type getReturnType() {
        return null;
    }

    @Override
    public List<Type> getParamTypes() {
        return null;
    }

    @Override
    public boolean matchParameterTypes(List<Type> paramTypes) {
        return false;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Scope getEnclosingScope() {
        return null;
    }

    @Override
    public boolean isType(Type type) {
        return false;
    }


    public static boolean isType(FunctionType type1, FunctionType type2){
        if (type1 == type2) return true;

        /**
         * 难道单纯的比较不行吗？
         */
        return false;
    }
}
