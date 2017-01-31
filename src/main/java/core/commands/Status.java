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

package core.commands;

import core.Command;
import core.Main;
import langs.LocaleString;
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

    @Override
    public final boolean called(String args, GuildMessageReceivedEvent event) {
        return true;
    }

    @Override
    public final void action(String args, GuildMessageReceivedEvent event) {
        // TODO: Clean this up and make it less bulky and more condensed
        DecimalFormat numFormat = new DecimalFormat("###,###,###,###");
        // Total of all guilds the bot is in
        Integer guildCount = Main.getJDA().getGuilds().size();

        // Total members across all guilds
        Integer memberCount = 0;
        for (Guild guild : Main.getJDA().getGuilds()) {
            Integer serverMemberCount = guild.getMembers().size();
            memberCount += serverMemberCount;
        }

        EmbedBuilder eBuilder = new EmbedBuilder();
        MessageBuilder mBuilder = new MessageBuilder();
        DateFormat dateTimeFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        eBuilder.setColor(Color.RED);
        eBuilder.setAuthor(Const.BOT_NAME + " Statistics", null, Const.BOT_LOGO);

        eBuilder.addField("# Servers", numFormat.format(guildCount), false);
        eBuilder.addField("Num. Unique Members", numFormat.format(memberCount), false);

        // Number of times commands have been used
        try {
            String query = "SELECT * FROM `commandtracker` ORDER BY `commandName` ASC";

            if (connection == null || connection.isClosed()) {
                connection = Database.getInstance().getConnection();
            }

            pStatement = connection.prepareStatement(query);
            result = pStatement.executeQuery();
            String heard;
            while (result.next()) {
                if (result.getString("commandName").equals("Messages")) {
                    heard = " Heard";
                } else {
                    heard = "";
                }

                eBuilder.addField(result.getString("commandName") + heard, numFormat.format(result.getInt("commandCount")), true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(result, pStatement, connection);
        }

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
                        new DiscordLogger(" :no_entry: Unable to send message, trying public channel.", event);
                        System.out.printf("[~ERROR~] Unable to send message to %s:%s %s:%s.  Trying public channel.%n",
                                event.getGuild().getName(),
                                event.getGuild().getId(),
                                event.getChannel().getName(),
                                event.getChannel().getId());
                    });
        } catch (PermissionException pe) {
            new DiscordLogger(" :no_entry: Permission error sending bot status", event);
            System.out.printf("[~ERROR~] Permission Exception! G:%s:%s C:%s:%s%n",
                    event.getGuild().getName(),
                    event.getGuild().getId(),
                    event.getChannel().getName(),
                    event.getChannel().getId());
        }
    }

    @Override
    public final void help(GuildMessageReceivedEvent event) {
        sendToChannel(event, LocaleString.getString(event.getMessage().getGuild().getId(), "statusHelp"));

    }

    @Override
    public final void executed(boolean success, GuildMessageReceivedEvent event) {
        new Tracker("Status");
    }
}
