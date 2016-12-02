/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package platform.discord.controller;

import core.Main;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.dv8tion.jda.core.requests.RestAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Const;
import util.DiscordLogger;
import util.database.Database;
import util.database.calls.Tracker;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static platform.generic.controller.PlatformController.*;
import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class DiscordController {

    private static final Logger logger = LoggerFactory.getLogger("Discord Controller");
    private static Connection connection;
    private static Connection nlConnection;
    private static Connection mhConnection;
    private static Connection ceConnection;
    private static Connection gciConnection;
    private static Connection gplConnection;
    private static Connection ccConnection;
    private static PreparedStatement nlStatement;
    private static PreparedStatement mhStatement;
    private static PreparedStatement ceStatement;
    private static PreparedStatement gciStatement;
    private static PreparedStatement gplStatement;
    private static PreparedStatement pStatement;
    private static String query;
    private static ResultSet gplResult;
    private static ResultSet mhResult;
    private static ResultSet ceResult;
    private static ResultSet gciResult;
    private static ResultSet nlResult;
    private static ResultSet result;
    private String guildIdMessageEvent;
    private String mentionedUsersId;

    public DiscordController(GuildMessageReceivedEvent event) {

        this.guildIdMessageEvent = event.getGuild().getId();
        mentionedUsersID(event);
    }

    public static void sendToChannel(GuildMessageReceivedEvent event, String message) {
        // if the bot doesn't have permissions to post in the channel (which shouldn't happen at this point, but is)
        try {
            // Try sending to the channel it was moved to
            event.getMessage().getChannel().sendMessage(message).queue(
                    success -> {
                        new DiscordLogger(message, event);
                        System.out.printf("[BOT -> GUILD] [%s:%s] [%s:%s] %s%n",
                                event.getGuild().getName(),
                                event.getGuild().getId(),
                                event.getChannel().getName(),
                                event.getChannel().getId(),
                                success.getContent());
                    },
                    failure -> System.out.printf("[~ERROR~] Unable to send message to %s:%s %s:%s.  Trying public " +
                                    "channel.%n",
                            event.getGuild().getName(),
                            event.getGuild().getId(),
                            event.getChannel().getName(),
                            event.getChannel().getId())

            );

        } catch (PermissionException ex) {
            // Try sending to the default channel
            event.getGuild().getPublicChannel().sendMessage(message).queue(
                    success -> {
                        new DiscordLogger(message, event);
                        System.out.printf("[BOT -> GUILD] [%s:%s] [%s:%s]: %s%n",
                                event.getGuild().getName(),
                                event.getGuild().getId(),
                                event.getGuild().getPublicChannel().getName(),
                                event.getGuild().getPublicChannel().getId(),
                                success.getContent());
                    },
                    failure -> System.out.printf("[~ERROR~] Unable to send message to %s:%s, Public Channel: %s:%s.%n",
                            event.getGuild().getName(),
                            event.getGuild().getId(),
                            event.getChannel().getName(),
                            event.getChannel().getId())
            );
        }
    }

    public static void sendToPm(GuildMessageReceivedEvent event, Message message) {
        event.getAuthor().openPrivateChannel().queue(
                success ->
                        event.getAuthor().getPrivateChannel().sendMessage(message).queue(
                                sentMessage -> System.out.printf("[BOT -> PM] [%s:%s] [%s:%s]: %s%n",
                                        event.getGuild().getName(),
                                        event.getGuild().getId(),
                                        event.getAuthor().getName(),
                                        event.getAuthor().getId(),
                                        sentMessage.getContent())
                        ),
                failure ->
                        System.out.printf("[~ERROR~] Unable to send PM to %s:%s, Author: %s:%s.%n",
                                event.getGuild().getName(),
                                event.getGuild().getId(),
                                event.getAuthor().getName(),
                                event.getAuthor().getId())
        );
    }

    public static void sendToPm(PrivateMessageReceivedEvent event, Message message) {
        if (!event.getAuthor().isBot()) { // Prevents errors if a bot auto-sends PM's on join
            event.getAuthor().openPrivateChannel().queue(
                    success ->
                            event.getAuthor().getPrivateChannel().sendMessage(message).queue(
                                    sentPM -> System.out.printf("[BOT -> PM] [%s:%s]: %s%n",
                                            event.getAuthor().getName(),
                                            event.getAuthor().getId(),
                                            sentPM.getContent())
                            ),
                    failure ->
                            System.out.printf("[~ERROR~] Unable to send PM to author: %s:%s.%n",
                                    event.getAuthor().getName(),
                                    event.getAuthor().getId())
            );
        }
    }


    public static synchronized void messageHandler(String guildId, String channelId, Integer platformId, String
            channelName, String streamTitle, String gameName, Integer online, String url, String thumbnail, String banner) {

        switch (online) {
            case 1: // Stream is online
                if (!checkStreamTable(guildId, platformId, channelName)) {
                    try {
                        announceStream(guildId, channelId, platformId, channelName, streamTitle, gameName, url,
                                thumbnail, banner);
                    } catch (Exception e) {
                        logger.error("There was an error announcing the stream.");
                        e.printStackTrace();
                    }
                }
                break;

            default: // Stream is offline
                if (checkStreamTable(guildId, platformId, channelName)) {
                    try {
                        mhConnection = Database.getInstance().getConnection();
                        query = "SELECT `cleanup`, `channelId` FROM `guild` WHERE `guildId` = ?";

                        mhStatement = mhConnection.prepareStatement(query);

                        mhStatement.setString(1, guildId);
                        mhResult = mhStatement.executeQuery();

                        while (mhResult.next()) {
                            String messageId = getMessageId(guildId, platformId, channelName);

                            // Grab the old message
                            if (messageId != null
                                    && Main.getJDA().getTextChannelById(channelId) != null
                                    && Main.getJDA().getTextChannelById(channelId).getMessageById(messageId) != null) {
                                RestAction<Message> oldMessage = Main.getJDA().getTextChannelById(channelId)
                                        .getMessageById(messageId);
                                switch (mhResult.getInt("cleanup")) {
                                    case 1: // Edit old message
                                        oldMessage.queue(
                                                success -> {
                                                    Message message = editMessage(platformId, channelName, url,
                                                            thumbnail, guildId, banner);

                                                    // Update the old message
                                                    try {
                                                        success.editMessage(message).queue(
                                                                editSuccess -> {
                                                                    // Remove the entry from the stream table
                                                                    deleteFromStream(guildId, platformId, channelName);

                                                                    new DiscordLogger(" :pencil2: " + channelName +
                                                                            " has gone offline. Message edited in G:"
                                                                            + Main.getJDA().getGuildById(guildId).getName(), null);
                                                                    System.out.printf("[OFFLINE STREAM] %s has gone offline. The " +
                                                                                    "announcement was successfully edited in: %s%n",
                                                                            channelName,
                                                                            Main.getJDA().getGuildById(guildId).getName());
                                                                }
                                                        );
                                                    } catch (UnsupportedOperationException ose) {
                                                        System.out.printf("[~ERROR~] Message has no content.  Cannot " +
                                                                        "send the edited message. G:%s:%s C:%s:%s, M:%s%n",
                                                                Main.getJDA().getGuildById(guildId).getName(),
                                                                guildId,
                                                                Main.getJDA().getTextChannelById(channelId).getName(),
                                                                channelId, messageId);
                                                    }
                                                });
                                        break;
                                    case 2: // Delete old message
                                        oldMessage.queue(
                                                success -> success.deleteMessage().queue(
                                                        deleteSuccess -> {
                                                            // Remove the entry from the stream table
                                                            deleteFromStream(guildId, platformId, channelName);

                                                            new DiscordLogger(" :x: " + channelName + " has gone" +
                                                                    " offline. Message deleted in G:" + Main.getJDA
                                                                    ().getGuildById(guildId).getName(), null);
                                                            System.out.printf("[OFFLINE STREAM] %s has gone offline. The " +
                                                                            "announcement was successfully deleted from: %s%n",
                                                                    channelName,
                                                                    Main.getJDA().getGuildById(guildId).getName());
                                                        }
                                                ),
                                                error -> {
                                                    new DiscordLogger(" :bangbang: " + channelName + " has gone " +
                                                            "offline. Error deleting message in G:" + Main.getJDA
                                                            ().getGuildById(guildId).getName(), null);
                                                    System.out.printf("[~ERROR~] %s has gone offline. There was an " +
                                                                    "error deleting it from guild: %s%n",
                                                            channelName,
                                                            Main.getJDA().getGuildById(guildId).getName());
                                                    error.printStackTrace();
                                                    deleteFromStream(guildId, platformId, channelName);
                                                });
                                        break;
                                    default:
                                        // No action taken, but still need to remove the entry from the stream table
                                        deleteFromStream(guildId, platformId, channelName);
                                        break;
                                }
                            } else {
                                deleteFromStream(guildId, platformId, channelName);
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (PermissionException pe) {
                        new DiscordLogger(" :no_entry: Permissions error in G:" + Main.getJDA
                                ().getGuildById(guildId).getName() + ".", null);

                        System.out.printf("[~ERROR~] There was a permission exception when trying to read message " +
                                        "history in G:%s, C:%s, M:%s%n",
                                Main.getJDA().getGuildById(guildId).getName(),
                                Main.getJDA().getTextChannelById(getChannelId(guildId)).getName(),
                                getMessageId(guildId, platformId, channelName));

                    } catch (NullPointerException npe) {
                        logger.error("There was a NPE when trying to edit/delete a message.",
                                npe.getMessage());
                        npe.printStackTrace();
                    } finally {
                        cleanUp(mhResult, mhStatement, mhConnection);
                    }
                }
                break;
        }

    }

    private static synchronized Message editMessage(Integer platformId, String channelName, String url, String
            thumbnail, String guildId, String banner) {
        EmbedBuilder eBuilder = new EmbedBuilder();
        StringBuilder msgDesc = new StringBuilder();
        MessageBuilder mBuilder = new MessageBuilder();

        float[] rgb;
        switch (platformId) {
            case 1:
                rgb = Color.RGBtoHSB(100, 65, 165, null);
                eBuilder.setColor(Color.getHSBColor(rgb[0], rgb[1], rgb[2]));
                break;
            case 2:
                rgb = Color.RGBtoHSB(83, 109, 254, null);
                eBuilder.setColor(Color.getHSBColor(rgb[0], rgb[1], rgb[2]));
                break;
            default:
                // Never should hit
                break;
        }

        eBuilder.setAuthor(Const.OFFLINE, Const.DISCORD_URL, Const.BOT_LOGO);

        eBuilder.setTitle(channelName + " has gone offline!");

        msgDesc.append("Make sure to watch for my next announcement of when they go live!");
        //msgDesc.append(url);

        eBuilder.setDescription(msgDesc.toString());

        if (thumbnail != null) {
            eBuilder.setThumbnail(thumbnail);
        }

        if (checkCompact(guildId).equals(0) && banner != null) {
            eBuilder.setImage(banner);
        }

        MessageEmbed embed = eBuilder.build();

        mBuilder.setEmbed(embed);

        return mBuilder.build();
    }

    private static synchronized void announceStream(String guildId, String channelId, Integer platformId, String
            channelName, String streamTitle, String gameName, String url, String thumbnail, String banner) {

        EmbedBuilder eBuilder = new EmbedBuilder();
        StringBuilder msgDesc = new StringBuilder();
        MessageBuilder mBuilder = new MessageBuilder();

        notifyLevel(guildId, mBuilder);
        // TODO: re-enable once emoji command is enabled
        //checkEmoji(guildId, message);

        float[] rgb;

        switch (platformId) {
            case 1:
                rgb = Color.RGBtoHSB(100, 65, 165, null);
                eBuilder.setColor(Color.getHSBColor(rgb[0], rgb[1], rgb[2]));
                break;
            case 2:
                rgb = Color.RGBtoHSB(83, 109, 254, null);
                eBuilder.setColor(Color.getHSBColor(rgb[0], rgb[1], rgb[2]));
                break;
            default:
                // Never should hit
                break;
        }

        eBuilder.setAuthor(Const.NOW_LIVE, Const.DISCORD_URL, Const.BOT_LOGO);

        //eBuilder.setTitle(channelName + " is streaming " + gameName + "!");

        msgDesc.append("**");
        msgDesc.append(channelName);
        msgDesc.append(" is streaming ");
        msgDesc.append(gameName);
        msgDesc.append("!**\n");
        msgDesc.append(streamTitle + "\n");
        msgDesc.append("Watch them here: " + url);

        eBuilder.setDescription(msgDesc.toString());
        if (thumbnail != null || !"".equals(thumbnail)) {
            eBuilder.setThumbnail(thumbnail);
        }

        if (checkCompact(guildId).equals(0)) {
            if (banner != null || !"".equals(banner)) {
                eBuilder.setImage(banner);
            }
        }

        MessageEmbed embed = eBuilder.build();

        mBuilder.setEmbed(embed);

        Message message = mBuilder.build();

        // If the channel doesn't exist, reset it to the default public channel which is the guildId
        if (Main.getJDA().getTextChannelById(channelId) == null) {
            try {
                if (Main.getJDA().getTextChannelById(guildId) != null) {
                    try {
                        connection = Database.getInstance().getConnection();
                        query = "UPDATE `guild` SET `channelId` = ? WHERE `guildId` = ?";
                        pStatement = connection.prepareStatement(query);
                        pStatement.setString(1, guildId);
                        pStatement.setString(2, guildId);
                        pStatement.executeUpdate();
                    } catch (SQLException e) {
                        logger.error("There was an SQL Exception", e);
                    } finally {
                        cleanUp(pStatement, connection);
                    }
                }
            } catch (NullPointerException npe) {
                logger.error("There was a NPE in Discord Controller");
            }
        }

        try {
            Main.getJDA().getTextChannelById(channelId).sendMessage(message).queue(
                    sentMessage -> {
                        // TODO: Fix this ugly mess!!
                        MessageBuilder discord = new MessageBuilder();

                        discord.appendString(" :tada: ");
                        discord.appendString("[G:");
                        discord.appendString(Main.getJDA().getGuildById(guildId).getName());
                        discord.appendString("][C:");
                        discord.appendString(Main.getJDA().getTextChannelById(channelId).getName());
                        discord.appendString("]");
                        discord.appendString(channelName);
                        discord.appendString(" is streaming ");
                        discord.appendString(gameName);

                        Message dMessage = discord.build();

                        addToStream(guildId, channelId, platformId, channelName, streamTitle, gameName, sentMessage
                                .getId());

                        new DiscordLogger(dMessage.getRawContent(), null);
                        System.out.printf("[STREAM ANNOUNCE] [%s:%s] [%s:%s] [%s]: %s%n",
                                Main.getJDA().getGuildById(guildId).getName(),
                                Main.getJDA().getGuildById(guildId).getId(),
                                Main.getJDA().getTextChannelById(getChannelId(guildId)).getName(),
                                Main.getJDA().getTextChannelById(getChannelId(guildId)).getId(),
                                sentMessage.getId(),
                                channelName + " is streaming " + gameName);

                        new Tracker("Streams Announced");
                    }
            );
        } catch (PermissionException pe) {
            new DiscordLogger(" :no_entry: Permission exception in G:" + Main.getJDA
                    ().getGuildById(guildId).getName() + ":" + guildId + ".", null);
            System.out.printf("[~ERROR~] Permission Exception! G:%s:%s C:%s:%s%n",
                    Main.getJDA().getGuildById(guildId).getName(),
                    guildId,
                    Main.getJDA().getTextChannelById(channelId).getName(),
                    channelId);
        }
    }

    private static synchronized Integer checkCompact(String guildId) {
        try {
            ccConnection = Database.getInstance().getConnection();
            query = "SELECT `isCompact` FROM `guild` WHERE `guildId` = ?";
            pStatement = ccConnection.prepareStatement(query);
            pStatement.setString(1, guildId);
            result = pStatement.executeQuery();

            while (result.next()) {
                Integer isCompact = result.getInt("isCompact");
                return isCompact;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(result, pStatement, ccConnection);
        }
        return -1;
    }

    private static synchronized MessageBuilder checkEmoji(String guildId, MessageBuilder message) {
        try {
            ceConnection = Database.getInstance().getConnection();
            query = "SELECT `emoji` FROM `guild` WHERE `guildId` = ?";
            ceStatement = ceConnection.prepareStatement(query);
            ceStatement.setString(1, guildId);
            ceResult = ceStatement.executeQuery();

            while (ceResult.next()) {
                if (ceResult.getString("emoji") != null) {
                    message.appendString(" ");
                    message.appendString(ceResult.getString("emoji"));
                    message.appendString(" ");
                    message.appendString(ceResult.getString("emoji"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(ceResult, ceStatement, ceConnection);
        }
        message.appendString("");
        return message;
    }

    public static synchronized String getChannelId(String guildId) {
        try {
            gciConnection = Database.getInstance().getConnection();
            query = "SELECT `channelId` FROM `guild` WHERE `guildId` = ?";
            gciStatement = gciConnection.prepareStatement(query);
            gciStatement.setString(1, guildId);

            gciResult = gciStatement.executeQuery();
            while (gciResult.next()) {
                String channelId = gciResult.getString("channelId");
                if (!"".equals(channelId)) {
                    return channelId;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(gciResult, gciStatement, gciConnection);
        }
        return "";
    }

    private static synchronized MessageBuilder notifyLevel(String guildId, MessageBuilder message) {
        try {
            nlConnection = Database.getInstance().getConnection();
            query = "SELECT `level`, `userId` FROM `notification` WHERE `guildId` = ?";
            nlStatement = nlConnection.prepareStatement(query);

            nlStatement.setString(1, guildId);
            nlResult = nlStatement.executeQuery();

            while (nlResult.next()) {
                switch (nlResult.getInt("level")) {
                    case 1: // User wants a @User mention
                        String userId = nlResult.getString("userId");
                        User user = Main.getJDA().getUserById(userId);
                        message.appendString("Hey ");
                        message.appendMention(user);
                        message.appendString("! Check out this streamer that just went live!");
                        break;
                    case 2: // User wants @here mention
                        message.appendString("Hey ");
                        message.appendHereMention();
                        message.appendString("! Check out this streamer that just went live!");
                        break;
                    case 3: // User wants @everyone mention
                        message.appendString("Hey ");
                        message.appendEveryoneMention();
                        message.appendString("! Check out this streamer that just went live!");
                        break;
                    default:
                        // No mention
                        break;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(nlResult, nlStatement, nlConnection);
        }
        return message;
    }

    private void mentionedUsersID(GuildMessageReceivedEvent event) {
        for (User u : event.getMessage().getMentionedUsers()) {
            this.mentionedUsersId = u.getId();
        }
    }

    public final String getMentionedUsersId() {
        return this.mentionedUsersId;
    }

    public final String getGuildId() {
        return this.guildIdMessageEvent;
    }
}
