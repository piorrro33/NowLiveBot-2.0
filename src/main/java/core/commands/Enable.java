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
public class Enable implements Command {
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
        return true;
    }

    /**
     * Action taken after the command is verified
     *
     * @param args  Arguments being passed
     * @param event From JDA: MessageReceivedEvent
     */
    @Override
    public final void action(String args, MessageReceivedEvent event) {
        try {
            connection = Database.getInstance().getConnection();
            String query = "UPDATE `guild` SET `isActive` = 1 WHERE `guildId` = ?";
            pStatement = connection.prepareStatement(query);

            pStatement.setString(1, event.getGuild().getId());
            result = pStatement.executeUpdate();

            if (result.equals(1)) {
                sendToChannel(event, Const.ENABLE_SUCCESS);
            } else {
                sendToChannel(event, Const.ENABLE_FAIL);

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
        sendToChannel(event, Const.ENABLE_HELP);
    }

    /**
     * Runs specified scripts which are determined by {success}
     *
     * @param success [boolean]
     * @param event   From JDA: MessageReceivedEvent
     */
    @Override
    public final void executed(boolean success, MessageReceivedEvent event) {
        new Tracker("Enable");
    }
}
