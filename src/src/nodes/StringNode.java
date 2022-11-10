package src.nodes;

import src.essentials.Token;

public class StringNode extends Node
{
    public StringNode(Token token)
    {
        super(null, token, null);
    }

    String getStr()
    {
        return token.type + ": '" + token.value.string + "'";
    }
}
