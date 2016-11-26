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
import net.dv8tion.jda.core.entities.Guild;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static util.database.Database.cleanUp;

/**
 * @author Veteran Software
 * @version 1.0
 * @since 09/28/2016
 */
public class Main {

    public static final CommandParser parser = new CommandParser();
    private static JDA jda;
    private static Logger logger = LoggerFactory.getLogger("Main");
    private static Connection connection;
    private static PreparedStatement pStatement;
    private static ResultSet result;
    private static Integer resultInt;
    private static List<String> tableList = new ArrayList<>();

    public static JDA getJDA() {
        return jda;
    }

    public static void main(String[] args) throws PropertyVetoException, IOException, SQLException {
        // Verify the database is there on startup
        Database.checkDatabase();
        //guildCheck();

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

    private static void guildCheck() {
        String query = "SELECT * FROM `guild`";
        connection = Database.getInstance().getConnection();
        try {
            pStatement = connection.prepareStatement(query);
            result = pStatement.executeQuery();

            while (result.next()) {
                System.out.println(result.getString("guildId"));
                Guild guildId = jda.getGuildById(result.getString("guildId"));
                if (guildId == null) {
                    tableList.add("channel");
                    tableList.add("game");
                    tableList.add("guild");
                    tableList.add("manager");
                    tableList.add("notification");
                    tableList.add("permission");
                    tableList.add("stream");
                    tableList.add("tag");
                    tableList.add("team");

                    try {
                        connection = Database.getInstance().getConnection();
                        for (String s : tableList) {
                            query = "DELETE FROM `" + s + "` WHERE `guildId` = ?";
                            pStatement = connection.prepareStatement(query);
                            pStatement.setString(1, result.getString("guildid"));
                            resultInt = pStatement.executeUpdate();
                            if (!resultInt.equals(0)) {
                                logger.info("Successfully deleted all data for Guild " + result.getString("guildid")
                                        + " from the " + s.toUpperCase() + " table.");
                            }
                        }

                    } catch (Exception e) {
                        logger.error("Failed to remove info from Guild " + result.getString("guildid") + ".");
                    } finally {
                        cleanUp(pStatement, connection);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(result, pStatement, connection);
        }
    }
}
