public class WhileNode extends Node
{
    Node conditionNode;
    Node bodyNode;

    WhileNode(Node conditionNode, Node bodyNode)
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
