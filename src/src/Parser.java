package src;
import java.util.ArrayList;

import src.essentials.Pair;
import src.essentials.Token;
import src.essentials.TokenType;
import src.essentials.Value;
import src.nodes.*;

public class Parser
{
    ArrayList<Token> tokens;
    int position;
    Token currentToken;

    Parser(ArrayList<Token> tokens)
    {
        this.tokens = tokens;
        position = -1;
        currentToken = null;
        advance();
    }

    void advance()
    {
        position++;
        currentToken = position < tokens.size() ? tokens.get(position) : null;
    }

    void backtrack()
    {
        position--;
        currentToken = position >= 0 ? tokens.get(position) : null;
    }

    Node parse() throws Exception
    {
        if (currentToken.type == TokenType.EOF)
            return new NoneTypeNode();

        var ast = statements();
        //System.out.println(currentToken.getStr());
        if (currentToken.type != TokenType.EOF)
            throw new Exception("SyntaxError: expected a newline or EOF");

        return ast;
    }
    Node statements() throws Exception
    {
        ArrayList<Node> statements = new ArrayList<Node>();

        while (currentToken != null && currentToken.type == TokenType.NEWLINE)
            advance();

        Node statement = statement();
        statements.add(statement);

        while (currentToken != null && (currentToken.type == TokenType.NEWLINE))
        {
            advance();

            if (currentToken.type != TokenType.EOF && currentToken.type != TokenType.RCURLY)
            {

                statement = statement();
                statements.add(statement);
            }
        }

        return new StatementNode(statements);
    }
    Node statement() throws Exception
    {
        if (currentToken != null && currentToken.matches(TokenType.KEYWORD, new Value("return")))
        {
            advance();
            var expr = expr();
            return new ReturnNode(expr);
        }
        if (currentToken != null && currentToken.matches(TokenType.KEYWORD, new Value("break")))
        {
            advance();
            return new BreakNode();
        }
        if (currentToken != null && currentToken.matches(TokenType.KEYWORD, new Value("continue")))
        {
            advance();
            return new ContinueNode();
        }
        Node statement = expr();
        return statement;
    }
    Node expr() throws Exception
    {
        if (currentToken != null)
        {
            if (currentToken.type == TokenType.IDENTIFIER)
            {
                var varName = currentToken;
                advance();

                if (currentToken != null && currentToken.type == TokenType.EQ)
                {
                    advance();
                    var expr = expr();
                    return new VarAssignNode(varName, expr);
                }
                else
                {
                    backtrack();
                }
            }
        }

        return or_expr();
    }

    Node or_expr() throws Exception
    {
        Node left = and_expr();

        while (currentToken != null && currentToken.matches(TokenType.KEYWORD, new Value("or")))
        {
            var op = currentToken;
            advance();
            var right = and_expr();
            left = new BinOpNode(left, op, right);
        }

        return left;
    }

    Node and_expr() throws Exception
    {
        Node left = comp_expr();

        while (currentToken != null && currentToken.matches(TokenType.KEYWORD, new Value("and")))
        {
            var op = currentToken;
            advance();
            var right = comp_expr();
            left = new BinOpNode(left, op, right);
        }

        return left;
    }

    Node comp_expr() throws Exception
    {
        if (currentToken != null && currentToken.matches(TokenType.KEYWORD, new Value("not")))
        {
            var unary = currentToken;
            advance();
            var right = comp_expr();
            return new UnaryOpNode(unary, right);
        }

        Node left = arith_expr();

        while (currentToken != null && (currentToken.type == TokenType.EE || currentToken.type == TokenType.NE ||
                                        currentToken.type == TokenType.LT || currentToken.type == TokenType.GT ||
                                        currentToken.type == TokenType.LTE || currentToken.type == TokenType.GTE))
        {
            var op = currentToken;
            advance();
            var right = arith_expr();
            left = new BinOpNode(left, op, right);
        }

        return left;
    }

