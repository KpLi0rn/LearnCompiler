package lexer;

public enum DfaState{
    // 标示状态机的初始化
    Initial,

    // 数字
    IntLiteral,
    // 符号，>= <= > <
    GT,GE, // GE >=
    Assignment, // =
    Plus, // +
    SemiColon, // ;
    Minus,  // -
    Star,   // *
    Slash,  // /

    // 保留字，int 这里只做int， if else if 这些不去管
    INT, // 识别出来是 int 类型
    Id_INT1, // => n
    Id_INT2, // => t
    Id_INT3, // blankt/switch

    // 用户定义的变量
    Identifier,Id,
}

