package playscript;

public interface LValue {

    public Object getValue();

    public void setValue(Object value);

    public Variable getVariable();

    public PlayObject getValueContainer();
    //public StackFrame getFrame();
}