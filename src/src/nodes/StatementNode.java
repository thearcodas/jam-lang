package src.nodes;
import java.util.*;

public class StatementNode extends Node 
{
    public ArrayList<Node> statements;
    public StatementNode(ArrayList<Node> statements)
    {
        super(null,null,null);
        this.statements = statements;
    }
    String getStr()
    {
        if(statements.size()>1)
        {
            String str= "Statements: [";
            for (var statement : statements)
            {
                str= str+ statement.getStr()+";";
            }
            str= str+ "]";
            return str;
        }
        return statements.get(0).getStr();
    }


}
