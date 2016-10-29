/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import platform.discord.listener.DiscordListener;
import platform.generic.listener.PlatformListener;
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
    public static JDA jda;
    public static Database data;
    private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws PropertyVetoException, IOException, SQLException {
        // Verify the database is there on startup
        data = Database.getInstance();
        Database.checkDatabase();

        // Run mode~
        logger.info("Debug mode: " + debugMode());

        // Instantiate the JDA Object
        try {

            // TODO: Double check JDA docs IRT buildBlocking() vs buildAsync()
            jda = new JDABuilder()
                    .setAudioEnabled(false) // Turn off JDA audio support
                    .setBulkDeleteSplittingEnabled(false)
                    .setBotToken(PropReader.getInstance().getProp().getProperty("discord.token"))
                    .addListener(new DiscordListener())
                    //.useSharding(2, 3)
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

        new PlatformListener();
    }

    public static boolean debugMode() {
        return Boolean.parseBoolean(PropReader.getInstance().getProp().getProperty("mode.debug"));
    }
}
