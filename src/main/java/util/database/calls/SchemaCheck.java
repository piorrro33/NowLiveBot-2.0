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

import core.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.PropReader;
import util.database.Database;

import java.io.*;
import java.sql.*;

/**
 * @author Veteran Software by Ague Mort
 */
public class SchemaCheck extends Database {

    private static PropReader prop = PropReader.getInstance();
    private static final String MYSQL_SCHEMA = prop.getProp().getProperty("mysql.schema");
    private static Logger logger = LoggerFactory.getLogger(SchemaCheck.class);

    private static Connection connection;
    private static PreparedStatement pStatement;
    private static ResultSet resultSet;

    public SchemaCheck() {
        super();
    }

    public static void checkDb() {
        System.out.println("Checking database schema.");


        try {
            String query = "SELECT `SCHEMA_NAME` FROM `information_schema`.`SCHEMATA` WHERE `SCHEMA_NAME` = ?";

            if (connection == null || connection.isClosed()) {
                connection = Database.getInstance().getConnection();
            }
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, MYSQL_SCHEMA);
            resultSet = pStatement.executeQuery();

            Boolean check = resultSet.last(); // Check to see if there's at least one row

            if (check) {
                System.out.println("[SYSTEM] MySQL schema exists.");
            } else {
                System.out.println("[SYSTEM] MySQL schema does not exist. Uploading schema.");
                // Load the raw SQL schema file in to the database
                uploadDatabase();
            }
        } catch (SQLSyntaxErrorException e) {
            logger.error("There is an error in the SQL syntax.", e);
        } catch (SQLException e) {
            logger.error("SQLException error.  No clue what.", e);
        } finally {
            cleanUp(resultSet, pStatement, connection);
        }
    }

    private static void uploadDatabase() {

        String line;
        StringBuilder buffer = new StringBuilder();

        try {
            FileReader readerF = new FileReader(new File("./nowlive_schema.sql"));
            BufferedReader readerB = new BufferedReader(readerF);

            while ((line = readerB.readLine()) != null) {
                buffer.append(line);
            }
            readerB.close();
        } catch (FileNotFoundException e) {
            logger.error("MySQL Schema file was not found.", e);
        } catch (IOException e) {
            logger.error("There was a problem reading the MySQL Schema from file.", e);
        }

        String[] inst = buffer.toString().split(";");

        try {
            if (connection == null || connection.isClosed()) {
                connection = Database.getInstance().getConnection();
            }

            for (String anInst : inst) {
                if (!anInst.trim().equals("")) {
                    pStatement = connection.prepareStatement(anInst);
                    pStatement.executeUpdate(anInst);
                    if (Main.debugMode()) {
                        logger.info("MySQL Query executed successfully:  " + anInst);
                    }
                }
            }
            String query = "USE `" + MYSQL_SCHEMA + "`";
            pStatement = connection.prepareStatement(query);

            if (pStatement.execute("USE `" + MYSQL_SCHEMA + "`")) {
                if (Main.debugMode()) {
                    logger.info("Now using schema: " + MYSQL_SCHEMA);
                }
            }
        } catch (SQLException e) {
            logger.error("SQLException error.  No clue what.", e);
        } finally {
            cleanUp(pStatement, connection);
        }
    }

}
