package util.database.calls;

import util.database.Database;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Veteran Software by Ague Mort
 */
public class Tracker extends Database {
    public Tracker(String command) throws PropertyVetoException, IOException, SQLException {
        super();

        Connection connection;
        Statement statement;
        Integer result;
        try {
            connection = Database.getInstance().getConnection();
            statement = connection.createStatement();
            String query = "INSERT INTO `commandtracker` (`commandName`, `commandCount`) VALUES ('" + command + "', " +
                    "1) ON DUPLICATE KEY UPDATE `commandCount` = `commandCount` + 1";
            result = statement.executeUpdate(query);
            if (result > 0) {
                logger.info("Command " + command + " was used and incremented in the database.");
                return;
            }
            Database.getInstance();
            Database.cleanUp(result, statement, connection);
        } catch (IOException | SQLException | PropertyVetoException e) {
            logger.warn("There was a problem updating the count for commands in my database.");
        }
    }
}
