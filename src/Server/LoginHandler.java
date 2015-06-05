package Server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.*;

import static Server.Server.db;

/**
 * Created by Bernardo on 04-06-2015.
 */
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

        if (method.equals("POST")){
            if (requestHeaders.getFirst("Content-type").equals("application/json")){
                try{
                    jsonRequest = new JSONObject(bfBody.readLine());
                    username = jsonRequest.getJSONObject("login").get("username").toString();
                    password = jsonRequest.getJSONObject("login").get("password").toString();

                    if (db.checkUser(username)){
                        if (db.validateLogin(username, password)){
                            jsonResponse = new JSONObject();
                            jsonResponse.put("status", "success");
                            jsonResponse = new JSONObject().put("login", jsonResponse);

                            responseHeaders.set("Content-Type", "application/json");
                            httpExchange.sendResponseHeaders(200, jsonResponse.toString().length());
                            responseBody = httpExchange.getResponseBody();
                            responseBody.write(jsonResponse.toString().getBytes());
                            System.out.println("sent response: " + jsonResponse);
                            responseBody.close();
                        }
                        else{
                            //TODO: "wrong password"
                            System.out.println("wrong password");
                        }

                    }
                    else{
                        //TODO: "name doesnt exist"
                        System.out.println("wrong username");
                    }


                }catch (Exception e){
                    System.err.println(e.getClass().getName() + ": " + e.getMessage());
                    e.printStackTrace();
                    //TODO: Tratamento de erros em todos os catches usando os status http como resposta;
                    //TODO: Consultar http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
                }
            }
        }
        else {
            System.err.println("ERROR: login requires POST method");
            httpExchange.sendResponseHeaders(405, 0);
        }
    }
}
