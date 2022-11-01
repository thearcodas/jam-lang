public class ForNode extends Node
{
    Token varNameTok;
    Node startValueNode;
    Node endValueNode;
    Node stepValueNode;
    Node bodyNode;

    ForNode(Token varNameTok, Node startValueNode, Node endValueNode, Node stepValueNode, Node bodyNode)
    {
        super(null, null, null);
        this.varNameTok = varNameTok;
        this.startValueNode = startValueNode;
        this.endValueNode = endValueNode;
        this.stepValueNode = stepValueNode;
        this.bodyNode = bodyNode;
    }

    String getStr()
    {
        return "FOR: (" + varNameTok.getStr() + " = " + startValueNode.getStr() + " TO " + endValueNode.getStr() +
            (stepValueNode != null ? ", STEP: " + stepValueNode.getStr() : "") + ") {" + bodyNode.getStr() + "}";
    }
}
