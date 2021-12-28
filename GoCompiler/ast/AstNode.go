package ast

type AstNode interface {

	GetText() string
	GetType() AstNodeType
	GetParent() AstNode
	GetChilds() []AstNode
	AddChild(node AstNode)
}

func NullNode() AstNode {
	return NewSimpleAstNode(Null,"")
}

type SimpleAstNode struct{
	Text string
	Type AstNodeType
	Parent AstNode
	Childs []AstNode
}

func NewSimpleAstNode(tp AstNodeType,text string) *SimpleAstNode{
	return &SimpleAstNode{
		Type: tp,
		Text: text,
		Parent: nil,
		Childs: nil,
	}
}


// 没继承接口...
func (s *SimpleAstNode) GetText() string{
	return s.Text
}

func (s *SimpleAstNode) GetType() AstNodeType{
	return s.Type
}

func (s *SimpleAstNode) GetParent() AstNode{
	return s.Parent
}

func (s *SimpleAstNode) GetChilds() []AstNode{
	return s.Childs
}

func (s *SimpleAstNode) AddChild(node AstNode){
	s.Childs = append(s.Childs, node)
	s.Parent = s
}