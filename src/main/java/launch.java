
import java.io.IOException;
import static spark.Spark.*;
import com.google.gson.Gson;


public class launch {
    public static void main (String[] args) throws IOException {

        //requete de type DDL : CREATE, ALTER standard et CREATE, ALTER avec notre langage
        post("/ddl", ( request,  response) -> {

            String body = request.body(); //le texte en entrée ne doit pas avoir de retour a la ligne
            QueryService queryService = new QueryService();
            queryService.executeDDLQueries(body);
            response.type("application/json");
            return new Gson()
                    .toJson(new StandardResponse(StatusResponse.SUCCESS));

        });

        
        //requete de type DML : SFW standard et SFW USING REALITY 
        post("/dml", ( request,  response) -> {

            String body = request.body(); //le texte en entrée ne doit pas avoir de retour a la ligne
            QueryService queryService = new QueryService();
            response.type("application/json");
            return  queryService.executeDMLQueries(body);
            //retourne un json dont la clé est le numéro de la colonne de la table résultante et la valeur est un tableau contenant
            //le nom de la colonne, le type de la colonne et un tableau des valeurs de cette colonne

        });

}}

