package lexer

import (
	"testing"
)

func TestTokenize(t *testing.T) {
	code := "int num=1+2+3;"
	tokenReader := Tokenize(code)
	Dump(tokenReader)
}
