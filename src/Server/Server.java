package Server;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {
    public static final int PORT = 8080;
    public static final String ADDR_NAME = "127.0.0.1";
    public static final String BASE_CONTEXT = "/";
    public static final String REGISTER_CONTEXT = "/register";
    public static final String LOGIN_CONTEXT = "/login";
    public static final String LIST_CONTEXT = "/list";
    public static final String UPLOAD_CONTEXT = "/upload";
    public static final String DOWNLOAD_CONTEXT = "/download";
    public static final String DELETE_CONTEXT = "/delete";
    public static final Database db = new Database();

    public static void main(String[] args) throws IOException{

        HttpServer httpServer = HttpServer.create(new InetSocketAddress(ADDR_NAME,PORT), 0);
        httpServer.createContext(BASE_CONTEXT, new BaseHandler());
        httpServer.createContext(REGISTER_CONTEXT, new RegisterHandler());
        httpServer.createContext(LOGIN_CONTEXT, new LoginHandler());
        httpServer.createContext(LIST_CONTEXT, new ListHandler());
        httpServer.createContext(UPLOAD_CONTEXT, new UploadHandler());
        httpServer.createContext(DOWNLOAD_CONTEXT, new DownloadHandler());
        httpServer.createContext(DELETE_CONTEXT, new DeleteHandler());
        httpServer.setExecutor(null); // allow default executor to be created
        System.out.println("Server starting...");
        httpServer.start();
        System.out.println(httpServer.getAddress());
    }

}
