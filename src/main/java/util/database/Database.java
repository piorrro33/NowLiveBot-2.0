package util.database;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.PooledDataSource;
import net.dv8tion.jda.MessageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.PropReader;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static util.database.calls.SchemaCheck.checkDb;

/**
 * @author Veteran Software by Ague Mort
 */
public class Database {

    public static Logger logger = LoggerFactory.getLogger(Database.class);
    private static PropReader prop = PropReader.getInstance();
    private static final String JDBC_DRIVER = prop.getProp().getProperty("jdbc.driver");
    private static final String MYSQL_URL = prop.getProp().getProperty("mysql.url");
    private static final String MYSQL_OPTIONS = prop.getProp().getProperty("mysql.options");
    private static final String MYSQL_USERNAME = prop.getProp().getProperty("mysql.username");
    private static final String MYSQL_PASSWORD = prop.getProp().getProperty("mysql.password");
    private static Database database;
    private ComboPooledDataSource cpds;

    public Database() {
        // c3p0 connection pooling instantiation
        this.cpds = new ComboPooledDataSource();
        try {
            this.cpds.setDriverClass(JDBC_DRIVER);
        } catch (PropertyVetoException e) {
            logger.error("There was an issue setting the JDBC Driver", e);
        }
        this.cpds.setJdbcUrl(MYSQL_URL + MYSQL_OPTIONS);
        this.cpds.setUser(MYSQL_USERNAME);
        this.cpds.setPassword(MYSQL_PASSWORD);
        // Optional c3p0 settings below
        this.cpds.setInitialPoolSize(50);
        this.cpds.setMaxPoolSize(50);
        this.cpds.setMaxIdleTime(0);
        this.cpds.setMinPoolSize(50);
        this.cpds.setAcquireIncrement(5);
        this.cpds.setNumHelperThreads(5);
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
        logger.info("Database check complete.");
    }

    public static void cleanUp(ResultSet resultSet, Statement statement, Connection connection) {
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

    public static void cleanUp(Integer resultSet, Statement statement, Connection connection) {
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

    public static void cleanUp(Boolean resultSet, Statement statement, Connection connection) {
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

    public Connection getConnection() {
        try {
            return this.cpds.getConnection();
        } catch (SQLException e) {
            logger.error("There was an error getting the connection.", e);
        }
        return null;
    }

    public void checkPooledStatus() {
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
                msg.appendString("num_connections: " + pds.getNumConnectionsDefaultUser());
                msg.appendString("\nnum_busy_connections: " + pds.getNumBusyConnectionsDefaultUser());
                msg.appendString("\nnum_idle_connections: " + pds.getNumIdleConnectionsDefaultUser());
                msg.appendString("\n");
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
