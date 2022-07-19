package github.killarexe.jsql.exemples;

import github.killarexe.jsql.Database;
import github.killarexe.jsql.JSQL;
import github.killarexe.jsql.Table;

public class Exemples {

    //A JSQL Exemple for creating a table in a database and inserting data in it. Then getting the data from the table.
    private void connectAndCreateTable(){
        JSQL jsql = new JSQL("localhost", "root", "root");
        Database database = jsql.connectToDataBase("user_database");
        Table table = new Table("users", database, new Table.Property[]{
                new Table.Property("id", "int", Table.Property.KeyType.PRIMARY, Table.Property.KeyType.NOT_NULL),
                new Table.Property("name", "varchar(255)", Table.Property.KeyType.NOT_NULL),
                new Table.Property("age", "int"),
                new Table.Property("email", "varchar(255)", Table.Property.KeyType.NOT_NULL),
                new Table.Property("password", "varchar(255)", Table.Property.KeyType.NOT_NULL),
        });
        table.createTable(); //If the table already exists, nothing happens.
        table.insert(new String[]{"1", "John", "25", "john@gmail.com", "password"}); //Add a user and if the user already exists, nothing happens.
        System.out.println("Username of john@gmail.com: " + table.select(new String[]{"name"}, "email = 'john@gmail.com'")); //Get the username of a user by the email.
        jsql.close(); //Close the connection.
    }
}
