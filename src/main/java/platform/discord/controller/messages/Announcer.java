package platform.discord.controller.messages;

import net.dv8tion.jda.MessageBuilder;
import util.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class Announcer {

    private Connection connection;
    private PreparedStatement pStatement;
    private String query;
    private ResultSet rs;
    private ResultSet chan;

    public Announcer() {
        connection = Database.getInstance().getConnection();
        try {
            connection.setAutoCommit(false);
            query = "SELECT `guildId`, platformId`, channelName`, `streamTitle`, `gameName` FROM `stream` WHERE " +
                    "`messageId` = null";
            pStatement = connection.prepareStatement(query);
            rs = pStatement.executeQuery(query);
            normalAnnounce();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(rs, pStatement, connection);
        }
    }

    private void normalAnnounce() {
        try {
            while (rs.next()) {
                query = "SELECT `channelId` FROM `guild` WHERE `guildId` = ?";
                pStatement = connection.prepareStatement(query);
                pStatement.setString(1, rs.getString("guildId"));
                chan = pStatement.executeQuery(query);

                MessageBuilder message = new MessageBuilder();

                while (chan.next()) {

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(chan, pStatement, connection);
        }

    }

    public void compactAnnounce() {

    }

}
