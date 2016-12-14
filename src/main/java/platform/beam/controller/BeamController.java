package platform.beam.controller;

import platform.beam.http.ApiRequest;
import platform.beam.http.BeamChannel;
import util.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static platform.discord.controller.DiscordController.announceStream;
import static platform.discord.controller.DiscordController.getChannelId;
import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class BeamController {

    private static Connection connection;
    private static PreparedStatement pStatement;
    private static ResultSet result;

    private static synchronized List<String> checkFilters(String guildId) {
        try {
            String query = "SELECT * FROM `filter` WHERE `guildId` = ?";

            connection = Database.getInstance().getConnection();
            if (connection == null || connection.isClosed()) {
                connection = Database.getInstance().getConnection();
            }
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, guildId);
            result = pStatement.executeQuery();

            List<String> filters = new ArrayList<>();

            if (result.isBeforeFirst()) {
                while (result.next()) {
                    filters.add(result.getString("name"));
                }
                return filters;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(result, pStatement, connection);
        }

        return null;
    }

    public static synchronized Boolean channelExists(String channel) {

        ApiRequest beamRequest = new ApiRequest();

        beamRequest.getChannel(channel);

        if (!beamRequest.getResponseCode().equals(404)) {
            return true;
        }

        return false;
    }

    public final synchronized void checkChannel(String channel, String guildId, Integer platformId) {

        BeamChannel beam = new ApiRequest().getChannel(channel);

        if (beam.getOnline() && beam.getType().getName() != null && !beam.getType().getName().isEmpty()) {

            List<String> filters = checkFilters(guildId);

            if (filters != null) {
                for (String filter : filters) {
                    if (beam.getType().getName().equalsIgnoreCase(filter)) {
                        announceStream(
                                guildId,
                                getChannelId(guildId),
                                platformId,
                                beam.getToken(),
                                beam.getName(),
                                beam.getType().getName(),
                                "https://beam.pro/" + beam.getToken(),
                                beam.getThumbnail().getUrl(),
                                beam.getCover().getUrl()
                        );
                    }
                }
            } else {
                announceStream(
                        guildId,
                        getChannelId(guildId),
                        platformId,
                        beam.getToken(),
                        beam.getName(),
                        beam.getType().getName(),
                        "https://beam.pro/" + beam.getToken(),
                        beam.getThumbnail().getUrl(),
                        beam.getCover().getUrl()
                );
            }
        }
    }

}
