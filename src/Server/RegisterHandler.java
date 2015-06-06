package Server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.*;

import static Server.Server.db;

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
                    password = jsonRequest.getJSONObject("registration").getString("password");
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
                }
            }
        } else {
            System.err.println("ERROR: registration requires POST method");
            httpExchange.sendResponseHeaders(405, 0);
        }
    }
}
