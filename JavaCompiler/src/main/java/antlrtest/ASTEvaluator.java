package antlrtest;

import antlrtest.PlayScriptParser.AdditiveExpressionContext;
import antlrtest.PlayScriptParser.LiteralContext;
import antlrtest.PlayScriptParser.MultiplicativeExpressionContext;
import antlrtest.PlayScriptParser.PrimaryExpressionContext;

/**
 * 一个Vistor类，只简单的实现了整数的加减乘除。
 */
public class ASTEvaluator extends PlayScriptBaseVisitor<Integer> {

    /**
     * 相当于最后面的那个 switch case 根据不同表达式来进行不同的运算
     * 相当于遍历树节点的每个 node 然后根据node的对应类型进行运算
     * @param ctx
     * @return
     */

    @Override
    public Integer visitAdditiveExpression(AdditiveExpressionContext ctx) {
        if (ctx.ADD() != null || ctx.SUB() != null) {
            Integer left = visitAdditiveExpression(ctx.additiveExpression());
            Integer right = visitMultiplicativeExpression(ctx.multiplicativeExpression());
            if (ctx.ADD() != null) {
                return left + right;
            } else {
                return left - right;
            }
        } else {
            return visitMultiplicativeExpression(ctx.multiplicativeExpression());
        }
    }

    @Override
    public Integer visitMultiplicativeExpression(MultiplicativeExpressionContext ctx) {
        if (ctx.MUL() != null || ctx.DIV() != null || ctx.MOD() != null) {
            Integer left = visitMultiplicativeExpression(ctx.multiplicativeExpression());
            Integer right = visitPrimaryExpression(ctx.primaryExpression());
            if (ctx.MUL() != null) {
                return left * right;
            } else if (ctx.DIV() != null) {
                return left / right;
            } else {
                return left % right;
            }
        } else {
            return visitPrimaryExpression(ctx.primaryExpression());
        }
    }

    @Override
    public Integer visitPrimaryExpression(PrimaryExpressionContext ctx) {
        if (ctx.literal() != null) {
            return visitLiteral(ctx.literal());
        }
        return 0;
    }

    @Override
    public Integer visitLiteral(LiteralContext ctx) {
        if (ctx.IntegerLiteral() !=null){
            return Integer.valueOf(ctx.IntegerLiteral().getText());
        }
        return 0;
    }

}
