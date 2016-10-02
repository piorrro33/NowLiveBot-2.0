/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import com.mb3364.twitch.api.Twitch;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;
import platform.discord.listener.DiscordListener;
import util.Const;

import javax.security.auth.login.LoginException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Veteran Software
 * @version 1.0
 * @since 09/28/2016
 */
public class Main {

    public static final CommandParser parser = new CommandParser();
    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {

        /**
         * Instantiate the JDA Object This 'try' block keeps the bot in the Guild.
         */
        try {
            DiscordListener discordListener = new DiscordListener();

            Twitch twitchListener = new Twitch();

            JDA jda = new JDABuilder()
                    .setAutoReconnect(true) // Ensure JDA auto-reconnects
                    .setAudioEnabled(false) // Turn off JDA audio support
                    .setBulkDeleteSplittingEnabled(false)
                    .setBotToken(Const.DISCORD_BOT_TOKEN)
                    .addListener(discordListener)
                    //.addListener(twitchListener)
                    .buildBlocking();
            jda.getAccountManager().setGame("#ReckingCrew4Life"); // Set the 'Playing...'
            jda.getAccountManager().update(); // Must call '.update()' in order for this to work.
        } catch (LoginException | IllegalArgumentException | InterruptedException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
