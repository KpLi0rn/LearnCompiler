package calc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimpleAstNode implements AstNode{

    private AstNodeType type = null;
    private String text = null;
    private AstNode parent = null; // 只有一个父节点
    private List<AstNode> childrens = new ArrayList<>();
    private List<AstNode> readOnlyChildrens = Collections.unmodifiableList(childrens);  // 创建一个可读的列表


    public SimpleAstNode(AstNodeType type, String text) {
        this.type = type;
        this.text = text;
    }

    @Override
    public AstNode getParent() {
        return parent;
    }

    @Override
    public List<AstNode> getChildren() {
        return readOnlyChildrens;
    }

    @Override
    public AstNodeType getType() {
        return type;
    }

    @Override
    public String getText() {
        return text;
    }

    public void addChildren(SimpleAstNode child){
        childrens.add(child); // 我之前为什么要多此一举...
        child.parent = this;
    }
}