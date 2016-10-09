/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

//import com.mb3364.twitch.api.Twitch;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import platform.discord.listener.DiscordListener;
import util.Const;
import util.PropReader;
import util.database.Database;

import javax.security.auth.login.LoginException;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.SQLException;

/**
 * @author Veteran Software
 * @version 1.0
 * @since 09/28/2016
 */
public class Main {

    public static final CommandParser parser = new CommandParser();
    private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws PropertyVetoException, IOException, SQLException {
        // Verify the database is there on startup
        Database.getInstance();
        Database.checkDatabase();

        // Run mode~
        logger.info("Debug mode: " + debugMode());

        // Instantiate the JDA Object
        try {

            DiscordListener discordListener = new DiscordListener();
            //Twitch twitchListener = new Twitch();

            JDA jda = new JDABuilder()
                    .setAutoReconnect(true) // Ensure JDA auto-reconnects
                    .setAudioEnabled(false) // Turn off JDA audio support
                    .setBulkDeleteSplittingEnabled(false)
                    .setBotToken(PropReader.getInstance().getProp().getProperty("discord.token"))
                    .addListener(discordListener)
                    //.addListener(twitchListener)
                    .buildBlocking();
            jda.getAccountManager().setGame(Const.PLAYING); // Set the 'Playing...'
            jda.getAccountManager().update(); // Must call '.update()' in order for this to work.
        } catch (LoginException ex) {
            logger.error("JDA Login failed. Bot token incorrect.", ex);
        } catch (IllegalArgumentException ex) {
            logger.error("JDA login failed. Bot token invalid.", ex);
        } catch (InterruptedException ex) {
            logger.error("InterruptedException", ex);
        }
    }

    public static boolean debugMode() {
        return Boolean.parseBoolean(PropReader.getInstance().getProp().getProperty("mode.debug"));
    }
}
