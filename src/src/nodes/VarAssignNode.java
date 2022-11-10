package src.nodes;

import src.essentials.Token;
import src.essentials.TokenType;

public class VarAssignNode extends Node
{
    public VarAssignNode(Token varName, Node expr)
    {
        super(new VarAccessNode(varName), new Token(TokenType.EQ), expr);
    }
}
