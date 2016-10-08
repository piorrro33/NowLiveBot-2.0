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
    public Tracker(String command) throws PropertyVetoException, SQLException, IOException {
        logger.info("Updating the command count in the database");
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
                logger.info("Command " + command + " updated");
                return;
            }
            Database.getInstance();
            Database.cleanUp(result, statement, connection);
        } catch (IOException | SQLException | PropertyVetoException e) {
            e.printStackTrace();
        }
    }
}
