package src;

import java.util.*;

import src.essentials.*;
import src.nodes.*;
public class Interpreter
{
    int returnNumber;
    boolean loopBreak, loopContinue, canReturn;
    public void reset()
    {
        loopBreak = false;
        loopContinue = false;
    }
    public void func_reset()
    {
        canReturn = false;
        returnNumber = 0;
    }
    public Token visit(Node node, SymbolTable symbolTable) throws Exception
    {
        var nodeClass = node.getClass();
        var symbolClass = symbolTable.getClass();
        var method = this.getClass().getDeclaredMethod("visit", nodeClass, symbolClass);
        return (Token)method.invoke(this, node, symbolTable);
    }

    public Token visit(StatementNode node, SymbolTable symbolTable) throws Exception
    {
        var statements = node.statements;
        var tokens = new ArrayList<Token>();
        var itr = statements.iterator();

        while (itr.hasNext())
        {
            if (loopBreak)
                break;

            var statement = itr.next();
            tokens.add(visit(statement, symbolTable));
        }
        var value = new Value(tokens);
        return new Token(TokenType.LIST, value);
    }

    public Token visit(NumberNode node, SymbolTable symbolTable)
    {
        return node.token;
    }

    public Token visit(StringNode node, SymbolTable symbolTable)
    {
        return node.token;
    }

    public Token visit(NoneTypeNode node, SymbolTable symbolTable)
    {
        return new Token(TokenType.KEYWORD, new Value("none"));
    }

    public Token visit(ListAccessNode node, SymbolTable symbolTable) throws Exception
    {
        var varName = visit(node.left, symbolTable);
        var index = visit(node.right, symbolTable);
        int i = Integer.parseInt(index.value.string);
        return varName.value.list.get(i);
    }

    public Token visit(VarAccessNode node, SymbolTable symbolTable) throws Exception
    {
        var varName = node.token.value;
        var value = symbolTable.get(varName.string);

        if (value != null)
            return value;
        throw new Exception("NameError: name "
                            + "'" + varName.string + "'"
                            + " is not defined");
    }

    public Token visit(VarAssignNode node, SymbolTable symbolTable) throws Exception
    {
        var varName = node.left.token.value;
        var value = visit(node.right, symbolTable);
        symbolTable.set(varName.string, value);
        return value;
    }

    public Token visit(IfNode node, SymbolTable symbolTable) throws Exception
    {
        for (var pair : node.cases)
        {
            var conditionValue = visit(pair.key, symbolTable);

            if (conditionValue.isTrue())
            {
                var exprValue = visit(pair.value, symbolTable);

                return exprValue;
            }
        }

        if (node.elseCase != null)
        {
            var elseValue = visit(node.elseCase, symbolTable);
            return elseValue;
        }

        return new Token(TokenType.KEYWORD, new Value("none"));
    }

    public Token visit(ForNode node, SymbolTable symbolTable) throws Exception
    {
        reset();
        var start = visit(node.startValueNode, symbolTable);
        var end = visit(node.endValueNode, symbolTable);
        var step = node.stepValueNode;
        var varName = node.varNameTok;

        ArrayList<Token> tokens = new ArrayList<Token>();
        symbolTable.set(varName.value.string, start);
        if (start.type == TokenType.INT && end.type == TokenType.INT)
        {
            var value = Integer.parseInt(symbolTable.get(varName.value.string).value.string);

            while (value < Integer.parseInt(end.value.string))
            {

                var val = Integer.parseInt(visit(step, symbolTable).value.string);
                var result = visit(node.bodyNode, symbolTable);

                symbolTable.set(varName.value.string, new Token(TokenType.INT, new Value(String.valueOf(val))));
                value = val;
                if (loopBreak)
                    break;
                if (loopContinue)
                {
                    loopContinue = false;
                    continue;
                }
                tokens.add(result);
            }
        }
        return new Token(TokenType.LIST, new Value(tokens));
    }

