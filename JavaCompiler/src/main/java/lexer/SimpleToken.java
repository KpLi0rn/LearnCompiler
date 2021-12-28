package lexer;

public final class SimpleToken implements Token {

    private String text = null;
    private TokenType type = null;

    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void setType(TokenType type) {
        this.type = type;
    }

    @Override
    public TokenType getType() {
        return type;
    }

    @Override
    public String getText() {
        return text;
    }
}