package platform.discord.util;

import core.Main;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.User;
import util.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class NotifyLevel {

    private Connection connection = Database.getInstance().getConnection();
    private PreparedStatement pStatement;
    private ResultSet result;

    NotifyLevel() {
        setConnection();
    }

    private void setConnection() {
        this.connection = Database.getInstance().getConnection();
    }

    private void setResult(ResultSet result) {
        this.result = result;
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

    public synchronized MessageBuilder action(String guildId, MessageBuilder message) {
        try {
            String query = "SELECT `level`, `userId` FROM `notification` WHERE `guildId` = ?";

            setStatement(query);
            pStatement.setString(1, guildId);
            setResult(pStatement.executeQuery());

            while (result.next()) {
                switch (result.getInt("level")) {
                    case 1: // User wants a @User mention
                        String userId = result.getString("userId");
                        User user = Main.getJDA().getUserById(userId);
                        message.appendString("Hey ");
                        message.appendMention(user);
                        message.appendString("! Check out this streamer that just went live!");
                        break;
                    case 2: // User wants @here mention
                        message.appendString("Hey ");
                        message.appendHereMention();
                        message.appendString("! Check out this streamer that just went live!");
                        break;
                    case 3: // User wants @everyone mention
                        message.appendString("Hey ");
                        message.appendEveryoneMention();
                        message.appendString("! Check out this streamer that just went live!");
                        break;
                    default:
                        // No mention
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(pStatement, connection);
        }
        return message;
    }

}
