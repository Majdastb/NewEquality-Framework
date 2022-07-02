import org.antlr.v4.runtime.tree.ParseTree;

import java.sql.ResultSet;
import java.util.*;

public class Mylistener extends PostgreSQLParserBaseListener{

    private List<String> comps ; //liste de tous les noms de comparabilités déclarés
    private List<String> labs ; //liste de labels déclarés dans la comparabilité
    private List<String> lattices ; //liste des noms de treillis déclarés
    private List<String> realities ; //liste des noms de réalités déclarés
    public List <String> semanticErrors ; //1. déclaration dupliquée 2. référence a une comp non déclarée
    PostgreSQLParser parser;

    public Mylistener(PostgreSQLParser parser) {
        this.parser = parser;
        comps = new ArrayList<>();
        labs = new ArrayList<>();
        lattices = new ArrayList<>();
        realities = new ArrayList<>();
        semanticErrors = new ArrayList<>();
    }


    @Override public void enterCreatecompstmt(PostgreSQLParser.CreatecompstmtContext ctx) {
        TraverseTree util = new TraverseTree();
        String comparability_name = util.TraverseTreeString(ctx.getChild(2));
        System.out.println("nom de la comp");
        System.out.println(comparability_name);

        String lattice_name = util.TraverseTreeString(ctx.getChild(5));
        System.out.println("nom du treillis");
        System.out.println(lattice_name);

        ParseTree noeud = ctx.getChild(2);
        while ( noeud.getChild(0) != null ) {
            noeud = noeud.getChild(0);
        }

        //creation de la table admin_comparability_lattice
        String comparability_lattice_creation = "CREATE TABLE IF NOT EXISTS public.admin_comparability_lattice  (" +
                "    id_user INTEGER, " +
                "cl_comparability_name VARCHAR (100)," +
                "cl_lattice_name VARCHAR (100)," +
                "PRIMARY KEY (id_user,cl_comparability_name ) ); " ;
        try {
            ConnectPostgreSQL.stmt = ConnectPostgreSQL.c.createStatement();
            ConnectPostgreSQL.stmt.executeUpdate(comparability_lattice_creation);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);

        }

