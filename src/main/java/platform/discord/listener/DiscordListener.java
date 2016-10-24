/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package platform.discord.listener;

import core.CommandParser;
import core.Main;
import net.dv8tion.jda.events.guild.GuildAvailableEvent;
import net.dv8tion.jda.events.guild.GuildJoinEvent;
import net.dv8tion.jda.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
import platform.generic.listener.PlatformListener;
import util.Const;
import util.database.calls.GuildJoin;
import util.database.calls.GuildLeave;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static util.database.Database.logger;

/**
 * @author keesh
 */
public class DiscordListener extends ListenerAdapter {

    private Connection connection;
    private PreparedStatement pStatement;
    private String query;

    public DiscordListener() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        Runnable checkLiveGames = PlatformListener::checkLiveGames;
        Runnable checkLiveChannels = PlatformListener::checkLiveChannels;
        Runnable messageFactory = PlatformListener::messageFactory;
        Runnable discordListener = PlatformListener::commandWorker;

        int initialDelay = 10; // Wait this long to start (2 seconds is ample when starting up the bot)
        int period = 60; // Run this task every {x} seconds

        logger.info("Starting the executor tasks");

        try {
            executor.submit(discordListener);
            executor.submit(checkLiveChannels);
            executor.submit(checkLiveGames);
            executor.submit(messageFactory);
        } catch (Exception e) {
            logger.info("******************* Caught an exception while keeping the executors active ", e);
            logger.info("Attempting to restart the executors...");
            new PlatformListener();
        }
    }

    /**
     * Incoming message handler.
     *
     * @param event JDA MessageReceivedEvent
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
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
            System.out.printf("[%s][%s][%s] : %s%n",
                    event.getGuild().getName(),
                    event.getTextChannel().getName(),
                    event.getAuthor().getUsername(),
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
    public void onGuildJoin(GuildJoinEvent event) {
        GuildJoin.joinGuild(event);
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        GuildLeave.leaveGuild(event);
        logger.info("NowLive bot has been dismissed from: " + event.getGuild().getName() + "(Id: " + event.getGuild
                ().getId() + ")");
    }

    private void commandFilter(String cntMsg, MessageReceivedEvent event) throws PropertyVetoException, IOException, SQLException {
        if (cntMsg.startsWith("ping", 1) || cntMsg.startsWith(Const.COMMAND, 1)) {
            CommandParser.handleCommand(Main.parser.parse(cntMsg, event));
        }
    }

    public void onGuildAvailble(GuildAvailableEvent event) {
        //DiscordController.
    }
}
