/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package platform.discord.listener;

import core.CommandParser;
import core.Main;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.events.DisconnectEvent;
import net.dv8tion.jda.core.events.ReconnectedEvent;
import net.dv8tion.jda.core.events.ResumedEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import platform.generic.listener.PlatformListener;
import util.Const;
import util.DiscordLogger;
import util.database.calls.AddGuild;
import util.database.calls.CheckBotInGuild;
import util.database.calls.GuildJoin;
import util.database.calls.GuildLeave;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.SQLException;

import static platform.discord.controller.DiscordController.sendToChannel;
import static platform.discord.controller.DiscordController.sendToPm;
import static util.database.Database.logger;

/**
 * @author keesh
 */
public class DiscordListener extends ListenerAdapter {

    /**
     * Incoming message handler.
     *
     * @param event JDA GuildMessageReceivedEvent
     */
    @Override
    public final void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        String cntMsg = event.getMessage().getContent();
        String authorID = event.getMessage().getAuthor().getId();

        // Pre-check all core.commands to ignore JDA written messages.
        if (cntMsg.startsWith(Const.COMMAND_PREFIX + Const.COMMAND) &&
                !authorID.equals(event.getJDA().getSelfUser().getId()) &&
                !event.getMessage().getAuthor().isBot()) {
            // A check to see if the bot was added to the guild while it was offline and to add it
            if (!CheckBotInGuild.action(event)) {
                AddGuild.action(event);
                new DiscordLogger(" :gear: Fixed broken guild.", event);
                System.out.printf("[SYSTEM] [%s:%s] [%s:%s] Broken guild fixed.%n",
                        event.getGuild().getName(),
                        event.getGuild().getId(),
                        event.getChannel().getName(),
                        event.getChannel().getId());
            }
            try {
                new DiscordLogger(event.getMessage().getContent(), event);
                System.out.printf("[COMMAND] [%s:%s] [%s:%s] [%s:%s] %s%n",
                        event.getGuild().getName(),
                        event.getGuild().getId(),
                        event.getChannel().getName(),
                        event.getChannel().getId(),
                        event.getAuthor().getName(),
                        event.getAuthor().getId(),
                        event.getMessage().getContent());
                commandFilter(cntMsg, event);
            } catch (PropertyVetoException | IOException | SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public final void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        if (!event.getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) {
            MessageBuilder message = new MessageBuilder();
            message.appendString(Const.PRIVATE_MESSAGE_REPLY);
            sendToPm(event, message.build());
        }
    }

    @Override
    public final void onDisconnect(DisconnectEvent event) {
        try {
            new DiscordLogger("Discord had been disconnected. Attempting to reconnect...", event);
            logger.info("Discord has been disconnected.  Reconnecting...");
            Main.main(null);
        } catch (PropertyVetoException | IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public final void onReconnect(ReconnectedEvent event) {
        logger.info("JDA has been reconnected.");
        new PlatformListener();
    }

    @Override
    public final void onGuildMemberJoin(GuildMemberJoinEvent event) {
        new DiscordLogger(null, event);
    }

    @Override
    public final void onResume(ResumedEvent event) {
        logger.info("The JDA instance has been resumed.");
        new PlatformListener();
    }

    @Override
    public final void onGuildJoin(GuildJoinEvent event) {
        GuildJoin.joinGuild(event);
        new DiscordLogger(null, event);
        System.out.printf("[GUILD JOIN] Now Live has joined G:%s:%s%n",
                event.getGuild().getName(),
                event.getGuild().getId());
    }

    @Override
    public final void onGuildLeave(GuildLeaveEvent event) {
        GuildLeave.leaveGuild(event);
        new DiscordLogger(null, event);
        System.out.printf("[GUILD LEAVE] Now Live has been dismissed from G:%s:%s%n",
                event.getGuild().getName(),
                event.getGuild().getId());
    }

    private void commandFilter(String cntMsg, GuildMessageReceivedEvent event)
            throws PropertyVetoException, IOException, SQLException {
        if (cntMsg.startsWith(Const.COMMAND_PREFIX + "ping") || cntMsg.startsWith(Const.COMMAND_PREFIX + Const.COMMAND)) {
            // Do a check to make sure that -nl add channel|team is not being used directly
            if (!cntMsg.startsWith(Const.COMMAND_PREFIX + Const.COMMAND + " add channel") &&
                    !cntMsg.startsWith(Const.COMMAND_PREFIX + Const.COMMAND + " remove channel")) {
                CommandParser.handleCommand(Main.parser.parse(cntMsg, event));
            } else {
                sendToChannel(event, Const.USE_PLATFORM);
            }
        }
    }
}
