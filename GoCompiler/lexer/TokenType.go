package lexer

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
