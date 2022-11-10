package src.nodes;
public class ReturnNode extends Node 
{
    public Node returnNode;
    public ReturnNode(Node returnNode)
    {
        super(null,null,null);
        this.returnNode= returnNode;
    }
    String getStr()
    {
        return "Return : "+ returnNode.getStr();
    }
}
