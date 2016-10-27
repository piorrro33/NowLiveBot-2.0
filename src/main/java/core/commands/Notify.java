package core.commands;

import core.Command;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Const;
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
public class Notify implements Command {

    private Logger logger = LoggerFactory.getLogger(Notify.class);

    private Connection connection;
    private PreparedStatement pStatement;
    private Integer result;
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
        if (args != null && !"".equals(args)) {
            if ("none".equals(args) || "me".equals(args) || "here".equals(args) || "everyone".equals(args) || "help"
                    .equals(args)) {
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
        System.out.println(args);
        switch (args.toLowerCase()) {
            case "none":
                if (update(event, 0)) {
                    sendToChannel(event, Const.NOTIFY_NONE);
                }
                break;
            case "me":
                if (update(event, 1)) {
                    sendToChannel(event, Const.NOTIFY_ME);
                }
                break;
            case "here":
                if (update(event, 2)) {
                    sendToChannel(event, Const.NOTIFY_HERE);
                }
                break;
            case "everyone":
                if (update(event, 3)) {
                    sendToChannel(event, Const.NOTIFY_EVERYONE);
                }
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

    private boolean update(MessageReceivedEvent event, Integer level) {
        try {
            String uId;

            if (level == 1) {
                uId = event.getAuthor().getId();
            } else {
                uId = null;
            }

            connection = Database.getInstance().getConnection();
            query = "UPDATE `notification` SET `userId` = ?, `level` = ? WHERE `guildId` = ?";
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, uId);
            pStatement.setInt(2, level);
            pStatement.setString(3, event.getGuild().getId());
System.out.println(pStatement);
            result = pStatement.executeUpdate();

            if (result > 0) {
                logger.info("Guild: " + event.getGuild().getName() + " has set notification level.");
                return true;
            } else {
                sendToChannel(event, Const.OOPS);
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
