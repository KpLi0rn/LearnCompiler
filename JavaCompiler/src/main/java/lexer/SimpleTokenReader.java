package lexer;

import java.util.List;

public class SimpleTokenReader implements TokenReader {
    private List<SimpleToken> tokenList = null;
    private int position = 0;

    public SimpleTokenReader(List<SimpleToken> tokenList) {
        this.tokenList = tokenList;
    }

    @Override
    public Token read() {
        // 需要做一个判断，如果读到末尾了就不读了
        if (position < tokenList.size()) {
            return tokenList.get(position++);
        }
        return null;
    }

    @Override
    public Token peek() {
        if (position < tokenList.size()) {
            return tokenList.get(position);
        }
        return null;
    }

    @Override
    public void unread() {
        if (position > 0) {
            position--;
        }
    }

    // 记录初始位置
    @Override
    public int getPosition() {
        return position;
    }

    // 根据初始位置进行回溯
    @Override
    public void setPosition(int position) {
        if (position >= 0 && position < tokenList.size())
            this.position = position;
    }
}