package util;

import core.Main;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.DisconnectEvent;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Veteran Software by Ague Mort
 */
public class DiscordLogger {

    private String localDateTime;

    public DiscordLogger(String message, Event event) {
        dateTime();
        if (event != null) {
            if (event instanceof GuildMessageReceivedEvent && !((GuildMessageReceivedEvent) event).getAuthor().isBot()) {
                discordLogGMRE(message, (GuildMessageReceivedEvent) event);
            }
            if (event instanceof PrivateMessageReceivedEvent && !((PrivateMessageReceivedEvent) event).getAuthor().isBot()) {
                discordLogPMRE((PrivateMessageReceivedEvent) event);
            }
            if (event instanceof GuildMemberJoinEvent) {
                discordLogGMJE((GuildMemberJoinEvent) event);
            }
            if (event instanceof GuildMemberLeaveEvent) {
                discordLogGMLE((GuildMemberLeaveEvent) event);
            }
            if (event instanceof DisconnectEvent) {
                discordLogNoEvent(" :gear: " + message);
            }
            if (event instanceof GuildJoinEvent) {
                discordLogGJE((GuildJoinEvent) event);
            }
            if (event instanceof GuildLeaveEvent) {
                discordLogGLE((GuildLeaveEvent) event);
            }
        } else {
            discordLogNoEvent(message);
        }
    }

    private void discordLogGMJE(GuildMemberJoinEvent event) {
        String user = event.getMember().getUser().getName();
        Main.getJDA().getTextChannelById(Const.LOG_CHANNEL).sendMessage(" :heart_eyes_cat: " + user + " joined the " +
                "guild.");
    }

    private void discordLogGMLE(GuildMemberLeaveEvent event) {
        String user = event.getMember().getUser().getName();
        Main.getJDA().getTextChannelById(Const.LOG_CHANNEL).sendMessage(" :scream_cat: " + user + " left the " +
                "guild.");
    }

    private void dateTime() {
        DateFormat dateTimeFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        this.localDateTime = dateTimeFormat.format(new Date());
    }

    private void discordLogGJE(GuildJoinEvent event) {
        String guildName = event.getGuild().getName();
        String guildId = event.getGuild().getId();

        MessageBuilder discord = new MessageBuilder();

        discord.appendString("[");
        discord.appendString(this.localDateTime);
        discord.appendString("][G:");
        discord.appendString(guildName);
        discord.appendString(":");
        discord.appendString(guildId);
        discord.appendString("] :inbox_tray: ");
        discord.appendString("Joined new guild.");

        Message dMessage = discord.build();

        Main.getJDA().getTextChannelById(Const.LOG_CHANNEL).sendMessage(dMessage).queue();
    }

    private void discordLogGLE(GuildLeaveEvent event) {
        String guildName = event.getGuild().getName();

        MessageBuilder discord = new MessageBuilder();

        discord.appendString("[");
        discord.appendString(this.localDateTime);
        discord.appendString("][G:");
        discord.appendString(guildName);
        discord.appendString("] :outbox_tray: ");
        discord.appendString("Left guild.");

        Message dMessage = discord.build();

        Main.getJDA().getTextChannelById(Const.LOG_CHANNEL).sendMessage(dMessage).queue();
    }

    private void discordLogNoEvent(String message) {
        MessageBuilder discord = new MessageBuilder();

        discord.appendString("[");
        discord.appendString(this.localDateTime);
        discord.appendString("] ");
        discord.appendString(message);

        Message dMessage = discord.build();

        Main.getJDA().getTextChannelById(Const.LOG_CHANNEL).sendMessage(dMessage).queue();
    }

    private void discordLogGMRE(String message, GuildMessageReceivedEvent event) {

        String guildName = event.getGuild().getName();
        String channelName = event.getChannel().getName();
        String authorName = event.getAuthor().getName();

        MessageBuilder discord = new MessageBuilder();

        discord.appendString("[");
        discord.appendString(this.localDateTime);
        discord.appendString("][G:");
        discord.appendString(guildName);
        discord.appendString("][C:");
        discord.appendString(channelName);
        discord.appendString("][A:");
        discord.appendString(authorName);
        discord.appendString("] ");
        discord.appendString(message);

        Message dMessage = discord.build();

        Main.getJDA().getTextChannelById(Const.LOG_CHANNEL).sendMessage(dMessage).queue();
    }

    private void discordLogPMRE(PrivateMessageReceivedEvent event) {

        String authorName = event.getAuthor().getName();
        String authorId = event.getAuthor().getId();

        MessageBuilder discord = new MessageBuilder();

        discord.appendString("[");
        discord.appendString(this.localDateTime);
        discord.appendString("][A:");
        discord.appendString(authorName);
        discord.appendString(":");
        discord.appendString(authorId);
        discord.appendString("] ");

        Main.getJDA().getTextChannelById(Const.LOG_CHANNEL).sendMessage(discord.build()).queue();
    }
}
