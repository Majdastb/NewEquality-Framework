import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.Tree;
import org.antlr.v4.runtime.misc.Interval;

import java.util.Stack;
import java.util.*;

public class Mylistener extends PostgreSQLParserBaseListener{

    //La maniere avec laquelle les noeuds sont visités dans le walker correspond a l'ordre dans lequel les lignes du programme sont écrites

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
        Traverse_tree_test util = new Traverse_tree_test();
        String comparability_name = util.traverse_tree(ctx.getChild(2));
        System.out.println("nom de la comp");
        System.out.println(comparability_name);


        ParseTree noeud = ctx.getChild(2);
        while ( noeud.getChild(0) != null ) {
            noeud = noeud.getChild(0);
        }

        //si la comparabilité existe deja dans la liste alors erreur sémantique sinon on l'ajoute à la liste
        if (comps.contains(comparability_name)) {
            semanticErrors.add("Error: comparability" + "\t" + comparability_name + "\t" + "already declared");
            //System.out.println("Error: comparability already declared" );
        }
        else { comps.add(comparability_name); }

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



        List<String> fonction_comparabilite = util.traverse_tree_label_attribute(ctx);
        for (String str : fonction_comparabilite) {
            System.out.println(str);
        }
        String label = null;
        String code = null;
        int i = 0;

