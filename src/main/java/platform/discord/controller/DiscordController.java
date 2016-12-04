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
import platform.discord.util.AnnounceStream;
import util.Const;
import util.DiscordLogger;
import util.database.Database;
import util.database.calls.CheckCompact;
import util.database.calls.CheckStreamTable;
import util.database.calls.GetChannelId;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static platform.generic.controller.PlatformController.deleteFromStream;
import static platform.generic.controller.PlatformController.getMessageId;
import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class DiscordController {

    private static final Logger logger = LoggerFactory.getLogger("Discord Controller");
    private static Connection mhConnection;
    private static Connection ceConnection;
    private static PreparedStatement mhStatement;
    private static PreparedStatement ceStatement;
    private static String query;
    private static ResultSet mhResult;
    private static ResultSet ceResult;
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


    public static synchronized void messageHandler(Map<String, String> args, Integer platformId, Integer online) {

        switch (online) {
            case 1: // Stream is online
                if (!new CheckStreamTable().action(args.get("guildId"), platformId, args.get("channelName"))) {
                    try {
                        new AnnounceStream().action(args, platformId);
                    } catch (Exception e) {
                        logger.error("There was an error announcing the stream.");
                        e.printStackTrace();
                    }
                }
                break;

            default: // Stream is offline
                if (new CheckStreamTable().action(args.get("guildId"), platformId, args.get("channelName"))) {
                    try {
                        mhConnection = Database.getInstance().getConnection();
                        query = "SELECT `cleanup`, `channelId` FROM `guild` WHERE `guildId` = ?";

                        mhStatement = mhConnection.prepareStatement(query);

                        mhStatement.setString(1, args.get("guildId"));
                        mhResult = mhStatement.executeQuery();

                        while (mhResult.next()) {
                            String messageId = getMessageId(args.get("guildId"), platformId, args.get("channelName"));

                            // Grab the old message
                            if (messageId != null
                                    && Main.getJDA().getTextChannelById(args.get("channelId")) != null
                                    && Main.getJDA().getTextChannelById(args.get("channelId")).getMessageById(messageId) != null) {

                                RestAction<Message> oldMessage = Main.getJDA().getTextChannelById(args.get("channelId"))
                                        .getMessageById(messageId);

                                switch (mhResult.getInt("cleanup")) {
                                    case 1: // Edit old message
                                        oldMessage.queue(
                                                success -> {
                                                    Message message = editMessage(args, platformId);

                                                    // Update the old message
                                                    try {
                                                        success.editMessage(message).queue(
                                                                editSuccess -> {
                                                                    // Remove the entry from the stream table
                                                                    deleteFromStream(args.get("guildId"), platformId, args.get("channelName"));

                                                                    new DiscordLogger(" :pencil2: " + args.get("channelName") +
                                                                            " has gone offline. Message edited in G:"
                                                                            + Main.getJDA().getGuildById(args.get("guildId")).getName(), null);
                                                                    System.out.printf("[OFFLINE STREAM] %s has gone offline. The " +
                                                                                    "announcement was successfully edited in: %s%n",
                                                                            args.get("channelName"),
                                                                            Main.getJDA().getGuildById(args.get("guildId")).getName());
                                                                }
                                                        );
                                                    } catch (UnsupportedOperationException ose) {
                                                        System.out.printf("[~ERROR~] Message has no content.  Cannot " +
                                                                        "send the edited message. G:%s:%s C:%s:%s, M:%s%n",
                                                                Main.getJDA().getGuildById(args.get("guildId")).getName(),
                                                                args.get("guildId"),
                                                                Main.getJDA().getTextChannelById(args.get("channelId")).getName(),
                                                                args.get("channelId"), messageId);
                                                    }
                                                });
                                        break;
                                    case 2: // Delete old message
                                        oldMessage.queue(
                                                success -> success.deleteMessage().queue(
                                                        deleteSuccess -> {
                                                            // Remove the entry from the stream table
                                                            deleteFromStream(args.get("guildId"), platformId, args.get("channelName"));

                                                            new DiscordLogger(" :x: " + args.get("channelName") + " has gone" +
                                                                    " offline. Message deleted in G:" + Main.getJDA
                                                                    ().getGuildById(args.get("guildId")).getName(), null);
                                                            System.out.printf("[OFFLINE STREAM] %s has gone offline. The " +
                                                                            "announcement was successfully deleted from: %s%n",
                                                                    args.get("channelName"),
                                                                    Main.getJDA().getGuildById(args.get("guildId")).getName());
                                                        }
                                                ),
                                                error -> {
                                                    new DiscordLogger(" :bangbang: " + args.get("channelName") + " has gone " +
                                                            "offline. Error deleting message in G:" + Main.getJDA
                                                            ().getGuildById(args.get("guildId")).getName(), null);
                                                    System.out.printf("[~ERROR~] %s has gone offline. There was an " +
                                                                    "error deleting it from guild: %s%n",
                                                            args.get("channelName"),
                                                            Main.getJDA().getGuildById(args.get("guildId")).getName());
                                                    error.printStackTrace();
                                                    deleteFromStream(args.get("guildId"), platformId, args.get("channelName"));
                                                });
                                        break;
                                    default:
                                        // No action taken, but still need to remove the entry from the stream table
                                        deleteFromStream(args.get("guildId"), platformId, args.get("channelName"));
                                        break;
                                }
                            } else {
                                deleteFromStream(args.get("guildId"), platformId, args.get("channelName"));
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (PermissionException pe) {
                        new DiscordLogger(" :no_entry: Permissions error in G:" + Main.getJDA
                                ().getGuildById(args.get("guildId")).getName() + ".", null);

                        System.out.printf("[~ERROR~] There was a permission exception when trying to read message " +
                                        "history in G:%s, C:%s, M:%s%n",
                                Main.getJDA().getGuildById(args.get("guildId")).getName(),
                                Main.getJDA().getTextChannelById(new GetChannelId().action(args.get("guildId")))
                                        .getName(),
                                getMessageId(args.get("guildId"), platformId, args.get("channelName")));

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

    private static synchronized Message editMessage(Map<String, String> args, Integer platformId) {
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

        eBuilder.setTitle(args.get("channelName") + " has gone offline!");

        msgDesc.append("Make sure to watch for my next announcement of when they go live!");
        //msgDesc.append(url);

        eBuilder.setDescription(msgDesc.toString());

        if (args.get("thumbnail") != null) {
            eBuilder.setThumbnail(args.get("thumbnail"));
        }

        if (new CheckCompact().action(args.get("guildId")).equals(0) && args.get("banner") != null) {
            eBuilder.setImage(args.get("banner"));
        }

        MessageEmbed embed = eBuilder.build();

        mBuilder.setEmbed(embed);

        return mBuilder.build();
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