    Node arith_expr() throws Exception
    {
        Node left = term();

        while (currentToken != null && (currentToken.type == TokenType.PLUS || currentToken.type == TokenType.MINUS))
        {
            var op = currentToken;
            advance();
            var right = term();
            left = new BinOpNode(left, op, right);
        }

        return left;
    }

    Node term() throws Exception
    {
        Node left = factor();

        while (currentToken != null && (currentToken.type == TokenType.MUL || currentToken.type == TokenType.DIV ||
                                        currentToken.type == TokenType.INTDIV || currentToken.type == TokenType.MOD))
        {
            var op = currentToken;
            advance();
            var right = factor();
            left = new BinOpNode(left, op, right);
        }

        return left;
    }

    Node factor() throws Exception
    {
        if (currentToken != null)
        {
            if (currentToken.type == TokenType.PLUS || currentToken.type == TokenType.MINUS)
            {
                var unary = currentToken;
                advance();
                var right = factor();
                return new UnaryOpNode(unary, right);
            }
        }

        return power();
    }

    Node power() throws Exception
    {
        Node left = call();

        while (currentToken != null && (currentToken.type == TokenType.POW))
        {
            var op = currentToken;
            advance();
            var right = factor();
            left = new BinOpNode(left, op, right);
        }

        return left;
    }

    Node call() throws Exception
    {
        var atom = atom();

        if (atom.getClass().equals(VarAccessNode.class) && currentToken != null &&
            currentToken.type == TokenType.LPAREN)
        {
            advance();
            var argNodes = new ArrayList<Node>();

            if (currentToken != null && currentToken.type == TokenType.RPAREN)
            {
                advance();
            }
            else
            {
                argNodes.add(expr());

                while (currentToken != null && currentToken.type == TokenType.COMMA)
                {
                    advance();
                    argNodes.add(expr());
                }

                if (currentToken == null || currentToken.type != TokenType.RPAREN)
                    throw new Exception("Expected ',' or ')'");

                advance();
            }

            return new CallNode(atom, argNodes);
        }

        return atom;
    }

    Node atom() throws Exception
    {
        if (currentToken != null)
        {

            if (currentToken.type == TokenType.INT || currentToken.type == TokenType.FLOAT)
            {
                var num = currentToken;
                advance();
                return new NumberNode(num);
            }
            else if (currentToken.type == TokenType.STRING)
            {
                var str = currentToken;
                advance();
                return new StringNode(str);
            }
            else if (currentToken.type == TokenType.IDENTIFIER)
            {
                var varName = currentToken;
                advance();
                if (currentToken != null && currentToken.type == TokenType.LSQAURE)
                {
                    advance();
                    var expr = expr();
                    if (currentToken != null && currentToken.type == TokenType.RSQUARE)
                    {
                        advance();
                        return new ListAccessNode(varName, expr);
                    }
                    throw new Exception("Expecting ']'");
                }
                return new VarAccessNode(varName);
            }
            else if (currentToken.matches(TokenType.KEYWORD, new Value("none")))
            {
                advance();
                return new NoneTypeNode();
            }

            else if (currentToken.type == TokenType.LPAREN)
            {
                advance();
                var expr = expr();

                if (currentToken != null && currentToken.type == TokenType.RPAREN)
                {
                    advance();
                    return expr;
                }
                else if (currentToken != null && currentToken.type == TokenType.COMMA)
                {
                    backtrack();
                    var argNodes = new ArrayList<Node>();

                    if (currentToken != null && currentToken.type == TokenType.RPAREN)
                    {
                        advance();
                    }
                    else
                    {
                        argNodes.add(expr());

                        while (currentToken != null && currentToken.type == TokenType.COMMA)
                        {
                            advance();
                            argNodes.add(expr());
                        }

                        if (currentToken == null || currentToken.type != TokenType.RPAREN)
                            throw new Exception("Expected ',' or ')'");

                        advance();
                    }

                    return new CallNode(new VarAccessNode(new Token(TokenType.IDENTIFIER, new Value("<anonymous>"))),
                                        argNodes);
                }

                throw new Exception("SyntaxError: expected a right parenthesis");
            }

            else if (currentToken.matches(TokenType.KEYWORD, new Value("if")))
            {
                var if_expr = if_expr();
                return if_expr;
            }
            else if (currentToken.matches(TokenType.KEYWORD, new Value("for")))
            {
                var for_expr = for_expr();
                return for_expr;
            }
            else if (currentToken.matches(TokenType.KEYWORD, new Value("while")))
            {
                var while_expr = while_expr();
                return while_expr;
            }
            else if (currentToken.matches(TokenType.KEYWORD, new Value("function")))
            {
                var func_def = func_def();
                return func_def;
            }
            else if (currentToken.type == TokenType.LSQAURE)
            {
                var list_expr = list_expr();
                return list_expr;
            }
        }

        throw new Exception("SyntaxError: expected an int, float, string, plus, minus, '(' or '['");
    }

