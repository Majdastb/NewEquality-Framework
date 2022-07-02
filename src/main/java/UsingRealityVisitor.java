import org.antlr.v4.runtime.tree.ParseTree;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSetMetaData;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;

public class UsingRealityVisitor extends PostgreSQLParserBaseVisitor {

    /**PostgreSQLParser parser;

    public UsingRealityVisitor (PostgreSQLParser parser) {
        this.parser = parser;
    }*/

    @Override public JSONObject visitSimple_select(PostgreSQLParser.Simple_selectContext ctx) {

        List<String> where_part_main;
        String rewritten = "";
        JSONObject obj = new JSONObject();
        //tester si le using reality est utilisé dans la requete select
        ParseTree ss_tree = ctx.getChild(10);

        System.out.print("Récupérer la aprtie select de la requete: ");
        TraverseTree util = new TraverseTree();
        String select_from_part = "select" + " " + (ctx.getChild(3)).getText();
        System.out.print(select_from_part);

        //récupérer la partie from de la requete
        System.out.print("Récupérer la aprtie from de la requete: ");
        select_from_part = select_from_part +  " " + util.TraverseTreeString(ctx.getChild(5));
        System.out.print(select_from_part);

        //récupérer la partie where de la requete
        System.out.print("Récupérer la partie where de la requete: ");
        String where_part = util.TraverseTreeString(ctx.getChild(6));
        System.out.println (where_part);

        if (ss_tree.getChild(0) != null ) {

            System.out.print("Récupérer la aprtie reality de la requete: ");
            String reality_part = ((ss_tree.getChild(2)).getChild(0)).getText();
            System.out.println(reality_part);

            /////////////////////////////////////////////////////////////////////////partie qui concerne la réécriture
            // mettre le where dans une liste
            int k =  0 ;
            where_part_main = util.TraverseTreeList(ctx.getChild(6));
            System.out.print("je print les elements de la LISTE WHERE PART ");
            while (k <= where_part_main.size()-1) {

                System.out.println (where_part_main.get(k));
                k++;
            }
            rewritten = Utilities.RewriteWhereClause(where_part_main, reality_part);
            System.out.println("La clause where réécrite est la suivante");
            System.out.println(rewritten);

            String sql = select_from_part + " " + "WHERE" + " " + rewritten + " " + ";";
            System.out.println("La requete complete est la suivante :");
            System.out.println(sql);


            try {

                ConnectPostgreSQL.stmt = ConnectPostgreSQL.c.createStatement();

                ResultSet rs = ConnectPostgreSQL.stmt.executeQuery( sql );
                RowSetFactory factory = RowSetProvider.newFactory();
                CachedRowSet rowset = factory.createCachedRowSet();

                rowset.populate(rs);
                ResultSetMetaData resultSetMetaData = rs.getMetaData();
//getting the column type
                int column_count = resultSetMetaData.getColumnCount();
                System.out.println("le column count est de ");
                System.out.println(column_count);
                System.out.println("je suis dans le try du visit simple select");
                //ConnectPostgreSQL.pstmt = ConnectPostgreSQL.c.prepareStatement(sql);
                //ResultSet rs = ConnectPostgreSQL.pstmt.executeQuery();

                // We use the executeQuery method. The method executes the given SQL statement,
                // which returns a single ResultSet object. The ResultSet is the data table returned by the SQL query.


                String colType = "";
                for (int i = 1; i <= column_count; i++) {

                    String columnName = resultSetMetaData.getColumnName(i);
                    System.out.println(columnName);
                    int columnType = resultSetMetaData.getColumnType(i);
                    System.out.println("Le type de la colonne est :");
                    System.out.println(columnType);

                    JSONArray col1 = new JSONArray();
                    col1.add (columnName);
                    JSONArray rowscol1 = new JSONArray();

                    rowset.beforeFirst();
                    while (rowset.next()) {

                        if (columnType == 2 || columnType == 7) {

                            float rowValue = rowset.getFloat(i);
                            rowscol1.add (rowValue);
                            colType = "real";}
                        else if (columnType == 12) {

                            String rowValue = rowset.getString(i);
                            rowscol1.add (rowValue);
                            colType = "text";}
                        else if (columnType == 4) {

                            Integer rowValue = rowset.getInt(i);
                            rowscol1.add (rowValue);
                            colType = "integer";}

                    }
                    col1.add (colType);
                    col1.add (rowscol1);
                    obj.put(i, col1);

                }

                rs.close();
            } catch (Exception e) {
                System.out.println("je suis dans le catch du visitsimple select");
                System.err.println( e.getClass().getName()+": "+ e.getMessage() );
                System.exit(0);

            }
        }
        else {
            String sql = select_from_part + " " + where_part + " " + ";" ;

            //String sql = select_from_part + " " + "WHERE" + " " + rewritten + " " + ";";

            try {

                ConnectPostgreSQL.stmt = ConnectPostgreSQL.c.createStatement();

                ResultSet rs = ConnectPostgreSQL.stmt.executeQuery( sql );
                RowSetFactory factory = RowSetProvider.newFactory();
                CachedRowSet rowset = factory.createCachedRowSet();

                rowset.populate(rs);
                ResultSetMetaData resultSetMetaData = rs.getMetaData();
                //getting the column type
                int column_count = resultSetMetaData.getColumnCount();
                System.out.println("le column count est de ");
                System.out.println(column_count);
                System.out.println("je suis dans le try du enter simple select");
                //ConnectPostgreSQL.pstmt = ConnectPostgreSQL.c.prepareStatement(sql);
                //ResultSet rs = ConnectPostgreSQL.pstmt.executeQuery();

                // We use the executeQuery method. The method executes the given SQL statement,
                // which returns a single ResultSet object. The ResultSet is the data table returned by the SQL query.
                System.out.println("Le resultat de la requete est :");

                String colType = "";
                for (int i = 1; i <= column_count; i++) {

                    String columnName = resultSetMetaData.getColumnName(i);
                    System.out.println(columnName);
                    int columnType = resultSetMetaData.getColumnType(i);
                    System.out.println(columnType);

                    JSONArray col1 = new JSONArray();
                    col1.add (columnName);
                    JSONArray rowscol1 = new JSONArray();

                    rowset.beforeFirst();
                    while (rowset.next()) {

                        if (columnType == 2 || columnType == 7) {

                            float rowValue = rowset.getFloat(i);
                            rowscol1.add (rowValue);
                            colType = "real";}
                        else if (columnType == 12) {

                            String rowValue = rowset.getString(i);
                            rowscol1.add (rowValue);
                            colType = "text";}
                        else if (columnType == 4) {

                            Integer rowValue = rowset.getInt(i);
                            rowscol1.add (rowValue);
                            colType = "integer";}

                    }
                    col1.add (colType);
                    col1.add (rowscol1);
                    obj.put(i, col1);

                }

                rs.close();
            } catch (Exception e) {
                System.out.println("je suis dans le catch du visit simple select partie else ");
                System.err.println( e.getClass().getName()+": "+ e.getMessage() );
                System.exit(0);

            }

        }

        return obj ;
    }


