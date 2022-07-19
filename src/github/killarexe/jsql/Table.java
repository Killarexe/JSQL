package github.killarexe.jsql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class is used to manage to a MySQL table.
 * @author Killarexe
 */
public class Table {

    private String name;
    private Database database;
    private Statement statement;
    private Property[] properties;

    /**
     * Constructor.
     * @param name The name of the table.
     * @param database The database the table is in.
     * @param properties The properties of the table.
     */
    public Table(String name, Database database, Property[] properties){
        this.name = name;
        this.database = database;
        this.properties = properties;
        this.statement = database.getStatement();
    }

    /**
     * Get the name of the table.(This constructor is used to get a table from a database).
     * @return The name of the table.
     */
    public Table(String name, Database database){
        this.name = name;
        this.database = database;
        this.statement = database.getStatement();
        this.properties = getPropertiesByRequest();
    }

    /**
     * Create a table. If is already exist nothing will happen.
     * @return The table
     */
    public int createTable(){
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ");
        sb.append(name);
        sb.append(" (");
        for(int i = 0; i < properties.length; i++){
            sb.append(properties[i].getName());
            sb.append(" ");
            sb.append(properties[i].getType());
            for(int j = 0; j < properties[i].getKeyTypes().length; j++){
                switch (properties[i].getKeyTypes()[j]) {
                    case PRIMARY:
                        sb.append(" PRIMARY KEY");
                        break;
                    case UNIQUE:
                        sb.append(" UNIQUE");
                        break;
                    case AUTO_INCREMENT:
                        sb.append(" AUTO_INCREMENT");
                        break;
                    case NOT_NULL:
                        sb.append(" NOT NULL");
                        break;
                    case INDEX:
                        sb.append(" INDEX");
                        break;
                    default:
                        break;
                }
            }
            if(i != properties.length - 1){
                sb.append(", ");
            }
        }
        sb.append(")");
        try {
            return statement.executeUpdate(sb.toString());
        } catch (SQLException e) {
            System.err.println("Failed to create table " + name + ":\n" + e.getMessage());
        }
        return -1;
    }

    /**
     * Insert a row in the table. If the row is already exist nothing will happen.
     * @param values
     * @return The error code. If the error code is 0, the row was inserted.
     */
    public int insert(Object... values){
        String query = "INSERT INTO " + name + " (";
        for(int i = 0; i < properties.length; i++){
            query += properties[i].getName();
            if(i < properties.length - 1){
                query += ", ";
            }
        }
        query += ") VALUES (";
        for(int i = 0; i < values.length; i++){
            query += "'" + values[i] + "'";
            if(i < values.length - 1){
                query += ", ";
            }
        }
        query += ")";
        try {
            return statement.executeUpdate(query);
        } catch (SQLException e) {
            System.err.println("Failed to insert into table " + name + ":\n" + e.getMessage());
        }
        return -1;
    }

    /**
     * Change a row in the table.
     * @return The error code. If the error code is 0, the row was changed.
     */
    public int update(Object[] values, String condition){
        String query = "UPDATE " + name + " SET ";
        for(int i = 0; i < properties.length; i++){
            query += properties[i].getName() + " = '" + values[i] + "'";
            if(i < properties.length - 1){
                query += ", ";
            }
        }
        query += " WHERE " + condition;
        try {
            return statement.executeUpdate(query);
        } catch (SQLException e) {
            System.err.println("Failed to update table " + name + ":\n" + e.getMessage());
        }
        return -1;
    }

    /**
     * Delete a row from a table.
     * @return The error code.
     */
    public int delete(String condition){
        String query = "DELETE FROM " + name + " WHERE " + condition;
        try {
            return statement.executeUpdate(query);
        } catch (SQLException e) {
            System.err.println("Failed to delete from table " + name + " with condition " + condition + ":\n" + e.getMessage());
        }
        return -1;
    }

    /**
     * Drop the table. If it doesn't exist an error will be printed.
     * @return The error code.
     */
    public int drop(){
        String query = "DROP TABLE " + name;
        try {
            return statement.executeUpdate(query);
        } catch (SQLException e) {
            System.err.println("Failed to drop table " + name + ":\n" + e.getMessage());
        }
        return -1;
    }

    public Object[] select(String[] columns, String where){
        String query = "SELECT ";
        for(int i = 0; i < columns.length; i++){
            query += columns[i];
            if(i < columns.length - 1){
                query += ", ";
            }
        }
        query += " FROM " + name;
        if(where != null){
            query += " WHERE " + where;
        }
        try {
            ResultSet resultSet = statement.executeQuery(query);
            resultSet.next();
            Object[] values = new Object[columns.length];
            for(int i = 0; i < columns.length; i++){
                values[i] = resultSet.getObject(columns[i]);
            }
            return values;
        } catch (SQLException e) {
            System.err.println("Failed to select from table " + name + ":\n" + e.getMessage());
        }
        return null;
    }

    public int getTableSize(){
        String query = "SELECT COUNT(*) FROM " + name;
        try {
            ResultSet resultSet = statement.executeQuery(query);
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            System.err.println("Failed to get table size of table " + name + ":\n" + e.getMessage());
        }
        return -1;
    }

    public Property[] getPropertiesByRequest(){
        if(exist() && properties != null){
            return properties;
        }
        String query = "SELECT * FROM " + name;
        try {
            ResultSet resultSet = statement.executeQuery(query);
            resultSet.next();
            Property[] properties = new Property[resultSet.getMetaData().getColumnCount()];
            for(int i = 0; i < properties.length; i++){
                properties[i] = new Property(resultSet.getMetaData().getColumnName(i + 1), resultSet.getMetaData().getColumnTypeName(i + 1));
            }
            return properties;
        } catch (SQLException e) {
            System.err.println("Failed to get properties of table " + name + ":\n" + e.getMessage());
        }
        return null;
    }

    public boolean exist(){
        String query = "SELECT * FROM " + name;
        try {
            ResultSet resultSet = statement.executeQuery(query);
            return resultSet.next();
        } catch (SQLException e) {
            return false;
        }
    }

    public String getName() {
        return name;
    }

    public Database getDatabase() {
        return database;
    }

    public Property[] getProperties() {
        return properties;
    }

    public static class Property{
        private String name;
        private String type;
        private KeyType[] keyTypes;

        public Property(String name, String type, KeyType... keyTypes){
            this.name = name;
            this.type = type;
            this.keyTypes = keyTypes;
        }

        public Property(String name, String type){
            this(name, type, KeyType.NONE);
        }
        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public KeyType[] getKeyTypes() {
            return keyTypes;
        }

        public enum KeyType{
            AUTO_INCREMENT,
            NOT_NULL,
            PRIMARY,
            UNIQUE,
            INDEX,
            NONE
        }
    }
}