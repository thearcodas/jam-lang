package src.nodes;

import java.util.ArrayList;

public class CallNode extends Node
{
    public Node nodeToCall;
    public ArrayList<Node> argNodes;

    public CallNode(Node nodeToCall, ArrayList<Node> argNodes)
    {
        super(null, null, null);
        this.nodeToCall = nodeToCall;
        this.argNodes = argNodes;
    }

    String getStr()
    {
        String str = "FUNCTION_CALL: " + nodeToCall.token.value.string + "(";

        for (int i = 0; i < argNodes.size(); i++)
            str += argNodes.get(i).getStr() + (i != argNodes.size() - 1 ? ", " : "");

        return str + ")";
    }
}
