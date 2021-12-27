package calc;

import lexer.TokenType;

import java.util.List;

public interface AstNode {

    public AstNode getParent();

    public List<AstNode> getChildren(); // 子节点很有可能有多个

    public AstNodeType getType();

    public String getText();

}
