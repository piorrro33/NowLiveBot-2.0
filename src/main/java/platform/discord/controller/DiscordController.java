/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package platform.discord.controller;

import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author keesh
 */
public class DiscordController {

    private static Logger logger = LoggerFactory.getLogger(DiscordController.class);
    private String guildIdMessageEvent;
    private String channelIdMessageEvent;
    private String userIdMessageEvent;
    private String mentionedChannelId;
    private String guildName;
    private String mentionedUsersId;

    public DiscordController(MessageReceivedEvent event) {

        this.guildIdMessageEvent = event.getGuild().getId();
        this.channelIdMessageEvent = event.getChannel().getId();
        this.userIdMessageEvent = event.getAuthor().getId();
        this.guildName = event.getGuild().getName();
        mentionedChannelId(event);
        mentionedUsersID(event);
    }

    private void mentionedChannelId(MessageReceivedEvent event) {
        if (event.getGuild().getTextChannels().contains(event.getMessage().getMentionedChannels())) {
            if (event.getMessage().getMentionedChannels().size() == 1) {
                this.mentionedChannelId = String.valueOf(event.getMessage().getMentionedChannels().get(0));
            }
        } else {
            this.mentionedChannelId = null;
        }
    }

    public void mentionedUsersID(MessageReceivedEvent event) {
        for (User u : event.getMessage().getMentionedUsers()) {
            this.mentionedUsersId = u.getId();
            logger.info("Mentioned Users Id's: " + this.mentionedUsersId);
        }
    }

    public String getMentionedUsersId() {
        return this.mentionedUsersId;
    }

    public String getguildId() {
        return this.guildIdMessageEvent;
    }

    public String getChannelId() {
        return this.channelIdMessageEvent;
    }

    public String getUserId() {
        return this.userIdMessageEvent;
    }

    public String getMentionedChannelId() {
        return this.mentionedChannelId;
    }

    public String getGuildName(MessageReceivedEvent event) {
        return this.guildName;
    }

    public String getAuthorName(MessageReceivedEvent event) {
        return event.getAuthor().getUsername();
    }
}
