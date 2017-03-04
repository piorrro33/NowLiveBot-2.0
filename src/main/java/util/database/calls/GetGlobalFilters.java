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

package util.database.calls;

import platform.twitch.models.Stream;
import util.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static util.database.Database.cleanUp;

public class GetGlobalFilters {

    private Connection connection = Database.getInstance().getConnection();

    public final synchronized List<String> fetch(Stream stream, String type) {
        PreparedStatement pStatement = null;
        ResultSet result = null;

        String guildId = stream.getAdditionalProperties().get("guildId").toString();

        try {
            String query;
            switch (type) {
                case "game":
                    query = "SELECT `gameFilter` FROM `twitch` WHERE `guildId` = ? AND `gameFilter` IS NOT NULL";
                    break;
                default:// Title filters
                    query = "SELECT `titleFilter` FROM `twitch` WHERE `guildId` = ? AND `titleFilter` IS NOT NULL";
                    break;
            }


            if (connection == null || connection.isClosed()) {
                connection = Database.getInstance().getConnection();
            }
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, guildId);
            result = pStatement.executeQuery();

            List<String> filters = new CopyOnWriteArrayList<>();

            if (result.isBeforeFirst()) {
                while (result.next()) {
                    switch (type) {
                        case "game":
                            filters.add(result.getString("gameFilter").replaceAll("''", "'"));
                            break;
                        default:
                            filters.add(result.getString("titleFilter").replaceAll("''", "'"));
                            break;
                    }
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
}