    Node list_expr() throws Exception
    {
        if (currentToken == null || currentToken.type != TokenType.LSQAURE)
            throw new Exception("Expected '['");

        advance();
        ArrayList<Node> elements = new ArrayList<Node>();

        if (currentToken != null && currentToken.type == TokenType.RSQUARE)
        {
            advance();
        }
        else
        {
            elements.add(expr());

            while (currentToken != null && currentToken.type == TokenType.COMMA)
            {
                advance();
                elements.add(expr());
            }

            if (currentToken == null || currentToken.type != TokenType.RSQUARE)
                throw new Exception("Expected ',' or ']'");

            advance();
        }
        return new ListNode(elements);
    }

    Node func_def() throws Exception
    {
        if (currentToken == null || !currentToken.matches(TokenType.KEYWORD, new Value("function")))
            throw new Exception("Expected 'function'");

        advance();

        Token varNameTok;

        if (currentToken != null && currentToken.type == TokenType.IDENTIFIER)
        {
            varNameTok = currentToken;
            advance();

            if (currentToken == null || currentToken.type != TokenType.LPAREN)
                throw new Exception("Expected '('");
        }
        else
        {
            varNameTok = new Token(TokenType.IDENTIFIER, new Value("<anonymous>"));

            if (currentToken == null || currentToken.type != TokenType.LPAREN)
                throw new Exception("Expected identifier or '('");
        }

        advance();

        var argNameToks = new ArrayList<Token>();

        if (currentToken != null && currentToken.type == TokenType.IDENTIFIER)
        {
            argNameToks.add(currentToken);
            advance();

            while (currentToken != null && currentToken.type == TokenType.COMMA)
            {
                advance();

                if (currentToken == null || currentToken.type != TokenType.IDENTIFIER)
                    throw new Exception("Expected identifier");

                argNameToks.add(currentToken);
                advance();
            }

            if (currentToken == null || currentToken.type != TokenType.RPAREN)
                throw new Exception("Expected ',' or ')'");
        }
        else
        {
            if (currentToken == null || currentToken.type != TokenType.RPAREN)
                throw new Exception("Expected identifier or ')'");
        }

        advance();
        Node bodynode = null;

        if (currentToken.type == TokenType.ARROW)
        {
            advance();
            bodynode = statement();

            if (currentToken.type == TokenType.NEWLINE)
                advance();
        }
        else if (currentToken == null || currentToken.type == TokenType.LCURLY)
        {
            advance();

            bodynode = statements();
            System.out.println(currentToken.getStr());
            if (currentToken == null || currentToken.type != TokenType.RCURLY)
                throw new Exception("Expected '}'");
            advance();
        }
        else
        {
            throw new Exception("Expected '{' or '->'");
        }

        return new FuncDefNode(varNameTok, argNameToks, bodynode);
    }

