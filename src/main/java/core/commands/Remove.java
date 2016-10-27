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
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static core.commands.Add.*;
import static platform.discord.controller.DiscordController.sendToChannel;
import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class Remove implements Command {

    private static Logger logger = LoggerFactory.getLogger(Remove.class);
    public String help;
    public String option;
    public String argument;
    private Connection connection;
    private PreparedStatement pStatement;
    private String query;
    private Integer resultInt;
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

        String guildId = dController.getGuildId();

        for (String s : this.options) {
            if (this.option.equals(s) && !this.option.equals("help")) {
                Integer platformId = 1; // platformId is always 1 for Twitch until other platforms are added
                this.argument = this.argument.replace("'", "''");

                // Check to see if the entry already exists in the db for that guild
                if (this.option.equals("manager")) {
                    try {
                        connection = Database.getInstance().getConnection();
                        query = "DELETE FROM `" + this.option + "` WHERE `guildId` = ? AND `userId` = ?";
                        pStatement = connection.prepareStatement(query);
                        pStatement.setString(1, guildId);
                        pStatement.setString(2, dController.getMentionedUsersId());
                        resultInt = pStatement.executeUpdate();
                    } catch (SQLException e) {
                        logger.error("Error when deleting info from the " + this.option + " table: ", e);
                    } finally {
                        cleanUp(pStatement, connection);
                    }
                } else {
                    query = "DELETE FROM `" + this.option + "` WHERE `guildId` = ? AND `platformId` = ? AND `name` = ?";
                    try {
                        connection = Database.getInstance().getConnection();
                        pStatement = connection.prepareStatement(query);
                        pStatement.setString(1, guildId);
                        pStatement.setInt(2, platformId);
                        pStatement.setString(3, this.argument);
                        resultInt = pStatement.executeUpdate();
                    } catch (SQLException e) {
                        logger.error("Error when deleting info from the " + this.option + " table: ", e);
                    } finally {
                        cleanUp(pStatement, connection);
                    }

                    switch (this.option) {
                        case "channel":
                            try {
                                connection = Database.getInstance().getConnection();
                                query = "DELETE FROM `queue` WHERE `guildId` = ? AND `platformId` = ? AND " +
                                        "`channelName` = ?";
                                pStatement = connection.prepareStatement(query);
                                pStatement.setString(1, guildId);
                                pStatement.setInt(2, platformId);
                                pStatement.setString(3, this.argument);
                                pStatement.executeUpdate();
                            } catch (SQLException e) {
                                logger.error("Error when deleting the channel info from the queue table: ", e);
                            } finally {
                                cleanUp(pStatement, connection);
                            }
                            break;
                        case "game":
                            try {
                                connection = Database.getInstance().getConnection();
                                query = "DELETE FROM `queue` WHERE `guildId` = ? AND `platformId` = ? AND " +
                                        "`gameName` = ?";
                                pStatement = connection.prepareStatement(query);
                                pStatement.setString(1, guildId);
                                pStatement.setInt(2, platformId);
                                pStatement.setString(3, this.argument);
                                pStatement.executeUpdate();
                            } catch (SQLException e) {
                                logger.error("Error when deleting the game info from the queue table: ", e);
                            } finally {
                                cleanUp(pStatement, connection);
                            }
                            break;
                        default:
                            break;
                    }
                }

                if (resultInt > 0) {
                    sendToChannel(event, "Removed `" + this.option + "` " + this.argument);
                    logger.info("Successfully removed " + this.argument + " from the database for guildId: " +
                            guildId + ".");
                } else {
                    sendToChannel(event, "I can't remove `" + this.option + "` " + this.argument + " because " +
                            "it's not in my database.");
                    logger.info("Failed to remove " + this.option + " " + this.argument + " from the database" +
                            " for guildId: " + guildId + ".");
                }
            }
        }
    }

    @Override
    public void help(MessageReceivedEvent event) {
        sendToChannel(event, Const.REMOVE_HELP);
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {
        new Tracker("Remove");
    }
}
