package src.essentials;
public class Token
{
    public TokenType type;
    public Value value;
    

    public Token(TokenType type, Value value)
    {
        this.type = type;
        this.value = value;
    }

    public Token(TokenType type)
    {
        this(type, new Value("\0"));
    }

    

    public String getStr()
    {
        return type + value.getStr();
    }

    public boolean matches(TokenType type, Value value)
    {
        return this.type == type && this.value.string.equals(value.string);
    }

    Token copy()
    {
        return new Token(type, value);
    }

    public boolean isTrue()
    {
        if (type == TokenType.INT)
            return Integer.parseInt(value.string) != 0;
        else if (type == TokenType.FLOAT)
            return Double.parseDouble(value.string) != 0f;
        else if (type == TokenType.STRING)
            return !value.string.equals("");

        return false;
    }
}
