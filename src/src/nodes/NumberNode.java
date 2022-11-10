package src.nodes;

import src.essentials.Token;

public class NumberNode extends Node
{
    public NumberNode(Token token)
    {
        super(null, token, null);
    }

    String getStr()
    {
        return token.getStr();
    }
}
