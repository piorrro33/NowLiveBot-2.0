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
import platform.twitch.listener.TwitchListener;
import util.Const;

import java.util.*;
import java.util.logging.Level;
import javax.security.auth.login.LoginException;
import java.time.LocalDateTime;

/**
 *
 * @author Veteran Software
 * @version 1.0
 * @since 09/28/2016
 */
public class Main {

    public static void main(String[] args) {

        DiscordListener discordListener = new DiscordListener();

        Twitch twitchListener = new Twitch();

        /**
         * Instantiate the JDA Object This 'try' block keeps the bot in the Guild.
         */
        try {
            JDA jda = new JDABuilder()
                    .setAudioEnabled(false)
                    .setBulkDeleteSplittingEnabled(false)
                    .setBotToken(Const.DISCORD_BOT_TOKEN)
                    .addListener(discordListener)
                    .addListener(twitchListener)
                    .buildBlocking();
        } catch (LoginException | IllegalArgumentException | InterruptedException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
