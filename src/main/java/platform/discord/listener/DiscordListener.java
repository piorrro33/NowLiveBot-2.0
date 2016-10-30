/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package platform.discord.listener;

import core.CommandParser;
import core.Main;
import net.dv8tion.jda.events.guild.GuildJoinEvent;
import net.dv8tion.jda.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
import util.Const;
import util.database.calls.GuildJoin;
import util.database.calls.GuildLeave;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.SQLException;

import static util.database.Database.logger;

/**
 * @author keesh
 */
public class DiscordListener extends ListenerAdapter {

    /**
     * Incoming message handler.
     *
     * @param event JDA MessageReceivedEvent
     */
    @Override
    public final void onMessageReceived(MessageReceivedEvent event) {
        // Log message to console
        if (event.isPrivate()) {
            // PM's are not Guild specific, so don't request Guild and/or channel specific info
            // Will cause an Uncaught Exception from JDA and the message won't be read
            System.out.printf("[PM][%s] : %s%n",
                    event.getAuthor().getUsername(),
                    event.getMessage().getContent());
            if (!event.getAuthor().isBot()) {
                event.getAuthor().getPrivateChannel().sendMessage(Const.PRIVATE_MESSAGE_REPLY);
            }
        } else {
            System.out.printf("[%s:%s][%s:%s][%s:%s] : %s%n",
                    event.getGuild().getName(),
                    event.getGuild().getId(),
                    event.getTextChannel().getName(),
                    event.getTextChannel().getId(),
                    event.getAuthor().getUsername(),
                    event.getAuthor().getId(),
                    event.getMessage().getContent());
        }

        String cntMsg = event.getMessage().getContent();
        String jdaID = event.getMessage().getAuthor().getId();

        // Pre-check all core.commands to ignore JDA written messages.
        if (cntMsg.startsWith(Const.COMMAND_PREFIX) && !jdaID.equals(event.getJDA().getSelfInfo().getId())) {
            try {
                commandFilter(cntMsg, event);
            } catch (PropertyVetoException | IOException | SQLException e) {
                e.printStackTrace();
            }
        }
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

    private void commandFilter(String cntMsg, MessageReceivedEvent event)
            throws PropertyVetoException, IOException, SQLException {
        if (cntMsg.startsWith("ping", 1) || cntMsg.startsWith(Const.COMMAND, 1)) {
            CommandParser.handleCommand(Main.parser.parse(cntMsg, event));
        }
    }
}
