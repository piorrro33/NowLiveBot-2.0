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

        // Run mode~
        System.out.printf("Debug mode: %s%n", debugMode());

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

        guildCheck();

        new PlatformListener();
    }

    public static boolean debugMode() {
        return Boolean.parseBoolean(PropReader.getInstance().getProp().getProperty("mode.debug"));
    }

    private static void guildCheck() {

        try {
            connection = Database.getInstance().getConnection();
            String query = "SELECT `guildId` from `guild`";
            pStatement = connection.prepareStatement(query);
            result = pStatement.executeQuery();

            List<Guild> guildList = jda.getGuilds();

            System.out.println(guildList);

            while (result.next()) {
                Integer found = 0;
                for (Guild guild : guildList) {
                    if (result.getString("guildId").equals(guild.getId())) {
                        found++; // Will be 1 if the guildId in the database is a valid guild the bot is in
                    } // i:found will be 0 if the guildId in the database is not a valid guild the bot is in
                }

                if (found.equals(0)) { // the bot is not in that guild, so remove all DB entries for it

                    String guildId = result.getString("guildId");

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
                        for (String table : tableList) {
                            query = "DELETE FROM `" + table + "` WHERE `guildId` = ?";
                            if (connection.isClosed()) {
                                connection = Database.getInstance().getConnection();
                            }
                            pStatement = connection.prepareStatement(query);
                            pStatement.setString(1, guildId);
                            resultInt = pStatement.executeUpdate();
                            if (resultInt > 0) {
                                System.out.printf("[SYSTEM] Successfully deleted all data for G:%s in db table %s%n",
                                        guildId,
                                        table);
                            } else {
                                System.out.printf("[SYSTEM] No data present for G:%s in db table %s%n",
                                        guildId,
                                        table);
                            }
                        }

                    } catch (Exception e) {
                        logger.error("Failed to remove info from Guild " + guildId + ".");
                        e.printStackTrace();
                    } finally {
                        cleanUp(pStatement, connection);
                    }
                    System.out.printf("[SYSTEM] All data removed for G:%s.%n",
                            guildId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
