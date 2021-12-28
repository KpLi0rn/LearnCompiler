package lexer

type Token interface {
	GetType() string
	GetText() string
}

type SimpleToken struct {
	Type string
	Text string
}

func NewSimpleToken(t string,text string) *SimpleToken{
	return &SimpleToken{
		Type: t,
		Text: text,
	}
}

func NullToken() SimpleToken {
	return *NewSimpleToken("","")
}

func (s *SimpleToken) GetText() string {
	return s.Text
}

func (s *SimpleToken) GetType() string {
	return s.Type
}
