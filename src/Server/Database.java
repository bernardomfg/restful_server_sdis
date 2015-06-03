package Server;/*
* Server.Database.java
* Created by Bernardo on 26-05-2015
* @version 0.1.0
*/

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private Connection conn = null;
    private Statement stmt = null;


    public Database(){
        createTables();

    }

    private void closeConnection(){

        try {
            this.stmt.close();
            this.conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
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
        }
    }

    public void insertUsers(String username, String password, String email){
        openConnection();
        StringBuilder sql_builder = new StringBuilder();
        sql_builder.append("INSERT INTO User (username, password, email) VALUES('");
        sql_builder.append(username);
        sql_builder.append("', '");
        sql_builder.append(password);
        sql_builder.append("', '");
        sql_builder.append(email);
        sql_builder.append("');");
        String sql_result = sql_builder.toString();
        System.out.println(sql_result);

        try {
            this.stmt.executeUpdate(sql_result);
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }


        closeConnection();

        System.out.println("Insertion completed!");
    }

    private void createUserTable(){
        String sql_create = "CREATE TABLE IF NOT EXISTS User(username varchar(50) PRIMARY KEY ON DELETE CASCADE, email varchar(255) NOT NULL, password varchar(255))";

        try {
            this.stmt.executeUpdate(sql_create);
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        System.out.println("User table created!");
    }

    public void insertFiles(String filename, String path, int version){
        openConnection();
        StringBuilder sql_builder = new StringBuilder();
        sql_builder.append("INSERT INTO File (filename, path, version) VALUES('");
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
        }


        closeConnection();

        System.out.println("Insertion completed!");
    }

    private void createFileTable(){
        String sql_create = "CREATE TABLE IF NOT EXISTS File(idFIle INTEGER PRIMARY KEY  AUTOINCREMENT ON DELETE CASCADE, filename varchar(50) NOT NULL, path varchar(255) NOT NULL, version INTEGER NOT NULL);";

        try {
            this.stmt.executeUpdate(sql_create);
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        System.out.println("User table created!");
    }

    public void insertUserFile(String username, int idFile, int permission){
        openConnection();
        StringBuilder sql_builder = new StringBuilder();
        sql_builder.append("INSERT INTO UserFile (idUser, idFile, permission) VALUES('");
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
        }


        closeConnection();

        System.out.println("Insertion completed!");
    }

    private void createUserFileTable(){
        String sql_create = "CREATE TABLE IF NOT EXISTS File(username VARCHAR, idFile INTEGER, permissions INTEGER NOT NULL, UNIQUE(username,idFile), FOREIGN KEY (username) REFERENCES User(username), FOREIGN KEY (idFile) REFERENCES File(idFile), CHECK (permission >= 0 && permission <= 1)));";
        /*
         * 0 - Editing permissions - User (Read/Write)
         * 1 - Removing permissions - Owner (User + Delete file)
         */
        try {
            this.stmt.executeUpdate(sql_create);
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        System.out.println("User table created!");
    }


    private void createTables(){
        openConnection();
        createUserTable();
        createFileTable();
        createUserFileTable();
        //TODO: create more tables

        closeConnection();
    }


}
