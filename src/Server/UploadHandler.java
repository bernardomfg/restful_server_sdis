package Server;

import Utils.Utils;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.*;

import static Server.Server.db;

public class UploadHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method;
        method = httpExchange.getRequestMethod();

        Headers requestHeaders;
        requestHeaders = httpExchange.getRequestHeaders();

        if (method.equals("PUT")) {
            if (requestHeaders.getFirst("Content-Type").equals("application/json")) {
                JSONObject jsonRequest, jsonResponse;

                String username, filename, path, version;

                InputStream inputStream;
                inputStream = httpExchange.getRequestBody();

                BufferedReader bfBody = new BufferedReader(new InputStreamReader(inputStream));

                Headers responseHeaders;
                responseHeaders = httpExchange.getResponseHeaders();

                OutputStream responseBody = null;
                BufferedInputStream bis = null;
                FileOutputStream fos = null;
                BufferedOutputStream bos = null;

                try {
                    int port = 9000;
                    while (!Utils.portIsAvailable(port) && port < 9999) {
                        port++;
                    }
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
                    httpExchange.sendResponseHeaders(200, jsonResponse.toString().length());
                    responseBody = httpExchange.getResponseBody();
                    responseBody.write(jsonResponse.toString().getBytes());
                    responseBody.flush();


                    SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept();
                    File f = new File(filename);
                    if (f.exists()) {
                        f.delete();
                        f.createNewFile();
                    }
                    bis = new BufferedInputStream(sslSocket.getInputStream());
                    fos = new FileOutputStream(f);
                    bos = new BufferedOutputStream(fos);


                    byte[] buffer = new byte[sslSocket.getReceiveBufferSize()];
                    int k;
                    while ((k = bis.read(buffer)) > -1)
                        bos.write(buffer, 0, k);

                    if (version.equals(Utils.getFileID(filename))) {
                        db.insertFiles(filename, path, version);
                        int i = db.getFileID(filename);
                        db.insertUserFile(username, i, 1);

                        jsonResponse = new JSONObject();
                        jsonResponse.put("status", "success");
                        jsonResponse = new JSONObject().put("upload", jsonResponse);

                    } else {
                        new File(filename).delete();
                        jsonResponse = new JSONObject();
                        jsonResponse.put("status", "failed");
                        jsonResponse = new JSONObject().put("upload", jsonResponse);
                    }
                    responseBody.write(jsonResponse.toString().getBytes());
                    responseBody.close();

                } catch (Exception e) {
                    System.err.println(e.getClass().getName() + ": " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    try {
                        if (bos != null)
                            bos.close();
                        if (bis != null)
                            bis.close();
                        if (fos != null)
                            fos.close();
                        if (responseBody != null) {
                            responseBody.close();
                        }
                    } catch (IOException ignore) {
                    }
                }
            }
        } else {
            System.err.println("ERROR: upload requires PUT method");
            httpExchange.sendResponseHeaders(405, 0);
        }
    }
}
