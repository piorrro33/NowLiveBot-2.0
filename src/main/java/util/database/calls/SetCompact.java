/*
 * Copyright $year Ague Mort of Veteran Software
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package util.database.calls;

import util.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class SetCompact {

    private static Connection connection = Database.getInstance().getConnection();
    private static PreparedStatement pStatement;

    public synchronized static Boolean action(String guildId, int isCompact) {
        try {
            String query = "UPDATE `guild` SET `isCompact` = ? WHERE `guildId` = ?";

            if (connection.isClosed()) {
                connection = Database.getInstance().getConnection();
            }

            pStatement = connection.prepareStatement(query);
            pStatement.setInt(1, isCompact);
            pStatement.setString(2, guildId);

            if (pStatement.executeUpdate() == 1) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(pStatement, connection);
        }
        return false;
    }

}
