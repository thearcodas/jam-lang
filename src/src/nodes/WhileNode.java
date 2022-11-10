package src.nodes;
public class WhileNode extends Node
{
    public Node conditionNode;
    public Node bodyNode;

    public WhileNode(Node conditionNode, Node bodyNode)
    {
        super(null, null, null);
        this.conditionNode = conditionNode;
        this.bodyNode = bodyNode;
    }

    String getStr()
    {
        return "WHILE: (" + conditionNode.getStr() + ") {" + bodyNode.getStr() + "}";
    }
}
