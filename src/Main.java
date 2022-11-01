import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;
class Main
{
    public static void main(String[] args)
    {

        try (Scanner scanner = new Scanner(System.in))
        {
            while (true)
            {
                System.out.print("jam >\t");
                int arrowcount = 0,bracketcount=0;
                var text = scanner.nextLine();
                while ((text.startsWith("if") || text.startsWith("for") || text.startsWith("while") ||
                        text.startsWith("function")))
                {
                    if (text.contains("->") && (arrowcount == 0 && bracketcount==0))
                        break;
                    
                    System.out.print("...\t");
                    var txt = scanner.nextLine();

                    if(txt.contains("}"))
                        bracketcount--;

                    if (txt.endsWith("}") && bracketcount==0)
                    {
                        text = text + txt;
                        break;
                    }
                    text = text + txt;
                    
                    if (text.contains("->"))
                        arrowcount++;
                    if(txt.contains("{"))
                        bracketcount++;
                }

                if (text.trim().isEmpty())
                    continue;

                try
                {

                    Run.run(text);
                }
                catch (InvocationTargetException ite)
                {
                    var underlyingException = ite.getCause();
                    underlyingException.printStackTrace();
                }
                catch (Exception e)
                {
                    System.err.println(e.getMessage());
                }
            }
        }
    }
}
