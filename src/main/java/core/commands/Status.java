package core.commands;

import core.Command;
import core.Main;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;
import util.Const;
import util.DiscordLogger;
import util.database.Database;
import util.database.calls.Tracker;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static platform.discord.controller.DiscordController.sendToChannel;
import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class Status implements Command {

    private static ResultSet result;
    private static PreparedStatement pStatement;
    private static Connection connection;
    private static StringBuilder commandUsage = new StringBuilder();
    private static DecimalFormat numFormat;

    @Override
    public final boolean called(String args, GuildMessageReceivedEvent event) {
        return true;
    }

    @Override
    public final void action(String args, GuildMessageReceivedEvent event) {
        numFormat = new DecimalFormat("###,###,###,###");
        // Total of all guilds the bot is in
        Integer guildCount = Main.getJDA().getGuilds().size();

        // Total members across all guilds
        Integer memberCount = 0;
        for (Guild guild : Main.getJDA().getGuilds()) {
            Integer serverMemberCount = guild.getMembers().size();
            memberCount += serverMemberCount;
        }

        // Number of times commands have been used
        try {
            connection = Database.getInstance().getConnection();
            String query = "SELECT * FROM `commandtracker` ORDER BY `commandName` ASC";
            pStatement = connection.prepareStatement(query);
            result = pStatement.executeQuery();

            commandUsage.setLength(0);

            while (result.next()) {
                commandUsage.append("\t> ");
                commandUsage.append(result.getString("commandName"));
                commandUsage.append(" - ");
                commandUsage.append(numFormat.format(result.getInt("commandCount")));
                commandUsage.append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(result, pStatement, connection);
        }

        EmbedBuilder eBuilder = new EmbedBuilder();
        StringBuilder sBuilder = new StringBuilder();
        MessageBuilder mBuilder = new MessageBuilder();
        DateFormat dateTimeFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        eBuilder.setColor(Color.WHITE);
        eBuilder.setAuthor(Const.BOT_NAME, null, Const.BOT_LOGO);
        eBuilder.setTitle(Const.BOT_NAME + " statistics");

        sBuilder.append("I am in **");
        sBuilder.append(numFormat.format(guildCount));
        sBuilder.append("** Discord servers\n\n");
        sBuilder.append("Total members of all servers I am in: **");
        sBuilder.append(numFormat.format(memberCount));
        sBuilder.append("**\n\n");
        sBuilder.append("**Command Usage**\n");
        sBuilder.append(commandUsage);

        eBuilder.setDescription(sBuilder.toString());

        eBuilder.setFooter("\nGenerated on: " + dateTimeFormat.format(new Date()), null);

        MessageEmbed mEmbed = eBuilder.build();
        mBuilder.setEmbed(mEmbed);
        Message message = mBuilder.build();

        try {
            Main.getJDA().getTextChannelById(event.getChannel().getId()).sendMessage(message).queue(
                    success -> {
                        new DiscordLogger("Bot status sent.", event);
                        System.out.printf("[BOT -> GUILD] [%s:%s] [%s:%s] %s%n",
                                event.getGuild().getName(),
                                event.getGuild().getId(),
                                event.getChannel().getName(),
                                event.getChannel().getId(),
                                "Bot Status Report");
                    },
                    failure -> {
                        new DiscordLogger("[PERMS] Unable to send message, trying public channel.", event);
                        System.out.printf("[~ERROR~] Unable to send message to %s:%s %s:%s.  Trying public channel.%n",
                                event.getGuild().getName(),
                                event.getGuild().getId(),
                                event.getChannel().getName(),
                                event.getChannel().getId());
                    });
        } catch (PermissionException pe) {
            new DiscordLogger("[PERMISSIONS] Permission exception. Check logs for stacktrace.", event);
            System.out.printf("[~ERROR~] Permission Exception! G:%s:%s C:%s:%s%n",
                    event.getGuild().getName(),
                    event.getGuild().getId(),
                    event.getChannel().getName(),
                    event.getChannel().getId());
        }
    }

    @Override
    public final void help(GuildMessageReceivedEvent event) {
        sendToChannel(event, Const.STATUS_HELP);

    }

    @Override
    public final void executed(boolean success, GuildMessageReceivedEvent event) {
        new Tracker("Status");
    }
}
