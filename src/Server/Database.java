package Server;/*
* Server.Database.java
* Created by Bernardo on 26-05-2015
* @version 0.1.0
*/

import java.sql.*;
import java.util.ArrayList;

public class Database {
    private final String SHUTDOWN_MESSAGE = "The Server will now shutdown.";
    private Connection conn = null;
    private Statement stmt = null;


    public Database() {
        createTables();

    }

    private void closeConnection() {

        try {
            this.stmt.close();
            this.conn.close();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println(SHUTDOWN_MESSAGE);
            System.exit(-1);
        }
    }

    private void openConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            this.conn = DriverManager.getConnection("jdbc:sqlite:sdis_db.db");

            System.out.println("Opened database successfully");

            this.stmt = this.conn.createStatement();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println(SHUTDOWN_MESSAGE);
            System.exit(-1);
        }
    }

    public void insertUsers(String username, String password, String email) throws SQLException {
        openConnection();
        StringBuilder sql_builder = new StringBuilder();
        String sql_result = "";
        sql_builder.append("INSERT OR IGNORE INTO User (username, password, email) VALUES('");
        sql_builder.append(username);
        sql_builder.append("', '");
        sql_builder.append(password);
        sql_builder.append("', '");
        sql_builder.append(email);
        sql_builder.append("');");
        sql_result = sql_builder.toString();
        System.out.println(sql_result);

        try {
            this.stmt.executeUpdate(sql_result);
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            /*
            System.err.println(SHUTDOWN_MESSAGE);
            System.exit(-1);
            */
        }


        closeConnection();

        System.out.println("Insertion completed!");
    }

    private void createUserTable() {
        String sql_create = "CREATE TABLE IF NOT EXISTS User(username varchar(50) PRIMARY KEY, password varchar(255), email varchar(255) NOT NULL UNIQUE);";

        try {
            this.stmt.executeUpdate(sql_create);
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println(SHUTDOWN_MESSAGE);
            System.exit(-1);
        }
        System.out.println("User table created!");
    }

    public void insertFiles(String filename, String path, String version) {
        openConnection();
        StringBuilder sql_builder = new StringBuilder();
        sql_builder.append("INSERT OR IGNORE INTO File (filename, path, version) VALUES('");
        sql_builder.append(filename);
        sql_builder.append("', '");
        sql_builder.append(path);
        sql_builder.append("', '");
        sql_builder.append(version);
        sql_builder.append("');");
        String sql_result = sql_builder.toString();
        System.out.println(sql_result);

        try {
            this.stmt.executeUpdate(sql_result);
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            /*
            System.err.println(SHUTDOWN_MESSAGE);
            System.exit(-1);
            */
        }


        closeConnection();

        System.out.println("Insertion completed!");
    }

    public int getFileID(String filename) {
        String sql_select;
        sql_select = "SELECT idFile FROM File WHERE filename = '" +
                filename + "'";
        try {
            openConnection();
            ResultSet results = this.stmt.executeQuery(sql_select);
            closeConnection();
            return results.getInt("idFile");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return -1;
    }

    private void createFileTable() {
        String sql_create = "CREATE TABLE IF NOT EXISTS File(idFIle INTEGER PRIMARY KEY  AUTOINCREMENT, filename " +
                "varchar(50) NOT NULL, path varchar(255) NOT NULL, version varchar(255) NOT NULL);";

        try {
            this.stmt.executeUpdate(sql_create);
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println(SHUTDOWN_MESSAGE);
            System.exit(-1);
        }
        System.out.println("File table created!");
    }

    public void insertUserFile(String username, int idFile, int permission) {
        openConnection();
        StringBuilder sql_builder = new StringBuilder();
        sql_builder.append("INSERT INTO UserFile (username, idFile, permission) VALUES('");
        sql_builder.append(username);
        sql_builder.append("', '");
        sql_builder.append(idFile);
        sql_builder.append("', '");
        sql_builder.append(permission);
        sql_builder.append("');");
        String sql_result = sql_builder.toString();
        System.out.println(sql_result);

        try {
            this.stmt.executeUpdate(sql_result);
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
           /*
            System.err.println(SHUTDOWN_MESSAGE);
            System.exit(-1);
            */
        }


        closeConnection();

        System.out.println("Insertion completed!");
    }

    public ArrayList<String> getUserFile(String username) {
        String sql_select;
        sql_select = "SELECT filename FROM File WHERE idFile = (SELECT idFile FROM UserFile WHERE username = '" +
                username + "')";
        try {
            openConnection();
            ResultSet results = this.stmt.executeQuery(sql_select);
            ArrayList<String> fileList = new ArrayList<>();
            while (results.next()) {
                fileList.add(results.getString("fileName"));
            }
            return fileList;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return null;
    }

    private void createUserFileTable() {
        String sql_create = "CREATE TABLE IF NOT EXISTS UserFile(username VARCHAR, idFile INTEGER, permission " +
                "INTEGER NOT NULL, UNIQUE(username,idFile), FOREIGN KEY (username) REFERENCES User(username) ON DELETE CASCADE , FOREIGN KEY (idFile) REFERENCES File(idFile) ON DELETE CASCADE, CHECK (permission >= 0 AND permission <= 1));";
        /*
         * 0 - Editing permissions - User (Read/Write)
         * 1 - Removing permissions - Owner (User + Delete file)
         */
        try {
            this.stmt.executeUpdate(sql_create);
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());

            System.err.println(SHUTDOWN_MESSAGE);
            System.exit(-1);

        }
        System.out.println("UserFile table created!");
    }

    public boolean checkUser(String username) throws SQLException {
        StringBuilder select = new StringBuilder();
        String result = "";
        select.append("SELECT username FROM User WHERE username = '" + username + ";");
        result = select.toString();
        ResultSet rs = this.stmt.executeQuery(result);
        return rs.next();
    }

    public boolean validateLogin(String username, String password) throws SQLException {
        StringBuilder select = new StringBuilder();
        String sql_result = "", result = "";
        select.append("SELECT username,password FROM User WHERE username = '" + username + "' AND password = " + password + ";");
        result = select.toString();
        ResultSet rs = this.stmt.executeQuery(result);
        return rs.next();
    }


    private void createTables() {
        openConnection();
        createUserTable();
        createFileTable();
        createUserFileTable();

        closeConnection();
    }


}