   /** @Override public String visitStmtmulti(PostgreSQLParser.StmtmultiContext ctx) {

        return ctx.getText();

        //créer un visiteur qui retourne un json object, si c'est un select je retourne la meme chose que ce que j'avais fait avec le using reality
        //si c'est pas un select, je retourne un json avec success
        System.out.println("CA MARCHE 1");
        JSONObject obj = new JSONObject();
        int i = 0 ;
        List<String> test = new ArrayList<>();
        TraverseTree util = new TraverseTree();
        test = util.TraverseTreeList(ctx);
        while (i < test.size()){
            System.out.println(test.get(i));
            i++;
        }

        if (!test.get(6).equalsIgnoreCase("comparability") && !test.get(4).equalsIgnoreCase("comparability") //cas du ALTER TABLE
                && (!test.get(test.size()-3).equalsIgnoreCase("reality") && !test.get(test.size()-4).equalsIgnoreCase("using"))
                && !test.get(1).equalsIgnoreCase("lattice") && !test.get(1).equalsIgnoreCase("comparability") && !test.get(1).equalsIgnoreCase("reality") )
        {
            //ya juste le select qui me retourne un résultat, pour le reste, je ne dois rien faire, les insertions, les updates etc se font automatiquement
            //etape 2 :je dois tester si j'ai un select dans la liste test, si oui alors j'envoie la requete et je récupère le résultat
            //
            System.out.println("c'est une requete standard SANS LE NOUVEAU LANGAGE ");

            if (test.get(0).equalsIgnoreCase ("select")) { //requete SFW

                try {

                    String sql = String.join(" ", test);
                    ConnectPostgreSQL.stmt = ConnectPostgreSQL.c.createStatement();
                    ResultSet rs = ConnectPostgreSQL.stmt.executeQuery( sql );
                    RowSetFactory factory = RowSetProvider.newFactory();
                    CachedRowSet rowset = factory.createCachedRowSet();

                    rowset.populate(rs);
                    ResultSetMetaData resultSetMetaData = rs.getMetaData();
//getting the column type
                    int column_count = resultSetMetaData.getColumnCount();
                    System.out.println("le column count est de ");
                    System.out.println(column_count);
                    System.out.println("je suis dans le try du enter simple select");
                    //ConnectPostgreSQL.pstmt = ConnectPostgreSQL.c.prepareStatement(sql);
                    //ResultSet rs = ConnectPostgreSQL.pstmt.executeQuery();

                    // We use the executeQuery method. The method executes the given SQL statement,
                    // which returns a single ResultSet object. The ResultSet is the data table returned by the SQL query.
                    System.out.println("Le resultat de la requete est :");

                    String colType = "";
                    for (int j = 1; j <= column_count; j++) {

                        String columnName = resultSetMetaData.getColumnName(i);
                        System.out.println(columnName);
                        int columnType = resultSetMetaData.getColumnType(i);
                        System.out.println(columnType);

                        JSONArray col1 = new JSONArray();
                        col1.add (columnName);
                        JSONArray rowscol1 = new JSONArray();

                        rowset.beforeFirst();
                        while (rowset.next()) {

                            if (columnType == 2 || columnType == 7) {

                                float rowValue = rowset.getFloat(i);
                                rowscol1.add (rowValue);
                                colType = "real";}
                            else if (columnType == 12) {

                                String rowValue = rowset.getString(i);
                                rowscol1.add (rowValue);
                                colType = "text";}
                            else if (columnType == 4) {

                                Integer rowValue = rowset.getInt(i);
                                rowscol1.add (rowValue);
                                colType = "integer";}

                        }
                        col1.add (colType);
                        col1.add (rowscol1);
                        obj.put(i, col1);

                    }

                    rs.close();
                } catch (Exception e) {
                    System.out.println("je suis dans le catch du select dans visit stmtmulti");
                    System.err.println( e.getClass().getName()+": "+ e.getMessage() );
                    System.exit(0);

                }


            }
            else { //tout autre type de requete
                try {
                    ConnectPostgreSQL.stmt = ConnectPostgreSQL.c.createStatement();
                    String sql2 = String.join(" ", test);
                    ConnectPostgreSQL.stmt.executeUpdate(sql2);
                    obj.put("status", "SUCCESS");
                } catch (Exception e) {
                    System.out.println("je suis dans le catch du else du visit stmt multi");
                    System.err.println(e.getClass().getName() + ": " + e.getMessage());
                    System.exit(0);

                }

            }

        }

        return obj;
    }*/
}