    public Token visit(WhileNode node, SymbolTable symbolTable) throws Exception
    {
        reset();

        ArrayList<Token> tokens = new ArrayList<Token>();
        while (true)
        {
            var condition = visit(node.conditionNode, symbolTable);

            if (!condition.isTrue())
                break;
            var result = visit(node.bodyNode, symbolTable);
            if (loopBreak)
                break;
            if (loopContinue)
            {
                loopContinue = false;
                continue;
            }
            tokens.add(result);
        }

        return new Token(TokenType.LIST, new Value(tokens));
    }
    public Token visit(FuncDefNode node, SymbolTable symbolTable)
    {
        var varNameTok = node.varNameTok.value;
        var argNameToks = node.argNameToks;
        var bodyNode = node.bodyNode;
        Value tok_value = new Value(argNameToks, bodyNode);
        Token value = new Token(TokenType.FUNCTION, tok_value);
        symbolTable.set(varNameTok.string, value);
        return new Token(TokenType.STRING, new Value("<Function defined>"));
    }

    public Token visit(CallNode node, SymbolTable symbolTable) throws Exception
    {
        func_reset();
        canReturn = true;
        if (BuiltInFunc.list.contains(node.nodeToCall.token.value.string))
        {
            var name = node.nodeToCall.token.value;
            var itr = node.argNodes.iterator();
            var argNameToks = new ArrayList<Token>();
            while (itr.hasNext())
            {
                argNameToks.add(visit(itr.next(), symbolTable));
            }
            var func = new BuiltInFunc().getClass().getDeclaredMethod("fun" + name.string, argNameToks.getClass());
            return (Token)func.invoke(new BuiltInFunc(), argNameToks);
        }
        else
        {
            var value = visit(node.nodeToCall, symbolTable);
            var itr = node.argNodes.iterator();
            if (node.argNodes.size() == value.value.argNameToks.size())
            {
                for (var args : value.value.argNameToks)
                {
                    symbolTable.set(args.value.string, visit(itr.next(), symbolTable));
                }
            }
            var result = visit(value.value.bodyNode, symbolTable);
            func_reset();
            System.out.println(result.getStr());
            return result;
        }
    }

    public Token visit(ReturnNode node, SymbolTable symbolTable) throws Exception
    {
        if (canReturn)
        {
            returnNumber++;
            if (returnNumber == 1)
                return visit(node.returnNode, symbolTable);
            else
                throw new Exception("Too many return statements");
        }
        throw new Exception("return not applicable");
    }
    public Token visit(BreakNode node, SymbolTable symbolTable)
    {
        loopBreak = true;
        return new Token(TokenType.KEYWORD, new Value("none"));
    }
    public Token visit(ContinueNode node, SymbolTable symbolTable)
    {
        loopContinue = true;
        return new Token(TokenType.KEYWORD, new Value("none"));
    }

    public Token visit(ListNode node, SymbolTable symbolTable) throws Exception
    {
        var elements = node.elementNodes;
        var itr = elements.iterator();
        ArrayList<Token> tokens = new ArrayList<Token>();
        while (itr.hasNext())
        {
            tokens.add(visit(itr.next(), symbolTable));
        }
        var value = new Value(tokens);
        return new Token(TokenType.LIST, value);
    }

