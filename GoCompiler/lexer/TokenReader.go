package lexer

import "fmt"

type TokenReader interface {
	Peek() EnumType
	Read() EnumType
	GetPosition()  int
}

type SimpleTokenReader struct{
	tokens []SimpleToken
	position int
}


func NewSimpleTokenReader(tokens []SimpleToken) *SimpleTokenReader{
	return &SimpleTokenReader{
		tokens: tokens,
		position: 0,
	}
}

func (s *SimpleTokenReader) getPosition() int{
	return s.position
}

// 预读，就是读了之后坐标不进行改变
func (s *SimpleTokenReader) Peek() SimpleToken{
	if s.position < len(s.tokens) {
		return s.tokens[s.position]
	}
	return NullToken()
}

func (s *SimpleTokenReader) Read() SimpleToken{
	if s.position < len(s.tokens) {
		token := s.tokens[s.position]
		s.position ++
		return token
	}
	return NullToken()
}

func (s *SimpleTokenReader) UnRead() {
	if s.position >= 0 {
		s.position --
	}
}

func Dump(reader *SimpleTokenReader){
	for _,token := range reader.tokens{
		fmt.Println(fmt.Sprintf("%s\t%s",token.GetType(),token.GetText()))
	}
}
/**
	tokenize 返回的是 Token 结构体，需要一个 TokenReader 来接收
 */

