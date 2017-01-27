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

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import util.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class CheckPerms {

    private Connection connection;
    private PreparedStatement pStatement;
    private ResultSet result;

    public final boolean checkManager(GuildMessageReceivedEvent event) {
        // Check if the called command requires a manager

        try {
            String query = "SELECT `userId` FROM `manager` WHERE `guildId` = ?";

            if (connection == null || connection.isClosed()) {
                connection = Database.getInstance().getConnection();
            }
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, event.getGuild().getId());
            result = pStatement.executeQuery();

            // Iterate through the result set looking to see if the author is a manager
            String userId = event.getAuthor().getId();
            while (result.next()) {
                if (userId.equals(result.getString("userId"))) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(result, pStatement, connection);
        }
        return false;
    }

    public final boolean checkAdmins(GuildMessageReceivedEvent event) {

        try {
            String query = "SELECT `userId` FROM `admins`";

            if (connection == null || connection.isClosed()) {
                connection = Database.getInstance().getConnection();
            }
            pStatement = connection.prepareStatement(query);
            result = pStatement.executeQuery();

            // Iterate through the result set looking to see if the author is a bot admin
            String userId = event.getAuthor().getId();
            while (result.next()) {
                if (userId.equals(result.getString("userId"))) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(result, pStatement, connection);
        }
        return false;
    }

}

