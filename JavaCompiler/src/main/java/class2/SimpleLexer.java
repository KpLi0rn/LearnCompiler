package class2;

import java.io.CharArrayReader;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class SimpleLexer {

    public static void main(String[] args) {
        SimpleLexer lexer = new SimpleLexer();

        String script = "int age = 45";
        System.out.println("parse :" + script);
        SimpleTokenReader tokenReader = lexer.tokenize(script);
        dump(tokenReader);
    }


    // 字符判断
    private boolean isAlpha(int ch){
        return ch>='a' && ch <= 'z'|| ch >='A' && ch <= 'Z';
    }
    // 数字判断
    private boolean isDigit(int ch) {
        return ch >= '0' && ch <= '9';
    }

    // 空白字符判断, char 类型直接进行比较
    private boolean isBlank(int ch){
        return ch == ' ' || ch == '\t' || ch == '\n';
    }

    // 这也太妙了把...
    private StringBuffer tokenText = null;
    private List<SimpleToken> tokenList = null;
    private SimpleToken token = null;

    // 状态机的初始化/切换，传入的应该是字符
    // 状态机状态和 token type 大部分情况下都是相似的
    private DfaState initToken(char ch){
        // 如果不是最开始的状态，说明上一个 token 已经解析完了
        if (tokenText.length() >0 ){
            // 上一个 token 已经解析完成了 ，把解析完成的 token 放到 list 中
            token.text = tokenText.toString();
            tokenList.add(token);
            // 重新进行置空
            tokenText = new StringBuffer();
            token = new SimpleToken();
        }

        DfaState newState = DfaState.Initial;
        if (isAlpha(ch)){
            // int 单独处理
            if (ch == 'i'){
                newState = DfaState.Id_INT1;
            }else {
                newState = DfaState.Id; // 状态机的类型
            }
            token.type = TokenType.Identifier; // token 的类型
            tokenText.append(ch); // 暂时存储
        }else if (isDigit(ch)){
            newState = DfaState.IntLiteral;
            token.type = TokenType.IntLiteral;
            tokenText.append(ch);
        }else if (ch == '>'){
            newState = DfaState.GT;
            token.type = TokenType.GT;
            tokenText.append(ch);
        }else if (ch == '=') {
            newState = DfaState.Assignment;
            token.type = TokenType.Assignment;
            tokenText.append(ch);
        }
        // 如果上面三个都没有走进去的话，那么就没遇到开始状态机的机会，所以还是 init
        return newState;
    }

    // int age = 10
    private SimpleTokenReader tokenize(String code){
        // 代表这开始，如果不重新进行赋值的话，那么上一句代码解析完的就会影响后面的结果
        tokenText = new StringBuffer();
        tokenList = new ArrayList<>();
        token = new SimpleToken();
        CharArrayReader arrayReader = new CharArrayReader(code.toCharArray());
        int ich = 0;
        char ch = 0;
        // 状态机开始
        DfaState state = DfaState.Initial;
        try {
            // 如果没有读到末尾就一直读
            while ( (ich = arrayReader.read()) != -1 ){
                // 读出来的是 ascii 码，进行类型转换
                ch = (char) ich;
                switch (state){
                    case Initial:
                        state = initToken(ch);
                        break;
                    case Id: // 如果是标志符的话就继续读,字符串遍历不需要我们管
                        if (isAlpha(ch) || isDigit(ch)){
                            tokenText.append(ch); // 然后不用做过多修改，状态机不变就行了
                            // 如果不是状态切换
                        }else {
                            state = initToken(ch);
                        }
                        break;
                        // >= 在这里处理 , 这里代表的是上一个
                    case GT:
                        if (ch == '='){
                            state = DfaState.GE;
                            token.type = TokenType.GE;
                            tokenText.append(ch);
                        }else {
                            state = initToken(ch); // 让 initToken 去添加
                        }
                        break;
                    case Assignment:  // 这个一定要加

                    case IntLiteral:
                        if (isDigit(ch)){
                            tokenText.append(ch);
                        }else {
                            state = initToken(ch);
                        }
                        break;
                        // int or innnst
                    case Id_INT1:
                        if (ch == 'n'){
                            state = DfaState.Id_INT2;
                            token.type = TokenType.Identifier;
                            tokenText.append(ch);
                        }else if (isAlpha(ch) || isDigit(ch)){
                            tokenText.append(ch);
                        }else {
                            state = initToken(ch);
                        }
                        break;
                    case Id_INT2:
                        if (ch == 't'){
                            // int age ，后面需要跟空格
                            state = DfaState.Id_INT3;
                            token.type = TokenType.Identifier;
                            tokenText.append(ch);
                        }else if (isAlpha(ch) || isDigit(ch)){
                            tokenText.append(ch);
                        }else {
                            state = initToken(ch);
                        }
                        break;
                        // int age or intaged
                    case Id_INT3:
                        if (isBlank(ch)){
                            token.type = TokenType.Int; // 前面的变量是 int 类型
                            state = initToken(ch);
                        }else {
                            // intaofjdoaf
                            state = DfaState.Id;
                            tokenText.append(ch);
                        }
                        break;
                    default:
                }
            }
            // 如果 tokenText > 0 那么就说明 token 还没有被放进去，因为放到 tokenlist 里面就会清空
            // 最后一个 token 的话要把它放到tokenlist 里面去
            if (tokenText.length()>0){
                initToken(ch);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return new SimpleTokenReader(tokenList);
    }


    private static void dump(SimpleTokenReader simpleTokenReader){

        System.out.println("text\ttype");
        Token token = null;
        while ((token= simpleTokenReader.read())!=null){

            System.out.println(token.getText()+"\t\t"+token.getType());
        }


    }


    private class SimpleTokenReader implements TokenReader{
        private List<SimpleToken> tokenList = null;
        private int position=0;

        public SimpleTokenReader(List<SimpleToken> tokenList) {
            this.tokenList = tokenList;
        }

        @Override
        public Token read() {
            // 需要做一个判断，如果读到末尾了就不读了
            if (position < tokenList.size()){
                return tokenList.get(position++);
            }
            return null;
        }

        @Override
        public Token peek() {
            if (position < tokenList.size()){
                return tokenList.get(position);
            }
            return null;
        }

        @Override
        public void unread() {
            if (position>0){
                position--;
            }
        }

        @Override
        public int getPosition() {
            return position;
        }

        @Override
        public void setPosition(int position) {
            if (position >= 0 && position < tokenList.size())
            this.position = position;
        }
    }







    // 存储 token 的值 和 类型
    private final class SimpleToken implements Token{

        private String text = null;
        private TokenType type = null;

        @Override
        public TokenType getType() {
            return type;
        }

        @Override
        public String getText() {
            return text;
        }
    }

    // 先定义状态机.. enum 不是类...
    private enum DfaState{
        // 标示状态机的初始化
        Initial,

        // 数字
        IntLiteral,
        // 符号，>= <= > <
        GT,GE, // GE >=
        Assignment, // =

        // 保留字，int 这里只做int， if else if 这些不去管
        INT, // 识别出来是 int 类型
        Id_INT1, // => n
        Id_INT2, // => t
        Id_INT3, // blankt/switch

        // 用户定义的变量
        Identifier,Id,

        }
}
