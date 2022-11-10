package src.nodes;

import src.essentials.Token;

public class UnaryOpNode extends Node
{
    public UnaryOpNode(Token token, Node right)
    {
        super(null, token, right);
    }

    String getStr()
    {
        return "(" + token.getStr() + ", " + right.getStr() + ")";
    }
}
