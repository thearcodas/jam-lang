package src.nodes;

import src.essentials.Token;

public class VarAccessNode extends Node
{
    public VarAccessNode(Token token)
    {
        super(null, token, null);
    }

    String getStr()
    {
        return token.getStr();
    }
}
