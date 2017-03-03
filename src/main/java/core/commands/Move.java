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
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.database.Database;
import util.database.calls.Tracker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static platform.discord.controller.DiscordController.sendToChannel;
import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class Move implements Command {
    private static final Logger logger = LoggerFactory.getLogger(Database.class);
    private Connection connection;
    private PreparedStatement pStatement;

    @Override
    public final boolean called(String args, GuildMessageReceivedEvent event) {
        if (args != null && !args.isEmpty()) {
            if (args.substring(0, 1).equals("#") && !args.contains(" ")) {
                return true;
            } else if (!"help".equals(args)) {
                sendToChannel(event, LocaleString.getString(event.getMessage().getGuild().getId(), "incorrectArgs"));
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public final void action(String args, GuildMessageReceivedEvent event) {
        // Get the channelID from the guild and insert into the DB

        for (TextChannel textChannel : event.getGuild().getTextChannelsByName(args.substring(1), true)) {

            if (textChannel.getGuild().getId().equals(event.getGuild().getId())) {
                try {
                    String query = "UPDATE `guild` SET `channelId` = ? WHERE `guildId` = ?";
                    if (connection == null || connection.isClosed()) {
                        connection = Database.getInstance().getConnection();
                    }
                    pStatement = connection.prepareStatement(query);

                    pStatement.setString(1, textChannel.getId());
                    pStatement.setString(2, event.getGuild().getId());

                    if (pStatement.executeUpdate() == 1) {
                        sendToChannel(event, LocaleString.getString(event.getMessage().getGuild().getId(), "moveSuccess"));
                    } else {
                        sendToChannel(event, LocaleString.getString(event.getMessage().getGuild().getId(), "moveFail"));
                    }
                } catch (SQLException e) {
                    logger.error("There was a problem updating Move in the database", e);
                } finally {
                    cleanUp(pStatement, connection);
                }
            } else {
                break;
            }
        }
    }

    @Override
    public final void help(GuildMessageReceivedEvent event) {
        sendToChannel(event, LocaleString.getString(event.getMessage().getGuild().getId(), "moveHelp"));
    }

    @Override
    public final void executed(boolean success, GuildMessageReceivedEvent event) {
        new Tracker("Command");
    }
}
