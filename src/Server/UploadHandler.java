package Server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.*;

public class UploadHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        JSONObject jsonRequest, jsonResponse;

        String username, filename, path, version;

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

        if (method.equals("PUT")) {
            if (requestHeaders.getFirst("Content-Type").equals("application/json")) {
                try {
                    /*
                    jsonRequest = new JSONObject(bfBody.readLine());
                    username = jsonRequest.getJSONObject("upload").get("username").toString();
                    filename = jsonRequest.getJSONObject("upload").get("filename").toString();
                    path = jsonRequest.getJSONObject("upload").get("path").toString();
                    version = jsonRequest.getJSONObject("upload").get("version").toString();

                    db.insertFiles(filename, path, version);
                    int i = db.getFileID(filename);
                    db.insertUserFile(username, i, 1);

                    jsonResponse = new JSONObject();
                    jsonResponse.put("status", "success");
                    jsonResponse = new JSONObject().put("upload", jsonResponse);

                    responseHeaders.set("Content-Type", "application/json");
                    httpExchange.sendResponseHeaders(200, jsonResponse.toString().length());
                    responseBody = httpExchange.getResponseBody();
                    responseBody.write(jsonResponse.toString().getBytes());
                    responseBody.close();

                    //TODO: receive file
*/
                    new UploadThread(httpExchange).run();
                } catch (Exception e) {
                    System.err.println(e.getClass().getName() + ": " + e.getMessage());
                    e.printStackTrace();
                    //TODO: Tratamento de erros em todos os catches usando os status http como resposta;
                    //TODO: Consultar http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
                }
            }
        } else {
            System.err.println("ERROR: upload requires PUT method");
            httpExchange.sendResponseHeaders(405, 0);
        }
    }
}
