public class ReturnNode extends Node 
{
    Node returnNode;
    ReturnNode(Node returnNode)
    {
        super(null,null,null);
        this.returnNode= returnNode;
    }
    String getStr()
    {
        return "Return : "+ returnNode.getStr();
    }
}