        //creation de la table admin_label_code
        String label_code_creation = "CREATE TABLE IF NOT EXISTS public.admin_label_code  (" +
                "  lc_function_name VARCHAR (100), " +
                " lc_label_name VARCHAR (5)," +
                " code VARCHAR, " +
                "PRIMARY KEY (lc_function_name, lc_label_name) ); " ;
        try {
            ConnectPostgreSQL.stmt = ConnectPostgreSQL.c.createStatement();
            ConnectPostgreSQL.stmt.executeUpdate(label_code_creation);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);

        }


        //si la comparabilité existe deja dans admin_comparability_lattice alors erreur sémantique sinon on l'Ajoute
        String sql = " SELECT cl_comparability_name "
               + "FROM admin_comparability_lattice"
                + " WHERE cl_comparability_name = ? ;";
        try {
            ConnectPostgreSQL.pstmt = ConnectPostgreSQL.c.prepareStatement(sql);
            ConnectPostgreSQL.pstmt.setString(1, comparability_name);
            ResultSet rs = ConnectPostgreSQL.pstmt.executeQuery();
            if (rs.next()) {
                semanticErrors.add("Error: comparability" + "\t" + comparability_name + "\t" + "already declared");

            } else {
                System.out.println("je suis dans le else qui signifie que la comp n'existe pas ");
                ////////////////////////insertion de la comparabilite dans admin_comparability_lattice
                try {
                    ConnectPostgreSQL.stmt = ConnectPostgreSQL.c.createStatement();
                     System.out.println("je suis dans le try de l'insertion de la comp dans admin_comp_latt");
                     String sql2 = "INSERT INTO admin_comparability_lattice (id_user, cl_comparability_name, cl_lattice_name) "
                            + "VALUES ('1','" + comparability_name  + "','" + lattice_name + "');";
                    ConnectPostgreSQL.stmt.executeUpdate(sql2);
                } catch (Exception e) {
                    System.out.println("je suis dans le catch de l'insertion de la comp dans admin_comp_latt");
                    System.err.println(e.getClass().getName() + ": " + e.getMessage());
                    System.exit(0);
                }

                //ajouter les labels de la labellist à la liste labs
                noeud = ctx.getChild(6);
                for (int i = 1; i < noeud.getChildCount(); i= i+2) {

                    labs.add(noeud.getChild(i).getText());
                    //System.out.println(ctx.getChild(i).getText());
                }

                //vérifier si les labels utilisés dans les case correspondent aux labels cités dans la labellist
                for (int i = 0; i < ctx.getChildCount(); i++) {

                    if (ctx.getChild(i).getText().equals(new String("case"))) {
                        if (!labs.contains(ctx.getChild(i+1).getText())) {
                            semanticErrors.add("Error: label" + "\t" + ctx.getChild(i+1).getText() + "\t" + "doesnt match label list");
                            //System.out.println("Error: label" + "\t" + ctx.getChild(i+1).getText() + "\t" + "doesnt match label list");
                        }
                    }

                }

                //partie insertion du code de la fonction de comparabilité dans admin_label_code

                List<String> fonction_comparabilite = util.TraverseTreeList(ctx);
                for (String str : fonction_comparabilite) {
                    System.out.println(str);
                }
                String label = null;
                String sauv = " ";
                String code = "";
                int i = 0;
                int size_comp = fonction_comparabilite.size()-1;

                while (i < size_comp) {
                    System.out.println("premiere boucle while");
                    if (fonction_comparabilite.get(i).equalsIgnoreCase("case") || (fonction_comparabilite.get(i)).equalsIgnoreCase("otherwise")) {
                        System.out.println("premiere condition if");
                        label = fonction_comparabilite.get(i + 1);
                        i = i + 3;

                        while (!(fonction_comparabilite.get(i)).equalsIgnoreCase("case") && !(fonction_comparabilite.get(i)).equalsIgnoreCase("otherwise")) {
                            System.out.println("deuxieme boucle while");
                            code = code + fonction_comparabilite.get(i) + " ";
                            //derouler pour voir sous quelle condition je mets pas de and et si le tout est logique
                            i++;
                        }

                        if ( fonction_comparabilite.get(i+1) != fonction_comparabilite.get(size_comp)) {sauv = sauv + " ( NOT ( " + code + " ) ) AND  " ;
                            System.out.println("condition if de la string sauv");}
                        else {sauv = sauv + " " + " ( NOT ( " + code + " ) ) " ;
                            System.out.println("condition else de la string sauv");}
                        //je reviens a la position avant le case
                        if ((fonction_comparabilite.get(i)).equalsIgnoreCase("otherwise")) {
                            try {

                                System.out.println("premier try");
                                ConnectPostgreSQL.stmt = ConnectPostgreSQL.c.createStatement();
                                //insertion du code du label juste avant le otherwise
                                String sql3 = "INSERT INTO admin_label_code (lc_function_name, lc_label_name, code) "
                                        + "VALUES ('" + comparability_name + "','" + label + "','" + code + "');"
                                        +  "INSERT INTO admin_label_code (lc_function_name, lc_label_name, code) "
                                        + "VALUES ('" + comparability_name + "','" +fonction_comparabilite.get(i+1)+ "','" + sauv + "');";
                                ConnectPostgreSQL.stmt.executeUpdate(sql3);

                                i++;

                            } catch (Exception e) {
                                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                                System.exit(0);

                            }


                        } else {
                            try {
                                ConnectPostgreSQL.stmt = ConnectPostgreSQL.c.createStatement();
                                String sql4 = "INSERT INTO admin_label_code (lc_function_name, lc_label_name, code) "
                                        + "VALUES ('" + comparability_name + "','" + label + "','" + code + "');";
                                ConnectPostgreSQL.stmt.executeUpdate(sql4);
                            } catch (Exception e) {
                                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                                System.exit(0);

                            }
                        }

                        i = i - 1;
                    }
                    code = "";
                    i++;

                }
            }

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    @Override public void enterAlter_table_cmd(PostgreSQLParser.Alter_table_cmdContext ctx) {

        if (ctx.getChild(2) != null) {
            ParseTree noeud = ctx.getChild(2);
            String element_name = noeud.getText();
            System.out.println("nom de la comp");
            System.out.println(element_name);


            //creation de la table admin_comparability_attribute
            String comparability_attribute_creation = "CREATE TABLE IF NOT EXISTS public.admin_comparability_attribute (" +
                    "    id_user INTEGER, " +
                    "ca_comparability_name VARCHAR (100)," +
                    "ca_attribute_name VARCHAR (100)," +
                    "PRIMARY KEY (id_user,ca_attribute_name )); " ;
            try {
                System.out.println("premier try");
                ConnectPostgreSQL.stmt = ConnectPostgreSQL.c.createStatement();
                ConnectPostgreSQL.stmt.executeUpdate(comparability_attribute_creation);
            } catch (Exception e) {
                System.out.println("ERROR: Declaration of multiple comparabilities on same attribute");
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                System.exit(0);

            }

            //vérifier si le nom de la comparabilité dans le add correspond à une comparabilité déja crée dans admin_comparability_lattice
            if ((ctx.getChild(1).getText().equalsIgnoreCase("comparability"))) {

                String sql = " SELECT cl_comparability_name "
                        + "FROM admin_comparability_lattice"
                        + " WHERE cl_comparability_name = ? ;";
                try {
                    ConnectPostgreSQL.pstmt = ConnectPostgreSQL.c.prepareStatement(sql);
                    ConnectPostgreSQL.pstmt.setString(1, element_name);
                    ResultSet rs = ConnectPostgreSQL.pstmt.executeQuery();
                    if (rs.next()) {
                        TraverseTree util = new TraverseTree();
                        List<String> attribute_list = util.TraverseTreeList(ctx.getChild(5));
                        String attribute_name = "";
                        for (int i = 0; i < attribute_list.size(); i++) {
                            System.out.println(i);
                            attribute_name = attribute_list.get(i);

                            try {
                                System.out.println("premier try");
                                ConnectPostgreSQL.stmt = ConnectPostgreSQL.c.createStatement();
                                String sql2 = "INSERT INTO admin_comparability_attribute (id_user, ca_attribute_name, ca_comparability_name) "
                                        + "VALUES ('1','" + attribute_name + "','" + element_name + "');";
                                ConnectPostgreSQL.stmt.executeUpdate(sql2);
                            } catch (Exception e) {
                                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                                System.exit(0);

                            }
                            i++;
                        }

                    } else {

                        semanticErrors.add("ERROR: comparability" + "\t" + element_name + "\t" + "not previously declared");
                        // System.out.println("Error: comparability" );
                    }
                } catch (Exception e) {
                    System.out.println("je suis dans le catch2");
                    System.err.println(e.getClass().getName() + ": " + e.getMessage());
                    System.exit(0);
                }

            } else {
                System.out.println(" ctx.get child 2 ");
            }

        }
    }

    @Override public void enterCreatelatticestmt(PostgreSQLParser.CreatelatticestmtContext ctx) {

        TraverseTree util = new TraverseTree();
        String lattice = util.TraverseTreeString(ctx.getChild(2));
        System.out.println("nom du treillis");
        System.out.println(lattice);
        List<String> label_ordering = util.TraverseTreeList(((ctx.orderedstmt()).orderedlist()).labelordering());
        String label_inf = null;
        String label_sup = null;
        int i = 0;

        //creation de la table admin_lattice si elle n'Existe pas ç
        String creation = "CREATE TABLE IF NOT EXISTS public.admin_lattice  ( " +
                " lattice_name VARCHAR (100)," +
                " parent_label VARCHAR(5) NOT NULL, " +
                "child_label VARCHAR(5) NOT NULL ); " ;
        try {
            System.out.println("premier try");
            ConnectPostgreSQL.stmt = ConnectPostgreSQL.c.createStatement();
            ConnectPostgreSQL.stmt.executeUpdate(creation);
        } catch (Exception e) {
            System.out.println("ERROR: Declaration of multiple comparabilities on same attribute");
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);

        }

        //si le treillis existe deja alors erreur sémantique sinon on l'ajoute à la table admin_lattice
        String sql = "SELECT lattice_name "
                + "FROM admin_lattice"
                + " WHERE lattice_name = ? ;";
        try {
            ConnectPostgreSQL.pstmt = ConnectPostgreSQL.c.prepareStatement(sql);
            ConnectPostgreSQL.pstmt.setString(1, lattice);
            ResultSet rs = ConnectPostgreSQL.pstmt.executeQuery();
            if (rs.next()) {
                semanticErrors.add("ERROR: lattice" + "\t" + lattice + "\t" + "already declared");
            } else {

                while (i <= label_ordering.size()-1) {
                    label_inf = label_ordering.get(i);
                    i++;
                    label_sup = label_ordering.get(i + 1);
                    i = i + 3;

                    try {
                        ConnectPostgreSQL.stmt = ConnectPostgreSQL.c.createStatement();
                        String sql2 = "INSERT INTO admin_lattice (lattice_name, parent_label, child_label) "
                                + "VALUES ('" + lattice + "','" + label_sup + "','" + label_inf + "');";
                        ConnectPostgreSQL.stmt.executeUpdate(sql2);
                    } catch (Exception e) {
                        System.out.println("je suis dans le catch de enterCreatelattice");
                        System.err.println(e.getClass().getName() + ": " + e.getMessage());
                        System.exit(0);

                    }
                }
            }
        } catch (Exception e) {
            System.out.println("je suis dans le catch de enterCreatelattice catch2");
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    @Override public void enterCreaterealitystmt(PostgreSQLParser.CreaterealitystmtContext ctx) {


        String label = null;
        String attribute = null;
        TraverseTree util = new TraverseTree();
        String reality = util.TraverseTreeString(ctx.getChild(2));
        List<String> lab_attribute = util.TraverseTreeList(ctx.getChild(7));
        int i = 0;
        System.out.println(lab_attribute.get(i));
        String curr, prev, next ;
        curr = null;
        prev = null;
        next = lab_attribute.get(i+1);

        //creation de la table admin_realities si elle n'Existe pas ç
        String realities_creation = "CREATE TABLE IF NOT EXISTS public.admin_realities  (" +
                "    reality_name VARCHAR (100), " +
                "    r_attribute_name VARCHAR (100)," +
                "r_label_name VARCHAR (5)," +
                "PRIMARY KEY (reality_name, r_attribute_name) ); " ;
        try {
            ConnectPostgreSQL.stmt = ConnectPostgreSQL.c.createStatement();
            ConnectPostgreSQL.stmt.executeUpdate(realities_creation);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);

        }

        //si la réalité existe deja dans admin_realities alors erreur sémantique sinon on l'ajoute
        String sql = "SELECT reality_name "
                + "FROM admin_realities"
                + " WHERE reality_name = ? ;";
        try {
            System.out.println("je print la reality");
            System.out.println(reality);
            ConnectPostgreSQL.pstmt = ConnectPostgreSQL.c.prepareStatement(sql);
            ConnectPostgreSQL.pstmt.setString(1, reality);
            ResultSet rs = ConnectPostgreSQL.pstmt.executeQuery();
            if (rs.next()) {
                semanticErrors.add("Error: reality" + "\t" + reality + "\t" + "already declared");
            } else {
                for (String str : lab_attribute) {
                    System.out.println(str);
                }
                while ( i <= lab_attribute.size()-3) {
                    boolean stop1 = false;
                    boolean stop2 = false;

                    while ( (i < lab_attribute.size()-2) && (!stop1 || !stop2))
                    {

                        i++;
                        curr = lab_attribute.get(i);
                        prev = lab_attribute.get(i-1);
                        next = lab_attribute.get(i+1);

                        if (prev != null) {

                            System.out.println(prev);

                            if (prev.equalsIgnoreCase("(") || (prev.equalsIgnoreCase(","))) {
                                label = curr;
                                stop1 = true;
                            } else {
                                if (prev.equalsIgnoreCase("attribute")) {
                                    attribute = curr;
                                    stop2 = true;

                                }
                            }
                        }

                    }

                    System.out.println("je print l'attriut ensuite le label");
                    System.out.println(attribute);
                    System.out.println(label);
                    try {
                        ConnectPostgreSQL.stmt = ConnectPostgreSQL.c.createStatement();
                        String sql2 = "INSERT INTO admin_realities (reality_name, r_attribute_name, r_label_name) "
                                + "VALUES ('"+reality+"','"+attribute+"','"+label+"');";
                        ConnectPostgreSQL.stmt.executeUpdate(sql2);
                    } catch (Exception e) {
                        System.err.println( e.getClass().getName()+": "+ e.getMessage() );
                        System.exit(0);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }


    @Override public void enterUsing_reality_clause(PostgreSQLParser.Using_reality_clauseContext ctx) {

        if ( ctx.getChild(2) != null ) {
            String reality = (ctx.getChild(2)).getChild(0).getText();

            String sql = "SELECT reality_name "
                    + "FROM admin_realities"
                    + " WHERE reality_name = ? ;";
            try {
                ConnectPostgreSQL.pstmt = ConnectPostgreSQL.c.prepareStatement(sql);
                ConnectPostgreSQL.pstmt.setString(1, reality);
                ResultSet rs = ConnectPostgreSQL.pstmt.executeQuery();
                if (rs.next()) {

                    System.out.println("je suis dans le if vide que je dois vider");
                } else {
                    semanticErrors.add("Error: reality" + "\t" + reality + "\t" + "not previously declared");
                }
                //si le if vide pose probleme mettre
                /** if (!rs.next())*/
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                System.exit(0);
            }
        }

    }

    /** @Override public void enterSimple_select(PostgreSQLParser.Simple_selectContext ctx) {

        List<String> where_part_main = new ArrayList<>();
        String rewritten = "";
        //tester si le using reality est utilisé dans la requete select
        ParseTree ss_tree = ctx.getChild(10);
        if (ss_tree.getChild(0) != null ) {

            //ParseTree reality_tree = (ss_tree.getChild(2));
            System.out.print("Récupérer la aprtie reality de la requete: ");
            String reality_part = ((ss_tree.getChild(2)).getChild(0)).getText();
            System.out.println(reality_part);

            //récupérer la partie select de la requete
            /**ss_tree = ctx.getChild(3);
            while (ss_tree.getChild(0) != null) {
                ss_tree = ss_tree.getChild(0);
            }

            System.out.print("Récupérer la aprtie select de la requete: ");
            TraverseTree util = new TraverseTree();
            String select_from_part = "select" + " " + (ctx.getChild(3)).getText();
            System.out.print(select_from_part);

            //récupérer la partie from de la requete
            System.out.print("Récupérer la aprtie from de la requete: ");
            //TraverseTree util3 = new TraverseTree();
            select_from_part = select_from_part +  " " + util.TraverseTreeString(ctx.getChild(5));
            System.out.print(select_from_part);

            //récupérer la partie where de la requete
            System.out.print("Récupérer la partie where de la requete: ");

            String where_part = util.TraverseTreeString(ctx.getChild(6));
            System.out.println (where_part);

            //where_part = where_part.replace( '\'', '_');

            /////////////////////////////////////////////////////////////////////////partie qui concerne le gros algorithme
            // mettre le where dans un list
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

                System.out.println("je suis dans le try du enter simple select");
                //ConnectPostgreSQL.pstmt = ConnectPostgreSQL.c.prepareStatement(sql);
                //ResultSet rs = ConnectPostgreSQL.pstmt.executeQuery();

                // We use the executeQuery method. The method executes the given SQL statement,
                // which returns a single ResultSet object. The ResultSet is the data table returned by the SQL query.
                System.out.println("Le resultat de la requete est :");

                //CopyManager copyManager = new CopyManager((BaseConnection) ConnectPostgreSQL.c);
                //File file = new File("/tmp/output.csv");
                //FileOutputStream fileOutputStream = new FileOutputStream(file);

                 //and finally execute the COPY command to the file with this method:
                //copyManager.copyOut("COPY (" + sql + ") TO STDOUT WITH (FORMAT CSV, HEADER)", fileOutputStream);

                 while (rs.next()) {
                     System.out.print (rs.getFloat(1));
                     System.out.print(" : ");
                     System.out.print (rs.getString(2));
                     System.out.print(" : ");
                     System.out.print (rs.getFloat(3));
                     System.out.print(" : ");
                     System.out.print (rs.getInt(4));
                     System.out.print(" : ");
                     System.out.print (rs.getInt(5));
                     System.out.print(" : ");
                     System.out.print (rs.getString(6));
                     System.out.print(" : ");
                     System.out.print (rs.getString(7));
                     System.out.print(" : ");
                     System.out.print (rs.getString(8));
                     System.out.print(" : ");
                     System.out.print (rs.getString(9));
                     System.out.print(" : ");
                     System.out.print (rs.getString(10));
                     System.out.print(" : ");
                     System.out.print (rs.getFloat(11));
                     System.out.print(" : ");
                     System.out.print (rs.getString(12));
                     System.out.print(" : ");
                     System.out.print (rs.getString(13));
                     System.out.print(" : ");
                     System.out.print (rs.getInt(14));
                     System.out.print(" : ");
                     System.out.print (rs.getInt(15));
                     System.out.print(" : ");
                     System.out.print (rs.getInt(16));
                     System.out.print(" : ");
                     System.out.print (rs.getFloat(17));
                     System.out.print(" : ");
                     System.out.print (rs.getInt(18));
                     System.out.print(" : ");
                     System.out.print (rs.getFloat(19));
                     System.out.print(" : ");
                     System.out.println (rs.getFloat(20));
                     /**System.out.print(" : ");
                     System.out.print (rs.getString(21));
                     System.out.print(" : ");
                     System.out.print (rs.getString(22));
                     System.out.print(" : ");
                     System.out.print (rs.getString(23));
                     System.out.print(" : ");
                     System.out.print (rs.getString(24));
                     System.out.print(" : ");
                     System.out.print (rs.getString(25));
                     System.out.print(" : ");
                     System.out.print (rs.getString(26));
                     System.out.print(" : ");
                     System.out.print (rs.getString(27));
                     System.out.print(" : ");
                     System.out.print (rs.getString(28));
                     System.out.print(" : ");
                     System.out.print (rs.getFloat(29));
                     System.out.print(" : ");
                     System.out.println (rs.getString(30));


                }

                rs.close();
            } catch (Exception e) {
                System.out.println("je suis dans le catch du entersimple select");
                System.err.println( e.getClass().getName()+": "+ e.getMessage() );
                System.exit(0);

            }

        }

         } */


    @Override public void enterStmtmulti(PostgreSQLParser.StmtmultiContext ctx) {
        //créer un visiteur qui retourne un json object, si c'est un select je retourne la meme chose que ce que j'avais fait avec le using reality : if test.get(0).equalsIgnoreCase ("select")
        //si c'est pas un select, je retourne un json avec success
        System.out.println("CA MARCHE 1");
        int i = 0 ;
        List<String> test = new ArrayList<>();
        TraverseTree util = new TraverseTree();
        test = util.TraverseTreeList(ctx);
        while (i < test.size()){
            System.out.println(test.get(i));
            i++;
        }

        System.out.println("je print le test.get 4");
        System.out.println(test.get(4));
        System.out.println("je print le test.get 6");
        System.out.println(test.get(6));
        //cas du ALTER TABLE
        if (!test.get(6).equalsIgnoreCase("comparability") && !test.get(4).equalsIgnoreCase("comparability")
        && (!test.get(test.size()-3).equalsIgnoreCase("reality") && !test.get(test.size()-4).equalsIgnoreCase("using"))
        && !test.get(1).equalsIgnoreCase("lattice") && !test.get(1).equalsIgnoreCase("comparability") && !test.get(1).equalsIgnoreCase("reality")
        && !test.get(0).equalsIgnoreCase("select"))
        {

            System.out.println("c'est une requete standard SANS LE NOUVEAU LANGAGE ");

            try {
                ConnectPostgreSQL.stmt = ConnectPostgreSQL.c.createStatement();
                String sql = String.join(" ", test);
                ConnectPostgreSQL.stmt.executeUpdate(sql);
            } catch (Exception e) {
                System.out.println("je suis dans le catch du else du visit stmt multi");
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                System.exit(0);

            }

                /**String sql = String.join(" ", test);
                ConnectPostgreSQL.pstmt = ConnectPostgreSQL.c.prepareStatement(sql);
                ResultSet rs = ConnectPostgreSQL.pstmt.executeQuery();*/
        }
    }
    }


