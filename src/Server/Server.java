package Server;

import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.util.concurrent.Executors;

public class Server {
    public static final int PORT = 9001;
    public static final String BASE_CONTEXT = "/";
    public static final String REGISTER_CONTEXT = "/register";
    public static final String LOGIN_CONTEXT = "/login";
    public static final String LIST_CONTEXT = "/list";
    public static final String UPLOAD_CONTEXT = "/upload";
    public static final String DOWNLOAD_CONTEXT = "/download";
    public static final String DELETE_CONTEXT = "/delete";
    public static final Database db = new Database();

    public static void main(String[] args) throws Exception {
        System.setProperty("javax.net.ssl.keyStore", "server.keys");
        System.setProperty("javax.net.ssl.keyStorePassword", "123456");

        HttpsServer httpsServer = HttpsServer.create(new InetSocketAddress(PORT), 0);
        SSLContext sslContext = SSLContext.getInstance("TLS");

        char[] passphrase = "123456".toCharArray();
        KeyStore ks = KeyStore.getInstance("JKS");
        FileInputStream fis = new FileInputStream("server.keys");
        ks.load(fis, passphrase);
        // setup the key manager factory
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, passphrase);
        // setup the trust manager factory
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ks);

        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        httpsServer.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
            public void configure(HttpsParameters params) {
                try {
                    // initialise the SSL context
                    SSLContext c = SSLContext.getDefault();
                    SSLEngine engine = c.createSSLEngine();
                    params.setNeedClientAuth(false);
                    params.setCipherSuites(engine.getEnabledCipherSuites());
                    params.setProtocols(engine.getEnabledProtocols());

                    // get the default parameters
                    SSLParameters defaultSSLParameters = c.getDefaultSSLParameters();
                    params.setSSLParameters(defaultSSLParameters);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Failed to create HTTPS port");
                }
            }
        });
        httpsServer.createContext(BASE_CONTEXT, new BaseHandler());
        httpsServer.createContext(REGISTER_CONTEXT, new RegisterHandler());
        httpsServer.createContext(LOGIN_CONTEXT, new LoginHandler());
        httpsServer.createContext(LIST_CONTEXT, new ListHandler());
        httpsServer.createContext(UPLOAD_CONTEXT, new UploadHandler());
        httpsServer.createContext(DOWNLOAD_CONTEXT, new DownloadHandler());
        httpsServer.createContext(DELETE_CONTEXT, new DeleteHandler());
        httpsServer.setExecutor(Executors.newCachedThreadPool());
        System.out.println("Server starting...");
        httpsServer.start();
    }

}
