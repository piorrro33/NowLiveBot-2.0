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
import platform.twitch.controller.TwitchController;
import util.Const;
import util.PropReader;
import util.database.Database;
import util.database.calls.GetDbChannels;
import util.database.calls.UpdateChannelId;

import javax.security.auth.login.LoginException;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
    private static List<String> tableList = new CopyOnWriteArrayList<>();

    public static void setJda(JDA jda) {
        Main.jda = jda;
    }

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
        convertChannelsToIds();

        new PlatformListener();
    }

    public static boolean debugMode() {
        return Boolean.parseBoolean(PropReader.getInstance().getProp().getProperty("mode.debug"));
    }

    private static void guildCheck() {

        try {
            String query = "SELECT `guildId` FROM `guild`";
            if (connection == null || connection.isClosed()) {
                connection = Database.getInstance().getConnection();
            }
            pStatement = connection.prepareStatement(query);
            result = pStatement.executeQuery();

            List<Guild> guildList = jda.getGuilds();

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
                        for (String table : tableList) {
                            query = "DELETE FROM `" + table + "` WHERE `guildId` = ?";

                            if (connection == null || connection.isClosed()) {
                                connection = Database.getInstance().getConnection();
                            }
                            pStatement = connection.prepareStatement(query);
                            pStatement.setString(1, guildId);
                            pStatement.executeUpdate();
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
        } finally {
            cleanUp(result, pStatement, connection);
        }
    }

    private static void convertChannelsToIds() {
        GetDbChannels getDbChannels = new GetDbChannels();
        List<String> channels = getDbChannels.fetch(-1);

        if (channels != null) {
            channels.forEach(channel -> {
                TwitchController twitchController = new TwitchController();
                String channelId = twitchController.convertNameToId(channel);

                System.out.println(channelId);

                UpdateChannelId updateChannelId = new UpdateChannelId();
                updateChannelId.executeUpdate(channelId, channel);
            });
        }
    }
}
