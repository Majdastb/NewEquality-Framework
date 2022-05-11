import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import java.io.IOException;
import static org.antlr.v4.runtime.CharStreams.fromFileName;

/** J'appelle les méthodes qui ouvrent la connexion et qui la ferment avec nom de la classe.la méthode */

public class launch {
    public static void main (String[] args) {
        try {
            ConnectPostgreSQL connectpostgresql = new ConnectPostgreSQL();
            connectpostgresql.open_JDBC_connection();

            String source = "test.txt";
            CharStream cs = fromFileName(source);
            PostgreSQLLexer lexer = new PostgreSQLLexer(cs);
            CommonTokenStream token = new CommonTokenStream(lexer);
            PostgreSQLParser parser = new PostgreSQLParser(token);



            //gParser.CompilationUnitContext tree = parser.compilationUnit(); // parse a compilationUnit (cette instruction qui ne marche pas est censé pouvoir remplacer celle qui est juste en dessous)
            ParseTree tree = parser.root(); //tree is result of parsing (soit cette instruction)

            //ParserRuleContext<Token> tree = parser.compilationUnit(); // parse
            ParseTreeWalker walker = new ParseTreeWalker(); // create standard walker
            Mylistener listener = new Mylistener(parser);
            walker.walk(listener, tree);// initiate walk of tree with listener

            //test parcous arbre


            if (listener.semanticErrors.isEmpty()) {
                System.out.println("no semantic errors");
            }
            else {
                for (String err: listener.semanticErrors) {
                    System.out.println(err);
                }
            }
            //Trees.inspect(tree, parser);

            connectpostgresql.close_JDBC_connection();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
