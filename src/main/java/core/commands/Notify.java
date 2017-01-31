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
import core.Main;
import langs.LocaleString;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.DiscordLogger;
import util.database.Database;
import util.database.calls.Tracker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import static platform.discord.controller.DiscordController.sendToChannel;
import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class Notify implements Command {

    private Logger logger = LoggerFactory.getLogger(Notify.class);

    private Connection connection;
    private PreparedStatement pStatement;

    /**
     * Used to determine if appropriate arguments exist
     *
     * @param args  Arguments being passed
     * @param event From JDA: GuildMessageReceivedEvent
     * @return boolean true if criteria is met, false if criteria not met
     */
    @Override
    public final boolean called(String args, GuildMessageReceivedEvent event) {
        if (args != null && !"".equals(args)) {

            switch (args.replaceFirst("@", "")) {
                case "none":
                case "here":
                case "everyone":
                case "help":
                    return true;
                default:
                    if (Main.debugMode()) {
                        logger.info(LocaleString.getString(event.getMessage().getGuild().getId(), "incorrectArgs"));
                    }
                    return false;
            }
        }
        return false;
    }

    /**
     * Action taken after the command is verified
     *
     * @param args  Arguments being passed
     * @param event From JDA: GuildMessageReceivedEvent
     */
    @Override
    public final void action(String args, GuildMessageReceivedEvent event) {
        switch (args.toLowerCase()) {
            case "none":
                if (update(event, 0)) {
                    sendToChannel(event, LocaleString.getString(event.getMessage().getGuild().getId(), "notifyNone"));
                }
                break;
            // Removed option "me"
            case "here":
                if (update(event, 2)) {
                    sendToChannel(event, LocaleString.getString(event.getMessage().getGuild().getId(), "notifyHere"));
                }
                break;
            case "everyone":
                if (update(event, 3)) {
                    sendToChannel(event, LocaleString.getString(event.getMessage().getGuild().getId(), "notifyEveryone"));
                }
                break;
            default:
                sendToChannel(event, LocaleString.getString(event.getMessage().getGuild().getId(), "incorrectArgs"));
                if (Main.debugMode()) {
                    logger.info("There was an error checking for the command arguments in Notify.");
                }
                break;
        }

    }

    /**
     * Returns help info for the command
     *
     * @param event From JDA: GuildMessageReceivedEvent
     */
    @Override
    public final void help(GuildMessageReceivedEvent event) {
        sendToChannel(event, LocaleString.getString(event.getMessage().getGuild().getId(), "notifyHelp"));
    }

    /**
     * Runs specified scripts which are determined by {success}
     *
     * @param success [boolean]
     * @param event   From JDA: GuildMessageReceivedEvent
     */
    @Override
    public final void executed(boolean success, GuildMessageReceivedEvent event) {
        new Tracker("Notify");
    }

    private boolean update(GuildMessageReceivedEvent event, Integer level) {
        try {
            String query = "INSERT INTO `notification` (`guildId`, `level`, `userId`) VALUES (?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE `level` = ?";
            if (connection == null || connection.isClosed()) {
                connection = Database.getInstance().getConnection();
            }
            pStatement = connection.prepareStatement(query);

            pStatement.setString(1, event.getGuild().getId());
            pStatement.setInt(2, level);
            pStatement.setNull(3, Types.VARCHAR);
            pStatement.setInt(4, level);

            if (pStatement.executeUpdate() > 0) {
                new DiscordLogger(" :telephone: Notification level changed to " + level, event);
                System.out.printf("[COMMAND-NOTIFY] Guild: %s has set notification level to %s.%n", event.getGuild()
                        .getName(), level);
                return true;
            } else {
                sendToChannel(event, LocaleString.getString(event.getMessage().getGuild().getId(), "oops"));
                return false;
            }
        } catch (SQLException e) {
            logger.error("There is a problem establishing a connection to the database in Notify.", e.getMessage());
        } finally {
            cleanUp(pStatement, connection);
        }
        return false;
    }
}
