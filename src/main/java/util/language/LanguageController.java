package util.language;

import util.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class LanguageController {

    private static Connection connection;
    private static PreparedStatement pStatement;
    private static ResultSet result;

    private String language;

    public LanguageController(String guildId) {
        setLang(guildId);
    }

    public synchronized String getLang() {
        return this.language;
    }

    private synchronized void setLang(String guildId) {
        try {
            String query = "SELECT `botLanguage` FROM `guild` WHERE `guildId` = ?";
            connection = Database.getInstance().getConnection();
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, guildId);
            result = pStatement.executeQuery();
            if (result.isBeforeFirst()) {
                while (result.next()) {
                    this.language = result.getString("botLanguage");
                }
            } else {
                this.language = "en";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(result, pStatement, connection);
        }
    }

}
