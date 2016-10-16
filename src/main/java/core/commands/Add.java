/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.commands;

import core.Command;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import platform.discord.controller.DiscordController;
import util.Const;
import util.database.Database;
import util.database.calls.Tracker;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static platform.discord.controller.DiscordController.sendToChannel;

/**
 * Add Command.
 * TODO: Move SQL calls to separate class.
 *
 * @author keesh
 */
public class Add implements Command {

    private static Logger logger = LoggerFactory.getLogger(Add.class);
    public String help;
    public String option;
    public String argument;
    private String[] options = new String[]{"channel", "game", "manager", "tag", "team", "help"};

    @Override
    public boolean called(String args, MessageReceivedEvent event) {

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
            } else {
                // If there are no passed arguments
                sendToChannel(event, Const.EMPTY_ARGS);
                return false;
            }
        }

        // If all checks fail
        return false;
    }

    @Override
    public void action(String args, MessageReceivedEvent event) {
        DiscordController dController = new DiscordController(event);

        String guildId = dController.getguildId();

        for (String s : this.options) {
            if (this.option.equals(s) && !this.option.equals("help")) {
                Connection connection;
                Statement statement;
                ResultSet resultSet;
                Integer resultInt;
                Integer platformId = 1; // platformId is always 1 for Twitch until other platforms are added
                this.argument = this.argument.replace("'", "''");

                try {
                    connection = Database.getInstance().getConnection();

                    // Check to see if the game already exists in the db for that guild
                    String query;

                    if (this.option.equals("manager")) {
                        // TODO: Do a bot check to make sure bots don't get added to the manager list
                        logger.info("Checking to see if " + dController.getMentionedUsersId()
                                + " already exists for guild: " + guildId);

                        query = "SELECT `userId` FROM `" + this.option + "` WHERE `guildId` = '" + guildId + "' AND " +
                                "`userId` = '" + String.valueOf(dController.getMentionedUsersId()) + "'";
                    } else {
                        logger.info("Checking to see if the " + this.option + " already exists for guild: " + guildId);

                        query = "SELECT `name` FROM `" + this.option + "` WHERE `guildId` = '" + guildId + "' AND " +
                                "`platformId` = " + platformId + " AND `name` = '" + this.argument + "'";
                    }
                    logger.info(query);
                    statement = connection.prepareStatement(query);

                    resultSet = statement.executeQuery(query);

                    if (resultSet.isBeforeFirst()) {
                        sendToChannel(event, Const.ALREADY_EXISTS);
                    } else {
                        if (this.option.equals("manager")) {
                            query = "INSERT INTO `" + this.option + "` (`id`, `guildId`, `userId`) VALUES " +
                                    "(null, '" + guildId + "', '" + String.valueOf(dController.getMentionedUsersId())
                                    + "')";
                        } else {
                            query = "INSERT INTO `" + this.option + "` (`id`, `guildId`, `platformId`, `name`) VALUES " +
                                    "(null, '" + guildId + "', " + platformId + ", '" + this.argument + "')";
                        }

                        resultInt = statement.executeUpdate(query);

                        if (resultInt > 0) {
                            sendToChannel(event, "Added `" + this.option + "` " + this.argument);
                            logger.info("Successfully added " + this.argument + " to the database for guildId: " +
                                    guildId + ".");
                        } else {
                            sendToChannel(event, "Failed to add `" + this.option + "` " + this.argument);
                            logger.info("Failed to add " + this.option + " " + this.argument + " to the database for " +
                                    "guildId: " + guildId + ".");
                        }
                    }

                    Database.getInstance();
                    Database.cleanUp(resultSet, statement, connection);

                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {

                }
            }
        }
    }

    @Override
    public void help(MessageReceivedEvent event) {
        sendToChannel(event, Const.ADD_HELP);
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {
        new Tracker("Add");

    }

    private boolean optionCheck(String args, String option) {
        return args.contains(" ") && args.toLowerCase().substring(0, option.length()).equals(option);
    }

    private boolean argumentCheck(String args, Integer spaceLocation) {
        return args.indexOf(" ") == spaceLocation && args.length() >= args.indexOf(" ") + 1;
    }

    private void missingArguments(MessageReceivedEvent event) {
        sendToChannel(event, Const.INCORRECT_ARGS);
    }

}
