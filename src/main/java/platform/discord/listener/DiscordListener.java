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
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import util.Const;
import util.database.calls.GuildJoin;
import util.database.calls.GuildLeave;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.SQLException;

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
        if (cntMsg.startsWith(Const.COMMAND_PREFIX) && !authorID.equals(event.getJDA().getSelfUser().getId())) {
            try {
                System.out.printf("[COMMAND][%s] : %s%n",
                        event.getAuthor().getName(),
                        event.getMessage().getContent());
                commandFilter(cntMsg, event);
            } catch (PropertyVetoException | IOException | SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public final void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        System.out.printf("[PM][%s] : %s%n",
                event.getAuthor().getName(),
                event.getMessage().getContent());
        if (!event.getAuthor().isBot()) {
            MessageBuilder message = new MessageBuilder();
            message.appendString(Const.PRIVATE_MESSAGE_REPLY);
            sendToPm(event, message.build());
        }
    }

    @Override
    public final void onDisconnect(DisconnectEvent event) {
        try {
            logger.info("Discord has been disconnected.  Reconnecting...");
            Main.main(null);
        } catch (PropertyVetoException | IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public final void onReconnect(ReconnectedEvent event) {
        logger.info("JDA has been reconnected.");
    }

    @Override
    public final void onResume(ResumedEvent event) {
        logger.info("The JDA instance has been resumed.");
    }

    @Override
    public final void onGuildJoin(GuildJoinEvent event) {
        GuildJoin.joinGuild(event);
    }

    @Override
    public final void onGuildLeave(GuildLeaveEvent event) {
        GuildLeave.leaveGuild(event);
        logger.info("NowLive bot has been dismissed from: " + event.getGuild().getName() + "(Id: " + event.getGuild
                ().getId() + ")");
    }

    private void commandFilter(String cntMsg, GuildMessageReceivedEvent event)
            throws PropertyVetoException, IOException, SQLException {
        if (cntMsg.startsWith(Const.COMMAND_PREFIX + "ping") || cntMsg.startsWith(Const.COMMAND_PREFIX + Const.COMMAND)) {
            CommandParser.handleCommand(Main.parser.parse(cntMsg, event));
        }
    }
}
