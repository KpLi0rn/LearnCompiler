package lexer;

public interface Token {
    /**
     * age=45 : type=>StringLiteral text=>45
     * @return
     */
    public TokenType getType();
    public String getText();
    public void setType(TokenType tokenType);
    public void setText(String text);
}
