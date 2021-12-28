package lexer

var (
	tokenText []rune // 保存临时的数据 ,我这个要数组然后一个个添加
	tokenList []SimpleToken	// 保存解析完毕的 token
	token SimpleToken
)

func isAlpha(ch rune) bool {
	return ch>= 'a' && ch <= 'z'|| ch >='A' && ch <= 'Z'
}

func isDigit(ch rune) bool {
	return ch >= '0' && ch <= '9'
}

func isBlank(ch rune) bool{
	return ch == ' ' || ch == '\t' || ch == '\n'
}

func initToken(ch rune) string {
	// 说明不是最开头，这时候 tokenText 中是已解析完成的
	if len(tokenText) >0 {
		token.Text = string(tokenText)
		tokenList = append(tokenList, token)
		// 清零
		tokenText = nil
		token = SimpleToken{}
	}

	// 对新的 token 进行解析/或从头开始解析
	state := DfaState2Str[Initial]
	if isAlpha(ch) {
		// 解析到关键字开头
		if ch == 'i' {
			state = DfaState2Str[Id_INT1]
		}else {
			state = DfaState2Str[Identifier]
		}
		token.Type = TokenType2Str[Identifier]
		tokenText = append(tokenText, ch)
	}else if isDigit(ch) {
		state = DfaState2Str[IntLiteral] // 数字
		token.Type = TokenType2Str[IntLiteral]
		tokenText = append(tokenText, ch)
	}else if ch == '>' {
		state = DfaState2Str[GT]
		token.Type = TokenType2Str[GT]
		tokenText = append(tokenText, ch)
	}else if ch == '=' {
		state = DfaState2Str[Assignment]
		token.Type = TokenType2Str[Assignment]
		tokenText = append(tokenText, ch)
	}else if ch == ';' {
		state = DfaState2Str[SemiColon]
		token.Type = TokenType2Str[SemiColon]
		tokenText = append(tokenText, ch)
	}else if ch == '+' {
		state = DfaState2Str[Plus]
		token.Type = TokenType2Str[Plus]
		tokenText = append(tokenText, ch)
	}else if ch == '-' {
		state = DfaState2Str[Minus]
		token.Type = TokenType2Str[Minus]
		tokenText = append(tokenText, ch)
	}else if ch == '*' {
		state = DfaState2Str[Star]
		token.Type = TokenType2Str[Star]
		tokenText = append(tokenText, ch)
	}else if ch == '/' {
		state = DfaState2Str[Slash]
		token.Type = TokenType2Str[Slash]
		tokenText = append(tokenText, ch)
	}
	return state
}

/**
	返回的应该是一个 Token ，然后我们可以定义一个 reader 去接受
*/
func Tokenize(code string) *SimpleTokenReader {

	// 变量初始化
	tokenText = nil
	token = SimpleToken{}
	tokenList = []SimpleToken{}
	codes := []rune(code)

	state := DfaState2Str[Initial]
	var ch rune
	for _,ch = range codes{ // range 是 index,char
		switch state {
		case "Initial":
			state = initToken(ch)
			break
		case "Assignment":
			fallthrough
		case "SemiColon":
			fallthrough
		case "GE":
			fallthrough
		case "Plus":   // +
			state = initToken(ch)
			break
		case "Minus":  // -
			state = initToken(ch)
			break
		case "Star":   // *
			state = initToken(ch)
			break
		case "Slash":  // /
			state = initToken(ch)
			break
		case "Identifier":
			if isDigit(ch) || isAlpha(ch){
				tokenText = append(tokenText, ch)
			}else {
				state = initToken(ch)
			}
			break
		case "IntLiteral":
			if isDigit(ch) {
				tokenText = append(tokenText, ch)
			}else {
				state = initToken(ch)
			}
			break

		case "Id_INT1":
			if ch == 'n' {
				state = DfaState2Str[Id_INT2]
				token.Type = TokenType2Str[Identifier] // 可以理解为中间量
				tokenText = append(tokenText, ch)
			}else if isAlpha(ch) || isDigit(ch) {
				tokenText = append(tokenText, ch)
			}else {
				state = initToken(ch)
			}
			break
		case "Id_INT2":
			if ch == 't' {
				state = DfaState2Str[Id_INT3]
				token.Type = TokenType2Str[Identifier]
				tokenText = append(tokenText, ch)
			}else if isAlpha(ch) || isDigit(ch) {
				tokenText = append(tokenText, ch)
			}else {
				state = initToken(ch)
			}
			break

		case "Id_INT3":
			if isBlank(ch) {
				// int xxx
				token.Type = TokenType2Str[Int]
				state = initToken(ch)
			} else {
				// 可以确定为 变量了
				// 因为可以确定不是 int 所以状态机进行变更
				state = DfaState2Str[Identifier]
				tokenText = append(tokenText, ch)
			}
			break
		case "GT":
			if ch == '=' {
				state = DfaState2Str[GE]
				token.Type = TokenType2Str[GE]
				tokenText = append(tokenText, ch)
			}else {
				state = initToken(ch)
			}
			break

		default:
		}
	}
	// 要在 for 外面 ... 不然的话 switch break 就会早最后面
	if len(tokenText) >0 {
		initToken(ch)
	}
	return NewSimpleTokenReader(tokenList);
}
