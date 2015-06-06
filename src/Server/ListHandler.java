package Server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;

import static Server.Server.db;

public class ListHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        JSONObject jsonResponse, jsonList;

        String username;
        int permission;

        String method;
        method = httpExchange.getRequestMethod();

        Headers requestHeaders;
        requestHeaders = httpExchange.getRequestHeaders();

        Headers responseHeaders;
        responseHeaders = httpExchange.getResponseHeaders();

        OutputStream responseBody;

        if (method.equals("POST")) {
            if (requestHeaders.getFirst("Content-Type").equals("application/json")) {
                try {
                    username = requestHeaders.getFirst("username").toString();
                    permission = Integer.parseInt(requestHeaders.getFirst("permission").toString());
                    System.out.println(permission);

                    ArrayList<String> fileList = db.getUserFile(username, permission);


                    responseHeaders.set("Content-Type", "application/json");
                    jsonResponse = new JSONObject().put("status", "success");
                    jsonList = new JSONObject();
                    for (String s : fileList) {
                        jsonList.put("file", s);
                    }
                    jsonResponse = new JSONObject().put("list", jsonResponse);
                    jsonResponse.put("list", fileList);
                    System.out.println(jsonResponse);

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
            System.out.println("ERROR: expected POST method");
            httpExchange.sendResponseHeaders(405, 0);
        }
    }
}