package calc

import (
	"GoCompiler/ast"
	"GoCompiler/lexer"
	"errors"
	"fmt"
	"log"
	"strconv"
)

func run(){
	code := "8-4"
	calc := NewMyCalculator()
	calc.Evaluate(code)
}

type Calculator interface {
	Additive() ast.AstNode // 加法表达式
	Multiplicative() ast.AstNode // 乘法表达式
	Primary() ast.AstNode // 常量表达式
	Evaluate(code string)
	Parse(code string) lexer.TokenReader
	AstDump(node ast.AstNode)
	EvaluateAll(node ast.AstNode) int
}

type MyCalculator struct {
}

func NewMyCalculator() *MyCalculator{
	return &MyCalculator{}
}

func (c *MyCalculator)Evaluate(code string){
	node := c.Parse(code)
	c.AstDump(node,"")
	result := c.EvaluateAll(node)
	fmt.Println(fmt.Sprintf("Result: %d",result))
}

func (c *MyCalculator)Parse(code string) ast.AstNode {
	reader := lexer.Tokenize(code)
	node := ast.NewSimpleAstNode(ast.Programm,"Calculator")
	child := c.Additive(reader)
	if child != ast.NullNode() {
		node.AddChild(child)
	}
	return node
}

func (c *MyCalculator)AstDump(node ast.AstNode,indent string){
	fmt.Println(fmt.Sprintf("%s%s: %s",indent,ast.NodeType2Str[node.GetType()],node.GetText()))
	for _,child := range node.GetChilds(){
		c.AstDump(child,indent+"\t")
	}
}

func (c *MyCalculator) EvaluateAll(node ast.AstNode) int {
	result := 0
	switch node.GetType() {
	case ast.Programm:
		for _,child := range node.GetChilds(){
			result = c.EvaluateAll(child)
		}
		break
		// 加法表达式
	case ast.Additive:
		child1 := node.GetChilds()[0] // 如果有子集
		value1 := c.EvaluateAll(child1)
		child2 := node.GetChilds()[1] // 如果有子集
		value2 := c.EvaluateAll(child2)
		if node.GetText() == "+" {
			result = value1 + value2
		}else if node.GetText() == "-" {
			result = value1 - value2
		}
		break
	case ast.Multiplicative:
		child1 := node.GetChilds()[0] // 如果有子集
		value1 := c.EvaluateAll(child1)
		child2 := node.GetChilds()[1] // 如果有子集
		value2 := c.EvaluateAll(child2)
		if node.GetText() == "*" {
			result = value1 * value2
		}else if node.GetText() == "/" {
			result = value1 / value2
		}
		break
	case ast.IntLiteral:
		result,_ = strconv.Atoi(node.GetText())
		break
	}
	return result
}

func (c *MyCalculator) Additive(reader *lexer.SimpleTokenReader) ast.AstNode {
	child1 := c.Multiplicative(reader)
	node := child1
	if child1 != ast.NullNode() {
		for{
			token := reader.Peek() // 预读
			if token != lexer.NullToken() && (token.GetType() == lexer.TokenType2Str[lexer.Plus] || token.GetType() == lexer.TokenType2Str[lexer.Minus]){
				token = reader.Read()
				child2 := c.Multiplicative(reader)
				node = ast.NewSimpleAstNode(ast.Additive,token.GetText()) // +
				node.AddChild(child1)
				node.AddChild(child2)
				child1 = node
			}else {
				break
			}
		}
	}
	return node
}

// mul = int|mul*int
func (c *MyCalculator)Multiplicative(reader *lexer.SimpleTokenReader) ast.AstNode {
	child1 := c.Primary(reader) // int
	node := child1
	token := reader.Peek()
	if token != lexer.NullToken() && node != ast.NullNode() {
		if token.GetType() == lexer.TokenType2Str[lexer.Star] || token.GetType() == lexer.TokenType2Str[lexer.Slash] {
			token = reader.Read()
			child2 := c.Multiplicative(reader) // 这里顺序有点问题 之前写错了
			if child2 != nil {
				node = ast.NewSimpleAstNode(ast.Multiplicative,token.GetText())
				node.AddChild(child1)
				node.AddChild(child2)
			}else {
				log.Fatalln(errors.New("expect right multiplicative syntax"))
			}
		}
	}
	return node
}

// 常量表达式
func (c *MyCalculator)Primary(reader *lexer.SimpleTokenReader) ast.AstNode {
	node := ast.NullNode()
	token := reader.Peek()
	if token != lexer.NullToken() {
		switch token.GetType() {
		case "IntLiteral": // 创建一个 Int 的 node 节点
			token = reader.Read()
			return ast.NewSimpleAstNode(ast.IntLiteral,token.GetText())
		}
	}
	return node
}


