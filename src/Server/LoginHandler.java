package Server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.*;

import static Server.Server.db;

public class LoginHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        JSONObject jsonRequest, jsonResponse;

        String username, password;

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
            if (requestHeaders.getFirst("Content-type").equals("application/json")) {
                try {
                    jsonRequest = new JSONObject(bfBody.readLine());
                    username = jsonRequest.getJSONObject("login").get("username").toString();
                    password = jsonRequest.getJSONObject("login").get("password").toString();
                    if (db.checkUser(username)) {
                        if (db.validateLogin(username, password)) {
                            jsonResponse = new JSONObject();
                            jsonResponse.put("status", "success");
                            jsonResponse = new JSONObject().put("login", jsonResponse);

                            responseHeaders.set("Content-Type", "application/json");
                            httpExchange.sendResponseHeaders(200, jsonResponse.toString().length());
                            responseBody = httpExchange.getResponseBody();
                            responseBody.write(jsonResponse.toString().getBytes());
                            responseBody.close();
                        } else {
                            System.out.println("wrong password");
                            jsonResponse = new JSONObject();
                            jsonResponse.put("status", "insuccess");
                            jsonRequest.put("message", "wrong password");
                            jsonResponse = new JSONObject().put("login", jsonResponse);

                            responseHeaders.set("Content-Type", "application/json");
                            httpExchange.sendResponseHeaders(401, jsonResponse.toString().length());
                            responseBody = httpExchange.getResponseBody();
                            responseBody.write(jsonResponse.toString().getBytes());
                            responseBody.close();
                        }
                    } else {
                        System.out.println("username");
                        jsonResponse = new JSONObject();
                        jsonResponse.put("status", "insuccess");
                        jsonRequest.put("message", "username not found");
                        jsonResponse = new JSONObject().put("login", jsonResponse);

                        responseHeaders.set("Content-Type", "application/json");
                        httpExchange.sendResponseHeaders(404, jsonResponse.toString().length());
                        responseBody = httpExchange.getResponseBody();
                        responseBody.write(jsonResponse.toString().getBytes());
                        responseBody.close();
                    }
                } catch (Exception e) {
                    System.err.println(e.getClass().getName() + ": " + e.getMessage());
                }
            }
        } else {
            System.err.println("ERROR: login requires POST method");
            httpExchange.sendResponseHeaders(405, 0);
        }
    }
}
