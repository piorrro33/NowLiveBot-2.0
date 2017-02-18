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

import util.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static util.database.Database.cleanUp;

/**
 * Created by Ague Mort of Veteran Software on 2/17/2017.
 */
public class V5TableMigration {
    private Connection connection;
    private PreparedStatement pStatement;
    private ResultSet result;

    public synchronized void execute() {
        channels();
        filters();
        games();
        tags();
        teams();
    }

    private synchronized void channels() {
        String query = "SELECT * FROM `channel` WHERE `platformId` = 1 ORDER BY `timeAdded` ASC";

        try {
            if (connection == null || connection.isClosed()) {
                this.connection = Database.getInstance().getConnection();
            }
            this.pStatement = connection.prepareStatement(query);
            this.result = pStatement.executeQuery();

            query = "INSERT INTO `twitch` (`guildId`, `channelName`, `channelId`, `timeAdded`) VALUES (?,?,?,?)";

            this.pStatement = connection.prepareStatement(query);

            while (result.next()) {
                pStatement.setString(1, result.getString("guildId"));
                pStatement.setString(2, result.getString("channelName"));
                pStatement.setString(3, result.getString("channelId"));
                pStatement.setTimestamp(4, result.getTimestamp("timeAdded"));
                pStatement.addBatch();
            }
            pStatement.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(result, pStatement, connection);
        }
    }

    private synchronized void filters() {
        String query = "SELECT * FROM `filter`";

        try {
            if (connection == null || connection.isClosed()) {
                this.connection = Database.getInstance().getConnection();
            }
            this.pStatement = connection.prepareStatement(query);
            this.result = pStatement.executeQuery();

            query = "INSERT INTO `twitch` (`guildId`, `gameFilter`) VALUES (?,?)";

            this.pStatement = connection.prepareStatement(query);

            while (result.next()) {
                pStatement.setString(1, result.getString("guildId"));
                pStatement.setString(2, result.getString("name"));
                pStatement.addBatch();
            }
            pStatement.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(result, pStatement, connection);
        }
    }

    private synchronized void games() {
        String query = "SELECT * FROM `game`";

        try {
            if (connection == null || connection.isClosed()) {
                this.connection = Database.getInstance().getConnection();
            }
            this.pStatement = connection.prepareStatement(query);
            this.result = pStatement.executeQuery();

            query = "INSERT INTO `twitch` (`guildId`, `gameName`) VALUES (?,?)";

            this.pStatement = connection.prepareStatement(query);

            while (result.next()) {
                pStatement.setString(1, result.getString("guildId"));
                pStatement.setString(2, result.getString("name"));
                pStatement.addBatch();
            }
            pStatement.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(result, pStatement, connection);
        }
    }

    private synchronized void tags() {
        String query = "SELECT * FROM `tag`";

        try {
            if (connection == null || connection.isClosed()) {
                this.connection = Database.getInstance().getConnection();
            }
            this.pStatement = connection.prepareStatement(query);
            this.result = pStatement.executeQuery();

            query = "INSERT INTO `twitch` (`guildId`, `titleFilter`) VALUES (?,?)";

            this.pStatement = connection.prepareStatement(query);

            while (result.next()) {
                pStatement.setString(1, result.getString("guildId"));
                pStatement.setString(2, result.getString("name"));
                pStatement.addBatch();
            }
            pStatement.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(result, pStatement, connection);
        }
    }

    private synchronized void teams() {
        String query = "SELECT * FROM `team`";

        try {
            if (connection == null || connection.isClosed()) {
                this.connection = Database.getInstance().getConnection();
            }
            this.pStatement = connection.prepareStatement(query);
            this.result = pStatement.executeQuery();

            query = "INSERT INTO `twitch` (`guildId`, `teamName`) VALUES (?,?)";

            this.pStatement = connection.prepareStatement(query);

            while (result.next()) {
                pStatement.setString(1, result.getString("guildId"));
                pStatement.setString(2, result.getString("name"));
                pStatement.addBatch();
            }
            pStatement.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(result, pStatement, connection);
        }
    }
}