        while (i < fonction_comparabilite.size()-1) {
            //System.out.println("premiere boucle while");
            if (fonction_comparabilite.get(i).equalsIgnoreCase("case")) {
                label = fonction_comparabilite.get(i + 1);
            } else if ((fonction_comparabilite.get(i)).equalsIgnoreCase("otherwise")) {
                label = fonction_comparabilite.get(i + 1);
            }

            if ( i+1 != fonction_comparabilite.size()-1) {
                System.out.println("print i+1");
                System.out.println(i+1);
                System.out.println("size -1 ");
                System.out.println(fonction_comparabilite.size()-1);
                i = i + 3;


                while (!(fonction_comparabilite.get(i)).equalsIgnoreCase("case") && !(fonction_comparabilite.get(i)).equalsIgnoreCase("otherwise")) {
                    code = code + fonction_comparabilite.get(i) + " ";
                    i++;
                }

                try {
                    System.out.println("premier try");
                    ConnectPostgreSQL.stmt = ConnectPostgreSQL.c.createStatement();
                    String sql = "INSERT INTO admin_label_code (lc_function_name, lc_label_name, code) "
                            + "VALUES ('" + comparability_name + "','" + label + "','" + code + "');";
                    ConnectPostgreSQL.stmt.executeUpdate(sql);
                } catch (Exception e) {
                    System.err.println(e.getClass().getName() + ": " + e.getMessage());
                    System.exit(0);

                }
            }
            else {
                try {
                    System.out.println("deuxieme try");
                    ConnectPostgreSQL.stmt = ConnectPostgreSQL.c.createStatement();
                    String sql = "INSERT INTO admin_label_code (lc_function_name, lc_label_name) "
                            + "VALUES ('" + comparability_name + "','" + label + "');";
                    ConnectPostgreSQL.stmt.executeUpdate(sql);
                } catch (Exception e) {
                    System.err.println(e.getClass().getName() + ": " + e.getMessage());
                    System.exit(0);

                }

            }
        }

    }

    @Override public void enterAlter_table_cmd(PostgreSQLParser.Alter_table_cmdContext ctx) {
        ParseTree noeud = ctx.getChild(2);
        List<String> predefinedComp = new ArrayList<>();
        predefinedComp.add ("C3U");
        predefinedComp.add ("C3S");
        predefinedComp.add ("C4");

        List<String> predefinedLattice = new ArrayList<>();
        predefinedLattice.add ("L3U");
        predefinedLattice.add ("L3S");
        predefinedLattice.add ("L4");

        //vérifier si le nom de la comparabilité dans le add correspond à une comparabilité déja crée
        if ( (ctx.getChild(1).getText().equalsIgnoreCase("comparability")) && ( ! (predefinedComp.contains(noeud.getText()) )))  {

            while ( noeud.getChild(0) != null ) {
                noeud = noeud.getChild(0);
            }

            String comp = noeud.getText();
            if (!comps.contains(comp)) {
                semanticErrors.add("Error: comparability" + "\t" + comp + "\t" + "not previously declared");
               // System.out.println("Error: comparability" );
            }
        }
        /**else {
            System.out.println("je dois revoir ma condition if dans le enter alter table");
            System.out.println("j'Affiche le get text de comparability et le 3 eme noeud " + ctx.getChild(1).getText()+ noeud.getText() );
        }*/

        //vérifier si le nom du treillis dans le add correspond à un treillis déja crée
        if ( (ctx.getChild(1).getText().equalsIgnoreCase("lattice")) && ( ! (predefinedLattice.contains(noeud.getText()) )))  {

            while ( noeud.getChild(0) != null ) {
                noeud = noeud.getChild(0);
            }

            String lattice = noeud.getText();
            if (!lattices.contains(lattice)) {
                semanticErrors.add("Error: lattice" + "\t" + lattice + "\t" + "not previously declared");
                // System.out.println("Error: comparability" );
            }
        }
    }


    @Override public void enterCreatelatticestmt(PostgreSQLParser.CreatelatticestmtContext ctx) {

        Traverse_tree_test util = new Traverse_tree_test();
        String lattice = util.traverse_tree(ctx.getChild(2));
        System.out.println("nom du treillis");
        System.out.println(lattice);

        //si la comparabilité existe deja dans la liste alors erreur sémantique sinon on l'ajoute à la liste
        if (lattices.contains(lattice)) {
            semanticErrors.add("Error: lattice" + "\t" + lattice + "\t" + "already declared");

        }
        else { lattices.add(lattice); }

        List<String> label_ordering = util.traverse_tree_label_attribute(((ctx.orderedstmt()).orderedlist()).labelordering());
        String label_inf = null;
        String label_sup = null;
        int i = 0;

        while (i <= label_ordering.size()-1) {
            System.out.println("premiere boucle while");
            label_inf = label_ordering.get(i);
            i++;
            label_sup = label_ordering.get(i+1);
            i = i+3;

            try {
                ConnectPostgreSQL.stmt = ConnectPostgreSQL.c.createStatement();
                String sql = "INSERT INTO admin_lattice (lattice_name, parent_label, child_label) "
                        + "VALUES ('"+lattice+"','"+label_sup+"','"+label_inf+"');";
                ConnectPostgreSQL.stmt.executeUpdate(sql);
            } catch (Exception e) {
                System.err.println( e.getClass().getName()+": "+ e.getMessage() );
                System.exit(0);

            }
        }


    }

    @Override public void enterCreaterealitystmt(PostgreSQLParser.CreaterealitystmtContext ctx) {

        //si la réalité existe deja dans la liste alors erreur sémantique sinon on l'ajoute à la liste
        String label = null;
        String attribute = null;
        Traverse_tree_test util = new Traverse_tree_test();
        String reality = util.traverse_tree(ctx.getChild(2));

        if (realities.contains(reality)) {
            semanticErrors.add("Error: reality" + "\t" + reality + "\t" + "already declared");

        }
        else { realities.add(reality); }


        ////////////////////////partie sans intérateur
        List<String> lab_attribute = util.traverse_tree_label_attribute(ctx.getChild(7));
        for (String str : lab_attribute) {
            System.out.println(str);
        }

        int i = 0;
        System.out.println(lab_attribute.get(i));
        String curr, prev, next ;
        curr = null;
        prev = null;
        next = lab_attribute.get(i+1);

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

                    System.out.println("je suis dans le has previous");
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
            //je mets le try catch ici
            try {
                ConnectPostgreSQL.stmt = ConnectPostgreSQL.c.createStatement();
                //String sql = "INSERT INTO admin_using_reality (sf_query, reality_query) " +
                //         "VALUES ('"+select_from_part+"','"+reality_part+"') ;";
                String sql = "INSERT INTO admin_realities (reality_name, r_attribute_name, r_label_name) "
                        + "VALUES ('"+reality+"','"+attribute+"','"+label+"');";
                ConnectPostgreSQL.stmt.executeUpdate(sql);
            } catch (Exception e) {
                System.err.println( e.getClass().getName()+": "+ e.getMessage() );
                System.exit(0);

            }
        }







        //////////////////////////////////partie avec itérateur
        /**ListIterator<String> listIterator = lab_attribute.listIterator();

        //Iterator<String> i = color.iterator();
        System.out.println("Backward Direction Iteration:");
        //je dois mettre une variable stop dans chaque if et un while dont la condition sera avec ls deux stop
        while (listIterator.hasNext()) {
            System.out.println("je suis dans la premiere boucole while");
            System.out.println(listIterator.hasNext());
            System.out.println(listIterator.next());

            boolean stop1 = false;
            boolean stop2 = false;

            while ((listIterator.hasNext()) && (!stop1 || !stop2)) {
               System.out.println("je suis dans la deuxieme boucle while");
                System.out.println(listIterator.hasNext());
                //System.out.println(listIterator.next());
                if ((listIterator.next()).equalsIgnoreCase("u")) System.out.println ("c'est ok");

                if (listIterator.hasPrevious()) {

                    System.out.println("je suis dans le has previous");
                    System.out.println(listIterator.previous());

                    if ((listIterator.previous()).equalsIgnoreCase("(") || (listIterator.previous()).equalsIgnoreCase(",")) {
                        label = listIterator.next();
                        stop1 = true;
                    } else {
                        if ((listIterator.previous()).equalsIgnoreCase("attribute")) {
                            attribute = listIterator.next();
                            stop2 = true;

                        }
                    }
                }
            }

            try {
                ConnectPostgreSQL.stmt = ConnectPostgreSQL.c.createStatement();
                //String sql = "INSERT INTO admin_using_reality (sf_query, reality_query) " +
                //         "VALUES ('"+select_from_part+"','"+reality_part+"') ;";
                String sql = "INSERT INTO admin_realities (reality_name, r_attribute_name, r_label_name) "
                        + "VALUES ('"+reality+"','"+attribute+"','"+label+"');";
                ConnectPostgreSQL.stmt.executeUpdate(sql);
            } catch (Exception e) {
                System.err.println( e.getClass().getName()+": "+ e.getMessage() );
                System.exit(0);

            }
        }*/
    }


    @Override public void enterUsing_reality_clause(PostgreSQLParser.Using_reality_clauseContext ctx) {
        ParseTree inter = ctx.getChild(2);
        String reality = inter.getChild(0).getText();
        if (!realities.contains(reality)) {
            semanticErrors.add("Error: reality" + "\t" + reality + "\t" + "not previously declared");
            // System.out.println("Error: comparability" );
        }

    }

    @Override public void enterSimple_select(PostgreSQLParser.Simple_selectContext ctx) {

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
            */
            System.out.print("Récupérer la aprtie select de la requete: ");
            Traverse_tree_test util = new Traverse_tree_test();
            String select_from_part = "select" + " " + util.traverse_tree(ctx.getChild(3));
            System.out.print(select_from_part);

            //récupérer la partie from de la requete
            System.out.print("Récupérer la aprtie from de la requete: ");
            //Traverse_tree_test util3 = new Traverse_tree_test();
            select_from_part = select_from_part + util.traverse_tree(ctx.getChild(5));
            System.out.print(select_from_part);

            //récupérer la partie where de la requete
            System.out.print("Récupérer la partie where de la requete: ");
            //Traverse_tree_test util2 = new Traverse_tree_test();
            String where_part = util.traverse_tree(ctx.getChild(6));
            System.out.print(where_part);


            try {
                ConnectPostgreSQL.stmt = ConnectPostgreSQL.c.createStatement();
                //String sql = "INSERT INTO admin_using_reality (sf_query, reality_query) " +
                //         "VALUES ('"+select_from_part+"','"+reality_part+"') ;";
                String sql = "INSERT INTO admin_using_reality (sf_query, where_query, reality_query) "
                        + "VALUES ('"+select_from_part+"','"+where_part+"','"+reality_part+"');";
                ConnectPostgreSQL.stmt.executeUpdate(sql);
            } catch (Exception e) {
                System.err.println( e.getClass().getName()+": "+ e.getMessage() );
                System.exit(0);

            }
        }

            /** ParseTree ss_tree = ctx.getChild(10);
              if (ss_tree.getChild(0) != null ) {

                  ParseTree reality_tree = ss_tree.getChild(2);
                  String reality_part = reality_tree.getChild(0).getText();

                  ss_tree = ctx.getChild(3);
                  while ( ss_tree.getChild(0) != null ) {
                      ss_tree = ss_tree.getChild(0);
                  }
                  String select_from_part = "select" + ss_tree.getText();

                  ss_tree =ctx.getChild(5);
                  while ( !ss_tree.getText().equalsIgnoreCase("identifier" )) {
                      if (ss_tree.getChild(1) != null) {
                          ss_tree = ss_tree.getChild(1);
                      }
                      else {
                          ss_tree = ss_tree.getChild(0);
                      }

                  }
                  select_from_part = select_from_part + "from" + ss_tree.getChild(0).getText();*/

             /**try {
                 ConnectPostgreSQL.c.setAutoCommit(false);
                 //perform the DML statements
                 //...
                 //commit the transaction
                 ConnectPostgreSQL.c.commit();
             } catch (Exception e) {
                 //in case of error, rollback the transaction
                 ConnectPostgreSQL.c.rollback();
                 //notify the error
                 e.printStackTrace();
              */


         }




    }

    /**@Override public void enterLabellist(PostgreSQLParser.LabellistContext ctx) {

        for (int i = 1; i < ctx.getChildCount(); i= i+2) {

            labs.add (ctx.getChild(i).getText());
            //System.out.println(ctx.getChild(i).getText());
        }
    }*/


   /** @Override public void enterSimple_select(PostgreSQLParser.Simple_selectContext ctx) {
        ParseTree tree2 = ctx.getChild(10);
        if (tree2 == null) System.out.println("le tree est null");
        ParseTree n = tree2;
        ParseTree parent = n.getParent();
        ParserRuleContext c = (ParserRuleContext) parent;
        if (c != null)
        {
            for (int i = 0; i < c.getChildCount(); i++)
            {
                ParseTree child = c.getChild(i);
                if (child == n)
                {

                    ParseTree tem = c.getChild(i);
                    if (tem instanceof TerminalNodeImpl)
                    {
                        TerminalNodeImpl t = (TerminalNodeImpl) tem;
                        t.parent = null;
                        c.children.remove(i);
                        System.out.println("le noeud a supprimer est final");
                        System.out.println("node succesfully deleted");
                    }
                    else if (tem instanceof ParserRuleContext)
                    {
                        ParserRuleContext t = (ParserRuleContext) tem;
                        t.parent = null;
                        c.children.remove(i);
                        System.out.println("le noeud a supprimer n'est pas final");
                        System.out.println("node successfully deleted");
                    }else
                        System.out.println("Tree contains something other than TerminalNodeImpl or ParserRuleContext");
                    break;
                }
            }
        }

    }



     @Override public void enterRoot(PostgreSQLParser.RootContext ctx) {

    System.out.println("Successssssssss");
    }*/


