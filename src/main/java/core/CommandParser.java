/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import core.commands.*;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Const;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * @author keesh
 */
public class CommandParser {
    public static HashMap<String, Command> commands = new HashMap<>();
    private static Logger logger = LoggerFactory.getLogger(CommandParser.class);

    CommandParser() {

        // Register core.commands with the bot
        commands.put("add", new Add());
        commands.put("announce", new Announce());
        commands.put("compact", new Compact());
        commands.put("help", new Help());
        commands.put("invite", new Invite());
        commands.put("move", new Move());
        commands.put("ping", new Ping());
        commands.put("remove", new Remove());
        commands.put("streams", new Streams());
    }

    /**
     * @return the core.commands
     */
    public static HashMap<String, Command> getCommands() {

        return commands;
    }

    /**
     * @param aCommands the core.commands to set
     */
    public static void setCommands(HashMap<String, Command> aCommands) {

        commands = aCommands;
    }

    /**
     * @param cmd Object containing required arguments to invoke the command
     */
    public static void handleCommand(CommandParser.CommandContainer cmd) throws PropertyVetoException, IOException, SQLException {

        logger.debug("\nVariables inside CommandContainer():");
        logger.debug("cmd.invoke: " + cmd.invoke);
        logger.debug("cmd.args: " + cmd.args);

        if (getCommands().containsKey(cmd.invoke)) {

            boolean safe = getCommands().get(cmd.invoke).called(cmd.args, cmd.event);

            if (safe) {
                // DEBUG STATEMENT: Remove in production
                logger.debug("Boolean 'safe' is " + safe + ".\n");
                logger.debug("cmd.args: " + cmd.args);

                // TODO: Match the capitalisation of ping and return in pong

                if (cmd.args != null && cmd.args.equals("help")) {
                    getCommands().get(cmd.invoke).help(cmd.event);
                } else {
                    getCommands().get(cmd.invoke).action(cmd.args, cmd.event);
                }
                getCommands().get(cmd.invoke).executed(safe, cmd.event);
            } else {
                // Send error message stating that the command wasn't formatted properly.
                // Possibly just send the help info.
            }
        }
    }

    public CommandContainer parse(String raw, MessageReceivedEvent event) {
        String beheaded = raw.replaceFirst(Const.COMMAND_PREFIX, "");  // Remove COMMAND_PREFIX

        String removeCommand;
        String invoke = null;
        String args = null;

        if (beheaded.contains(" ")) {
            removeCommand = beheaded.substring(beheaded.indexOf(" ") + 1); // Remove Const.COMMAND {add opt opt}

            if (removeCommand.contains(" ")) {
                invoke = removeCommand.substring(0, removeCommand.indexOf(" ")); // Return just the command
                args = removeCommand.substring(removeCommand.indexOf(" ") + 1);
            } else {
                // Send to commands with no args
                invoke = removeCommand;
            }
        } else if (beheaded.equals("ping")) {
            invoke = beheaded;
            args = null;
        } else {
            event.getTextChannel().sendMessage(Const.EMPTY_COMMAND);
        }

        return new CommandContainer(raw, invoke, args, event);
    }

    private static class CommandContainer {

        public final String args;
        public final MessageReceivedEvent event;
        final String raw;
        final String invoke;

        CommandContainer(String rw, String invoke, String args, MessageReceivedEvent event) {
            this.raw = rw;
            this.invoke = invoke.toLowerCase(); // The Command (ensure the command is always passes as lowercase)
            this.args = args; // Command Arguments
            this.event = event; // The Event
        }
    }
}
