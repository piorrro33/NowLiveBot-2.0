package util.database;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.PooledDataSource;
import core.Main;
import net.dv8tion.jda.core.MessageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.PropReader;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
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
        cpds.setMaxPoolSize(100);
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

    public final void checkPooledStatus() {
        try {
            Context initContext = new InitialContext();
            // TODO: Figure out the JNDI name of the database
            Context appContext = (Context) initContext.lookup("java:/comp/env");
            Database ds = (Database) appContext.lookup("jdbc/nowlivebot");
            //InitialContext ictx = new InitialContext();
            //Database ds = (Database) ictx.lookup("java:comp/env/jdbc/nowlivebot");

            if (ds instanceof PooledDataSource) {
                PooledDataSource pds = (PooledDataSource) ds;
                MessageBuilder msg = new MessageBuilder();
                msg.append("num_connections: " + pds.getNumConnectionsDefaultUser());
                msg.append("\nnum_busy_connections: " + pds.getNumBusyConnectionsDefaultUser());
                msg.append("\nnum_idle_connections: " + pds.getNumIdleConnectionsDefaultUser());
                msg.append("\n");
                msg.build();
            } else {
                logger.warn("Not a c3p0 PooledDataSource!!");
            }
        } catch (NamingException e) {
            logger.error("Naming Exception encountered while checking the pooled connection status.", e);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}