
import java.io.IOException;
import static spark.Spark.*;
import com.google.gson.Gson;

//solution: traiter les requetes select dans un visiteur : s'il y a le using reality alors je fais comme j'ai fait sinon je ressors les resultats
//(normalement pour savoir si le using reality est utilisé je prends la condition qui est dans enter using reality)
//traiter les requetes sans select dans le my listener en les envoyant simplement au sgbd

public class launch {
    public static void main (String[] args) throws IOException {

        /** String test = "SELECT user_id, username, password FROM test.accounts2 ;";
              String test = "SELECT k_reference, nature_cellule, vieillissement_annuel, poids_vide, poids_tac,\n" +
             "nature_revetment_interieur2_plafond_toto, nature_froid_groupe, type_temprature_groupe, genre_engin,\n" +
             "marque_groupe, surface_interieure_caisse_m2, classe_origine, nature_revetment_interieur1_plafond_toto ,\n" +
             "nombre_ouvertures_caisse, battant1_arriere,battant1_lateral_droit, nombre_plinthes_toto,\n" +
             "densite_isolant_plafond_toto, epaisseur_total_plafond_toto, epaisseur_total_plancher_toto\n" +
             "\n" +
             "FROM test.engins500\n" +
             "\n" +
             "WHERE k_reference = 0.3 AND nature_cellule = 'rapportee'\n" +
             "AND vieillissement_annuel = 3.6 AND poids_vide = 7523 AND poids_tac = 19769\n" +
             " AND nature_revetment_interieur2_plafond_toto = 'gelcoat' AND nature_froid_groupe = 'réfrigérant'\n" +
             " AND type_temprature_groupe = 'multi' AND genre_engin = 'camion' AND marque_groupe = 'Transcool'\n" +
             " AND surface_interieure_caisse_m2 = 15.20 AND classe_origine = 'abc' AND\n" +
             " nature_revetment_interieur1_plafond_toto = 'polyester' AND nombre_ouvertures_caisse = 2\n" +
             " AND  battant1_arriere = 0 AND battant1_lateral_droit = 0 AND nombre_plinthes_toto = 8.5 AND\n" +
             "  densite_isolant_plafond_toto = 15 AND epaisseur_total_plafond_toto = 46\n" +
             "  AND epaisseur_total_plancher_toto = 62.91\n" +
             "\n" +
             "USING REALITY r20_engins ;";
            ConnectPostgreSQL connectpostgresql = new ConnectPostgreSQL();

            String s = connectpostgresql.queryHandlingVisitor(test);
            System.out.println("Result: " + s);

            post("/selectother", ( request,  response) -> {
                
                String body = request.body();
                char c = '"' ;
                //ConnectPostgreSQL connectpostgresql = new ConnectPostgreSQL();
                //response.type("application/json");

                //JSONObject jsonObject = connectpostgresql.testFunction();
                //return new Gson().toJson( new StandardResponse(body,new Gson().toJsonTree(jsonObject)));
                return  body ;


            });

        get("/select", ( request,  response) -> {
            ConnectPostgreSQL connectpostgresql = new ConnectPostgreSQL();
            return connectpostgresql.testFunction();
        });*/


       /** ConnectPostgreSQL connectpostgresql = new ConnectPostgreSQL();
        connectpostgresql.open_JDBC_connection();


        String testString = "SELECT user_id, username, password FROM test.accounts2 ;";
        PostgreSQLLexer lexer = new PostgreSQLLexer(CharStreams.fromString("SELECT k_reference, nature_cellule, vieillissement_annuel, poids_vide, poids_tac,\n" +
                "nature_revetment_interieur2_plafond_toto, nature_froid_groupe, type_temprature_groupe, genre_engin,\n" +
                "marque_groupe, surface_interieure_caisse_m2, classe_origine, nature_revetment_interieur1_plafond_toto ,\n" +
                "nombre_ouvertures_caisse, battant1_arriere,battant1_lateral_droit, nombre_plinthes_toto,\n" +
                "densite_isolant_plafond_toto, epaisseur_total_plafond_toto, epaisseur_total_plancher_toto\n" +
                "\n" +
                "FROM test.engins500\n" +
                "\n" +
                "WHERE k_reference = 0.3 AND nature_cellule = 'rapportee'\n" +
                "AND vieillissement_annuel = 3.6 AND poids_vide = 7523 AND poids_tac = 19769\n" +
                " AND nature_revetment_interieur2_plafond_toto = 'gelcoat' AND nature_froid_groupe = 'réfrigérant'\n" +
                " AND type_temprature_groupe = 'multi' AND genre_engin = 'camion' AND marque_groupe = 'Transcool'\n" +
                " AND surface_interieure_caisse_m2 = 15.20 AND classe_origine = 'abc' AND\n" +
                " nature_revetment_interieur1_plafond_toto = 'polyester' AND nombre_ouvertures_caisse = 2\n" +
                " AND  battant1_arriere = 0 AND battant1_lateral_droit = 0 AND nombre_plinthes_toto = 8.5 AND\n" +
                "  densite_isolant_plafond_toto = 15 AND epaisseur_total_plafond_toto = 46\n" +
                "  AND epaisseur_total_plancher_toto = 62.91\n" +
                "\n" +
                "USING REALITY r20_engins ;"));

        //String testString = "SELECT user_id, username, password FROM test.accounts2 ; " ;
        //String source = "test.txt";
        //CharStream cs = fromFileName(source);
        //PostgreSQLLexer lexer = new PostgreSQLLexer(cs);

        CommonTokenStream token = new CommonTokenStream(lexer);
        PostgreSQLParser parser = new PostgreSQLParser(token);

        //gParser.CompilationUnitContext tree = parser.compilationUnit(); // parse a compilationUnit (cette instruction qui ne marche pas est censé pouvoir remplacer celle qui est juste en dessous)
        ParseTree tree = parser.simple_select(); //tree is result of parsing (soit cette instruction)
        UsingRealityVisitor usingRealityVisit = new UsingRealityVisitor();
        JSONObject result =  usingRealityVisit.visitSimple_select((PostgreSQLParser.Simple_selectContext) tree);
        //usingRealityVisit.visit(tree);

        System.out.println("Result: " + result);

        connectpostgresql.close_JDBC_connection();*/











        post("/ddl", ( request,  response) -> {

            String body = request.body(); //le texte en entrée ne doit pas avoir de retour a la ligne
            QueryService queryService = new QueryService();
            queryService.executeDDLQueries(body);
            response.type("application/json");
            return new Gson()
                    .toJson(new StandardResponse(StatusResponse.SUCCESS));

        });

        post("/dml", ( request,  response) -> {

            String body = request.body(); //le texte en entrée ne doit pas avoir de retour a la ligne
            QueryService queryService = new QueryService();
            response.type("application/json");
            return  queryService.executeDMLQueries(body);
            //retourne un json dont la clé est le numéro de la colonne de la table résultante et la valeur est un tableau contenant
            //le nom de la colonne, le type de la colonne et un tableau des valeurs de cette colonne

        });


        get("/select", ( request,  response) -> {
            String test = "SELECT k_reference, nature_cellule, vieillissement_annuel, poids_vide, poids_tac,\n" +
                    "nature_revetment_interieur2_plafond_toto, nature_froid_groupe, type_temprature_groupe, genre_engin,\n" +
                    "marque_groupe, surface_interieure_caisse_m2, classe_origine, nature_revetment_interieur1_plafond_toto ,\n" +
                    "nombre_ouvertures_caisse, battant1_arriere,battant1_lateral_droit, nombre_plinthes_toto,\n" +
                    "densite_isolant_plafond_toto, epaisseur_total_plafond_toto, epaisseur_total_plancher_toto\n" +
                    "\n" +
                    "FROM test.engins500\n" +
                    "\n" +
                    "WHERE k_reference = 0.3 AND nature_cellule = 'rapportee'\n" +
                    "AND vieillissement_annuel = 3.6 AND poids_vide = 7523 AND poids_tac = 19769\n" +
                    " AND nature_revetment_interieur2_plafond_toto = 'gelcoat' AND nature_froid_groupe = 'réfrigérant'\n" +
                    " AND type_temprature_groupe = 'multi' AND genre_engin = 'camion' AND marque_groupe = 'Transcool'\n" +
                    " AND surface_interieure_caisse_m2 = 15.20 AND classe_origine = 'abc' AND\n" +
                    " nature_revetment_interieur1_plafond_toto = 'polyester' AND nombre_ouvertures_caisse = 2\n" +
                    " AND  battant1_arriere = 0 AND battant1_lateral_droit = 0 AND nombre_plinthes_toto = 8.5 AND\n" +
                    "  densite_isolant_plafond_toto = 15 AND epaisseur_total_plafond_toto = 46\n" +
                    "  AND epaisseur_total_plancher_toto = 62.91\n" +
                    "\n" +
                    "USING REALITY r20_engins ;";
            QueryService queryService = new QueryService();
            return queryService.executeDMLQueries(test);
        });



}}


















/**
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import java.io.IOException;
import static org.antlr.v4.runtime.CharStreams.fromFileName;


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
*/