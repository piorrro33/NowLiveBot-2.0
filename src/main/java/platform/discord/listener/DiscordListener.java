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

package platform.discord.listener;

import core.CommandParser;
import core.Main;
import langs.LocaleString;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.events.DisconnectEvent;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.ReconnectedEvent;
import net.dv8tion.jda.core.events.ResumedEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import platform.generic.listener.PlatformListener;
import util.Const;
import util.DiscordLogger;
import util.PropReader;
import util.database.calls.*;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

import static platform.discord.controller.DiscordController.sendToChannel;
import static platform.discord.controller.DiscordController.sendToPm;
import static util.database.Database.logger;

/**
 * @author Veteran Software by Ague Mort
 */
public class DiscordListener extends ListenerAdapter {

    private String buffer = "";

    /**
     * Incoming message handler.
     *
     * @param event JDA GuildMessageReceivedEvent
     */
    @Override
    public synchronized final void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        if (!event.getAuthor().isBot()) {//Don't log other bot messages
            new Tracker("Messages Heard");
        }
        if (!event.getMessage().getContent().equals(this.buffer)) {

            this.buffer = event.getMessage().getContent();

            String cntMsg = event.getMessage().getContent();
            String authorID = event.getMessage().getAuthor().getId();

            //if (!event.getChannel().getId().equals("250045505659207699")) {
            // Pre-check all core.commands to ignore JDA written messages.
            if (cntMsg.startsWith(Const.COMMAND_PREFIX + Const.COMMAND) &&
                    !authorID.equals(event.getJDA().getSelfUser().getId()) &&
                    !event.getMessage().getAuthor().isBot()) {

                // A check to see if the bot was added to the guild while it was offline and to add it
                if (!CheckBotInGuild.action(event)) {
                    AddGuild addGuild = new AddGuild();
                    addGuild.action(event);
                    new DiscordLogger(" :gear: Fixed broken guild.", event);
                    System.out.printf("[SYSTEM] [%s:%s] [%s:%s] Broken guild fixed.%n",
                            event.getGuild().getName(),
                            event.getGuild().getId(),
                            event.getChannel().getName(),
                            event.getChannel().getId());
                }
                try {
                    new DiscordLogger(" :arrow_left: " + event.getMessage().getContent(), event);
                    System.out.printf("[COMMAND] [%s:%s] [%s:%s] [%s#%s:%s] %s%n",
                            event.getGuild().getName(),
                            event.getGuild().getId(),
                            event.getChannel().getName(),
                            event.getChannel().getId(),
                            event.getAuthor().getName(),
                            event.getAuthor().getDiscriminator(),
                            event.getAuthor().getId(),
                            event.getMessage().getContent());
                    commandFilter(cntMsg, event);
                } catch (PropertyVetoException | IOException | SQLException e) {
                    e.printStackTrace();
                }
            }
            //}
        }
    }

    @Override
    public void onReady(ReadyEvent event) {
        super.onReady(event);
        //updateDiscordBotsServerCount(event.getJDA().getGuilds().size());
    }

    @Override
    public final void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        if (!event.getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) {
            MessageBuilder message = new MessageBuilder();
            message.append(LocaleString.getString("250045505659207699", "privateMessageReply"));
            sendToPm(event, message.build());
        }
    }

    @Override
    public final void onDisconnect(DisconnectEvent event) {
        new DiscordLogger(" :broken_heart: Discord had been disconnected. Attempting to reconnect...", event);
        logger.info("Discord has been disconnected.  Reconnecting...");
        System.out.println("Client Close Frame: " + event.getClientCloseFrame());
        System.out.println("Service Close Frame: " + event.getServiceCloseFrame());
        System.out.println("Response Number: " + event.getResponseNumber());
    }

    @Override
    public final void onReconnect(ReconnectedEvent event) {
        new DiscordLogger(" :heart: Discord's connection has been reconnected!", event);
        logger.info("JDA has been reconnected.");
        new PlatformListener();
    }

    @Override
    public final void onGuildMemberJoin(GuildMemberJoinEvent event) {
        new DiscordLogger(null, event);
    }

    @Override
    public final void onResume(ResumedEvent event) {
        new DiscordLogger(" :heart: Discord's connection has been resumed!", event);
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
        updateDiscordBotsServerCount(event.getJDA().getGuilds().size());
    }

    @Override
    public final void onGuildLeave(GuildLeaveEvent event) {
        GuildLeave.leaveGuild(event);
        new DiscordLogger(null, event);
        System.out.printf("[GUILD LEAVE] Now Live has been dismissed/left from G:%s:%s%n",
                event.getGuild().getName(),
                event.getGuild().getId());
        updateDiscordBotsServerCount(event.getJDA().getGuilds().size());
    }

    private void commandFilter(String cntMsg, GuildMessageReceivedEvent event)
            throws PropertyVetoException, IOException, SQLException {
        if (cntMsg.startsWith(Const.COMMAND_PREFIX + "ping") || cntMsg.startsWith(Const.COMMAND_PREFIX + Const.COMMAND)) {
            // Do a check to make sure that -nl add channel|team is not being used directly
            if (!cntMsg.startsWith(Const.COMMAND_PREFIX + Const.COMMAND + " add channel") &&
                    !cntMsg.startsWith(Const.COMMAND_PREFIX + Const.COMMAND + " remove channel")) {
                CommandParser.handleCommand(Main.parser.parse(cntMsg, event));
            } else {
                sendToChannel(event, LocaleString.getString(event.getMessage().getGuild().getId(), "usePlatform"));
            }
        }
    }

    private void updateDiscordBotsServerCount(Integer count) {
        HttpClient client = HttpClientBuilder.create().disableCookieManagement().build();
        URIBuilder uriBuilder = new URIBuilder();
        uriBuilder.setScheme("https").setHost("bots.discord.pw").setPath("/api/bots/240729664035880961/stats");
        HttpPost post = null;
        try {
            post = new HttpPost(uriBuilder.build());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        if (post != null) {
            post.addHeader("Authorization", PropReader.getInstance().getProp().getProperty("discord.bots.auth"));
            post.addHeader("Content-Type", "application/json");
            String json = "{ \"server_count\": " + count + "}";
            try {
                StringEntity entity = new StringEntity(json);
                post.setEntity(entity);

                HttpResponse response = client.execute(post);

                if (response.getStatusLine().getStatusCode() != 200) {
                    System.out.println("[~ERROR~] Failed updating server count on bots.discord.pw");
                } else {
                    System.out.println("[SYSTEM] Successfully updated server count on bots.discord.pw");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
