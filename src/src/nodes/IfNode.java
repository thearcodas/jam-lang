package src.nodes;

import java.util.ArrayList;

import src.essentials.Pair;

public class IfNode extends Node
{
    public ArrayList<Pair<Node, Node>> cases;
    public Node elseCase;

    public IfNode(ArrayList<Pair<Node, Node>> cases, Node elseCase)
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
