package Server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Bernardo on 01-06-2015.
 */
public class RegisterHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        JSONObject json;

        String method;
        method = httpExchange.getRequestMethod();

        Headers headers;
        headers = httpExchange.getRequestHeaders();

        InputStream inputStream;
        inputStream = httpExchange.getRequestBody();

        BufferedReader bfBody = new BufferedReader(new InputStreamReader(inputStream));

        System.out.println(httpExchange.getHttpContext().getPath());
        System.out.println("request method: " + httpExchange.getRequestMethod());

        if (method.equals("POST")){
            if (headers.getFirst("Content-Type").equals("application/json")){
                try {
                    json = new JSONObject(bfBody.readLine());
                    System.out.println(json);
                } catch (Exception e) {
                    System.err.println(e.getClass().getName() + ": " + e.getMessage());
                }


            }


        }


    }
}
