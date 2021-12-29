package script

import (
	"GoCompiler/ast"
	"GoCompiler/lexer"
	"errors"
	"fmt"
	"log"
	"strconv"
)

var (
	varName = ""
	variables = make(map[string]int)
)

func run(){
	code := "num=1+2+3*6;"
	parse := NewSimpleParser()
	parse.Evaluate(code)

	// 要实现一个命令行的脚本
}

type SimpleParser interface {
	Additive() ast.AstNode // 加法表达式
	Multiplicative() ast.AstNode // 乘法表达式
	Primary() ast.AstNode // 常量表达式
	Evaluate(code string)
	Parse(code string) lexer.TokenReader
	AstDump(node ast.AstNode)
	EvaluateAll(node ast.AstNode) int
}

type MySimpleParser struct {
}

func NewSimpleParser() *MySimpleParser{
	return &MySimpleParser{
	}
}

func (c *MySimpleParser)Evaluate(code string){
	node := c.Parse(code)
	c.AstDump(node,"")
	result := c.EvaluateAll(node)
	fmt.Println(fmt.Sprintf("Result: %d",result))
}


func (c *MySimpleParser)Parse(code string) ast.AstNode {
	var node ast.AstNode
	tokens := lexer.Tokenize(code)
	//lexer.Dump(tokens)
	token := tokens.Peek()
	// 根据输入的不同情况来建立对应的语法树
	if (token != lexer.NullToken()){
		if token.GetType() == "Identifier" {
			node = c.assignmentStatement(tokens)			// num = xxx
		}else if token.GetType() == "IntLiteral" {
			node = c.Additive(tokens)
		}else if token.GetType() == "Int" {
			node = c.intDeclare(tokens)			// int num = xxx
		}
	}
	return node;
}

func (c *MySimpleParser)AstDump(node ast.AstNode,indent string){
	fmt.Println(fmt.Sprintf("%s%s: %s",indent,ast.NodeType2Str[node.GetType()],node.GetText()))
	for _,child := range node.GetChilds(){
		c.AstDump(child,indent+"\t")
	}
}

func (c *MySimpleParser) EvaluateAll(node ast.AstNode) int {
	result := 0
	switch node.GetType() {
	case ast.Identifier:
		varName = node.GetText()
		// if varName exist
		if _,ok := variables[varName]; ok {
			result = variables[varName]
		}else {
			for _,child := range node.GetChilds(){
				result = c.EvaluateAll(child)
			}
		}
		break
		// 加法表达式
	case ast.AssignmentStmt:
		if node.GetText() != "" {
			varName = node.GetText()
			variables[varName] = 0
		}else {
			errors.New(fmt.Sprintf("unknown variable: %s\n",varName))
		}
		break
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
		// num = xxx
		// 1+2+3+4+5..;
		result,_ = strconv.Atoi(node.GetText())
		if varName != "" { // 如果变量存在就从变量表里面进行获取
			value,_ := variables[varName]
			value = result + value
			variables[varName] = value
		}
		break
	}
	return result
}

/**
	num = 1+1+1;
 */
func (c *MySimpleParser) assignmentStatement(tokens *lexer.SimpleTokenReader) ast.AstNode{
	node := ast.NullNode()
	token := tokens.Peek()
	// 识别到了变量
	if token != lexer.NullToken() && token.GetType() == "Identifier" {
		tokens.Read()
		node = ast.NewSimpleAstNode(ast.Identifier,token.GetType())
		token = tokens.Peek()
		if token != lexer.NullToken() && token.GetType() == "Assignment" {
			tokens.Read()
			token = tokens.Peek()
			if token != lexer.NullToken() && token.GetType() == "IntLiteral"{
				child := c.Additive(tokens)
				if child != ast.NullNode() {
					node.AddChild(child)
					token = tokens.Peek()
					if token != lexer.NullToken() && token.GetType() == "SemiColon" {
						tokens.Read()
					}else {
						// 抛出报错 即期望有分号
						errors.New("invalid statement, expecting semicolon")
					}
				}
			}
		}else {
			// num;
			tokens.UnRead()
		}
	}
	return node
}

func (c *MySimpleParser) intDeclare(tokens *lexer.SimpleTokenReader) ast.AstNode{
	node := ast.NullNode()
	token := tokens.Peek()
	if token != lexer.NullToken() && token.GetType() == "Int"{ // 这种标识量直接消耗就好了
		tokens.Read()
		token = tokens.Peek()
		// 从变量开始创建树
		if token != lexer.NullToken() && token.GetType() == "Identifier"{
			tokens.Read()
			node = ast.NewSimpleAstNode(ast.Identifier,token.GetType())
			token = tokens.Peek()
			if token != lexer.NullToken() && token.GetType() == "Assignment" {
				tokens.Read()
				token = tokens.Peek()
				if token != lexer.NullToken() && token.GetType() == "IntLiteral"{
					child := c.Additive(tokens)
					if child != ast.NullNode() {
						node.AddChild(child)
						token = tokens.Peek()
						if token != lexer.NullToken() && token.GetType() == "SemiColon" {
							tokens.Read()
						}else {
							// 抛出报错 即期望有分号
							errors.New("invalid statement, expecting semicolon")
						}
					}
				}
			}else {
				// 回退
				tokens.UnRead()
				node = ast.NullNode() // 在 int num 中如果不为 等号 node 设为 null
			}
		}
	}
	return node
}

func (c *MySimpleParser) Additive(reader *lexer.SimpleTokenReader) ast.AstNode {
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
func (c *MySimpleParser)Multiplicative(reader *lexer.SimpleTokenReader) ast.AstNode {
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
func (c *MySimpleParser)Primary(reader *lexer.SimpleTokenReader) ast.AstNode {
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



