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

    public void closeConnection(){

        try {
            this.stmt.close();
            this.conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void openConnection() {
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

        System.out.println("Insert completed!");
    }

    public void createUserTable(){
        String sql_create = "CREATE TABLE IF NOT EXISTS User(idUser INTEGER PRIMARY KEY  AUTOINCREMENT, username varchar(50) NOT NULL, email varchar(255) NOT NULL, password varchar(255))";

        try {
            this.stmt.executeUpdate(sql_create);
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        System.out.println("User table created!");
    }

    public void createTables(){
        openConnection();
        createUserTable();
        //TODO: create more tables

        closeConnection();
    }


}
