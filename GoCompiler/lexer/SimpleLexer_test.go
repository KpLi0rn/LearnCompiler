package lexer

import (
	"testing"
)

func TestTokenize(t *testing.T) {
	code := "int age=18;"
	tokenReader := Tokenize(code)
	Dump(tokenReader)
}
