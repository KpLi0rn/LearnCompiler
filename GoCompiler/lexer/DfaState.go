package lexer


var DfaState2Str = map[EnumType]string{
	GT:				"GT",
	GE: 			"GT",
	INT: 			"INT",
	Id: 			"Id",
	Id_INT1: 		"Id_INT1",
	Id_INT2: 		"Id_INT2",
	Id_INT3: 		"Id_INT3",
	Initial: 		"Initial",
	SemiColon: 		"SemiColon",
	IntLiteral: 	"IntLiteral",
	Identifier: 	"Identifier",
	Assignment: 	"Assignment",

	Plus:   		"Plus", 		// +
	Minus:			"Minus",  		// -
	Star:			"Star",   		// *
	Slash:			"Slash",  		// /
}


