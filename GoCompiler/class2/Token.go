package class2


type Token interface {
	getType() string
	getText() string
}

// Type 为 TokenType
type SimpleToken struct {
	Type string
	Text string
}

func (simpleToken *SimpleToken) GetText() string {
	return simpleToken.Text
}

func (simpleToken *SimpleToken) GetType() string {
	return simpleToken.Type
}