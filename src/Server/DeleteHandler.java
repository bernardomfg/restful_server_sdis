package Server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.*;

public class DeleteHandler  implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        JSONObject jsonRequest, jsonResponse;

        String filename;

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

        if (method.equals("DELETE")) {
            if (requestHeaders.getFirst("Content-Type").equals("application/json")) {
                try {
                    jsonRequest = new JSONObject(bfBody.readLine());
                    filename = jsonRequest.getJSONObject("delete").get("filename").toString();

                    new File(filename).delete();
                    //TODO: complete this
                    //db.removeUserFile(filename);
                    //db.removeFile(filename);

                    jsonResponse = new JSONObject();
                    jsonResponse.put("status", "success");
                    jsonResponse = new JSONObject().put("delete", jsonResponse);

                    responseHeaders.set("Content-Type", "application/json");
                    httpExchange.sendResponseHeaders(200, jsonResponse.toString().length());
                    responseBody = httpExchange.getResponseBody();
                    responseBody.write(jsonResponse.toString().getBytes());
                    responseBody.close();

                } catch (Exception e) {
                    System.err.println(e.getClass().getName() + ": " + e.getMessage());
                }
            }
        } else {
            System.err.println("ERROR: delete requires DELETE method");
            httpExchange.sendResponseHeaders(405, 0);
        }
    }
}
