package playscript;

import org.antlr.v4.runtime.ParserRuleContext;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * 函数作用域
 */
@SuppressWarnings("all")
public class  FunctionScope extends Scope implements FunctionType{

    protected List<Variable>  parameters = new LinkedList<>();

    protected Type returnType = null;

    //闭包变量，即它所引用的外部环境变量，奥。。。 还要处理闭包
    protected Set<Variable> closureVariables = null;


    private List<Type> paramTypes = null;

    protected FunctionScope(String name, Scope enclosingScope, ParserRuleContext ctx) {
        this.name = name;
        this.enclosingScope = enclosingScope;
        this.ctx = ctx;
    }

    @Override
    public Type getReturnType() {
        return returnType;
    }

    /**
     * 获取函数参数类型
     * @return
     */
    @Override
    public List<Type> getParamTypes() {
        if (parameters == null){
            paramTypes =  new LinkedList<>();
        }
        for (Variable parameter:parameters){
            paramTypes.add(parameter.type);
        }
        return paramTypes;
    }

    /**
     * 在函数处理这边需要事先检查类型是否匹配
     * @param paramTypes
     * @return
     */
    @Override
    public boolean matchParameterTypes(List<Type> paramTypes) {
        if (paramTypes.size() != parameters.size()){
            return false;
        }
        boolean match = true;
        for (int i=0;i<parameters.size();i++){
            Type varType = parameters.get(i).type;
            Type type = paramTypes.get(i);
            if (!varType.isType(type)){
                match = false;
                break;
            }
        }
        return match;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Scope getEnclosingScope() {
        return enclosingScope;
    }

    /**
     * 类型检查的时候进行比较是否和需要的参数类型所匹配
     * 类型必须要是 FunctionType ，说明是在函数场景下使用
     * 他那边做的也太复杂了把，感觉没必要啊
     * @param type 目标类型
     * @return
     */
    @Override
    public boolean isType(Type type) {
        if (type instanceof FunctionType){
            return DefaultFunctionType.isType(this, (FunctionType) type);
        }
        return false;
    }

    @Override
    public String toString(){
        return "Function " + name;
    }
}
