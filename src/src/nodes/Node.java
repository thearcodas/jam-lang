package src.nodes;

import src.essentials.Token;

public class Node
{
    public Node left;
    public Token token;
    public Node right;

    Node(Node left, Token token, Node right)
    {
        this.left = left;
        this.token = token;
        this.right = right;
    }

    String getStr()
    {
        return "(" + left.getStr() + ", " + token.getStr() + ", " + right.getStr() + ")";
    }
}
