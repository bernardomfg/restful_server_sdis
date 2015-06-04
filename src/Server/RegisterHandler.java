package Server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.*;

import static Server.Server.db;

/**
 * Created by Bernardo on 01-06-2015.
 */
public class RegisterHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        JSONObject jsonRequest, jsonResponse;

        String username, password, email;

        String method;
        method = httpExchange.getRequestMethod();

        Headers requestHeaders;
        requestHeaders = httpExchange.getRequestHeaders();

        InputStream inputStream;
        inputStream = httpExchange.getRequestBody();

        BufferedReader bfBody = new BufferedReader(new InputStreamReader(inputStream));

        Headers responseHeaders;
        responseHeaders = httpExchange.getResponseHeaders();

        OutputStream responseBody = null;

        if (method.equals("POST")) {
            if (requestHeaders.getFirst("Content-Type").equals("application/json")) {
                try {
                    jsonRequest = new JSONObject(bfBody.readLine());
                    username = jsonRequest.getJSONObject("registration").get("username").toString();
                    password = jsonRequest.getJSONObject("registration").get("password").toString();
                    email = jsonRequest.getJSONObject("registration").get("email").toString();

                    db.insertUsers(username, password, email);

                    jsonResponse = new JSONObject();
                    jsonResponse.put("status", "success");
                    jsonResponse = new JSONObject().put("registration", jsonResponse);

                    responseHeaders.set("Content-Type", "application/json");
                    httpExchange.sendResponseHeaders(200, jsonResponse.toString().length());
                    responseBody = httpExchange.getResponseBody();
                    responseBody.write(jsonResponse.toString().getBytes());
                    responseBody.close();

                } catch (Exception e) {
                    System.err.println(e.getClass().getName() + ": " + e.getMessage());
                    e.printStackTrace();
                    //TODO: Tratamento de erros em todos os catches usando os status http como resposta;
                    //TODO: Consultar http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
                }
            }
        } else {
            System.out.println("ERROR: registration requires POST method");
            httpExchange.sendResponseHeaders(405, 0);
        }
    }
}
