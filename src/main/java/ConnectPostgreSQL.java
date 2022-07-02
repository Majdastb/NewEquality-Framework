import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

import com.google.gson.JsonObject;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.postgresql.core.BaseConnection;
import org.postgresql.copy.CopyManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.io.IOException;
import java.sql.SQLException;


import java.io.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

/** Dans cette classe, je met les attributs connection et statement en dehors de la méthode connectJDBC comme attributs statiques
    Je vérifie s'il faut que la méthode soit statique ou pas
    Je cré deux méthodes ici, une qui ouvre la connection et une qui la ferme */

public class ConnectPostgreSQL {

    public static Connection c = null;
    public static Statement stmt = null;
    public static PreparedStatement pstmt = null;

    public void open_JDBC_connection (){

        final String url = "jdbc:postgresql://localhost:5432/new_equality_db";
        final String user = "openpg";
        final String password = "openpgpwd";
            try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection(url, user, password);

            //Connection connection = DriverManager.getConnection(url);
           // CopyManager copyManager = new CopyManager((BaseConnection) c);


            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

        } catch (Exception e) {
                System.out.println("Database not opened ");
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
    }

    public void close_JDBC_connection () {

        try {
            System.out.println("Database closed successfully");
            if (stmt instanceof Statement)
            {System.out.println("je suis dans le if du close ");
                stmt.close(); }
            else { System.out.println("je suis dans le else du close  ");
                pstmt.close();
                }
            c.commit();
            c.close();
        } catch (Exception e ) {
            System.out.println("database not closed successfully");
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }

    }


}
