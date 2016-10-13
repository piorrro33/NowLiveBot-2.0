package core.commands;

import core.Command;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class Enable implements Command {

    Logger logger = LoggerFactory.getLogger("[TEST LOGGER]");

    /**
     * Used to determine if appropriate arguments exist
     *
     * @param args  Arguments being passed
     * @param event From JDA: MessageReceivedEvent
     * @return boolean true if criteria is met, false if criteria not met
     */
    @Override
    public boolean called(String args, MessageReceivedEvent event) {
        // Permissions stuff here
        return true;
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
            Connection connection = Database.getInstance().getConnection();
            Statement statement = connection.createStatement();
            String query;
            query = "UPDATE `guild` SET `isActive` = 1 WHERE `guildId` = '" + event.getGuild().getId() + "'";
            Integer result = statement.executeUpdate(query);

            if (result.equals(1)) {
                sendToChannel(event, Const.ENABLE_SUCCESS);
            } else {
                sendToChannel(event, Const.ENABLE_FAIL);

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
        sendToChannel(event, Const.ENABLE_HELP);
    }

    /**
     * Runs specified scripts which are determined by {success}
     *
     * @param success [boolean]
     * @param event   From JDA: MessageReceivedEvent
     */
    @Override
    public void executed(boolean success, MessageReceivedEvent event) {
        new Tracker("Enable");
    }
}
