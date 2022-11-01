import java.util.Dictionary;
import java.util.Hashtable;

public class SymbolTable
{
    Dictionary<String, Token> symbols = new Hashtable<String, Token>();
    SymbolTable parent;

    SymbolTable()
    {
        this(null);
    }

    SymbolTable(SymbolTable parent)
    {
        this.parent = parent;
    }

    public Token get(String key)
    {
        var value = symbols.get(key);

        if (value == null && parent != null)
            return parent.get(key);

        return value;
    }

    public void set(String key, Token value)
    {
        symbols.put(key, value);
    }

    void remove(String key)
    {
        symbols.remove(key);
    }
}
