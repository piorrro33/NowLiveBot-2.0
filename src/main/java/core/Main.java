/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import com.mb3364.twitch.api.Twitch;
import commands.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;
import platform.discord.listener.DiscordListener;
import util.CommandParser;
import util.Const;

/**
 * @author Veteran Software
 * @version 1.0
 * @since 09/28/2016
 */
public class Main {

    public static final CommandParser parser = new CommandParser();
    private static final Logger LOG = Logger.getLogger(Main.class.getName());
    private static HashMap<String, Command> commands = new HashMap<>();

    public static void main(String[] args) {

        /**
         * Register commands with the bot
         */
        commands.put("ping", new CommandPing());
        commands.put("invite", new CommandInvite());
        commands.put("add", new CommandAdd());

        /**
         * Instantiate the JDA Object This 'try' block keeps the bot in the Guild.
         */
        try {
            DiscordListener discordListener = new DiscordListener();

            Twitch twitchListener = new Twitch();

            JDA jda = new JDABuilder()
                    .setAutoReconnect(true)// Ensure JDA auto-reconnects
                    .setAudioEnabled(false)// Turn off JDA audio support
                    .setBulkDeleteSplittingEnabled(false)
                    .setBotToken(Const.DISCORD_BOT_TOKEN)
                    .addListener(discordListener)
                    //.addListener(twitchListener)
                    .buildBlocking();
            jda.getAccountManager().setGame("#ReckingCrew4Life");// Set the 'Playing...'
            jda.getAccountManager().update();// Must call '.update()' in order for this to work.
        } catch (LoginException | IllegalArgumentException | InterruptedException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the commands
     */
    public static HashMap<String, Command> getCommands() {
        return commands;
    }

    /**
     * @param aCommands the commands to set
     */
    public static void setCommands(HashMap<String, Command> aCommands) {
        commands = aCommands;
    }

    /**
     * @param cmd
     */
    public static void handleCommand(CommandParser.CommandContainer cmd) {
        if (getCommands().containsKey(cmd.invoke)) {
            boolean safe = getCommands().get(cmd.invoke).called(cmd.args, cmd.event);
            if (safe) {
                // TODO: Move help check to CommandXxxxx#called
                // DEBUG STATEMENT: Remove in production
                System.out.println("Boolean 'safe' is true.\n");
                System.out.println("cmd.args as a string: " + Arrays.toString(cmd.args) + "\n");
                if (Arrays.toString(cmd.args).equals("[help]")) {
                    // DEBUG STATEMENT: Remove in production
                    System.out.println("Asked for help with the command: " + cmd.invoke + "\n");
                    getCommands().get(cmd.invoke).help(cmd.event);
                } else {
                    getCommands().get(cmd.invoke).action(cmd.args, cmd.event);
                    getCommands().get(cmd.invoke).executed(safe, cmd.event);
                }
            } else {
                getCommands().get(cmd.invoke).executed(safe, cmd.event);
            }
        }
    }
}
