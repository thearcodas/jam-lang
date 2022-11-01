import java.util.ArrayList;

public class IfNode extends Node
{
    ArrayList<Pair<Node, Node>> cases;
    Node elseCase;

    IfNode(ArrayList<Pair<Node, Node>> cases, Node elseCase)
    {
        super(null, null, null);
        this.cases = cases;
        this.elseCase = elseCase;
    }

    String getStr()
    {
        String str = "";

        for (int i = 0; i < cases.size(); i++)
            str += (i == 0 ? "IF:" : " ELIF:") + " (" + cases.get(i).key.getStr() + ") {" +
                   cases.get(i).value.getStr() + "}";

        if (elseCase != null)
            str += " ELSE: {" + elseCase.getStr() + "}";

        return str;
    }
}
