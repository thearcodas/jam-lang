package src;
import java.util.ArrayList;

import src.essentials.Keywords;
import src.essentials.Token;
import src.essentials.TokenType;
import src.essentials.Value;

public class Lexer
{
    String text;
    int position;
    Character currentChar;

    Lexer(String text)
    {
        this.text = text;
        position = -1;
        currentChar = null;
        advance();
    }

    void advance()
    {
        position++;
        currentChar = position < text.length() ? text.charAt(position) : null;
    }
    void backtrack()
    {
        position--;
        currentChar = position >=0 ? text.charAt(position) : null;
    }

    ArrayList<Token> tokenize() throws Exception
    {
        var tokens = new ArrayList<Token>();

        while (currentChar != null)
        {
            if (currentChar == ' ' || currentChar == '\t')
            {
                advance();
            }
            else if (Character.isDigit(currentChar) || currentChar == '.')
            {
                tokens.add(makeNumber());
            }
            else if (currentChar == '"' || currentChar == '\'')
            {
                tokens.add(makeString());
            }
            else if (Character.isLetter(currentChar) || currentChar == '_')
            {
                tokens.add(makeIdentifier());
            }
            else if (currentChar == '+')
            {
                tokens.add(new Token(TokenType.PLUS));
                advance();
            }
            else if (currentChar == '-')
            {
                tokens.add(makeMinusOrArrow(tokens));
            }
            else if (currentChar == '*')
            {
                tokens.add(makeMul());
            }
            else if (currentChar == '/')
            {
                tokens.add(makeDiv());
            }
            else if (currentChar == '%')
            {
                tokens.add(new Token(TokenType.MOD));
                advance();
            }
            else if (currentChar == '=')
            {
                tokens.add(makeEquals());
            }
            else if (currentChar == '!')
            {
                advance();

                if (currentChar != null && currentChar == '=')
                {
                    tokens.add(new Token(TokenType.NE));
                    advance();
                }
                else
                {
                   tokens.add(new Token(TokenType.KEYWORD ,new Value("not")));
                    
                }
            }
            else if (currentChar == '<')
            {
                tokens.add(makeLessThan());
            }
            else if (currentChar == '>')
            {
                tokens.add(makeGreaterThan());
            }
            else if (currentChar == ',')
            {
                tokens.add(new Token(TokenType.COMMA));
                advance();
            }
            else if (currentChar == '(')
            {
                tokens.add(new Token(TokenType.LPAREN));
                advance();
            }
            else if (currentChar == ')')
            {
                tokens.add(new Token(TokenType.RPAREN));
                advance();
            }
            else if (currentChar == '[')
            {
                tokens.add(new Token(TokenType.LSQAURE));
                advance();
            }
            else if (currentChar == ']')
            {
                tokens.add(new Token(TokenType.RSQUARE));
                advance();
            }
            else if (currentChar == '{')
            {
                tokens.add(new Token(TokenType.LCURLY));
                advance();
            }
            else if (currentChar == '}')
            {
                tokens.add(new Token(TokenType.RCURLY));
                advance();
            }
            else if (currentChar == ';')
            {
                tokens.add(new Token(TokenType.NEWLINE));
                advance();
            }
            else if (currentChar == '#')
            {
                skipComment();
            }
            else
            {
                throw new Exception("IllegalCharacter: "
                                    + "'" + currentChar + "'");
            }
        }

        tokens.add(new Token(TokenType.EOF));

        return tokens;
    }

    void skipComment()
    {
        advance();

        while (currentChar != null && currentChar != '\n')
            advance();

        advance();
    }

    Token makeMinusOrArrow(ArrayList<Token> tokens)
    {
        TokenType type = TokenType.MINUS;
        advance();

        if (currentChar != null && currentChar == '>')
        {
            type = TokenType.ARROW;
            advance();
        }

        return new Token(type);
    }

    Token makeIdentifier()
    {
        var idStr = "";

        while (currentChar != null && (Character.isLetterOrDigit(currentChar) || currentChar == '_'))
        {
            idStr += currentChar;
            advance();
        }

        return new Token(Keywords.values.contains(idStr) ? TokenType.KEYWORD : TokenType.IDENTIFIER,new Value(idStr));
    }

    Token makeMul()
    {
        TokenType type = TokenType.MUL;
        advance();

        if (currentChar != null && currentChar == '*')
        {
            type = TokenType.POW;
            advance();
        }

        return new Token(type);
    }

    Token makeDiv()
    {
        TokenType type = TokenType.DIV;
        advance();

        if (currentChar != null && currentChar == '/')
        {
            type = TokenType.INTDIV;
            advance();
        }

        return new Token(type);
    }

    Token makeEquals()
    {
        TokenType type = TokenType.EQ;
        advance();

        if (currentChar != null && currentChar == '=')
        {
            type = TokenType.EE;
            advance();
        }

        return new Token(type);
    }

    Token makeLessThan()
    {
        TokenType type = TokenType.LT;
        advance();

        if (currentChar != null && currentChar == '=')
        {
            type = TokenType.LTE;
            advance();
        }

        return new Token(type);
    }

    Token makeGreaterThan()
    {
        TokenType type = TokenType.GT;
        advance();

        if (currentChar != null && currentChar == '=')
        {
            type = TokenType.GTE;
            advance();
        }

        return new Token(type);
    }

    Token makeString() throws Exception
    {
        var str = "";
        var invertedComma = currentChar;

        advance();

        while (currentChar != null)
        {
            if (currentChar == '\\')
            {
                advance();

                if (currentChar == null)
                    break;

                if (currentChar == 'n')
                    str += '\n';
                else if (currentChar == 't')
                    str += '\t';
                else if (currentChar == '\'')
                    str += '\'';
                else if (currentChar == '\"')
                    str += '\"';
                else if (currentChar == '\\')
                    str += '\\';
                else
                    str += "\\" + currentChar;

                advance();
                continue;
            }
            else if (currentChar == invertedComma)
            {
                advance();
                return new Token(TokenType.STRING, new Value(str));
            }

            str += currentChar;
            advance();
        }

        throw new Exception("SyntaxError: unterminated string literal");
    }

    Token makeNumber() throws Exception
    {
        var numStr = "";
        var dotCount = 0;

        while (currentChar != null && (Character.isDigit(currentChar) || currentChar == '.'))
        {
            if (currentChar == '.')
            {
                if (dotCount == 1)
                    break;

                dotCount++;
            }

            numStr += currentChar;
            advance();
        }

        if (numStr.equals("."))
            throw new Exception("SyntaxError: '.' is not a valid token");

        if (dotCount == 0)
            return new Token(TokenType.INT, new Value(String.valueOf(Integer.parseInt(numStr))));
        else
            return new Token(TokenType.FLOAT, new Value(String.valueOf(Double.parseDouble(numStr))));
    }
}
