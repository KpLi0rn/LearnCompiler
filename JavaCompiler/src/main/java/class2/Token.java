package class2;

public interface Token {
    /**
     * age=45 : type=>StringLiteral text=>45
     * @return
     */
    public TokenType getType();
    public String getText();
}
