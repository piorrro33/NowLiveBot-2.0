/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import core.commands.Add;
import core.commands.Compact;
import core.commands.Invite;
import core.commands.Ping;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import util.Const;

import java.util.HashMap;
import java.util.logging.Logger;

/**
 * @author keesh
 */
public class CommandParser {
    private static final Logger LOG = Logger.getLogger(CommandParser.class.getName());

    public static HashMap<String, Command> commands = new HashMap<>();

    public CommandParser() {

        /**
         * Register core.commands with the bot
         */
        commands.put("ping", new Ping());
        commands.put("invite", new Invite());
        commands.put("add", new Add());
        commands.put("compact", new Compact());

        // ping check
        // slice and dice the command into an array
        // pass the array to parser
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
     * @param cmd
     */
    public static void handleCommand(CommandParser.CommandContainer cmd) {
        System.out.println("Made it into handleCommand()");

        System.out.println("\nVariables inside handleCommand():");
        System.out.println("invoke: " + cmd.invoke);
        System.out.println("args: " + cmd.args);
        System.out.println("event: " + cmd.event);

        if (getCommands().containsKey(cmd.invoke)) {
            System.out.println("cmd.args as a string: " + cmd.args + "\n");

            boolean safe = getCommands().get(cmd.invoke).called(cmd.args, cmd.event);

            if (safe) {
                // DEBUG STATEMENT: Remove in production
                System.out.println("Boolean 'safe' is " + safe + ".\n");
                System.out.println("cmd.args: " + cmd.args);
                //System.out.println("cmd.args.length(): " + cmd.args.length());
                // TODO: Match the capitalisation of ping and return in pong
                if (cmd.args != null && cmd.args.equals("help")) {
                    // DEBUG STATEMENT: Remove in production
                    System.out.println("Asked for help with the command: " + cmd.invoke + "\n");
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
        // Remove COMMAND_PREFIX
        String beheaded = raw.replaceFirst(Const.COMMAND_PREFIX, "");
        System.out.println("beheaded inside parser method: " + beheaded);

        String removeCommand = null;
        String invoke = null;
        String args = null;

        if (beheaded.contains(" ")) {
            removeCommand = beheaded.substring(beheaded.indexOf(" ") + 1); // Remove Const.COMMAND {add opt opt}
            Integer spaceIndex = removeCommand.indexOf(" ");
            System.out.println("spaceIndex: " + spaceIndex);
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

        System.out.println("\nVariables inside parse():");
        System.out.println("removeCommand: " + removeCommand);
        System.out.println("invoke: " + invoke);
        System.out.println("args: " + args);

        return new CommandContainer(raw, invoke, args, event);
    }

    public static class CommandContainer {

        public final String raw;
        public final String invoke;
        public final String args;
        public final MessageReceivedEvent event;

        public CommandContainer(String rw, String invoke, String args, MessageReceivedEvent event) {
            this.raw = rw;
            this.invoke = invoke; // The Command
            this.args = args; // Command Arguments
            this.event = event; // The Event
        }
    }
}
