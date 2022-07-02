import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;

public class QueryService {

    public void executeDDLQueries (String query) throws IOException {
        ConnectPostgreSQL connectpostgresql = new ConnectPostgreSQL();
        connectpostgresql.open_JDBC_connection();


        //String testString = "SELECT user_id, username, password FROM test.accounts2 ;";
        PostgreSQLLexer lexer = new PostgreSQLLexer(CharStreams.fromString(query));

        //String testString = "SELECT user_id, username, password FROM test.accounts2 ; " ;
        //String source = "test.txt";
        //CharStream cs = fromFileName(source);
        //PostgreSQLLexer lexer = new PostgreSQLLexer(cs);

        CommonTokenStream token = new CommonTokenStream(lexer);
        PostgreSQLParser parser = new PostgreSQLParser(token);

        ParseTree tree = parser.root(); //tree is result of parsing (soit cette instruction)

        //ParserRuleContext<Token> tree = parser.compilationUnit(); // parse
        ParseTreeWalker walker = new ParseTreeWalker(); // create standard walker
        Mylistener listener = new Mylistener(parser);

        long startTime = System.currentTimeMillis();

        walker.walk(listener, tree);// initiate walk of tree with listener

        long endTime = System.currentTimeMillis();

        System.out.println("debut"+ (endTime));
        System.out.println("fin"+ (startTime));
        System.out.println("le temps d'execution est:");
        System.out.println(endTime-startTime);

        if (listener.semanticErrors.isEmpty()) {
            System.out.println("no semantic errors");
        }
        else {
            for (String err: listener.semanticErrors) {
                System.out.println(err);
            }
        }
        connectpostgresql.close_JDBC_connection();
    }

    public JSONObject executeDMLQueries (String query) throws IOException {
        ConnectPostgreSQL connectpostgresql = new ConnectPostgreSQL();
        connectpostgresql.open_JDBC_connection();


        //String testString = "SELECT user_id, username, password FROM test.accounts2 ;";
        PostgreSQLLexer lexer = new PostgreSQLLexer(CharStreams.fromString(query));

        //String testString = "SELECT user_id, username, password FROM test.accounts2 ; " ;
        //String source = "test.txt";
        //CharStream cs = fromFileName(source);
        //PostgreSQLLexer lexer = new PostgreSQLLexer(cs);

        CommonTokenStream token = new CommonTokenStream(lexer);
        PostgreSQLParser parser = new PostgreSQLParser(token);

        //String s = String.valueOf(new UsingRealityVisitor().visit(parser.simple_select()));
        //boucle du test de temps d'exécution

        //gParser.CompilationUnitContext tree = parser.compilationUnit(); // parse a compilationUnit (cette instruction qui ne marche pas est censé pouvoir remplacer celle qui est juste en dessous)
        ParseTree tree = parser.simple_select(); //tree is result of parsing (soit cette instruction)
        UsingRealityVisitor usingRealityVisit = new UsingRealityVisitor();

        long startTime = System.currentTimeMillis();

        JSONObject result =  usingRealityVisit.visitSimple_select((PostgreSQLParser.Simple_selectContext) tree);

        long endTime = System.currentTimeMillis();
        System.out.println("debut"+ (endTime));
        System.out.println("fin"+ (startTime));
        System.out.println("le temps d'execution est:");
        System.out.println(endTime-startTime);

        connectpostgresql.close_JDBC_connection();
        return result;
    }



    public JSONObject testFunction() {

        JSONObject obj = new JSONObject();

        JSONArray col1 = new JSONArray();
        col1.add ("nomcol1");
        JSONArray rawscol1 = new JSONArray();
        rawscol1.add ("ligne1col1");
        rawscol1.add ("ligne2col1");
        col1.add (rawscol1);

        obj.put("1", col1);

        JSONArray col2 = new JSONArray();
        col2.add ("nomcol2");
        JSONArray rawscol2 = new JSONArray();
        rawscol2.add ("ligne1col2");
        rawscol2.add ("ligne2col2");
        col2.add (rawscol2);
        obj.put("2", col2);

        JSONArray col3 = new JSONArray();
        col3.add ("nomcol3");
        JSONArray rawscol3 = new JSONArray();
        rawscol3.add ("ligne1col3");
        rawscol3.add ("ligne2col3");
        col3.add (rawscol3);
        obj.put("3", col3);

        JSONArray col4 = new JSONArray();
        col4.add ("nomcol4");
        JSONArray rawscol4 = new JSONArray();
        rawscol4.add ("ligne1col4");
        rawscol4.add ("ligne2col4");
        col4.add (rawscol4);
        obj.put("4", col4);

        System.out.print(obj);
        return obj;
    }
}
