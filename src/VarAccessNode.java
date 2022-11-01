public class VarAccessNode extends Node
{
    VarAccessNode(Token token)
    {
        super(null, token, null);
    }

    String getStr()
    {
        return token.getStr();
    }
}
