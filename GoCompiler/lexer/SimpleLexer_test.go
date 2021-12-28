package lexer

import (
	"testing"
)

func TestTokenize(t *testing.T) {
	code := "2+3*5"
	tokenReader := Tokenize(code)
	Dump(tokenReader)

	//for _,value := range list{
	//	fmt.Println(value.GetType(),value.GetText())
	//}
}
