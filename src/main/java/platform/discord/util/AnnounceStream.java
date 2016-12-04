package platform.discord.util;

import core.Main;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.exceptions.PermissionException;
import util.Const;
import util.DiscordLogger;
import util.database.Database;
import util.database.calls.AddToStream;
import util.database.calls.CheckCompact;
import util.database.calls.GetChannelId;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class AnnounceStream {

    private Connection connection;
    private PreparedStatement pStatement;
    private JDA jda = Main.getJDA();

    private void setConnection() {
        this.connection = Database.getInstance().getConnection();
    }

    private void setStatement(String query) {
        try {
            if (connection.isClosed() || connection == null) {
                setConnection();
            }
            this.pStatement = connection.prepareStatement(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public final void action(Map<String, String> args, Integer platformId) {
        EmbedBuilder eBuilder = new EmbedBuilder();
        StringBuilder msgDesc = new StringBuilder();
        MessageBuilder mBuilder = new MessageBuilder();

        new NotifyLevel().action(args.get("guildId"), mBuilder);

        // TODO: re-enable once emoji command is enabled
        //checkEmoji(guildId, message);

        float[] rgb;

        switch (platformId) {
            case 1:
                rgb = Color.RGBtoHSB(100, 65, 165, null);
                eBuilder.setColor(Color.getHSBColor(rgb[0], rgb[1], rgb[2]));
                break;
            case 2:
                rgb = Color.RGBtoHSB(83, 109, 254, null);
                eBuilder.setColor(Color.getHSBColor(rgb[0], rgb[1], rgb[2]));
                break;
            default:
                // Never should hit
                break;
        }

        eBuilder.setAuthor(Const.NOW_LIVE, Const.DISCORD_URL, Const.BOT_LOGO);

        //eBuilder.setTitle(channelName + " is streaming " + gameName + "!");

        msgDesc.append("**");
        msgDesc.append(args.get("channelName"));
        msgDesc.append(" is streaming ");
        msgDesc.append(args.get("gameName"));
        msgDesc.append("!**\n");
        msgDesc.append(args.get("streamTitle"));
        msgDesc.append("\n");
        msgDesc.append("Watch them here: ");
        msgDesc.append(args.get("url"));

        eBuilder.setDescription(msgDesc.toString());
        if (args.get("thumbnail") != null) {
            eBuilder.setThumbnail(args.get("thumbnail"));
        }

        if (new CheckCompact().action(args.get("guildId")).equals(0)) {
            if (args.get("banner") != null) {
                eBuilder.setImage(args.get("banner"));
            }
        }

        MessageEmbed embed = eBuilder.build();

        mBuilder.setEmbed(embed);

        Message message = mBuilder.build();

        if (jda.getTextChannelById(args.get("channelId")) == null) {
            if (jda.getTextChannelById(args.get("guildId")) != null) {
                String query = "UPDATE `guild` SET `channelId` = ? WHERE `guildId` = ?";
                setStatement(query);
                try {
                    pStatement.setString(1, args.get("guildId"));
                    pStatement.setString(2, args.get("guildId"));
                    pStatement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    cleanUp(pStatement, connection);
                }
            }
        }
        try {
            jda.getTextChannelById(args.get("channelId")).sendMessage(message).queue(
                    success -> {
                        MessageBuilder discord = new MessageBuilder();

                        discord.appendString(" :tada: ");
                        discord.appendString("[G:");
                        discord.appendString(Main.getJDA().getGuildById(args.get("guildId")).getName());
                        discord.appendString("][C:");
                        discord.appendString(Main.getJDA().getTextChannelById(args.get("channelId")).getName());
                        discord.appendString("]");
                        discord.appendString(args.get("channelName"));
                        discord.appendString(" is streaming ");
                        discord.appendString(args.get("gameName"));

                        Message dMessage = discord.build();

                        args.put("messageId", success.getId());

                        new AddToStream().action(args, platformId);

                        new DiscordLogger(dMessage.getRawContent(), null);

                        System.out.printf("[STREAM ANNOUNCE] [%s:%s] [%s:%s] [%s]: %s%n",
                                Main.getJDA().getGuildById(args.get("guildId")).getName(),
                                Main.getJDA().getGuildById(args.get("guildId")).getId(),
                                Main.getJDA().getTextChannelById(new GetChannelId().action(args.get("guildId")))
                                        .getName(),
                                Main.getJDA().getTextChannelById(new GetChannelId().action(args.get("guildId")))
                                        .getId(),
                                args.get("messageId"),
                                args.get("channelName") + " is streaming " + args.get("gameName"));
                    }
            );
        } catch (PermissionException pe) {
            new DiscordLogger(" :no_entry: Permission exception in G:" + Main.getJDA
                    ().getGuildById(args.get("guildId")).getName() + ":" + args.get("guildId") + ".", null);
            System.out.printf("[~ERROR~] Permission Exception! G:%s:%s C:%s:%s%n",
                    Main.getJDA().getGuildById(args.get("guildId")).getName(),
                    args.get("guildId"),
                    Main.getJDA().getTextChannelById(args.get("channelId")).getName(),
                    args.get("channelId"));
        }
    }
}
