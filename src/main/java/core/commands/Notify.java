package core.commands;

import core.Command;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Const;
import util.database.Database;
import util.database.calls.Tracker;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static platform.discord.controller.DiscordController.sendToChannel;
import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class Notify implements Command {

    Logger logger = LoggerFactory.getLogger(Notify.class);

    private Connection connection = Database.getInstance().getConnection();
    private Statement statement = null;
    private Integer result = null;
    private ResultSet resultSet = null;
    private String query;

    /**
     * Used to determine if appropriate arguments exist
     *
     * @param args  Arguments being passed
     * @param event From JDA: MessageReceivedEvent
     * @return boolean true if criteria is met, false if criteria not met
     */
    @Override
    public boolean called(String args, MessageReceivedEvent event) {
        if (args != null && args != "") {
            if (args.equals("me") || args.equals("here") || args.equals("everyone") || args.equals("none") || args
                    .equals("help")) {
                return true;
            } else {
                sendToChannel(event, Const.INCORRECT_ARGS);
                logger.info(Const.INCORRECT_ARGS);
                return false;
            }
        } else {
            sendToChannel(event, Const.EMPTY_ARGS);
            return false;
        }
    }

    /**
     * Action taken after the command is verified
     *
     * @param args  Arguments being passed
     * @param event From JDA: MessageReceivedEvent
     */
    @Override
    public void action(String args, MessageReceivedEvent event) {

        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            logger.error("There is a problem establishing a connection to the database in Notify.", e.getMessage());
        } finally {
            cleanUp(result, statement, connection);
        }

        switch (args.toLowerCase()) {
            case "none":
                query = "UPDATE `notification` SET `userId` = null, `level` = 0 WHERE `guildId` = '"
                        + event.getGuild().getId() + "'";
                logger.info(query);
                try {
                    result = statement.executeUpdate(query);
                    if (result > 0) {
                        sendToChannel(event, Const.NOTIFY_NONE);
                        logger.info("Guild: " + event.getGuild().getName() + " has set notification to NONE.");
                    }
                } catch (SQLException e) {
                    sendToChannel(event, Const.ALREADY_EXISTS);
                    logger.error("There is a problem updating to 'NONE' in Notify.", e.getMessage());
                } finally {
                    cleanUp(result, statement, connection);
                }
                break;
            // TODO:  Need to finish working on this to get it WAI
            case "me":
                // First, check to see if they're already set to receive notifications for that guild
                query = meSelect(event);
                try {
                    assert statement != null;
                    resultSet = statement.executeQuery(query);
                    logger.warn("There was an SQL Exception when checking for 'me' notification for user "
                            + event.getMessage().getAuthor().getId() + " in Guild " + event.getGuild().getId());

                    assert resultSet != null;

                    if (!resultSet.next()) {
                        // If they weren't set up yet for that guild, add them
                        query = meInsert(event);
                        result = statement.executeUpdate(query);
                        // Only one row should be updated here.
                        if (result == 1) {
                            sendToChannel(event, Const.NOTIFY_ME);
                        } else {
                            sendToChannel(event, Const.OOPS);
                        }
                    } else {
                        sendToChannel(event, Const.ALREADY_EXISTS);
                    }
                } catch (SQLException e) {
                    logger.warn("There was an SQL Exception when checking the result set.");
                }
                break;
            // TODO: gotta get this WAI as well
            case "here":
                // First, check to see if they're already set to receive notifications for that guild
                query = hereSelect(event);
                try {
                    assert statement != null;
                    resultSet = statement.executeQuery(query);
                    logger.warn("There was an SQL Exception when checking for 'me' notification for user "
                            + event.getMessage().getAuthor().getId() + " in Guild " + event.getGuild().getId());

                    assert resultSet != null;

                    if (!resultSet.next()) {
                        // Set the @here notification if it already wasn't
                        query = hereInsert(event);
                        result = statement.executeUpdate(query);
                        // Only one row should be updated here.
                        if (result == 1) {
                            sendToChannel(event, Const.NOTIFY_ME);
                        } else {
                            sendToChannel(event, Const.OOPS);
                        }
                    } else {
                        sendToChannel(event, Const.ALREADY_EXISTS);
                    }
                } catch (SQLException e) {
                    logger.warn("There was an SQL Exception when checking the result set.");
                }
                break;
            case "everyone":

                break;
            default:
                sendToChannel(event, Const.INCORRECT_ARGS);
                logger.info("There was an error checking for the command arguments in Notify.");
                break;
        }

    }

    /**
     * Returns help info for the command
     *
     * @param event From JDA: MessageReceivedEvent
     */
    @Override
    public void help(MessageReceivedEvent event) {
        sendToChannel(event, Const.NOTIFY_HELP);
    }

    /**
     * Runs specified scripts which are determined by {success}
     *
     * @param success [boolean]
     * @param event   From JDA: MessageReceivedEvent
     */
    @Override
    public void executed(boolean success, MessageReceivedEvent event) {
        new Tracker("Notify");
    }

    private String meSelect(MessageReceivedEvent event) {
        return "SELECT `userId`, `level` FROM `notification` WHERE `guildId` = '" + event.getGuild().getId()
                + "' AND `userId` = '" + event.getMessage().getAuthor().getId() + "' AND `level` = 1";
    }

    public String meInsert(MessageReceivedEvent event) {
        return "INSERT INTO `notification` (`id`, guildId`, `userId`, `level`) VALUES (null, '" +
                event.getGuild().getId() + "', '" + event.getMessage().getAuthor().getId() + "', 1)";
    }

    private String hereSelect(MessageReceivedEvent event) {
        return "SELECT `userId`, `level` FROM `notification` WHERE `guildId` = '" + event.getGuild().getId()
                + "' AND `level` = 2";
    }

    public String hereInsert(MessageReceivedEvent event) {
        return "INSERT INTO `notification` (`id`, guildId`, `userId`, `level`) VALUES (null, '" +
                event.getGuild().getId() + "', null, 2)";
    }
}
