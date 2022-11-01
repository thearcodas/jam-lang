public class UnaryOpNode extends Node
{
    UnaryOpNode(Token token, Node right)
    {
        super(null, token, right);
    }

    String getStr()
    {
        return "(" + token.getStr() + ", " + right.getStr() + ")";
    }
}
