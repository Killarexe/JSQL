package github.killarexe.jsql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class is used to connect to a MySQL database.
 * @author Killarexe
 */
public class Database {

    private String name;
    private Statement statement;

    /**
     * Constructor.
     * @param name The name of the database.
     * @param statement The statement used to connect to the database.
     */
    public Database(String name, Statement statement) {
        this.name = name;
        this.statement = statement;
    }

    /**
     *  Execute a query (DELETE, CREATE, UPDATE can execute in executeUpdate method).
     *  @param query The query to execute.
     */
    public ResultSet executeQuery(String query) {
        try {
            return statement.executeQuery(query);
        } catch (SQLException e) {
            System.err.println("Failed to execute query: " + query + ":\n" + e.getMessage());
        }
        return null;
    }

    /**
     * Execute a query (SELECT can execute in executeQuery method).
     * @param query The query to execute.
     */
    public int executeUpdate(String query) {
        try {
            return statement.executeUpdate(query);
        } catch (SQLException e) {
            System.err.println("Failed to execute query: " + query + ":\n" + e.getMessage());
        }
        return -1;
    }

    /**
     * Get a table (Only if the table all ready exist not recommended to use it).
     * @param name The name of the table.
     * @return The table.
     */
    public Table getTable(String name) {
        return new Table(name, this);
    }

    /**
     * Delete a table.
     * @param table The table to delete. If the table doesn't exist, nothing will happen.
     * @return The error code. If the error code is 0, the table was deleted.
     */
    public int dropTable(Table table){
        return table.drop();
    }

    public Statement getStatement() {
        return statement;
    }

    public String getName() {
        return name;
    }
}
