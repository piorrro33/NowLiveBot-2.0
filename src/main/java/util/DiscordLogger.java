package util;

import core.Main;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

/**
 * @author Veteran Software by Ague Mort
 */
public class DiscordLogger {

    public DiscordLogger(String logType, String message, Event event) {
        /*if (event instanceof GuildMessageReceivedEvent) {
            discordLogGMRE(logType, message, (GuildMessageReceivedEvent) event);
        }
        if (event instanceof PrivateMessageReceivedEvent) {
            discordLogPMRE(logType, (PrivateMessageReceivedEvent) event);
        }*/
    }

    private static String discordLogType(String logType) {
        switch (logType) {
            case "debug":
                if (Main.debugMode()) {
                    return "[DEBUG]";
                }
            case "error":
                return "[ERROR]";
            case "warn":
                return "[WARN]";
            case "system":
                return "[SYSTEM]";
            case "guildMessage":
                return "[BOT -> GUILD]";
            case "streamAnnounce":
                return "[STREAM ANNOUNCE]";
            case "offlineStream":
                return "[OFFLINE STREAM]";
            case "pmMessage":
                return "[BOT -> PM]";
            case "jda":
                return "[JDA]";
            case "guildJoin":
                return "[GUILD JOIN]";
            case "guildLeave":
                return "[GUILD LEAVE]";
            case "command":
                return "[COMMAND]";
            default:
                return null;
        }
    }

    public static void discordLogGMRE(String logType, String message, GuildMessageReceivedEvent event) {

        String guildName = event.getGuild().getName();
        String guildId = event.getGuild().getId();
        String channelName = event.getChannel().getName();
        String channelId = event.getChannel().getId();
        String authorName = event.getAuthor().getName();
        String authorId = event.getAuthor().getId();

        MessageBuilder discord = new MessageBuilder();

        discord.appendString(discordLogType(logType));
        discord.appendString("[G:");
        discord.appendString(guildName);
        discord.appendString(":");
        discord.appendString(guildId);
        discord.appendString("][TC:");
        discord.appendString(channelName);
        discord.appendString(":");
        discord.appendString(channelId);
        discord.appendString("][A:");
        discord.appendString(authorName);
        discord.appendString(":");
        discord.appendString(authorId);
        discord.appendString("]");
        discord.appendString(message);

        Message dMessage = discord.build();

        event.getJDA().getTextChannelById("253333740942655491").sendMessage(dMessage).queue();
    }

    private static void discordLogPMRE(String logType, PrivateMessageReceivedEvent event) {

        String authorName = event.getAuthor().getName();
        String authorId = event.getAuthor().getId();

        MessageBuilder discord = new MessageBuilder();

        discord.appendString(discordLogType(logType));
        discord.appendString("[A:");
        discord.appendString(authorName);
        discord.appendString(":");
        discord.appendString(authorId);
        discord.appendString("]");

        Main.getJDA().getTextChannelById(Const.LOG_CHANNEL).sendMessage(discord.build()).queue();
    }
}
