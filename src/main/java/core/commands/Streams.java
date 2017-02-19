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
import langs.LocaleString;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
            String query = "SELECT COUNT(streamsId) AS `rowCount` FROM `twitchstreams` WHERE `guildId` = ?";

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
        if (rowCount.equals(0)) {
            MessageBuilder noneOnline = new MessageBuilder();
            noneOnline.append(LocaleString.getString(event.getMessage().getGuild().getId(), "noneOnline"));
            sendToPm(event, noneOnline.build());
        } else {
            try {
                // Grab the actual results to iterate through
                String query = "SELECT `channelName`, `streamsGame`, `channelUrl` " +
                        "FROM `twitchstreams` " +
                        "WHERE `guildId` = ? " +
                        "AND `messageId` IS NOT NULL " +
                        "ORDER BY `channelName` ASC";

                if (connection == null || connection.isClosed()) {
                    this.connection = Database.getInstance().getConnection();
                }
                this.pStatement = connection.prepareStatement(query);

                pStatement.setString(1, event.getGuild().getId());
                this.result = pStatement.executeQuery();

                MessageBuilder message = new MessageBuilder();
                message.append(LocaleString.getString(event.getMessage().getGuild().getId(), "onlineStreamPm1"));
                message.append(String.valueOf(rowCount));
                message.append(LocaleString.getString(event.getMessage().getGuild().getId(), "onlineStreamPm2"));
                message.append("\n___Twitch Streams__\n");
                while (result.next()) {
                    message.append("**");
                    message.append(result.getString("channelName")); // Channel Name
                    message.append("**");
                    message.append(LocaleString.getString(event.getMessage().getGuild().getId(), "nowPlayingLower")); // " is now playing"
                    message.append("**"); // name of the game
                    message.append(result.getString("streamsGame"));
                    message.append("**. ");
                    message.append(LocaleString.getString(event.getMessage().getGuild().getId(), "watchThemHere"));
                    message.append("__*");
                    message.append(result.getString("channelUrl"));
                    message.append("*__\n\n");
                    if (message.length() >= 1800) {
                        sendToPm(event, message.build());
                        message = new MessageBuilder();
                        message.append("__*Here's some more streams!*__\n");
                    }
                }
                sendToPm(event, message.build());

            } catch (SQLException e) {
                System.out.println("[~ERROR~] There was a problem fetching live streams for an on demand request.");
                e.printStackTrace();
            } finally {
                cleanUp(result, pStatement, connection);
            }
        }
    }

    @Override
    public final void help(GuildMessageReceivedEvent event) {
        sendToChannel(event, LocaleString.getString(event.getMessage().getGuild().getId(), "streamsHelp"));
    }

    @Override
    public final void executed(boolean success, GuildMessageReceivedEvent event) {
        new Tracker("Command");
    }
}
