public class Run
{
    static SymbolTable symbolTable = new SymbolTable();

    static void run(String text) throws Exception
    {
        Lexer lexer = new Lexer(text);
        var tokens = lexer.tokenize();

        /*for (var token : tokens)
            System.out.print(token.getStr() + ", ");*/

        //System.out.println();

        Parser parser = new Parser(tokens);
        var ast = parser.parse();

        //System.out.println(ast.getStr());

        Interpreter interpreter = new Interpreter();
        var result = interpreter.visit(ast, symbolTable);

        if (result.matches(TokenType.KEYWORD, new Value("none")))
            return;

        System.out.println(result.value.string);
    }
}
