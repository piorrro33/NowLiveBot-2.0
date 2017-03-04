/*
 * Copyright 2016-2017 Ague Mort of Veteran Software
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package core;

import core.commands.*;
import langs.LocaleString;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import util.Const;
import util.database.calls.CheckPerms;

import java.util.HashMap;
import java.util.Map;

import static platform.discord.controller.DiscordController.sendToChannel;

/**
 * @author Veteran Software by Ague Mort
 */
public class CommandParser {
    private final static CheckPerms perms = new CheckPerms();
    private static Map<String, Command> commands = new HashMap<>();

    CommandParser() {

        // Register core.commands with the bot
        commands.put("add", new Add());
        commands.put("announce", new Announce());
        commands.put("beam", new Beam());
        commands.put("botlang", new BotLanguage());
        commands.put("cleanup", new CleanUp());
        commands.put("compact", new Compact());
        commands.put("help", new Help());
        commands.put("list", new List());
        commands.put("invite", new Invite());
        commands.put("kappa", new Kappa());
        commands.put("leave", new Leave());
        commands.put("move", new Move());
        commands.put("notify", new Notify());
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
     * @param cmd Object containing required arguments to invoke the command
     */
    public static void handleCommand(CommandContainer cmd) {

        if (getCommands().containsKey(cmd.invoke)) {

            // Check and see if the command requires elevated permissions and how to handle that
            Boolean adminCheck = perms.checkAdmins(cmd.event);
            Boolean managerCheck = perms.checkManager(cmd.event);
            switch (cmd.invoke) {
                case "announce":
                case "leave":
                case "kappa":
                    if (adminCheck) {
                        runCommand(cmd);
                    } else {
                        sendToChannel(cmd.event, LocaleString.getString(cmd.event.getMessage().getGuild().getId(), "notAnAdmin"));
                    }
                    break;
                case "add":
                case "beam":
                case "botlang":
                case "cleanup":
                case "lang":
                case "move":
                case "notify":
                case "remove":
                case "streamlang":
                case "twitch":
                    if (managerCheck || adminCheck || cmd.event.getAuthor().getId().equals("146275186142871552")) {
                        if (adminCheck) {
                            sendToChannel(cmd.event, LocaleString.getString(cmd.event.getMessage().getGuild().getId(), "adminOverride"));
                        }
                        runCommand(cmd);
                    } else {
                        sendToChannel(cmd.event, LocaleString.getString(cmd.event.getMessage().getGuild().getId(), "notAManager"));
                    }
                    break;
                default:
                    if (!cmd.invoke.equalsIgnoreCase("announce")) {
                        runCommand(cmd);
                    }
                    break;
            }
        } else {
            sendToChannel(cmd.event, LocaleString.getString(cmd.event.getMessage().getGuild().getId(), "wrongCommand"));
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
            sendToChannel(event, LocaleString.getString(event.getMessage().getGuild().getId(), "emptyCommand"));
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
