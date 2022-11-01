import java.util.*;
public class ListNode extends Node
{
     ArrayList<Node> elementNodes;
    
    ListNode( ArrayList<Node> elementNodes)
    {
        super(null,null,null);
        this.elementNodes = elementNodes;
        
    }
    Node get(int Index)
    {
        return elementNodes.get(Index);
    }

    String getStr()
    {
        String str= "List: [";
        for (var element : elementNodes)
        {
            str= str+ element.getStr()+" ";
        }
        str= str+ "]";
        return str;
    }
}
