package Server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static Server.Server.db;

/**
 * Created by Bernardo on 01-06-2015.
 */
public class RegisterHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        JSONObject json = null;

        String username = "", password = "", email = "";

        String method;
        method = httpExchange.getRequestMethod();

        Headers headers;
        headers = httpExchange.getRequestHeaders();

        InputStream inputStream;
        inputStream = httpExchange.getRequestBody();

        BufferedReader bfBody = new BufferedReader(new InputStreamReader(inputStream));

        System.out.println(httpExchange.getHttpContext().getPath());
        System.out.println("request method: " + method);

        if (method.equals("POST")){
            if (headers.getFirst("Content-Type").equals("application/json")){
                try {
                    json = new JSONObject(bfBody.readLine());
                    username = json.get("username").toString();
                    password = json.get("password").toString();
                    email = json.get("email").toString();

                    System.out.println("Username: " + username);
                    System.out.println("Password (encrypted): " + password);
                    System.out.println("Email: " + email);


                    db.insertUsers(username, password, email);

                } catch (Exception e) {
                    System.err.println(e.getClass().getName() + ": " + e.getMessage());

                    //TODO: Tratamento de erros em todos os catches usando os status http como resposta;
                    //TODO: Consultar http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
                }



            }


        }


    }
}
