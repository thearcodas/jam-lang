package src.nodes;

import src.essentials.Token;

public class BinOpNode extends Node
{
    public BinOpNode(Node left, Token token, Node right)
    {
        super(left, token, right);
    }
}
