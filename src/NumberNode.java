public class NumberNode extends Node
{
    NumberNode(Token token)
    {
        super(null, token, null);
    }

    String getStr()
    {
        return token.getStr();
    }
}
