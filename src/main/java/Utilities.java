import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;

public final class Utilities {

    private Utilities() {
        throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static String replaceChar(String str, String oldValue, String newValue) {

        String[] split = str.split(" ");
        for (int i=0; i<split.length; i++) {
            if (split[i].equalsIgnoreCase(oldValue) && split[i].length() == 1) split[i] = newValue ;
        }

        for (int i=0; i<split.length; i++) {
        System.out.println(split[i]);}

        return String.join(" ", split);
    }
    /**
     * Add both traverse tree methods as public methods
     */

    /**
     * Returns labels greater than childLabel according to the lattice latticeName
     */
    //Faire une méthode qui recherche seulement les parents directs d'un label et l'appeler ici
    public static List<String> FindParentLabels (String latticeName, String childLabel){

        List<String> parent_labels = new ArrayList<>();
        parent_labels.add(childLabel);
        Boolean stop = false;
        String sql = "SELECT parent_label "
                + "FROM admin_lattice "
                + "WHERE lattice_name = ? AND child_label = ? ;";
        while (!stop)
        {
            try {
                ConnectPostgreSQL.pstmt = ConnectPostgreSQL.c.prepareStatement(sql);
                ConnectPostgreSQL.pstmt.setString(1, latticeName);
                ConnectPostgreSQL.pstmt.setString(2, childLabel);
                ResultSet rs = ConnectPostgreSQL.pstmt.executeQuery();
                if (rs.next()) {
                    parent_labels.add(rs.getString(1));
                    childLabel = rs.getString(1);
                } else stop = true;

            } catch (Exception e) {
                System.out.println("je suis dans le catch du find parent labels");
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                System.exit(0);
            }
        }
        return parent_labels;
    }


    /**
     * Retourne une chaine de caracteres qui constitue le code correspondant a chaque label de la liste de labels donnée en entrée
     * et de la fonction de comparabilité donnée en entrée
     * le code de chaque label est alterné par un OR
     */

    //public static String FindCodebyLabels (List<String> labels , String functionName)
    public static String FindCodebyLabels ( List<String> labels , String functionName) {
        String label = "";
        String code = "";
        for (int i = 0; i < labels.size(); i++) {
            System.out.println("je print le i");
            System.out.println(i);
            label = labels.get(i);
            System.out.println("je print le labels.geti");
            System.out.println(label);
            String sql = "SELECT code FROM admin_label_code WHERE lc_function_name = ? AND lc_label_name = ?  ;";
            try {
                ConnectPostgreSQL.pstmt = ConnectPostgreSQL.c.prepareStatement(sql);
                ConnectPostgreSQL.pstmt.setString(1, functionName);
                ConnectPostgreSQL.pstmt.setString(2, label);
                ResultSet rs = ConnectPostgreSQL.pstmt.executeQuery();

                while (rs.next()){

                        if (labels.size() - 1 == i) code = code + " ( " + rs.getString(1) + " ) ";
                        else code = code + " ( " + rs.getString(1) + " ) " + " " + "OR" + " ";

                }

            } catch (Exception e) {
                System.out.println("je suis dans le catch du find code by labels");
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                System.exit(0);

            }
        }
        code = " ( " + code + " ) " ;
        return code;
    }

    /**
     * Retourns label that corresponds to the reality realityName associated with attribute attributeName
     */

    public static String FindLabelByAttributeReality ( String realityName , String attributeName) {

        String label = "";
        String sql = "SELECT r_label_name "
                + "FROM admin_realities "
                + "WHERE reality_name = ? AND r_attribute_name = ? ;";
        try {
            ConnectPostgreSQL.pstmt = ConnectPostgreSQL.c.prepareStatement(sql);
            ConnectPostgreSQL.pstmt.setString(1, realityName);
            ConnectPostgreSQL.pstmt.setString(2, attributeName);
            ResultSet rs = ConnectPostgreSQL.pstmt.executeQuery();
            rs.next();
            label = rs.getString(1);

        } catch (Exception e) {
            System.out.println("je suis dans le catch de find label by attribute reality");
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return label;
    }

    /**
     * Returns the comparability name associated with attributeName
     */
    public static String FindCompByAttribute (String attributeName) {

        String comparability = "";
        String sql = "SELECT ca_comparability_name "
                + "FROM admin_comparability_attribute "
                + "WHERE ca_attribute_name = ? ;";
        try {
            ConnectPostgreSQL.pstmt = ConnectPostgreSQL.c.prepareStatement(sql);
            ConnectPostgreSQL.pstmt.setString(1, attributeName);
            ResultSet rs = ConnectPostgreSQL.pstmt.executeQuery();
            rs.next();
            comparability = rs.getString(1);

        } catch (Exception e) {
            System.out.println("je suis dans le catch du find comp by attribute");
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return comparability;
    }


    /**
     * Returns the comparability name associated with attributeName
     */
    public static String FindLatticeByComp (String comparabilityName) {

        String lattice = "";
        String sql = "SELECT cl_lattice_name "
                + "FROM admin_comparability_lattice "
                + "WHERE cl_comparability_name = ? ;";
        try {
            ConnectPostgreSQL.pstmt = ConnectPostgreSQL.c.prepareStatement(sql);
            ConnectPostgreSQL.pstmt.setString(1, comparabilityName);
            ResultSet rs = ConnectPostgreSQL.pstmt.executeQuery();
            rs.next();
            lattice = rs.getString(1);

        } catch (Exception e) {
            System.out.println("je suis dans le catch du find lattice by comp");
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return lattice;
    }

    /**
     * Rewriting method : returns new where query from
     */
    public static String RewriteWhereClause (List<String> whereClause, String realityName) {

        int i = 0 ;
        int j = 0 ;
        List<String> label_list = new ArrayList<>();
        String attribute = "" ;
        String value = "";
        String label = "";
        String lattice = "";
        String comparability = "";
        String new_whereClause = "";
        while (i < whereClause.size()-1) {
            while (i < whereClause.size()-1 && !whereClause.get(i).equalsIgnoreCase("AND") )
            {
                if (whereClause.get(i).equalsIgnoreCase("="))
                {
                    attribute = whereClause.get(i-1);
                    value = whereClause.get(i+1);

                    //if (value.contains("_")) value = value.replace("_", "\'");
                }
                i++;
            }
            //regrouper toutes les parties en une seule ligne
            //exemple:  label_list = FindParentLabels(FindLatticeByComp(FindCompByAttribute(attribute)), label);
            label = FindLabelByAttributeReality(realityName, attribute);
            System.out.println ("le label limite est ");
            System.out.println (label);
            comparability = FindCompByAttribute(attribute);
            System.out.println ("la comparabilité est");
            System.out.println (comparability);
            lattice = FindLatticeByComp(comparability);
            System.out.println ("le lattice est ");
            System.out.println (lattice);
            label_list = FindParentLabels(lattice, label);
            System.out.println ("les elements de label list sont");
            while (j < label_list.size()-1) {
                System.out.println (label_list.get(j));
                j++;
            }
            System.out.println ("le code du where que j'obtient avec FindCodeBylABELS");
            System.out.println (FindCodebyLabels(label_list, comparability));
            new_whereClause = new_whereClause + FindCodebyLabels(label_list, comparability);
            System.out.println ("new where clause avant remplacement des x et y ");
            System.out.println (new_whereClause);
            new_whereClause = replaceChar(new_whereClause,"x", attribute);
            System.out.println ("nw where clause apres remplacement du x ");
            System.out.println (new_whereClause);
            new_whereClause = replaceChar(new_whereClause,"y", value);
            System.out.println ("new where clause apres remplacement du y");
            System.out.println (new_whereClause);

            System.out.println ("print i pour voir s'il est égal a whereclause.size");
            System.out.println (i);
            System.out.println ("print where clause . size ");
            System.out.println (whereClause.size());
            i++;
            if (i != whereClause.size()) new_whereClause = new_whereClause + " " + "AND" + " ";

        }
        return new_whereClause;
    }

}
