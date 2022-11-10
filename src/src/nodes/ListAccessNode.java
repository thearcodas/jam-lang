package src.nodes;

import src.essentials.Token;

public class ListAccessNode extends Node
{
    
    public ListAccessNode(Token varName, Node index)
    {
        super(new VarAccessNode(varName),null,index);
        

    }
    String getStr()
    {
        return "("+left.getStr()+",Index:"+ right.getStr()+")";
    }
}
