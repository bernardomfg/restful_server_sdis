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
            this.conn = DriverManager.getConnection("jdbc:sqlite:sdis.db");
            this.stmt = this.conn.createStatement();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println(SHUTDOWN_MESSAGE);
            System.exit(-1);
        }
    }

    public void insertUsers(String username, String password, String email) throws SQLException {
        StringBuilder sql_builder = new StringBuilder();
        String sql_result;
        sql_builder.append("INSERT OR IGNORE INTO User (username, password, email) VALUES('");
        sql_builder.append(username);
        sql_builder.append("', '");
        sql_builder.append(password);
        sql_builder.append("', '");
        sql_builder.append(email);
        sql_builder.append("');");
        sql_result = sql_builder.toString();
        try {
            openConnection();
            this.stmt.executeUpdate(sql_result);
            closeConnection();
            System.out.println(sql_result);
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
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
    }

    public void insertFiles(String filename, String path, String version) {
        StringBuilder sql_builder = new StringBuilder();
        sql_builder.append("INSERT OR IGNORE INTO File (filename, path, version) VALUES('");
        sql_builder.append(filename);
        sql_builder.append("', '");
        sql_builder.append(path);
        sql_builder.append("', '");
        sql_builder.append(version);
        sql_builder.append("');");
        String sql_result = sql_builder.toString();
        try {
            openConnection();
            this.stmt.executeUpdate(sql_result);
            System.out.println(sql_result);
            closeConnection();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

    }

    public int getFileID(String filename) {
        String sql_select;
        sql_select = "SELECT * FROM File WHERE filename = '" +
                filename + "'";
        try {
            openConnection();
            ResultSet results = stmt.executeQuery(sql_select);

            closeConnection();
            System.out.println(results.getObject("idFile").toString());
            return -1;
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
    }

    public void insertUserFile(String username, int idFile, int permission) {

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
            openConnection();
            this.stmt.executeUpdate(sql_result);
            closeConnection();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public ArrayList<String> getUserFile(String username, int permission) {
        String sql_select = null;
        openConnection();
        if (permission == 0)
            sql_select = "SELECT filename FROM File WHERE idFile = (SELECT idFile FROM UserFile WHERE username = '" +
                username + "');";
        else if (permission == 1)
            sql_select = "SELECT filename FROM File WHERE idFile = (SELECT idFile FROM UserFile WHERE username = '" +
                    username + "' AND permission = " + permission + ");";
        try {

            ResultSet results = this.stmt.executeQuery(sql_select);
            System.out.println("finished stmt");
            ArrayList<String> fileList = new ArrayList<>();
            results.next();
            System.out.println(results.getString("filename"));
            /*while (results.next()) {
                fileList.add(results.getString("filename"));
                System.out.println(results.getString("filename"));
            }*/
            closeConnection();
            for (int i = 0; i< fileList.size(); i++){
                System.out.println("Database -> getUserFile " + fileList.get(i));
            }
            return fileList;
        } catch (SQLException e) {
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
    }

    public boolean checkUser(String username) throws SQLException {
        StringBuilder select = new StringBuilder();
        String result;
        boolean existsUser;
        openConnection();
        select.append("SELECT username FROM User WHERE username = '").append(username).append("';");
        result = select.toString();
        ResultSet rs = this.stmt.executeQuery(result);
        existsUser = rs.next();
        closeConnection();
        return existsUser;
    }

    public boolean validateLogin(String username, String password) throws SQLException {
        StringBuilder select = new StringBuilder();
        String result;
        boolean passwordOK;

        openConnection();
        select.append("SELECT username,password FROM User WHERE username = '").append(username).append("' AND password = '").append(password).append("';");
        result = select.toString();
        ResultSet rs = this.stmt.executeQuery(result);
        passwordOK = rs.next();
        closeConnection();
        return passwordOK;
    }
    public void deleteFile(String username, String filename) throws SQLException {
        openConnection();
        StringBuilder select = new StringBuilder();
        ArrayList<Integer> ids = new ArrayList<>();
        String result;
        select.append("SELECT idFile FROM File WHERE filemane = '").append(filename).append("';");
        result = select.toString();
        ResultSet rs = this.stmt.executeQuery(result);
        while(rs.next()){
            ids.add(rs.getInt("idFile"));
        }
        deleteFileByPermissions(username, ids);
        while(!ids.isEmpty()){
            StringBuilder delete = new StringBuilder();
            String str_delete;
            delete.append("DELETE FROM File WHERE username = '").append(username).append("';");
            str_delete = delete.toString();
            stmt.executeQuery(str_delete);
        }
        closeConnection();
    }
    public void deleteFileByPermissions(String username, ArrayList<Integer> list) throws SQLException {
        openConnection();
        StringBuilder select = new StringBuilder();
        String result;
        select.append("SELECT idFile FROM UserFile WHERE username = '").append(username).append("' AND permissions = 1;");
        result = select.toString();
        ResultSet rs = this.stmt.executeQuery(result);
        while(rs.next()){
            for(int i = 0; i < list.size(); i++){
                if(list.get(i).equals(rs.getInt("idFile"))){
                    StringBuilder delete = new StringBuilder();
                    String str_delete;
                    delete.append("DELETE FROM UserFile WHERE idFile = '").append(list.get(i)).append("';");
                    str_delete = delete.toString();
                    stmt.executeQuery(str_delete);
                }
            }
        }
        closeConnection();
    }

    private void createTables() {
        openConnection();
        createUserTable();
        createFileTable();
        createUserFileTable();
        closeConnection();
    }


}
