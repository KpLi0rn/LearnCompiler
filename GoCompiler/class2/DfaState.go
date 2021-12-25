package class2

//// 状态机的变量要参与 switch case 这个适合利用 const
//type DfaState struct {
//	// 标示状态机的初始化
//	Initial
//
//	// 数字
//	IntLiteral
//	// 符号，>= <= > <
//	GT
//	GE // GE >=
//	Assignment // =
//
//	// 保留字，int 这里只做int， if else if 这些不去管
//	INT // 识别出来是 int 类型
//	Id_INT1 // => n
//	Id_INT2 // => t
//	Id_INT3 // blankt/switch
//
//	// 用户定义的变量
//	Identifier
//	Id
//}


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
}


