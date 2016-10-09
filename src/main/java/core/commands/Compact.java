package core.commands;

import core.Command;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Const;
import util.database.Database;
import util.database.calls.Tracker;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static platform.discord.controller.DiscordController.sendToChannel;

/**
 * @author Veteran Software by Ague Mort
 */
public class Compact implements Command {

    private static Logger logger = LoggerFactory.getLogger(Compact.class);
    private String option;

    @Override
    public boolean called(String args, MessageReceivedEvent event) {
        String[] options = new String[]{"on", "off", "help"};

        for (String s : options) { // Iterate through the available options for this command
            if (args != null && !args.isEmpty()) {
                if (optionCheck(args, s)) {
                    option = s;
                    return true;
                } else if (args.equals("help")) { // If the help argument is the only argument that is passed
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
                Connection connection = Database.getInstance().getConnection();
                Statement statement = connection.createStatement();
                String query = "UPDATE `guild` SET `isCompact` = " + intArg;
                Integer result = statement.executeUpdate(query);
                if (result.equals(1)) {
                    if (intArg.equals(0)) {
                        sendToChannel(event, Const.COMPACT_MODE_OFF);
                    } else {
                        sendToChannel(event, Const.COMPACT_MODE_ON);
                    }

                } else {
                    sendToChannel(event, Const.COMPACT_FAILURE);
                }
            } catch (IOException | SQLException | PropertyVetoException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void help(MessageReceivedEvent event) {
        sendToChannel(event, Const.COMPACT_HELP);
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {
        try {
            new Tracker("Compact");
        } catch (PropertyVetoException | IOException | SQLException e) {
            logger.warn("There was a problem tracking this command usage.");
        }
    }

    private boolean optionCheck(String args, String option) {
        return args.toLowerCase().substring(0, option.length()).equals(option);
    }
}
