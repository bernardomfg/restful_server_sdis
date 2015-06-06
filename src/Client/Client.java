package Client;

import Utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;


class Client {
    static URL baseURL;
    private static boolean login_checked = false;
    private static String username;
    private static String password;
    private static String email;

    private static void manageFilesMenu() throws Exception {
        // Local variable
        int swValue;
        do {
            // Display menu graphics
            System.out.println("===================================");
            System.out.println("|         Manage My Files         |");
            System.out.println("===================================");
            System.out.println("| Options:                        |");
            System.out.println("|        1. Upload File           |");
            System.out.println("|        2. Download File         |");
            System.out.println("|        3. List Existent Files   |"); //list files, Permissions, Remove files
            System.out.println("|        4. Edit File Permissions |");
            System.out.println("|        5. Delete File           |");
            System.out.println("|        6. Exit                  |");
            System.out.println("===================================");
            swValue = Keyin.inInt(" Select option: ");

            Scanner s = new Scanner(System.in);
            switch (swValue) {
                case 1:
                    System.out.println("Please insert the path to the desired file: ");
                    String filePath;
                    filePath = s.next();
                    //TODO: CALL UPLOAD HANDLER WHEN FINISHED
                    break;
                case 2:
                    //TODO DOWNLOAD ON CLIENT SIDE
                    break;
                case 3:
                    System.out.println(username + "owns all these files: ");
                    //TODO PRINT LSIT
                    retrieveFileList(username);
                    break;
                case 4:
                    System.out.println("Please insert the file name you want to edit permissions: ");
                    String fileName;
                    fileName = s.next();
                    String option, usernameToSearch, emailToSearch;
                    option = s.next();
                    do {
                        System.out.println("Want to identify the user by username or email?: ");

                        if (option.equals("username")) {
                            System.out.println("Username: ");
                            usernameToSearch = s.next();
                        } else if (option.equals("email")) {
                            System.out.println("Email: ");
                            emailToSearch = s.next();
                        }
                    } while ((!option.equals("username")) || (!option.equals("email")));
                    //TODO HANDLER TO INSERT user ON UserFIle table
                    break;
                case 5:
                    //Print list
                    //TODO: display filename where user has permission to delete
                    ArrayList<String> files = getPermittedFiles();
                    displayPermittedFiles(files);
                    System.out.println("Which file you want to delete?: ");
                    String fileToDelete;
                    fileToDelete = s.next();
                    deleteFile(files.get(Integer.parseInt(fileToDelete)-1));
                    break;
                case 6:
                    break;
                default:
                    System.out.println("Invalid selection");
                    break; // This break is not really necessary
            }
        } while (swValue != 7);
    }

