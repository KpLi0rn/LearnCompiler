package lexer

import (
	"testing"
)

func TestTokenize(t *testing.T) {
	code := "2+3*5-3/8"
	tokenReader := Tokenize(code)
	Dump(tokenReader)
}
