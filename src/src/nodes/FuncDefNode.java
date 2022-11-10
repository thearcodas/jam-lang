package src.nodes;
import java.util.ArrayList;

import src.essentials.Token;

public class FuncDefNode extends Node
{
    public Token varNameTok;
    public ArrayList<Token> argNameToks;
    public Node bodyNode;


    FuncDefNode(ArrayList<Token> argNameToks, Node bodyNode)
    {
        this(null, argNameToks, bodyNode);
    }

    public FuncDefNode(Token varNameTok, ArrayList<Token> argNameToks, Node bodyNode)
    {
        super(null, null, null);
        this.varNameTok = varNameTok;
        this.argNameToks = argNameToks;
        this.bodyNode = bodyNode;
    }

    String getStr()
    {
        String str = "FUNCTION_DEFINITION: " +  varNameTok.value.string  + "(";

        for (int i = 0; i < argNameToks.size(); i++)
            str += argNameToks.get(i).value.string + (i != argNameToks.size() - 1 ? ", " : "");

        return str + ") {" + bodyNode.getStr() + "}";
    }
}
