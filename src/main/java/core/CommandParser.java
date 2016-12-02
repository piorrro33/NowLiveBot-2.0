/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package core;

import core.commands.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import util.Const;
import util.database.calls.CheckPerms;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static platform.discord.controller.DiscordController.sendToChannel;

/**
 * @author keesh
 */
public class CommandParser {
    private static Map<String, Command> commands = new HashMap<>();

    private static CheckPerms perms = new CheckPerms();

    CommandParser() {

        // Register core.commands with the bot
        commands.put("add", new Add());
        commands.put("announce", new Announce());
        commands.put("beam", new Beam());
        commands.put("cleanup", new CleanUp());
        commands.put("compact", new Compact());
        commands.put("help", new Help());
        commands.put("list", new List());
        commands.put("invite", new Invite());
        commands.put("move", new Move());
        commands.put("notify", new Notify());
        commands.put("permissions", new Permissions());
        commands.put("ping", new Ping());
        commands.put("remove", new Remove());
        commands.put("status", new Status());
        commands.put("streamlang", new Language());
        commands.put("streams", new Streams());
        commands.put("twitch", new Twitch());
    }

    /**
     * @return the core.commands
     */
    public static Map<String, Command> getCommands() {

        return commands;
    }

    /**
     * @param aCommands the core.commands to set
     */
    public static void setCommands(final Map<String, Command> aCommands) {

        commands = aCommands;
    }

    /**
     * @param cmd Object containing required arguments to invoke the command
     */
    public static void handleCommand(CommandContainer cmd) throws PropertyVetoException, IOException,
            SQLException {

        if (getCommands().containsKey(cmd.invoke)) {

            // Check and see if the command requires elevated permissions and how to handle that
            Boolean adminCheck = perms.checkAdmins(cmd.event);
            Boolean managerCheck = perms.checkManager(cmd.event);
            switch (cmd.invoke) {
                case "announce":
                    if (adminCheck) {
                        runCommand(cmd);
                    } else {
                        sendToChannel(cmd.event, Const.NOT_AN_ADMIN);
                    }
                    break;
                case "add":
                case "beam":
                case "cleanup":
                case "lang":
                case "move":
                case "notify":
                case "streamlang":
                case "twitch":
                    if (managerCheck || adminCheck) {
                        if (adminCheck) {
                            sendToChannel(cmd.event, Const.ADMIN_OVERRIDE);
                        }
                        runCommand(cmd);
                    } else {
                        sendToChannel(cmd.event, Const.NOT_A_MANAGER);
                    }
                    break;
                default:
                    if (!cmd.invoke.equalsIgnoreCase("announce")) {
                        runCommand(cmd);
                    }
                    break;
            }
        } else {
            sendToChannel(cmd.event, Const.WRONG_COMMAND);
        }
    }

    private static void runCommand(CommandContainer cmd) {
        boolean safe = getCommands().get(cmd.invoke).called(cmd.args, cmd.event);

        if (safe) {
            if (cmd.args != null && cmd.args.equals("help")) {
                getCommands().get(cmd.invoke).help(cmd.event);
            } else {
                getCommands().get(cmd.invoke).action(cmd.args, cmd.event);
            }
        } else {
            sendToChannel(cmd.event, Const.INCORRECT_ARGS);
        }
        getCommands().get(cmd.invoke).executed(safe, cmd.event);
    }

    public final CommandContainer parse(String raw, GuildMessageReceivedEvent event) {
        String beheaded = raw.replaceFirst(Const.COMMAND_PREFIX, "");  // Remove COMMAND_PREFIX

        String removeCommand;
        String invoke = "";
        String args = "";

        if (beheaded.contains(" ")) {
            removeCommand = beheaded.substring(beheaded.indexOf(' ') + 1); // Remove Const.COMMAND {add opt opt}

            if (removeCommand.contains(" ")) {
                invoke = removeCommand.substring(0, removeCommand.indexOf(' ')); // Return just the command
                args = removeCommand.substring(removeCommand.indexOf(' ') + 1);
            } else {
                // Send to commands with no args
                invoke = removeCommand;
            }
        } else if ("ping".equals(beheaded)) {
            invoke = beheaded;
            args = "";
        } else {
            sendToChannel(event, Const.EMPTY_COMMAND);
        }

        return new CommandContainer(invoke, args, event);
    }

    private static class CommandContainer {

        private final String args;
        private final GuildMessageReceivedEvent event;
        private final String invoke;

        CommandContainer(String passedInvoke, String passedArgs, GuildMessageReceivedEvent passedEvent) {
            this.invoke = passedInvoke.toLowerCase(); // The Command (ensure the command is always passes as lowercase)
            this.args = passedArgs; // Command Arguments
            this.event = passedEvent; // The Event
        }
    }
}
