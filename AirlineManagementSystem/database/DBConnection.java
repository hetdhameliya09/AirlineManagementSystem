package database;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection getConnection() {
        Connection con = null;
        try {
            String dburl = "jdbc:mysql://localhost:3306/airline?useSSL=false&allowPublicKeyRetrieval=true";
            String dbuser = "root";
            String dbpass = "";
            String driver = "com.mysql.cj.jdbc.Driver";

            Class.forName(driver);
            con = DriverManager.getConnection(dburl, dbuser, dbpass);
        } catch (Exception e) {
            System.err.println("Database Connection Error: " + e.getMessage());
        }
        return con;
    }
}
