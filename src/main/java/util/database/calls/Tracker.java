package util.database.calls;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public final class Tracker {
    public static final Logger logger = LoggerFactory.getLogger(Tracker.class);
    private static Connection connection;
    private static PreparedStatement pStatement;

    public Tracker(String command) {
        super();
        doStuff(command);
    }

    private static void doStuff(String command) {
        try {
            String query = "INSERT INTO `commandtracker` (`commandName`, `commandCount`) VALUES (?, 1) " +
                    "ON DUPLICATE KEY UPDATE `commandCount` = `commandCount` + 1";
            connection = Database.getInstance().getConnection();
            pStatement = connection.prepareStatement(query);

            pStatement.setString(1, command);
            pStatement.executeUpdate();
        } catch (SQLException e) {
            logger.warn("There was a problem updating the count for commands in my database.");
        } finally {
            cleanUp(pStatement, connection);
        }
    }
}
