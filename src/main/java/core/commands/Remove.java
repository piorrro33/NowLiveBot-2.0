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
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import platform.discord.controller.DiscordController;
import util.Const;
import util.database.Database;
import util.database.calls.Tracker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static core.commands.Add.*;
import static platform.discord.controller.DiscordController.sendToChannel;
import static platform.generic.controller.PlatformController.getPlatformId;
import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class Remove implements Command {

    private static Logger logger = LoggerFactory.getLogger(Remove.class);
    private String option;
    private String argument;
    private Connection connection;
    private PreparedStatement pStatement;
    private String query;
    private ResultSet result;
    private String[] options = new String[]{"channel", "filter", "game", "manager", "help"};

    @Override
    public final boolean called(String args, GuildMessageReceivedEvent event) {

        for (String s : this.options) { // Iterate through the available options for this command
            if (args != null && !args.isEmpty()) {
                if (optionCheck(args, s)) {
                    if (argumentCheck(args, s.length())) {
                        // Sets the class scope variables that will be used by action()
                        this.option = s;
                        this.argument = args.substring(this.option.length() + 1);
                        return true;
                    } else {
                        // If the required arguments for the option are missing
                        missingArguments(event);
                        return false;
                    }
                } else if ("help".equals(args)) {
                    // If the help argument is the only argument that is passed
                    return true;
                }
            }
        }
        // If all checks fail
        return false;
    }

    @Override
    public final void action(String args, GuildMessageReceivedEvent event) {

        DiscordController dController = new DiscordController(event);
        String guildId = event.getGuild().getId();
        Integer platformId;

        if (getPlatformId(args) > 0) {
            platformId = getPlatformId(args);
        } else {
            platformId = 1;
        }

        for (String s : this.options) {
            if (this.option.equals(s) && !this.option.equals("help")) {

                this.argument = this.argument.replace("'", "''");

                // Check to see if the entry already exists in the db for that guild
                switch (this.option) {
                    case "manager":
                        if (!event.getGuild().getOwner().getUser().getId().equals(String.valueOf(dController
                                .getMentionedUsersId()))) {
                            if (managerCount(guildId)) { // Make sure there is going to be enough managers
                                try {
                                    query = "DELETE FROM `" + this.option + "` WHERE `guildId` = ? AND `userId` = ?";

                                    if (connection == null || connection.isClosed()) {
                                        connection = Database.getInstance().getConnection();
                                    }
                                    pStatement = connection.prepareStatement(query);

                                    pStatement.setString(1, guildId);
                                    pStatement.setString(2, dController.getMentionedUsersId());
                                    Integer removeManager = pStatement.executeUpdate();
                                    removeResponse(event, removeManager);
                                } catch (SQLException e) {
                                    logger.error("Error when deleting info from the " + this.option + " table: ", e);
                                } finally {
                                    cleanUp(pStatement, connection);
                                }
                            } else {
                                sendToChannel(event, Const.NEED_ONE_MANAGER);
                            }
                        } else {
                            sendToChannel(event, Const.CANT_REMOVE_OWNER);
                        }
                        break;

                    default:
                        query = "DELETE FROM `" + this.option + "` WHERE `guildId` = ? AND `platformId` = ? AND " +
                                "`name` LIKE ?";
                        try {
                            if (connection == null || connection.isClosed()) {
                                connection = Database.getInstance().getConnection();
                            }
                            pStatement = connection.prepareStatement(query);

                            pStatement.setString(1, guildId);
                            pStatement.setInt(2, platformId);
                            pStatement.setString(3, "%" + this.argument + "%");
                            Integer removeOther = pStatement.executeUpdate();
                            removeResponse(event, removeOther);
                        } catch (SQLException e) {
                            logger.error("Error when deleting info from the " + this.option + " table: ", e);
                        } finally {
                            cleanUp(pStatement, connection);
                        }
                        break;
                }
            }
        }
    }

    private void removeResponse(GuildMessageReceivedEvent event, Integer resultVar) {
        if (resultVar > 0) {
            sendToChannel(event, "Removed `" + this.option + "` " + this.argument);
        } else {
            sendToChannel(event, "I can't remove `" + this.option + "` " + this.argument + " because " +
                    "it's not in my database.");
        }
    }

    @Override
    public final void help(GuildMessageReceivedEvent event) {
        sendToChannel(event, Const.REMOVE_HELP);
    }

    @Override
    public final void executed(boolean success, GuildMessageReceivedEvent event) {
        new Tracker("Remove");
    }

    private boolean managerCount(String guildId) {
        try {
            query = "SELECT COUNT(*) AS `count` FROM `manager` WHERE `guildId` = ?";

            if (connection == null || connection.isClosed()) {
                connection = Database.getInstance().getConnection();
            }
            pStatement = connection.prepareStatement(query);

            pStatement.setString(1, guildId);
            result = pStatement.executeQuery();
            while (result.next()) {
                if (result.getInt("count") > 1) {
                    return true; // There are ample managers to remove this one
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(result, pStatement, connection);
        }
        return false; // Unable to remove the manager because there's only one manager
    }
}