    private static ArrayList<String> getPermittedFiles() throws Exception {

        HttpURLConnection connection = null;
        OutputStreamWriter out = null;
        BufferedReader in = null;
        JSONObject jsonResponse = null;
        ArrayList<String> permittedFiles = new ArrayList<>();

        try {
            URL listURL = new URL(baseURL, "list");
            connection = (HttpURLConnection) listURL.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("username", username);
            connection.setRequestProperty("permission", "1");

            if (connection.getResponseCode() == 200) {
                //jsonResponse = new JSONObject(connection.getResponseMessage()).getJSONObject("file list");
                jsonResponse = new JSONObject(connection.getResponseMessage().toString());
                System.out.println(jsonResponse);

                for(Iterator iterator = jsonResponse.keys(); iterator.hasNext();) {
                    String key = (String) iterator.next();
                    permittedFiles.add(jsonResponse.get(key).toString());
                }
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
            return permittedFiles;
        }

    }

    private static void displayPermittedFiles(ArrayList<String> filenames) {
        for (int i = 0; i < filenames.size(); i++){
            System.out.println(i+1 + ". " + filenames.get(i));
        }
    }

    private static void userMenu() throws Exception {
        // Local variable
        int swValue;
        do {
            // Display menu graphics
            System.out.println("================================");
            System.out.println("|         User Menu            |");
            System.out.println("================================");
            System.out.println("| Options:                     |");
            System.out.println("|        1. Manage My Files    |");
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


    private static void firstMenu() throws Exception {
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

            // Switch construct
            Scanner s = new Scanner(System.in);
            switch (swValue) {
                case 1:
                    System.out.println("REGISTER");
                    System.out.println("Username: ");
                    username = s.next();
                    System.out.println("Password: ");
                    password = s.next(); //password encoding done in register and login functions;
                    System.out.println("Email: ");
                    email = s.next();
                    register(username, password, email);
                    break;
                case 2:
                    System.out.println("LOGIN");
                    //METHOD TO ASK FOR CREDENTIALS AND TRY TO LOGIN FROM DB

                    System.out.println("Username: ");
                    username = s.next();
                    System.out.println("Password: ");
                    password = s.next();
                    login(username,password);
                    login_checked = true; //TODO Check login return, or server sucess message
                    if (login_checked == true) {
                        userMenu();
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
    }

    public static void main(String[] args) throws Exception {



        baseURL = new URL("http://localhost:9999/");
        //username = "dusty";
        retrieveFileList("luis");
        //upload("test/hue.txt");
        register("asdwr", "cenas", "email@email");
        register("asd", "cenas", "email@email");
        register("dsa", "cenas", "email@email");
        register("duwesty", "cenas", "email@email");
        //firstMenu();
    }

    public static String md5Encode(String pw) throws NoSuchAlgorithmException {
        //code found on:
        //http://www.avajava.com/tutorials/lessons/how-do-i-generate-an-md5-digest-for-a-string.html
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(pw.getBytes());
        byte[] digest = md.digest();
        StringBuffer sb = new StringBuffer();
        for (byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }
        System.out.println(sb);
        return sb.toString();
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
            password = md5Encode(password);
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

    public static void retrieveFileList(String username) throws Exception {
        HttpURLConnection connection = null;
        OutputStreamWriter out = null;
        BufferedReader in = null;
        InputStreamReader inputReader = null;
        String line, result;
        JSONObject jsonResponse = null;
        JSONArray jsonArray = null;

        try {
            URL listURL = new URL(baseURL, "list");
            connection = (HttpURLConnection) listURL.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("username", username);
            connection.setRequestProperty("permission", "0");

            if (connection.getResponseCode() == 200) {
                inputReader = new InputStreamReader(connection.getInputStream());
                in = new BufferedReader(inputReader);
                do {
                    line = in.readLine();
                    result = line;
                } while (line != null);





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

    public static void login(String username, String password) throws Exception {
        HttpURLConnection connection = null;
        OutputStreamWriter out = null;
        BufferedReader in = null;
        password = md5Encode(password);
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
                login_checked = true;
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

    public static void upload(String filePath) throws Exception {
        URL uploadURL = new URL(baseURL, "upload");
        HttpURLConnection connection = null;
        BufferedReader br = null;
        OutputStreamWriter out = null;
        BufferedReader in = null;
        OutputStreamWriter osw = null;
        FileInputStream fis = null;
        SSLSocket sslSocket = null;
        try {
            connection = (HttpURLConnection) uploadURL
                    .openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            JSONObject msg = new JSONObject();
            msg.put("username", username);//TODO change back to username
            msg.put("filename", new File(filePath).getName());
            msg.put("path", filePath);
            msg.put("version", Utils.getFileID(filePath));
            msg = new JSONObject().put("upload", msg);
            out = new OutputStreamWriter(connection.getOutputStream());

            out.write(msg.toString());
            out.close();
            if (connection.getResponseCode() == 200) {
                in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                JSONObject responseMessage = new JSONObject(in.readLine());
                int port = responseMessage.getJSONObject("upload").getInt("port");
                SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                sslSocket = (SSLSocket) sslSocketFactory.createSocket("localhost", port);

                fis = new FileInputStream(filePath);

                /*osw = new OutputStreamWriter(sslSocket.getOutputStream());
                osw.flush();*/
                byte[] buffer = new byte[sslSocket.getSendBufferSize()];
                int i = 0;
                while ((i = fis.read(buffer)) > -1) {
                    sslSocket.getOutputStream().write(buffer, 0, i);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (osw != null) osw.close();
            if (fis != null) fis.close();
            if (sslSocket != null) sslSocket.close();
            if (connection != null) connection.disconnect();
            if (br != null) br.close();
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

    public static void deleteUser() throws Exception {
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

    public static void deleteFile(String file) throws Exception {
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
            msg.put("username", username);
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
