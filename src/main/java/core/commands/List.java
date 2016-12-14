package core.commands;

import core.Command;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import platform.discord.controller.DiscordController;
import util.Const;
import util.database.Database;
import util.database.calls.Tracker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static platform.discord.controller.DiscordController.sendToChannel;
import static platform.discord.controller.DiscordController.sendToPm;
import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class List implements Command {

    private static Connection connection;
    private static PreparedStatement pStatement;
    private static ResultSet resultSet;
    private String option;
    private String query;
    private String guildId;
    private String[] options = new String[]{"channel", "game", "manager", "streamLang", "tag", "team", "help"};

    private Message createNotificationMessage(MessageBuilder message, GuildMessageReceivedEvent event) {
        try {
            connection = Database.getInstance().getConnection();
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, guildId);
            resultSet = pStatement.executeQuery();

            if (resultSet.isBeforeFirst()) {
                while (resultSet.next()) {
                    message.append("\n\t");
                    if (!"manager".equals(option)) {
                        message.append(resultSet.getString(1).replaceAll("''", "'"));
                        switch (resultSet.getInt(2)) {
                            case 1:
                                message.append(" on Twitch.tv");
                                break;
                            default:
                                break;
                        }
                    } else {
                        String userId = resultSet.getString("userId");
                        User user = event.getJDA().getUserById(userId);
                        String userName = user.getName();
                        message.append(userName);
                    }

                    // Large message handler
                    if (message.length() > 1850) {
                        sendToPm(event, message.build());
                        message = new MessageBuilder();
                    }
                }
            } else {
                message.append("\n\tRuh Roh!  I can't seem to find anything here...");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(resultSet, pStatement, connection);
        }
        return message.build();
    }

    /**
     * Used to determine if appropriate arguments exist
     *
     * @param args  Arguments being passed
     * @param event From JDA: GuildMessageReceivedEvent
     * @return boolean true if criteria is met, false if criteria not met
     */
    @Override
    public final boolean called(String args, GuildMessageReceivedEvent event) {
        for (String s : this.options) { // Iterate through the available options for this command
            if (args != null && !args.isEmpty()) {
                if (args.equals(s)) {
                    // Sets the class scope variables that will be used by action()
                    option = s;
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
     * @param event From JDA: GuildMessageReceivedEvent
     */
    @Override
    public final void action(String args, GuildMessageReceivedEvent event) {
        DiscordController dController = new DiscordController(event);

        guildId = dController.getGuildId();

        MessageBuilder message = new MessageBuilder();
        message.append("Heya!  Here's a list of " + option + "s that this Discord server is keeping " +
                "tabs on:\n");

        switch (option) {
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

        sendToPm(event, createNotificationMessage(message, event));
    }

    /**
     * Returns help info for the command
     *
     * @param event From JDA: GuildMessageReceivedEvent
     */
    @Override
    public final void help(GuildMessageReceivedEvent event) {
        sendToChannel(event, Const.LIST_HELP);
    }

    /**
     * Runs specified scripts which are determined by {success}
     *
     * @param success [boolean]
     * @param event   From JDA: GuildMessageReceivedEvent
     */
    @Override
    public final void executed(boolean success, GuildMessageReceivedEvent event) {
        new Tracker("List");
    }
}
