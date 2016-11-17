package util.database.calls;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.PropReader;
import util.database.Database;

import java.beans.PropertyVetoException;
import java.io.*;
import java.sql.*;

/**
 * @author Veteran Software by Ague Mort
 */
public class SchemaCheck extends Database {

    private static PropReader prop = PropReader.getInstance();
    private static final String MYSQL_SCHEMA = prop.getProp().getProperty("mysql.schema");
    private static Logger logger = LoggerFactory.getLogger(SchemaCheck.class);

    private static Connection connection;
    private static PreparedStatement pStatement;
    private static ResultSet resultSet;

    public SchemaCheck() throws PropertyVetoException, IOException, SQLException {
        super();
    }

    public static void checkDb() {
        logger.info("Checking to see if the schema is present.");


        try {
            String query = "SELECT `SCHEMA_NAME` FROM `information_schema`.`SCHEMATA` WHERE `SCHEMA_NAME` = ?";
            connection = Database.getInstance().getConnection();
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, MYSQL_SCHEMA);
            resultSet = pStatement.executeQuery();

            Boolean check = resultSet.last(); // Check to see if there's at least one row

            if (check) {
                logger.info("MySQL schema exists.");
            } else {
                logger.info("MySQL schema does not exist.");
                // Load the raw SQL schema file in to the database
                uploadDatabase();
            }
        } catch (SQLSyntaxErrorException e) {
            logger.error("There is an error in the SQL syntax.", e);
        } catch (SQLException e) {
            logger.error("SQLException error.  No clue what.", e);
        } finally {
            cleanUp(resultSet, pStatement, connection);
        }
    }

    private static void uploadDatabase() throws SQLException {

        String line;
        StringBuilder buffer = new StringBuilder();

        try {
            FileReader readerF = new FileReader(new File("./nowlive_schema.sql"));
            BufferedReader readerB = new BufferedReader(readerF);

            while ((line = readerB.readLine()) != null) {
                buffer.append(line);
            }
            readerB.close();
        } catch (FileNotFoundException e) {
            logger.error("MySQL Schema file was not found.", e);
        } catch (IOException e) {
            logger.error("There was a problem reading the MySQL Schema from file.", e);
        }

        String[] inst = buffer.toString().split(";");

        try {
            connection = Database.getInstance().getConnection();
            for (String anInst : inst) {
                if (!anInst.trim().equals("")) {
                    pStatement = connection.prepareStatement(anInst);
                    pStatement.executeUpdate(anInst);
                    logger.info("MySQL Query executed successfully:  " + anInst);
                }
            }
            String query = "USE `" + MYSQL_SCHEMA + "`";
            pStatement = connection.prepareStatement(query);

            if (pStatement.execute("USE `" + MYSQL_SCHEMA + "`")) {
                logger.info("Now using schema: " + MYSQL_SCHEMA);
            }
        } catch (SQLException e) {
            logger.error("SQLException error.  No clue what.", e);
        } finally {
            cleanUp(pStatement, connection);
        }
    }

}
