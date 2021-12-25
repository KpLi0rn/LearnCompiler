package class2

type EnumType int

const (
	Plus=iota   // +
	Minus  // -
	Star   // *
	Slash  // /

	GE     // >=
	GT     // >
	EQ     // ==
	LE     // <=
	LT     // <

	SemiColon // ;
	LeftParen // (
	RightParen// )

	Assignment // =

	If
	Else

	Int

	Identifier     //标识符

	IntLiteral     //整型字面量
	StringLiteral   //字符串字面量


	// 状态机状态
	Initial

	// 保留字，int 这里只做int， if else if 这些不去管
	INT // 识别出来是 int 类型
	Id_INT1 // => n
	Id_INT2 // => t
	Id_INT3 // blankt/switch

	// 用户定义的变量
	Id
)

var TokenType2Str = map[EnumType]string{
	Plus: 				"Plus",
	Minus: 				"Minus",
	Star: 				"Star",
	Slash: 				"Slash",
	GE: 				"GE",
	GT: 				"GT",
	EQ: 				"EQ",
	LE: 				"LE",
	LT: 				"LT",
	If: 				"If",
	Else: 				"Else",
	Int: 				"Int",
	SemiColon: 			"SemiColon",
	LeftParen: 			"LeftParen",
	RightParen: 		"RightParen",
	Assignment: 		"Assignment",
	Identifier: 		"Identifier",
	IntLiteral: 		"IntLiteral",
	StringLiteral: 		"StringLiteral",
}
