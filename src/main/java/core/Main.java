/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
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
    private static JDA jda;
    private static Logger logger = LoggerFactory.getLogger("Main");

    public static JDA getJDA() {
        return jda;
    }

    public static void main(String[] args) throws PropertyVetoException, IOException, SQLException {
        // Verify the database is there on startup
        Database.checkDatabase();

        // Run mode~
        logger.info("Debug mode: " + debugMode());

        // Instantiate the JDA Object
        try {
            jda = new JDABuilder(AccountType.BOT)
                    .setToken(PropReader.getInstance().getProp().getProperty("discord.token"))
                    .addListener(new DiscordListener())
                    .setAudioEnabled(false)
                    .setBulkDeleteSplittingEnabled(false)
                    .buildBlocking();
            jda.getPresence().setGame(Game.of(Const.PLAYING));
        } catch (LoginException e) {
            logger.error("JDA Login failed. Bot token incorrect.", e);
        } catch (IllegalArgumentException e) {
            logger.error("JDA login failed. Bot token invalid.", e);
        } catch (InterruptedException e) {
            logger.error("InterruptedException", e);
        } catch (RateLimitedException e) {
            logger.error("Uh, oh...  We got rate limited.", e);
        }

        new PlatformListener();
    }

    public static boolean debugMode() {
        return Boolean.parseBoolean(PropReader.getInstance().getProp().getProperty("mode.debug"));
    }
}
