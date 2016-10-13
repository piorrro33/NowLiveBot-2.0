package core.commands;

import core.Command;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import util.Const;
import util.database.Database;
import util.database.calls.Tracker;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static platform.discord.controller.DiscordController.sendToChannel;

/**
 * @author Veteran Software by Ague Mort
 */
public class CleanUp implements Command {
    /**
     * Used to determine if appropriate arguments exist
     *
     * @param args  Arguments being passed
     * @param event From JDA: MessageReceivedEvent
     * @return boolean true if criteria is met, false if criteria not met
     */
    @Override
    public boolean called(String args, MessageReceivedEvent event) {
        if (args != null && !args.isEmpty()) {
            return args.equals("none") || args.equals("edit") || args.equals("delete") || args.equals("help");
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
        String query;
        String returnStatement;

        switch (args) {
            case "none":
                query = "UPDATE `guild` SET `cleanup` = 0 WHERE `guildId` = '" + event.getGuild().getId() + "'";
                returnStatement = Const.CLEANUP_SUCCESS_NONE;
                break;
            case "edit":
                query = "UPDATE `guild` SET `cleanup` = 1 WHERE `guildId` = '" + event.getGuild().getId() + "'";
                returnStatement = Const.CLEANUP_SUCCESS_EDIT;
                break;
            case "delete":
                query = "UPDATE `guild` SET `cleanup` = 2 WHERE `guildId` = '" + event.getGuild().getId() + "'";
                returnStatement = Const.CLEANUP_SUCCESS_DELETE;
                break;
            default:
                query = null;
                return;
        }
        try {
            Connection connection = Database.getInstance().getConnection();
            Statement statement = connection.createStatement();
            Integer result = statement.executeUpdate(query);
            if (result.equals(1)) {
                sendToChannel(event, returnStatement);
            } else {
                sendToChannel(event, Const.CLEANUP_FAIL);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Returns help info for the command
     *
     * @param event From JDA: MessageReceivedEvent
     */
    @Override
    public void help(MessageReceivedEvent event) {
        sendToChannel(event, Const.CLEANUP_HELP);
    }

    /**
     * Runs specified scripts which are determined by {success}
     *
     * @param success [boolean]
     * @param event   From JDA: MessageReceivedEvent
     */
    @Override
    public void executed(boolean success, MessageReceivedEvent event) {
        new Tracker("Cleanup");
    }
}
