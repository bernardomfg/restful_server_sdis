package Client;

import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


class Client {
    static URL baseURL;
    private static boolean login_checked = false;
    private static String username;
    private static String password;
    private static String email;

    private static void manageFilesMenu() throws Exception{
        baseURL = new URL("http://localhost:8000/");
        // Local variable
        int swValue;
        do {
            // Display menu graphics
            System.out.println("===================================");
            System.out.println("|         Manage My Files         |");
            System.out.println("===================================");
            System.out.println("| Options:                        |");
            System.out.println("|        1. Upload File           |");
            System.out.println("|        2. List Existent Files   |"); //list files, Permissions, Remove files
            System.out.println("|        3. Edit File Permissions |");
            System.out.println("|        4. Delete File           |");
            System.out.println("|        5. Exit                  |");
            System.out.println("===================================");
            swValue = Keyin.inInt(" Select option: ");

            Scanner s = new Scanner(System.in);
            switch (swValue) {
                case 1:

                    break;
                case 2:

                    //Don't even know what this is
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    break;
                default:
                    System.out.println("Invalid selection");
                    break; // This break is not really necessary
            }
        } while (swValue != 5);
    }

    private static void usertMenu () throws Exception {
        baseURL = new URL("http://localhost:8000/");
        // Local variable
        int swValue;
        do {
            // Display menu graphics
            System.out.println("================================");
            System.out.println("|         User Menu            |");
            System.out.println("================================");
            System.out.println("| Options:                     |");
            System.out.println("|        1. Manage My Files    |"); //list files, Permissions, Remove files
            System.out.println("|        2. Log                |");
            System.out.println("|        3. Exit               |");
            System.out.println("================================");
            swValue = Keyin.inInt(" Select option: ");

            Scanner s = new Scanner(System.in);
            switch (swValue) {
                case 1:
                   manageFilesMenu();
                    break;
                case 2:
                    System.out.println("LOG");
                    //Don't even know what this is
                    break;
                case 3:
                    break;
                default:
                    System.out.println("Invalid selection");
                    break; // This break is not really necessary
            }
        } while (swValue != 3);
    }


    private static void firstMenu () throws Exception {
        baseURL = new URL("http://localhost:8000/");
        // Local variable
        int swValue;
        do {
            // Display menu graphics
            System.out.println("================================");
            System.out.println("|        Client Menu           |");
            System.out.println("================================");
            System.out.println("| Options:                     |");
            System.out.println("|        1. Register           |");
            System.out.println("|        2. Login              |");
            System.out.println("|        3. Exit               |");
            System.out.println("================================");
            swValue = Keyin.inInt(" Select option: ");

            // Init db = new Init();

            // db.log <- devolve o log
            //db.username <- username
            //db.password  <- password

            // Switch construct
            Scanner s = new Scanner(System.in);
            switch (swValue) {
                case 1:
                    System.out.println("REGISTER");
                    System.out.println("Username: ");
                    username = s.next();
                    System.out.println("Password: ");
                    password = s.next();
                    //TODO Encrypt password
                    System.out.println("Email: ");
                    email = s.next();
                    register(username,password,email);
                    break;
                case 2:
                    System.out.println("LOGIN");
                    //METHOD TO ASK FOR CREDENTIALS AND TRY TO LOGIN FROM DB

                    System.out.println("Username: ");

                    //TODO CHECK IF USERNAME EXISTS
                    username = s.next();
                    System.out.println("Password: ");
                    login(username,password);
                    //TODO CHECK IF password matches EXISTS
                    password = s.next();
                    if (login_checked == true) {
                        //TODO If loged in with sucess, USer Menu
                    } else {
                        System.out.println("Please login first.");
                    }
                    break;
                case 3:
                    break;
                default:
                    System.out.println("Invalid selection");
                    break; // This break is not really necessary
            }
        } while (swValue != 3);

        // register("asdwr", "cenas", "email@email");
        // register("asd", "cenas", "email@email");
        // register("dsa", "cenas", "email@email");
        //register("duwesty", "cenas", "email@email");

    }

    public static void main(String[] args) throws Exception {
        firstMenu();
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

    public static void login(String username, String password) throws Exception {
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

    public static void upload(String filePath) throws Exception {
        URL uploadURL = new URL(baseURL, "upload");
        HttpURLConnection connection = (HttpURLConnection) uploadURL
                .openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-Type", "application/json");

        BufferedReader br = null;
        OutputStreamWriter out = null;
        BufferedReader in = null;
        String file  = ""; // Encrypt file
        filePath = filePath.replace("\\", "/");
        String splitString[] = filePath.split("/");
        String fileName = splitString[splitString.length-1];

        try {
            String sCurrentLine;

            br = new BufferedReader(new FileReader(filePath));

            while ((sCurrentLine = br.readLine()) != null) {
                file += sCurrentLine;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }


        JSONObject msg = new JSONObject();
        msg = new JSONObject().put("fileName", fileName);//String com a todos os ficheiro do utilizador
        msg.put("file", file);
        out = new OutputStreamWriter(connection.getOutputStream());
        System.out.println(msg);

        out.write(file);
        out.close();

        if (connection.getResponseCode() == 200) {
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            System.out.println(in.readLine());
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
