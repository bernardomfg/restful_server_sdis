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
        JSONObject jsonRequest, jsonResponse;

        String username;

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

        if (method.equals("GET")) {
            try {
                username = requestHeaders.getFirst("username").toString();

                ArrayList<String> fileList = db.getUserFile(username);

                jsonResponse = new JSONObject();
                jsonResponse.put("status", "success");
                for (String s : fileList) {
                    jsonResponse.put("file", s);
                }
                System.out.println(jsonResponse);

                    jsonResponse = new JSONObject().put("registration", jsonResponse);
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
        } else {
            System.out.println("ERROR: expected GET method");
            httpExchange.sendResponseHeaders(405, 0);
        }
    }
}