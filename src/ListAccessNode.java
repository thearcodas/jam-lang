public class ListAccessNode extends Node
{
    
    ListAccessNode(Token varName, Node index)
    {
        super(new VarAccessNode(varName),null,index);
        

    }
    String getStr()
    {
        return "("+left.getStr()+",Index:"+ right.getStr()+")";
    }
}
