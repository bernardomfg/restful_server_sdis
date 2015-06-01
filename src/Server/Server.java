package Server;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetAddress;
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

    public static void main(String[] args) throws IOException{

        final HttpServer httpServer = HttpServer.create(new InetSocketAddress(InetAddress.getByName(ADDR_NAME), PORT), BACKLOG);
        httpServer.createContext(BASE_CONTEXT, new BaseHandler());
        httpServer.createContext(REGISTER_CONTEXT, new RegisterHandler());




    }

}
