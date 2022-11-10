package src.essentials;
import java.util.*;

import src.nodes.Node;
public class Value 
{
    public String string;
    public ArrayList<Token> list;
    public ArrayList<Token> argNameToks; 
    public Node bodyNode;
    public Value( String value)
    {
        this.string=value;
    }
    public Value( ArrayList<Token> list)
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

    public Value( ArrayList<Token> argNameToks, Node bodyNode)
    {
        
        this.argNameToks= argNameToks;
        this.bodyNode= bodyNode;
    }
    String getStr()
    {
        return (string != "\0" ? ": " + string : "");
    }
    
}
