import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/** Dans cette classe, je met les attributs connection et statement en dehors de la méthode connectJDBC comme attributs statiques
    Je vérifie s'il faut que la méthode soit statique ou pas
    Je cré deux méthodes ici, une qui ouvre la connection et une qui la ferme */

public class ConnectPostgreSQL {

    public static Connection c = null;
    public static Statement stmt = null;

    public void open_JDBC_connection (){

            try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/new_equality_db",
                            "openpg", "openpgpwd");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
    }

    public void close_JDBC_connection () {

        try {
            stmt.close();
            c.commit();
            c.close();
        } catch (Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }

    }
}

/**
 stmt = c.createStatement();
 String sql = "INSERT INTO bdd (nom_etudiant,chose,nom_famille,eclat) "
 + "VALUES (5, 'Paul', 32, 'California');";
 stmt.executeUpdate(sql);

 sql = "INSERT INTO bdd (nom_etudiant,chose,nom_famille,eclat) "
 + "VALUES (6, 'Allen', 25, 'Texas');";
 stmt.executeUpdate(sql);

 sql = "INSERT INTO bdd (nom_etudiant,chose,nom_famille,eclat) "
 + "VALUES (7, 'Teddy', 23, 'Norway');";
 stmt.executeUpdate(sql);

 sql = "INSERT INTO bdd (nom_etudiant,chose,nom_famille,eclat) "
 + "VALUES (8, 'Mark', 25, 'Rich-Mond ');";
 stmt.executeUpdate(sql);

 */
