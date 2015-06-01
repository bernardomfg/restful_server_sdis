/*
* HTTPFileUploadServer.java
* Author: Bernardo Gonçalves
* @version 1.00
*/

// A File upload server which will handle files of any type and size
// (Text as well as binary files including images)

import javax.xml.crypto.Data;
import java.io.*;
import java.net.*;
import java.util.*;


public class HTTPFileUploadServer extends Thread {

    static final String HTML_START =
            "<html>" +
                    "<title>HTTP POST Server in java</title>" +
                    "<body>";

    static final String HTML_END =
            "</body>" +
                    "</html>";

    Socket connectedClient = null;
    DataInputStream inFromClient = null;
    DataOutputStream outToClient = null;


    public HTTPFileUploadServer(Socket client) {
        connectedClient = client;
    }

    void closeStreams() throws Exception {
        inFromClient.close();
        outToClient.close();
        connectedClient.close();
    }

    // A routine to find the POST request end string from the
    // Inputstream
    int sub_array(byte [] array1, byte [] array2) throws Exception {

        int i = array1.length - 1;
        int j = array2.length - 1;
        boolean found = false;

        for (int k = i; k >=0; k--) {
            if (array1[k] == array2[j]) {
                found = true;
                for (int l = j - 1; l >=0; l--) {
                    k = k - 1;
                    if (k < 0) return -1;
                    if (array1[k] == array2[l]) continue;
                    else {found = false; break;}
                }
                if (found == true) return k;
            }
        }
        return -1;
    }

    // Read from InputStream
    public String readLine() throws Exception {
        String line = "";

        char c = (char) inFromClient.read();

        while (c != '\n'){
            line = line + Character.toString(c);
            c = (char) (inFromClient.read());
        }
        return line.substring(0,line.lastIndexOf('\r'));
    }

