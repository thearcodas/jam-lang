package src.nodes;
public class NoneTypeNode extends Node
{
    public NoneTypeNode()
    {
        super(null, null, null);
    }

    String getStr()
    {
        return "KEYWORD: none";
    }
}
