package Server;

import Utils.Utils;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.json.JSONObject;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.*;

import static Server.Server.db;

public class UploadThread extends Thread {
    HttpExchange exchange;

    public UploadThread(HttpExchange exchange) {
        this.exchange = exchange;
    }

    @Override
    public void run() {

        System.out.println("starting new upThread, using port ");
        JSONObject jsonRequest, jsonResponse;

        String username, filename, path, version;

        String method;
        method = exchange.getRequestMethod();

        Headers requestHeaders;
        requestHeaders = exchange.getRequestHeaders();

        InputStream inputStream;
        inputStream = exchange.getRequestBody();

        BufferedReader bfBody = new BufferedReader(new InputStreamReader(inputStream));

        Headers responseHeaders;
        responseHeaders = exchange.getResponseHeaders();

        OutputStream responseBody = null;

        try {
            int port = 9000;
            while (!Utils.portIsAvailable(port) && port < 9999) {
                port++;
            }
            System.out.println("starting new upThread, using port "+port);
            SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port);
            sslServerSocket.setReuseAddress(true);


            jsonRequest = new JSONObject(bfBody.readLine());
            username = jsonRequest.getJSONObject("upload").get("username").toString();
            filename = jsonRequest.getJSONObject("upload").get("filename").toString();
            path = jsonRequest.getJSONObject("upload").get("path").toString();
            version = jsonRequest.getJSONObject("upload").get("version").toString();

            jsonResponse = new JSONObject();
            jsonResponse.put("status", "ready");
            jsonResponse.put("port", port);
            jsonResponse = new JSONObject().put("upload", jsonResponse);

            responseHeaders.set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, jsonResponse.toString().length());
            responseBody = exchange.getResponseBody();
            responseBody.write(jsonResponse.toString().getBytes());
            responseBody.flush();
            //responseBody.close();

            SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept();

            InputStream is = sslSocket.getInputStream();
            FileOutputStream fos = new FileOutputStream(filename);
            BufferedOutputStream bos = new BufferedOutputStream(fos);

            byte[] buffer = new byte[sslSocket.getReceiveBufferSize()];
            int k;
            while ((k = is.read(buffer)) > -1)
                bos.write(buffer, 0, k);


            db.insertFiles(filename, path, version);
            int i = db.getFileID(filename);
            db.insertUserFile(username, i, 1);

            jsonResponse = new JSONObject();
            jsonResponse.put("status", "success");
            jsonResponse = new JSONObject().put("upload", jsonResponse);

            responseHeaders.set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, jsonResponse.toString().length());
            responseBody = exchange.getResponseBody();
            responseBody.write(jsonResponse.toString().getBytes());
            responseBody.close();

            //TODO: receive file

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            //TODO: Tratamento de erros em todos os catches usando os status http como resposta;
            //TODO: Consultar http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
        }
    }
}

