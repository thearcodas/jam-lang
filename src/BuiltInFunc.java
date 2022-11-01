import java.util.*;
import java.io.*;
public class BuiltInFunc
{
    public static ArrayList<String> list = new ArrayList<String>();
    Token varNameTok;
    ArrayList<Token> arglist;
    Scanner scanner = new Scanner(System.in);

    static
    {
        list.add("input");
        list.add("output");
        list.add("execute");
        list.add("exit");
    }

    public Token funinput(ArrayList<Token> argNameToks) throws Exception
    {
        this.arglist = argNameToks;
        if (arglist.size() != 1)
            throw new Exception("Invalid Syntax");
        else
        {
            System.out.print(arglist.get(0).value.string);
            return new Token(TokenType.STRING, new Value(scanner.nextLine()));
        }
    }
    public Token funoutput(ArrayList<Token> argNameToks) throws Exception
    {
        this.arglist = argNameToks;
        if (arglist.size() != 1)
            throw new Exception("Invalid Syntax");
        else
        {

            System.out.print(arglist.get(0).value.string);
            return new Token(TokenType.KEYWORD, new Value("none"));
        }
    }
    public Token funexecute(ArrayList<Token> argNameToks) throws Exception
    {
        this.arglist= argNameToks;
        if(arglist.size() != 1)
            throw new Exception("Invalid Syntax");
        else
        {
            var fn=arglist.get(0);
            if(fn.type != TokenType.STRING)
                throw new Exception("Argument must be a string");

            File file = new File(fn.value.string);
            StringBuilder buffer = new StringBuilder();

            if (!file.exists() || !file.canRead())
                throw new Exception("Failed to load script");
            
            Scanner dataReader = new Scanner(file);
            String text;
            while (dataReader.hasNextLine()) 
            {  
                text = dataReader.nextLine();
                buffer.append(text);
            }
            dataReader.close();
            Run.run(buffer.toString());
        } 
        return new Token(TokenType.KEYWORD, new Value("none")); 

    }

}

