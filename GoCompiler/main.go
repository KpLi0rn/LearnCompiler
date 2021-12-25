package main

import (
	"GoCompiler/class2"
	"fmt"
)

func main()  {
	/**
		ps: 一个坑：go 和 java 的switch 不一样，java中 case xxx: 后没语句会默认执行下去
		go 则必须要加 fallthrough
	 */
	code := "int age >= 18;"
	list := class2.Tokenize(code)
	for _,value := range list{
		fmt.Println(value.GetType(),value.GetText())
	}
}
