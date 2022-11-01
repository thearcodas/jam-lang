public class Node
{
    Node left;
    Token token;
    Node right;

    Node(Node left, Token token, Node right)
    {
        this.left = left;
        this.token = token;
        this.right = right;
    }

    String getStr()
    {
        return "(" + left.getStr() + ", " + token.getStr() + ", " + right.getStr() + ")";
    }
}
