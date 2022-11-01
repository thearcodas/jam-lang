import java.util.*;
public class Value 
{
    String string;
    ArrayList<Token> list;
    ArrayList<Token> argNameToks; 
    Node bodyNode;
    Value( String value)
    {
        this.string=value;
    }
    Value( ArrayList<Token> list)
    {
        this.list=list;
        if(this.list.size()>1)
        {
        var itr= list.iterator();
        this.string="[" + itr.next().value.string;
        while(itr.hasNext())
        {
            this.string= this.string+","+itr.next().value.string;
        }
        this.string=this.string+"]";
        }
        else
        {
            this.string=list.get(0).value.string;
        }
    }

    Value( ArrayList<Token> argNameToks, Node bodyNode)
    {
        
        this.argNameToks= argNameToks;
        this.bodyNode= bodyNode;
    }
    String getStr()
    {
        return (string != "\0" ? ": " + string : "");
    }
    
}