    public Token visit(BinOpNode node, SymbolTable symbolTable) throws Exception
    {
        var left = visit(node.left, symbolTable);

        if (node.token.matches(TokenType.KEYWORD, new Value("and")))
        {
            return !left.isTrue() ? left : visit(node.right, symbolTable);
        }
        else if (node.token.matches(TokenType.KEYWORD, new Value("or")))
        {
            return left.isTrue() ? left : visit(node.right, symbolTable);
        }

        var right = visit(node.right, symbolTable);

        if (left.type == TokenType.LIST || right.type == TokenType.LIST)
        {
            if (left.type == TokenType.LIST && right.type == TokenType.LIST)
            {
                if (node.token.type == TokenType.PLUS)
                {
                    var result = left.value.list;
                    result.addAll(right.value.list);
                    return new Token(TokenType.LIST, new Value(result));
                }
            }
            else if (left.type == TokenType.LIST)
            {
                if (right.type == TokenType.INT)
                {
                    var rval = Integer.parseInt(right.value.string);
                    if (node.token.type == TokenType.MINUS)
                    {
                        var result = left.value.list;
                        result.remove(rval);
                        return new Token(TokenType.LIST, new Value(result));
                    }
                    if (node.token.type == TokenType.PLUS)
                    {
                        var result = left.value.list;
                        result.add(right);
                        return new Token(TokenType.LIST, new Value(result));
                    }
                    if (node.token.type == TokenType.DIV)
                    {
                        var result = left.value.list;
                        return result.get(rval);
                    }
                }
                if (right.type == TokenType.FLOAT)
                {
                    if (node.token.type == TokenType.PLUS)
                    {
                        var result = left.value.list;
                        result.add(right);
                        return new Token(TokenType.LIST, new Value(result));
                    }
                }
                if (right.type == TokenType.STRING)
                {
                    if (node.token.type == TokenType.PLUS)
                    {
                        var result = left.value.list;
                        result.add(right);
                        return new Token(TokenType.LIST, new Value(result));
                    }
                }
            }
            else if (right.type == TokenType.LIST)
            {
                if (left.type == TokenType.INT)
                {
                    var rval = Integer.parseInt(left.value.string);
                    if (node.token.type == TokenType.MINUS)
                    {
                        var result = right.value.list;
                        result.remove(rval);
                        return new Token(TokenType.LIST, new Value(result));
                    }
                    if (node.token.type == TokenType.PLUS)
                    {
                        var result = right.value.list;
                        result.add(left);
                        return new Token(TokenType.LIST, new Value(result));
                    }
                    if (node.token.type == TokenType.DIV)
                    {
                        var result = right.value.list;
                        return result.get(rval);
                    }
                }
                if (left.type == TokenType.FLOAT)
                {
                    if (node.token.type == TokenType.PLUS)
                    {
                        var result = right.value.list;
                        result.add(left);
                        return new Token(TokenType.LIST, new Value(result));
                    }
                }
                if (left.type == TokenType.STRING)
                {
                    if (node.token.type == TokenType.PLUS)
                    {
                        var result = right.value.list;
                        result.add(left);
                        return new Token(TokenType.LIST, new Value(result));
                    }
                }
            }
        }

        if ((left.type == TokenType.INT || left.type == TokenType.FLOAT) &&
            (right.type == TokenType.INT || right.type == TokenType.FLOAT))
        {
            if (left.type == TokenType.INT && right.type == TokenType.INT)
            {
                var leftVal = Integer.parseInt(left.value.string);
                var rightVal = Integer.parseInt(right.value.string);

                if (node.token.type == TokenType.PLUS)
                {
                    return new Token(TokenType.INT, new Value(String.valueOf(leftVal + rightVal)));
                }
                else if (node.token.type == TokenType.MINUS)
                {
                    return new Token(TokenType.INT, new Value(String.valueOf(leftVal - rightVal)));
                }
                else if (node.token.type == TokenType.MUL)
                {
                    return new Token(TokenType.INT, new Value(String.valueOf(leftVal * rightVal)));
                }
                else if (node.token.type == TokenType.DIV || node.token.type == TokenType.INTDIV)
                {
                    if (rightVal == 0)
                        throw new Exception("ZeroDivisionError: division or integer division by zero");

                    var res = (double)leftVal / rightVal;

                    if (node.token.type == TokenType.INTDIV)
                        return new Token(TokenType.INT, new Value(String.valueOf((int)Math.floor(res))));
                    else
                        return new Token(TokenType.FLOAT, new Value(String.valueOf(res)));
                }
                else if (node.token.type == TokenType.MOD)
                {
                    if (rightVal == 0)
                        throw new Exception("ZeroDivisionError: integer modulo by zero");

                    return new Token(TokenType.INT, new Value(String.valueOf((int)Mathf.modulus(leftVal, rightVal))));
                }
                else if (node.token.type == TokenType.POW)
                {
                    return new Token(TokenType.INT, new Value(String.valueOf((int)Math.pow(leftVal, rightVal))));
                }
                else if (node.token.type == TokenType.EE)
                {
                    return new Token(TokenType.INT, new Value(String.valueOf(leftVal == rightVal ? 1 : 0)));
                }
                else if (node.token.type == TokenType.NE)
                {
                    return new Token(TokenType.INT, new Value(String.valueOf(leftVal != rightVal ? 1 : 0)));
                }
                else if (node.token.type == TokenType.LT)
                {
                    return new Token(TokenType.INT, new Value(String.valueOf(leftVal < rightVal ? 1 : 0)));
                }
                else if (node.token.type == TokenType.GT)
                {
                    return new Token(TokenType.INT, new Value(String.valueOf(leftVal > rightVal ? 1 : 0)));
                }
                else if (node.token.type == TokenType.LTE)
                {
                    return new Token(TokenType.INT, new Value(String.valueOf(leftVal <= rightVal ? 1 : 0)));
                }
                else if (node.token.type == TokenType.GTE)
                {
                    return new Token(TokenType.INT, new Value(String.valueOf(leftVal >= rightVal ? 1 : 0)));
                }
            }
            else
            {
                var leftVal = Double.parseDouble(left.value.string);
                var rightVal = Double.parseDouble(right.value.string);

                if (node.token.type == TokenType.PLUS)
                {
                    return new Token(TokenType.FLOAT, new Value(String.valueOf(leftVal + rightVal)));
                }
                else if (node.token.type == TokenType.MINUS)
                {
                    return new Token(TokenType.FLOAT, new Value(String.valueOf(leftVal - rightVal)));
                }
                else if (node.token.type == TokenType.MUL)
                {
                    return new Token(TokenType.FLOAT, new Value(String.valueOf(leftVal * rightVal)));
                }
                else if (node.token.type == TokenType.DIV || node.token.type == TokenType.INTDIV)
                {
                    if (rightVal == 0)
                        throw new Exception("ZeroDivisionError: float division or float floor division by zero");

                    var res = leftVal / rightVal;

                    if (node.token.type == TokenType.INTDIV)
                        return new Token(TokenType.FLOAT, new Value(String.valueOf(Math.floor(res))));
                    else
                        return new Token(TokenType.FLOAT, new Value(String.valueOf(res)));
                }
                else if (node.token.type == TokenType.MOD)
                {
                    if (rightVal == 0)
                        throw new Exception("ZeroDivisionError: float modulo");

                    return new Token(TokenType.FLOAT, new Value(String.valueOf(Mathf.modulus(leftVal, rightVal))));
                }
                else if (node.token.type == TokenType.POW)
                {
                    var res = Math.pow(leftVal, rightVal);

                    if (Double.isNaN(res))
                        throw new Exception("MathError: imaginary result is not supported yeeeeeet");

                    return new Token(TokenType.FLOAT, new Value(String.valueOf(res)));
                }
                else if (node.token.type == TokenType.EE)
                {
                    return new Token(TokenType.INT, new Value(String.valueOf(leftVal == rightVal ? 1 : 0)));
                }
                else if (node.token.type == TokenType.NE)
                {
                    return new Token(TokenType.INT, new Value(String.valueOf(leftVal != rightVal ? 1 : 0)));
                }
                else if (node.token.type == TokenType.LT)
                {
                    return new Token(TokenType.INT, new Value(String.valueOf(leftVal < rightVal ? 1 : 0)));
                }
                else if (node.token.type == TokenType.GT)
                {
                    return new Token(TokenType.INT, new Value(String.valueOf(leftVal > rightVal ? 1 : 0)));
                }
                else if (node.token.type == TokenType.LTE)
                {
                    return new Token(TokenType.INT, new Value(String.valueOf(leftVal <= rightVal ? 1 : 0)));
                }
                else if (node.token.type == TokenType.GTE)
                {
                    return new Token(TokenType.INT, new Value(String.valueOf(leftVal >= rightVal ? 1 : 0)));
                }
            }
        }
        else if (left.type == TokenType.STRING && right.type == TokenType.STRING)
        {
            if (node.token.type == TokenType.PLUS)
            {
                return new Token(TokenType.STRING, new Value(left.value.string + right.value.string));
            }
            else if (node.token.type == TokenType.EE)
            {
                return new Token(TokenType.INT,
                                 new Value(String.valueOf(left.value.string.equals(right.value.string) ? 1 : 0)));
            }
            else if (node.token.type == TokenType.NE)
            {
                return new Token(TokenType.INT,
                                 new Value(String.valueOf(!left.value.string.equals(right.value.string) ? 1 : 0)));
            }
            else if (node.token.type == TokenType.LT)
            {
                return new Token(TokenType.INT, new Value(String.valueOf(
                                                    left.value.string.compareTo(right.value.string) < 0 ? 1 : 0)));
            }
            else if (node.token.type == TokenType.GT)
            {
                return new Token(TokenType.INT, new Value(String.valueOf(
                                                    left.value.string.compareTo(right.value.string) > 0 ? 1 : 0)));
            }
            else if (node.token.type == TokenType.LTE)
            {
                return new Token(TokenType.INT, new Value(String.valueOf(
                                                    left.value.string.compareTo(right.value.string) <= 0 ? 1 : 0)));
            }
            else if (node.token.type == TokenType.GTE)
            {
                return new Token(TokenType.INT, new Value(String.valueOf(
                                                    left.value.string.compareTo(right.value.string) >= 0 ? 1 : 0)));
            }
        }
        else if (left.matches(TokenType.KEYWORD, new Value("none")) &&
                 right.matches(TokenType.KEYWORD, new Value("none")))
        {
            if (node.token.type == TokenType.EE)
            {
                return new Token(TokenType.INT, new Value(String.valueOf(1)));
            }
            else if (node.token.type == TokenType.NE)
            {
                return new Token(TokenType.INT, new Value(String.valueOf(0)));
            }
        }
        if (node.token.type == TokenType.EE)
        {
            return new Token(TokenType.INT, new Value(String.valueOf(0)));
        }
        else if (node.token.type == TokenType.NE)
        {
            return new Token(TokenType.INT, new Value(String.valueOf(1)));
        }
        else if ((left.type == TokenType.STRING && right.type == TokenType.INT) ||
                 (left.type == TokenType.INT && right.type == TokenType.STRING))
        {
            if (node.token.type == TokenType.MUL)
            {
                var cond = left.type == TokenType.INT;
                var str = cond ? right.value : left.value;
                var count = Integer.parseInt(cond ? left.value.string : right.value.string);
                var res = "";

                for (int i = 0; i < count; i++)
                    res += str;

                return new Token(TokenType.STRING, new Value(res));
            }
        }

        throw new Exception("TypeError: unsupported operand type(s) for " + node.token.getStr() + ": "
                            + "'" + left.getStr() + "'"
                            + " and "
                            + "'" + right.getStr() + "'");
    }

    public Token visit(UnaryOpNode node, SymbolTable symbolTable) throws Exception
    {
        var operand = visit(node.right, symbolTable);

        if (node.token.matches(TokenType.KEYWORD, new Value("not")))
        {
            return new Token(TokenType.INT, new Value(String.valueOf(!operand.isTrue() ? 1 : 0)));
        }
        else if (operand.type == TokenType.INT || operand.type == TokenType.FLOAT)
        {
            if (node.token.type == TokenType.MINUS)
            {
                if (operand.type == TokenType.INT)
                {
                    var res = -Integer.parseInt(operand.value.string);
                    return new Token(TokenType.INT, new Value(String.valueOf(res)));
                }
                else
                {
                    var res = -Double.parseDouble(operand.value.string);
                    return new Token(TokenType.FLOAT, new Value(String.valueOf(res)));
                }
            }

            return operand;
        }

        throw new Exception("TypeError: bad operand type for unary " + node.token.getStr() + ": " + operand.getStr());
    }
}