    //Thread for processing individual clients
    public void run() {

        String currentLine = null, postBoundary = null,
                filename = null, contentLength = null;
        FileOutputStream fout = null;

        // Change these two parameters depending on the size of the file to be uploaded
        // For a very large file > 200 MB, increase THREAD_SLEEP_TIME to prevent connection
        // resets during upload, current settings are good tor handling upto 100 MB file size
        long THREAD_SLEEP_TIME = 20;
        int BUFFER_SIZE = 65535;

        // Upload File size limit = 25MB, can be increased
        long FILE_SIZE_LIMIT = 50000000;

        try {

            System.out.println( "The Client "+
                    connectedClient.getInetAddress() + ":" + connectedClient.getPort() + " is connected");

            inFromClient = new DataInputStream(connectedClient.getInputStream());
            outToClient = new DataOutputStream(connectedClient.getOutputStream());

            connectedClient.setReceiveBufferSize(BUFFER_SIZE);

            currentLine = readLine();
            String headerLine = currentLine;
            StringTokenizer tokenizer = new StringTokenizer(headerLine);
            String httpMethod = tokenizer.nextToken();
            String httpQueryString = tokenizer.nextToken();

            System.out.println(currentLine);

            if (httpMethod.equals("GET")) { // GET Request
                System.out.println("GET request");
                if (httpQueryString.equals("/")) {
                    // The default home page
                    String responseString = HTTPFileUploadServer.HTML_START +
                            "<form action=\"http://127.0.0.1:5000\" enctype=\"multipart/form-data\"" +
                            "method=\"post\">" +
                            "Enter the name of the File <input name=\"file\" type=\"file\"><br>" +
                            "<input value=\"Upload\" type=\"submit\"></form>" +
                            "Upload only text files." +
                            HTTPFileUploadServer.HTML_END;


                    sendResponse(200, responseString , false);
                } else {
                    sendResponse(404, "<b>The Requested resource not found ...." +
                            "Usage: http://127.0.0.1:5000</b>", false);
                }

            } //if
            else if(httpMethod.equals("POST")) { //POST Request

                System.out.println("POST request");

                if (httpQueryString.equals("/register")){
                    while(true) {
                        currentLine = readLine();
                        System.out.println(currentLine);
                        if (currentLine.equals("")) {
                            break;
                        }
                    }


                }

                else if (httpQueryString.equals("/")) {
                    boolean fileupload = false;

                    while (true) {
                        currentLine = readLine();
                        if (currentLine.indexOf("Content-Length:") != -1) {
                            contentLength = currentLine.split(" ")[1];
                            System.out.println("Content Length = " + contentLength);
                        }

                        if (currentLine.indexOf("Content-Type: multipart/form-data") != -1) {
                            postBoundary = currentLine.split("boundary=")[1];
                            fileupload = true;
                            System.out.println("fileupload = true");
                        } //if

                        if (currentLine.equals("")) {
                            break;
                        }

                    }//while (true); //End of do-while
                    if (fileupload) {

                        // The POST boundary
                        // Content length should be <= 25MB
                        if (Long.valueOf(contentLength) < FILE_SIZE_LIMIT) {

                            while (true) {
                                currentLine = readLine();
                                System.out.println(currentLine);
                                if (currentLine.indexOf("--" + postBoundary) != -1) {
                                    filename = readLine().split("filename=")[1].replaceAll("\"", "");
                                    String[] filelist = filename.split("\\" + System.getProperty("file.separator"));
                                    filename = filelist[filelist.length - 1];
                                    filename = filename.trim();
                                    break;
                                }
                            }

                            if (filename.length() != 0) {

                                String fileContentType = null;

                                try {
                                    fileContentType = readLine().split(" ")[1];
                                } catch (Exception e) {
                                    System.out.println("Can't determine POST request length");
                                }

                                System.out.println("File content type = " + fileContentType);
                                sendResponse(200, fileContentType, false);

                                readLine(); //assert(readLine(inFromClient).equals("")) : "Expected line in POST request is "" ";
                                fout = new FileOutputStream(filename);

                                byte[] buffer = new byte[BUFFER_SIZE], endarray;
                                String end_flag = "--" + postBoundary + "--";

                                endarray = end_flag.getBytes();
                            }
                        }
                    }
                }
            }//else
            //Close all streams
            System.out.println("Closing All Streams....");
            closeStreams();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Done....");
    }

    public void sendResponse(int statusCode, String responseString, boolean isFile) throws Exception {

        String statusLine = null;
        String serverdetails = "Server: Java HTTPServer";
        String contentLengthLine = null;
        String fileName = null;
        String contentTypeLine = "Content-Type: text/html" + "\r\n";
        FileInputStream fin = null;

        if (statusCode == 200)
            statusLine = "HTTP/1.1 200 OK" + "\r\n";
        else
            statusLine = "HTTP/1.1 404 Not Found" + "\r\n";

        if (isFile) {
            fileName = responseString;
            fin = new FileInputStream(fileName);
            contentLengthLine = "Content-Length: " + Integer.toString(fin.available()) + "\r\n";
            if (!fileName.endsWith(".htm") && !fileName.endsWith(".html"))
                contentTypeLine = "Content-Type: \r\n";
        }
        else {
            responseString = HTTPFileUploadServer.HTML_START + responseString + HTTPFileUploadServer.HTML_END;
            contentLengthLine = "Content-Length: " + responseString.length() + "\r\n";
        }


        outToClient.writeBytes(statusLine);
        outToClient.writeBytes(serverdetails);
        outToClient.writeBytes(contentTypeLine);
        outToClient.writeBytes(contentLengthLine);
        outToClient.writeBytes("Connection: close\r\n");
        outToClient.writeBytes("\r\n");

        if (isFile) sendFile(fin);
        else outToClient.writeBytes(responseString);
    }

    //Send the requested file
    public void sendFile(FileInputStream fin) throws Exception {
        byte[] buffer = new byte[1024] ;
        int bytesRead;

        while ((bytesRead = fin.read(buffer)) != -1 ) {
            outToClient.write(buffer, 0, bytesRead);
        }
        fin.close();
    }
}