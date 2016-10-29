package core.commands;

import core.Command;
import core.Main;
import net.dv8tion.jda.MessageBuilder;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
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
 * @author Veteran Software by Ague Mort
 */
public class List implements Command {

    private static final Logger logger = LoggerFactory.getLogger(Add.class);
    public String help;
    public String option;
    public Connection connection;
    public PreparedStatement pStatement;
    public String query;
    public ResultSet resultSet;
    private DiscordController dController;
    private String[] options = new String[]{"channel", "game", "manager", "tag", "team", "help"};

    /**
     * Used to determine if appropriate arguments exist
     *
     * @param args  Arguments being passed
     * @param event From JDA: MessageReceivedEvent
     * @return boolean true if criteria is met, false if criteria not met
     */
    @Override
    public boolean called(String args, MessageReceivedEvent event) {
        for (String s : this.options) { // Iterate through the available options for this command
            if (args != null && !args.isEmpty()) {
                if (args.equals(s)) {
                    // Sets the class scope variables that will be used by action()
                    this.option = s;
                    return true;
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

    /**
     * Action taken after the command is verified
     *
     * @param args  Arguments being passed
     * @param event From JDA: MessageReceivedEvent
     */
    @Override
    public void action(String args, MessageReceivedEvent event) {
        dController = new DiscordController(event);

        String guildId = dController.getGuildId();

        MessageBuilder message = new MessageBuilder();
        message.appendString("Heya!  Here's a list of " + this.option + "s that this Discord server is keeping " +
                "tabs on:\n");

        connection = Database.getInstance().getConnection();
        switch (this.option) {
            case "channel":
                query = "SELECT `name`, `platformId` FROM `channel` WHERE `guildId` = ? ORDER BY " +
                        "`platformId` ASC, `name` ASC";
                break;
            case "game":
                query = "SELECT `name`, `platformId` FROM `game` WHERE `guildId` = ? ORDER BY `platformId` " +
                        "ASC, `name` ASC";
                break;
            case "manager":
                query = "SELECT `userId` FROM `manager` WHERE `guildId` = ? ORDER BY `userId` ASC";
                break;
            case "tag":
                query = "SELECT `name`, `platformId` FROM `tag` WHERE `guildId` = ? ORDER BY `platformId` " +
                        "ASC, `name` ASC";
                break;
            case "team":
                query = "SELECT `name`, `platformId` FROM `team` WHERE `guildId` = ? ORDER BY `platformId` " +
                        "ASC, `name` ASC";
                break;
            default:
                break;
        }
        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, guildId);
            resultSet = pStatement.executeQuery();
            if (resultSet.isBeforeFirst()) {
                while (resultSet.next()) {
                    message.appendString("\n\t");
                    if (!this.option.equals("manager")) {
                        message.appendString(resultSet.getString(1));
                        switch (resultSet.getInt(2)) {
                            case 1:
                                message.appendString(" on Twitch.tv");
                                break;
                            default:
                                break;
                        }
                    } else {
                        String userId = resultSet.getString("userId");
                        User user = event.getJDA().getUserById(userId);
                        String userName = user.getUsername();
                        message.appendString(userName);
                    }
                }
            } else {
                message.appendString("\n\tRuh Roh!  I can't seem to find anything here...");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(resultSet, pStatement, connection);
        }
        Message msg = event.getAuthor().getPrivateChannel().sendMessage(message.build());
    }

    /**
     * Returns help info for the command
     *
     * @param event From JDA: MessageReceivedEvent
     */
    @Override
    public void help(MessageReceivedEvent event) {
        sendToChannel(event, Const.LIST_HELP);
    }

    /**
     * Runs specified scripts which are determined by {success}
     *
     * @param success [boolean]
     * @param event   From JDA: MessageReceivedEvent
     */
    @Override
    public void executed(boolean success, MessageReceivedEvent event) {
        new Tracker("List");
    }
}
