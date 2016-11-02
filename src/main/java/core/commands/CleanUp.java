package core.commands;

import core.Command;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
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
public class CleanUp implements Command {

    private Connection connection;
    private PreparedStatement pStatement;
    private Integer result;


    /**
     * Used to determine if appropriate arguments exist
     *
     * @param args  Arguments being passed
     * @param event From JDA: MessageReceivedEvent
     * @return boolean true if criteria is met, false if criteria not met
     */
    @Override
    public final boolean called(String args, MessageReceivedEvent event) {
        if (args != null && !args.isEmpty()) {
            return "none".equals(args) || "edit".equals(args) || "delete".equals(args) || "help".equals(args);
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
    public final void action(String args, MessageReceivedEvent event) {
        String returnStatement;
        String query;
        switch (args) {
            case "none":
                query = "UPDATE `guild` SET `cleanup` = 0 WHERE `guildId` = ?";
                returnStatement = Const.CLEANUP_SUCCESS_NONE;
                break;
            case "edit":
                query = "UPDATE `guild` SET `cleanup` = 1 WHERE `guildId` = ?";
                returnStatement = Const.CLEANUP_SUCCESS_EDIT;
                break;
            case "delete":
                query = "UPDATE `guild` SET `cleanup` = 2 WHERE `guildId` = ?";
                returnStatement = Const.CLEANUP_SUCCESS_DELETE;
                break;
            default:
                return;
        }
        try {
            connection = Database.getInstance().getConnection();
            pStatement = connection.prepareStatement(query);

            pStatement.setString(1, event.getGuild().getId());
            result = pStatement.executeUpdate();

            if (result.equals(1)) {
                sendToChannel(event, returnStatement);
            } else {
                sendToChannel(event, Const.CLEANUP_FAIL);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(pStatement, connection);
        }
    }

    /**
     * Returns help info for the command
     *
     * @param event From JDA: MessageReceivedEvent
     */
    @Override
    public final void help(MessageReceivedEvent event) {
        sendToChannel(event, Const.CLEANUP_HELP);
    }

    /**
     * Runs specified scripts which are determined by {success}
     *
     * @param success [boolean]
     * @param event   From JDA: MessageReceivedEvent
     */
    @Override
    public final void executed(boolean success, MessageReceivedEvent event) {
        new Tracker("Cleanup");
    }
}
