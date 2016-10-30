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
public class Compact implements Command {

    private Connection connection;
    private PreparedStatement pStatement;
    private Integer result;
    private String query;

    @Override
    public final boolean called(String args, MessageReceivedEvent event) {
        String[] options = new String[]{"on", "off", "help"};

        for (String s : options) {
            // Iterate through the available options for this command
            if (args != null && !args.isEmpty()) {
                if (optionCheck(args, s)) {
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

    @Override
    public final void action(String args, MessageReceivedEvent event) {
        Integer intArg = -1;
        // Make sure that on/off are transposed properly for DB insertion
        switch (args) {
            case "on":
                intArg = 1;
                break;
            case "off":
                intArg = 0;
                break;
            default:
                sendToChannel(event, Const.INCORRECT_ARGS);
                break;
        }

        if (intArg.equals(1) || intArg.equals(0)) {
            try {
                connection = Database.getInstance().getConnection();
                query = "UPDATE `guild` SET `isCompact` = ? WHERE `guildId` = ?";
                pStatement = connection.prepareStatement(query);
                pStatement.setInt(1, intArg);
                pStatement.setString(2, event.getGuild().getId());

                result = pStatement.executeUpdate();

                if (result.equals(1)) {
                    if (intArg.equals(0)) {
                        sendToChannel(event, Const.COMPACT_MODE_OFF);
                    } else {
                        sendToChannel(event, Const.COMPACT_MODE_ON);
                    }

                } else {
                    sendToChannel(event, Const.COMPACT_FAILURE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                cleanUp(pStatement, connection);
            }
        }
    }

    @Override
    public final void help(MessageReceivedEvent event) {
        sendToChannel(event, Const.COMPACT_HELP);
    }

    @Override
    public final void executed(boolean success, MessageReceivedEvent event) {
        new Tracker("Compact");
    }

    private boolean optionCheck(String args, String option) {
        return args.toLowerCase().substring(0, option.length()).equals(option);
    }
}
