/*
 * Copyright 2016-2017 Ague Mort of Veteran Software
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
    private static final Logger logger = LoggerFactory.getLogger(Tracker.class);
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
