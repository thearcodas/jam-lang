public class StringNode extends Node
{
    StringNode(Token token)
    {
        super(null, token, null);
    }

    String getStr()
    {
        return token.type + ": '" + token.value.string + "'";
    }
}
