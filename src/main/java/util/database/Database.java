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

package util.database;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import core.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.PropReader;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static util.database.calls.SchemaCheck.checkDb;

/**
 * @author Veteran Software by Ague Mort
 */
public class Database {

    public static final Logger logger = LoggerFactory.getLogger(Database.class);
    private static PropReader prop = PropReader.getInstance();
    private static final String JDBC_DRIVER = prop.getProp().getProperty("jdbc.driver");
    private static final String MYSQL_URL = prop.getProp().getProperty("mysql.url");
    private static final String MYSQL_OPTIONS = prop.getProp().getProperty("mysql.options");
    private static final String MYSQL_USERNAME = prop.getProp().getProperty("mysql.username");
    private static final String MYSQL_PASSWORD = prop.getProp().getProperty("mysql.password");
    private static Database database;
    private static ComboPooledDataSource cpds = new ComboPooledDataSource();

    public Database() {
        // c3p0 connection pooling instantiation
        try {
            cpds.setDriverClass(JDBC_DRIVER);
        } catch (PropertyVetoException e) {
            logger.error("There was an issue setting the JDBC Driver", e);
        }
        cpds.setJdbcUrl(MYSQL_URL + MYSQL_OPTIONS);
        cpds.setUser(MYSQL_USERNAME);
        cpds.setPassword(MYSQL_PASSWORD);
        // Optional c3p0 settings below
        cpds.setInitialPoolSize(3);
        cpds.setMaxPoolSize(120);
        cpds.setMaxIdleTime(9);
        cpds.setMinPoolSize(3);
        cpds.setAcquireIncrement(3);
        cpds.setNumHelperThreads(2);
        cpds.setMaxStatements(0);
        cpds.setPreferredTestQuery("SELECT 1");
        cpds.setIdleConnectionTestPeriod(5);
        cpds.setTestConnectionOnCheckin(true);
    }

    public static Database getInstance() {
        if (database == null) {
            database = new Database();
            return database;
        } else {
            return database;
        }
    }

    public static void checkDatabase() {
        checkDb();
        if (Main.debugMode()) {
            logger.info("Database check complete.");
        }
    }

    public static void cleanUp(ResultSet resultSet, PreparedStatement statement, Connection connection) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                logger.error("Not able to close the Result Set.", e);
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                logger.error("Not able to close the Statement.", e);
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.error("Not able to close the Connection.", e);
            }
        }
    }

    public static void cleanUp(PreparedStatement statement, Connection connection) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                logger.error("Not able to close the Statement.", e);
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.error("Not able to close the Connection.", e);
            }
        }
    }

    public final Connection getConnection() {
        try {
            return cpds.getConnection();
        } catch (SQLException e) {
            logger.error("There was an error getting the connection.", e);
        }
        return null;
    }
}