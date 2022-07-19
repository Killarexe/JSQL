package github.killarexe.jsql;

import java.sql.*;

/**
 * JSQL is a Java library to interact with a MySQL database.
 * @author Killarexe
 */
public class JSQL {

    private String host, user, password;
    private Connection connection;
    private Statement statement;

    /**
     * Constructor.
     * @param host The host of the MySQL database.
     * @param user The user of the MySQL database.
     * @param password The password of the MySQL database.
     */
    public JSQL(String host, String user, String password) {
        this.host = host;
        this.user = user;
        this.password = password;
    }

    /**
     * Connect to the MySQL database.
     * @param name The name of the database.
     * @return The database.
     */
    public Database connectToDataBase(String name) {
        System.out.println("Connecting to database " + name + "...");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":3306/" + name, user, password);
            statement = connection.createStatement();
            System.out.println("Connected to database " + name + ".");
        } catch (ClassNotFoundException e) {
            System.err.println("JBDC driver not found. Please install it.");
            System.exit(1);
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database " + name + ":\n" + e.getMessage());
        }
        return new Database(name, statement);
    }

    /**
     * Close the connection to the MySQL database.
     */
    public void close(){
        System.out.println("Closing connection...");
        try {
            connection.close();
            statement.close();
            System.out.println("Connection closed.");
        } catch (SQLException e) {
            System.err.println("Failed to close the connection. Be sure to be connected to the database! :\n" + e.getMessage());
            System.exit(1);
        }
    }

    public String getPassword() {
        return password;
    }

    public String getHost() {
        return host;
    }

    public String getUser() {
        return user;
    }
}
