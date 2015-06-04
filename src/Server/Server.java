package Server;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;


/**
 * Created by Bernardo on 01-06-2015.
 */
public class Server {
    public static final int PORT = 8000;
    public static final int BACKLOG = 10;   // none
    public static final String ADDR_NAME = "127.0.0.1";
    public static final String BASE_CONTEXT = "/";
    public static final String REGISTER_CONTEXT = "/register";
    public static final String LOGIN_CONTEXT = "/login";
    public static final Database db = new Database();

    public static void main(String[] args) throws IOException{

        final HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext(BASE_CONTEXT, new BaseHandler());
        httpServer.createContext(REGISTER_CONTEXT, new RegisterHandler());
        httpServer.createContext(LOGIN_CONTEXT, new LoginHandler());
        httpServer.setExecutor(null); // allow default executor to be created
        System.out.println("Server starting...");
        httpServer.start();
    }

}