    Node if_expr() throws Exception
    {
        if (currentToken == null || !currentToken.matches(TokenType.KEYWORD, new Value("if")))
            throw new Exception("Expected 'if'");

        advance();

        var condition = expr();
        var cases = new ArrayList<Pair<Node, Node>>();

        Node elseCase = null;

        if (currentToken.type == TokenType.ARROW)
        {
            advance();
            var expr = statement();
            cases.add(new Pair<Node, Node>(condition, expr));
        }
        else if (currentToken == null || currentToken.type == TokenType.LCURLY)
        {
            advance();

            var statements = statements();

            if (currentToken == null || currentToken.type != TokenType.RCURLY)
                throw new Exception("Expected '}'");
            advance();

            cases.add(new Pair<Node, Node>(condition, statements));
        }
        else
        {
            throw new Exception("Expected '{' or '->'");
        }
        while (currentToken != null && currentToken.matches(TokenType.KEYWORD, new Value("elif")))
        {
            advance();

            condition = expr();

            if (currentToken.type == TokenType.ARROW)
            {
                advance();
                var expr = statement();
                cases.add(new Pair<Node, Node>(condition, expr));
                if (currentToken.type == TokenType.NEWLINE)
                    advance();
            }
            else if (currentToken == null || currentToken.type == TokenType.LCURLY)
            {
                advance();

                var statements = statements();
                // System.out.println(currentToken.getStr());
                if (currentToken == null || currentToken.type != TokenType.RCURLY)
                    throw new Exception("Expected '}'");
                advance();

                cases.add(new Pair<Node, Node>(condition, statements));
            }
            else
            {
                throw new Exception("Expected '{' or '->'");
            }
        }

        if (currentToken != null && currentToken.matches(TokenType.KEYWORD, new Value("else")))
        {
            advance();
            if (currentToken.type == TokenType.ARROW)
            {
                advance();
                elseCase = statement();
                if (currentToken.type == TokenType.NEWLINE)
                    advance();
            }
            else if (currentToken == null || currentToken.type == TokenType.LCURLY)
            {
                advance();

                elseCase = statements();
                // System.out.println(currentToken.getStr());
                if (currentToken == null || currentToken.type != TokenType.RCURLY)
                    throw new Exception("Expected '}'");
                advance();
            }
            else
            {
                throw new Exception("Expected '{' or '->'");
            }
        }

        return new IfNode(cases, elseCase);
    }

    Node for_expr() throws Exception
    {
        if (currentToken == null || !currentToken.matches(TokenType.KEYWORD, new Value("for")))
            throw new Exception("Expected 'for'");

        advance();

        if (currentToken == null || currentToken.type != TokenType.IDENTIFIER)
            throw new Exception("Expected identifier");

        var varName = currentToken;

        advance();

        if (currentToken == null || currentToken.type != TokenType.EQ)
            throw new Exception("Expected '='");

        advance();

        var startValue = expr();

        if (currentToken == null || !currentToken.matches(TokenType.KEYWORD, new Value("to")))
            throw new Exception("Expected 'to'");

        advance();

        var endValue = expr();

        Node stepValue = null;
        Node body = null;

        if (currentToken != null && currentToken.matches(TokenType.KEYWORD, new Value("do")))
        {
            advance();
            stepValue = expr();
        }

        if (currentToken.type == TokenType.ARROW)
        {
            advance();
            body = statement();
        }
        else if (currentToken == null || currentToken.type == TokenType.LCURLY)
        {
            advance();

            body = statements();

            if (currentToken == null || currentToken.type != TokenType.RCURLY)
                throw new Exception("Expected '}'");
            advance();
        }
        else
        {
            throw new Exception("Expected '{' or '->'");
        }

        return new ForNode(varName, startValue, endValue, stepValue, body);
    }

    Node while_expr() throws Exception
    {
        if (currentToken == null || !currentToken.matches(TokenType.KEYWORD, new Value("while")))
            throw new Exception("Expected 'while'");

        advance();

        var condition = expr();

        Node body = null;

        if (currentToken.type == TokenType.ARROW)
        {
            advance();
            body = statement();
        }
        else if (currentToken == null || currentToken.type == TokenType.LCURLY)
        {
            advance();

            body = statements();

            if (currentToken == null || currentToken.type != TokenType.RCURLY)
                throw new Exception("Expected '}'");
            advance();
        }
        else
        {
            throw new Exception("Expected '{' or '->'");
        }

        return new WhileNode(condition, body);
    }
}
