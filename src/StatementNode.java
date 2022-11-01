import java.util.*;

public class StatementNode extends Node 
{
    ArrayList<Node> statements;
    StatementNode(ArrayList<Node> statements)
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
