package Client;

import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;


class Client {
    static URL baseURL;
    private static boolean login_checked = false;
    private static String username;
    private static String password;

    public static void main(String[] args) throws Exception {

        baseURL = new URL("http://localhost:8000/");
        //register("dusty", "cenas", "email@email");
        retrieveFileList();
        //commented out for testing

       /*
        // Local variable
        int swValue;
        do {
            // Display menu graphics
            System.out.println("================================");
            System.out.println("|        Client Menu           |");
            System.out.println("================================");
            System.out.println("| Options:                     |");
            System.out.println("|        1. Login              |");
            System.out.println("|        2. Change Permissions |");
            System.out.println("|        3. Log                |");
            System.out.println("|        4. Exit               |");
            System.out.println("================================");
            swValue = Keyin.inInt(" Select option: ");

            // Init db = new Init();

            // db.log <- devolve o log
            //db.username <- username
            //db.password  <- password

            // Switch construct

            switch (swValue) {
                case 1:
                    System.out.println("LOGIN");
                    //METHOD TO ASK FOR CREDENTIALS AND TRY TO LOGIN FROM DB
                    Scanner s = new Scanner(System.in);
                    System.out.println("Username: ");

                    //TODO CHECK IF USERNAME EXISTS
                    username = s.next();

                    if (true) {
                        System.out.println("Password: ");
                        //TODO CHECK IF password matches EXISTS
                        //s.next()
                        password = s.next();

                    }

                    break;
                case 2:
                    if (login_checked == true) {


                    } else {
                        System.out.println("Please login first.");
                    }
                    break;
                case 3:
                    if (login_checked == true) {


                    } else {
                        System.out.println("Please login first.");
                    }
                    break;
                case 4:


                    break;
                default:
                    System.out.println("Invalid selection");
                    break; // This break is not really necessary
            }
        } while (swValue != 4);
        */
    }

    public static void register(String username, String password, String email)
            throws Exception {
        HttpURLConnection connection = null;
        OutputStreamWriter out = null;
        BufferedReader in = null;

        try {
            URL registerURL = new URL(baseURL, "register");
            connection = (HttpURLConnection) registerURL.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            JSONObject msg = new JSONObject();
            msg.put("username", username);
            msg.put("password", password);
            msg.put("email", email);
            msg = new JSONObject().put("registration", msg);

            out = new OutputStreamWriter(connection.getOutputStream());

            out.write(msg.toString());
            out.close();

            if (connection.getResponseCode() == 200) {
                in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                System.out.println(in.readLine());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
            if (in != null)
                connection.disconnect();
        }
    }

    public static void retrieveFileList() throws Exception {
        URL registerURL = new URL(baseURL, "list");
        HttpURLConnection connection = (HttpURLConnection) registerURL
                .openConnection();
        connection.setRequestMethod("GET");
        //connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("username", "dusty");
        /*
        JSONObject msg = new JSONObject();
        msg.put("username", "dbones");
        msg = new JSONObject().put("list files", msg);
        System.out.println(msg);

        OutputStreamWriter out = new OutputStreamWriter(
                connection.getOutputStream());

        out.write(msg.toString());
        out.close();

        BufferedReader in = new BufferedReader(new InputStreamReader(
                connection.getInputStream()));
*/
        String resp = connection.getResponseMessage();

        System.out.println(resp);

    }

    public void login(String username, String password) throws Exception {
        HttpURLConnection connection = null;
        OutputStreamWriter out = null;
        BufferedReader in = null;
        try {
            URL loginURL = new URL(baseURL, "login");
            connection = (HttpURLConnection) loginURL.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            JSONObject msg = new JSONObject();
            msg.put("username", username);
            msg.put("password", password);
            msg = new JSONObject().put("login", msg);

            out = new OutputStreamWriter(connection.getOutputStream());

            out.write(msg.toString());
            out.close();

            if (connection.getResponseCode() == 200) {
                in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                System.out.println(in.readLine());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
            if (in != null)
                connection.disconnect();
        }
    }

    public void download() throws Exception {
        URL downloadURL = new URL(baseURL, "download");
        HttpURLConnection connection = (HttpURLConnection) downloadURL
                .openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("GET");

        if (connection.getResponseCode() == 200) {
            InputStream is = connection.getInputStream();
            FileOutputStream fos = new FileOutputStream("hue");

            byte[] buffer = new byte[4096]; // buffer
            int len;

            while ((len = is.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }

        }
    }

    public void upload() throws Exception {
        URL uploadURL = new URL(baseURL, "upload");
        HttpURLConnection connection = (HttpURLConnection) uploadURL
                .openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("PUT");

        if (connection.getResponseCode() == 200) {

        }
    }

    public void deleteUser() throws Exception {
        HttpURLConnection connection = null;
        OutputStreamWriter out = null;
        BufferedReader in = null;
        try {
            URL deleteURL = new URL(baseURL, "delete");
            connection = (HttpURLConnection) deleteURL.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Content-Type", "application/json");
            JSONObject msg = new JSONObject();
            msg.put("username", "dbones");
            msg = new JSONObject().put("delete", msg);

            out = new OutputStreamWriter(connection.getOutputStream());

            out.write(msg.toString());
            out.close();

            if (connection.getResponseCode() == 200) {
                in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                System.out.println(in.readLine());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
            if (in != null)
                connection.disconnect();
        }
    }

    public void deleteFile(String file) throws Exception {
        HttpURLConnection connection = null;
        OutputStreamWriter out = null;
        BufferedReader in = null;
        try {
            URL deleteURL = new URL(baseURL, "delete");
            connection = (HttpURLConnection) deleteURL.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Content-Type", "application/json");
            JSONObject msg = new JSONObject();
            msg.put("filename", file);
            msg = new JSONObject().put("delete", msg);

            out = new OutputStreamWriter(connection.getOutputStream());

            out.write(msg.toString());
            out.close();

            if (connection.getResponseCode() == 200) {
                in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                System.out.println(in.readLine());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
            if (in != null)
                connection.disconnect();
        }
    }
}
