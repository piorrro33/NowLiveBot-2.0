/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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

import static platform.discord.controller.DiscordController.sendToChannel;
import static util.database.Database.cleanUp;

/**
 * Add Command.
 * TODO: Move SQL calls to separate class.
 *
 * @author keesh
 */
public class Add implements Command {

    private static final Logger logger = LoggerFactory.getLogger(Add.class);
    private String option;
    private String argument;
    private Connection connection;
    private PreparedStatement pStatement;
    private String query;
    private ResultSet resultSet;
    private Integer resultInt;
    private DiscordController dController;
    private String[] options = new String[]{"channel", "filter", "game", "manager", "tag", "team", "help"};

    public static boolean optionCheck(String args, String option) {
        return args.contains(" ") && args.toLowerCase().substring(0, option.length()).equals(option);
    }

    public static boolean argumentCheck(String args, Integer spaceLocation) {
        return args.indexOf(' ') == spaceLocation && args.length() >= args.indexOf(' ') + 1;
    }

    public static void missingArguments(GuildMessageReceivedEvent event) {
        sendToChannel(event, Const.INCORRECT_ARGS);
    }

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
    public final void action(String args, GuildMessageReceivedEvent event) {

        dController = new DiscordController(event);

        String guildId = dController.getGuildId();

        for (String s : this.options) {
            if (this.option.equals(s) && !this.option.equals("help")) {
                Integer platformId = 1; // platformId is always 1 for Twitch until other platforms are added
                this.argument = this.argument.replace("'", "''");

                switch (this.option) {
                    case "manager":
                        logger.info("Checking to see if " + dController.getMentionedUsersId()
                                + " already exists for guild: " + guildId);

                        // Check to make sure the user is not a bot
                        if (!event.getJDA().getUserById(String.valueOf(dController.getMentionedUsersId())).isBot()) {
                            logger.info("User is not a bot");
                            if (!alreadyManager(guildId, String.valueOf(dController.getMentionedUsersId()))) {
                                logger.info("User is currently not a manger.");
                                addManager(guildId, event);
                            } else {
                                logger.info("User is currently a manager");
                                sendToChannel(event, "It seems I've already hired that user as a manager.  Find moar " +
                                        "humanz!");
                            }
                        } else {
                            sendToChannel(event, Const.NO_BOT_MANAGER);
                        }
                        break;
                    default:
                        logger.info("Checking to see if the " + this.option + " already exists for guild: " + guildId);
                        try {
                            connection = Database.getInstance().getConnection();
                            query = "SELECT `name` FROM `" + this.option + "` WHERE `guildId` = ? AND `platformId` = ? " +
                                    "AND `name` = ?";
                            pStatement = connection.prepareStatement(query);

                            pStatement.setString(1, guildId);
                            pStatement.setInt(2, platformId);
                            pStatement.setString(3, this.argument);
                            resultSet = pStatement.executeQuery();
                            if (resultSet.isBeforeFirst()) {
                                sendToChannel(event, Const.ALREADY_EXISTS);
                            } else {
                                addOther(guildId, platformId, event);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } finally {
                            cleanUp(resultSet, pStatement, connection);
                        }
                        break;
                }
            }
        }
    }

    private void returnStatement(String guildId, GuildMessageReceivedEvent event) {
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

    private void addOther(String guildId, Integer platformId, GuildMessageReceivedEvent event) {
        try {
            connection = Database.getInstance().getConnection();
            query = "INSERT INTO `" + this.option + "` (`id`, `guildId`, `platformId`, `name`) VALUES " +
                    "(null, ?, ?, ?)";
            pStatement = connection.prepareStatement(query);

            pStatement.setString(1, guildId);
            pStatement.setInt(2, platformId);
            pStatement.setString(3, this.argument);
            resultInt = pStatement.executeUpdate();
            returnStatement(guildId, event);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(pStatement, connection);
        }
    }

    private void addManager(String guildId, GuildMessageReceivedEvent event) {
        try {
            connection = Database.getInstance().getConnection();
            query = "INSERT INTO `" + this.option + "` (`id`, `guildId`, `userId`) VALUES " +
                    "(null, ?, ?)";
            pStatement = connection.prepareStatement(query);

            pStatement.setString(1, guildId);
            pStatement.setString(2, String.valueOf(dController.getMentionedUsersId()));
            resultInt = pStatement.executeUpdate();
            returnStatement(guildId, event);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(pStatement, connection);
        }
    }

    @Override
    public final void help(GuildMessageReceivedEvent event) {
        sendToChannel(event, Const.ADD_HELP);
    }

    @Override
    public final void executed(boolean success, GuildMessageReceivedEvent event) {
        new Tracker("Add");

    }

    private boolean alreadyManager(String guildId, String userId) {
        try {
            connection = Database.getInstance().getConnection();
            query = "SELECT COUNT(*) AS `count` FROM `manager` WHERE `guildId` = ? AND `userId` = ?";
            pStatement = connection.prepareStatement(query);

            pStatement.setString(1, guildId);
            pStatement.setString(2, userId);
            resultSet = pStatement.executeQuery();
            while (resultSet.next()) {
                if (resultSet.getInt("count") > 0) {
                    return true; // If they are a manager already
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(resultSet, pStatement, connection);
        }
        return false; // If they're not a manger
    }

}