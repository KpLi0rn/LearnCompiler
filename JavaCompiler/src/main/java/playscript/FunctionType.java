package playscript;

import java.util.List;

public interface FunctionType extends Type{

    Type getReturnType();

    public List<Type> getParamTypes();

    public boolean matchParameterTypes(List<Type> paramTypes);
}
