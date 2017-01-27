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

package core.commands;

import core.Command;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Const;
import util.database.Database;
import util.database.calls.Tracker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static platform.discord.controller.DiscordController.sendToChannel;
import static platform.discord.controller.DiscordController.sendToPm;
import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class Streams implements Command {

    private static Logger logger = LoggerFactory.getLogger(Streams.class);
    private Connection connection;
    private PreparedStatement pStatement;
    private ResultSet result;
    private Integer rowCount = -1;

    @Override
    public final boolean called(String args, GuildMessageReceivedEvent event) {
        // If the help argument is the only argument that is passed
        return !(args != null && !args.isEmpty()) || "help".equals(args);
    }

    @Override
    public final void action(String args, GuildMessageReceivedEvent event) {
        try {
            String query = "SELECT COUNT(*) AS `rowCount` FROM `stream` WHERE `guildId` = ?";

            if (connection == null || connection.isClosed()) {
                this.connection = Database.getInstance().getConnection();
            }

            this.pStatement = connection.prepareStatement(query);

            pStatement.setString(1, event.getGuild().getId());
            this.result = pStatement.executeQuery();

            while (result.next()) {
                rowCount = result.getInt("rowCount");
            }
        } catch (SQLException e) {
            logger.error("There was a problem fetching live streams for an on demand request.", e);
        } finally {
            cleanUp(result, pStatement, connection);
        }
        try {
            // Grab the actual results to iterate through
            String query = "SELECT `platform`.`baseLink` AS `link`, `stream`.`channelName` AS `channel`, `platform`.`name` " +
                    "AS `platform`, `stream`.`gameName` AS `game` " +
                    "FROM `stream` " +
                    "INNER JOIN `platform` " +
                    "ON `stream`.`platformId` = `platform`.`id` " +
                    "WHERE `stream`.`guildId` = ? ORDER BY `stream`.`channelName`";

            if (connection == null || connection.isClosed()) {
                this.connection = Database.getInstance().getConnection();
            }
            this.pStatement = connection.prepareStatement(query);

            pStatement.setString(1, event.getGuild().getId());
            this.result = pStatement.executeQuery();

            if (rowCount < 1) { // If no streams are online
                MessageBuilder noneOnline = new MessageBuilder();
                noneOnline.append(Const.NONE_ONLINE);
                sendToPm(event, noneOnline.build());
            } else { // If there's at least one stream online
                MessageBuilder message = new MessageBuilder();
                message.append(Const.ONLINE_STREAM_PM_1);
                message.append(String.valueOf(rowCount));
                message.append(Const.ONLINE_STREAM_PM_2);
                while (result.next()) {
                    message.append("**" + result.getString("channel") + "**"); // Channel Name
                    message.append(Const.NOW_PLAYING_LOWER); // " is now playing"
                    message.append("**" + result.getString("game") + "**"); // name of the game
                    message.append(Const.ON); // " on "
                    message.append("**" + result.getString("platform") + "**!\n\t");
                    message.append(Const.WATCH_THEM_HERE);
                    message.append("__*" + result.getString("link") + result.getString("channel") + "*__\n\n");
                    if (message.length() >= 1800) {
                        sendToPm(event, message.build());
                        message = new MessageBuilder();
                    }
                }
                // TODO: Add DB value to offer preference to user to send pm vs send to channel
                sendToPm(event, message.build());
            }

        } catch (SQLException e) {
            logger.error("There was a problem fetching live streams for an on demand request.", e);
        } finally {
            cleanUp(result, pStatement, connection);
        }
    }

    @Override
    public final void help(GuildMessageReceivedEvent event) {
        sendToChannel(event, Const.STREAMS_HELP);
    }

    @Override
    public final void executed(boolean success, GuildMessageReceivedEvent event) {
        new Tracker("Streams");
    }
}
