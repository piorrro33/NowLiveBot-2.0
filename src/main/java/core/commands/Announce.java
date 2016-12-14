package core.commands;

import core.Command;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import util.Const;
import util.DiscordLogger;
import util.database.Database;
import util.database.calls.Tracker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static platform.discord.controller.DiscordController.sendToChannel;
import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class Announce implements Command {

    private static ResultSet result;
    private static PreparedStatement pStatement;
    private static Connection connection;

    @Override
    public final boolean called(String args, GuildMessageReceivedEvent event) {
        if (args != null && !args.isEmpty()) {
            return true;
        } else {
            sendToChannel(event, Const.INCORRECT_ARGS);
            return false;
        }
    }

    @Override
    public final void action(String args, GuildMessageReceivedEvent event) {
        try {
            connection = Database.getInstance().getConnection();
            String query = "SELECT `guildId` FROM `guild` ORDER BY `guildId` ASC";
            if (connection == null || connection.isClosed()) {
                connection = Database.getInstance().getConnection();
            }
            pStatement = connection.prepareStatement(query);

            result = pStatement.executeQuery();
            while (result.next()) {
                event.getJDA().getGuildById(result.getString("guildId")).getPublicChannel()
                        .sendMessage("*Message from the " + Const.BOT_NAME + " developers:*\n\n\t" + args).queue();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(result, pStatement, connection);
        }

        sendToChannel(event, "*Message from the " + Const.BOT_NAME + " developers:*\n\n\t" + args);
        new DiscordLogger(" :globe_with_meridians: Global announcement sent.", event);
        System.out.println("[SYSTEM] Global announcement sent.");
    }

    @Override
    public final void help(GuildMessageReceivedEvent event) {
        // TODO: Add some sort of check for being a bot admin here so this doesn't show up to guild owners and users
        sendToChannel(event, Const.ANNOUNCE_HELP);
    }

    @Override
    public final void executed(boolean success, GuildMessageReceivedEvent event) {
        new Tracker("Announce");
    }
}
