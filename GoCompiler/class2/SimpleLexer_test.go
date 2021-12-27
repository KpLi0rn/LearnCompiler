package class2

import (
	"fmt"
	"testing"
)

func TestTokenize(t *testing.T) {
	code := "int age >= 18;"
	list := Tokenize(code)
	for _,value := range list{
		fmt.Println(value.GetType(),value.GetText())
	}
}
