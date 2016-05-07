package com.cyberwalkabout.cyberfit.db.sqlite;

import com.cyberwalkabout.cyberfit.config.Config;
import com.cyberwalkabout.cyberfit.db.DBCreator;
import com.cyberwalkabout.cyberfit.db.DBException;
import com.cyberwalkabout.cyberfit.db.DBSchemaCreator;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.DBSchema;
import com.cyberwalkabout.cyberfit.google.Auth;
import com.cyberwalkabout.cyberfit.google.YoutubeVideoInfoProvider;
import com.google.api.client.auth.oauth2.Credential;
import com.google.common.collect.Lists;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.sqlite.SQLiteConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author Andrii Kovalov
 */
@Component
public class SQLiteDBCreator implements DBCreator {
    private static final Logger LOG = LoggerFactory.getLogger(SQLiteDBCreator.class);

    private static final boolean DELETE_DB_FILE_IF_EXISTS = true;

    private boolean populateFromConfig = true;
    private boolean populateFromYoutube = true;
    private boolean populateInitialData = true;

    public SQLiteDBCreator() {
    }

    public boolean isPopulateFromConfig() {
        return populateFromConfig;
    }

    public void setPopulateFromConfig(boolean populateFromConfig) {
        this.populateFromConfig = populateFromConfig;
    }

    public boolean isPopulateFromYoutube() {
        return populateFromYoutube;
    }

    public void setPopulateFromYoutube(boolean populateFromYoutube) {
        this.populateFromYoutube = populateFromYoutube;
    }

    @Override
    public void createDatabase(Config config, DBSchema dbSchema, Path dbPath) throws DBException {
        deleteDBIfExists(dbPath);

        Properties properties = createSQLiteConfig().toProperties();

        LOG.debug("Creating database '" + dbPath + "' - '" + properties + "'");

        // 'jdbc:log4' prefix required to trace all sql statements to logs/sql.log file
        try (Connection connection = DriverManager.getConnection("jdbc:log4jdbc:sqlite:" + dbPath, properties)) {
            DBSchemaCreator schemaCreator = new SQLiteDBSchemaCreator();
            schemaCreator.createSchema(dbSchema, connection);

            if (populateFromConfig) {
                new ConfigContentPopulator(config).populateDB(connection);
            }

            if (populateFromYoutube) {
                Credential credential = Auth.authorize(Lists.newArrayList("https://www.googleapis.com/auth/youtube.readonly"), "cyberfit_dbmaker");
                new YoutubeContentPopulator(new YoutubeVideoInfoProvider(credential)).populateDB(connection);
            }

            if (populateInitialData) {
                new InitialDataPopulator().populateDB(connection);
            }
        } catch (SQLException | IOException e) {
            LOG.error("Stopping because of: " + e.getMessage(), e);
        }
    }

    private void deleteDBIfExists(Path dbPath) {
        if (DELETE_DB_FILE_IF_EXISTS) {
            try {
                Files.deleteIfExists(dbPath);
            } catch (IOException ignore) {
            }
        }
    }

    private SQLiteConfig createSQLiteConfig() {
        SQLiteConfig sqliteConfig = new SQLiteConfig();
        sqliteConfig.setPragma(SQLiteConfig.Pragma.FOREIGN_KEYS, "ON");
        sqliteConfig.setUserVersion(DBSchema.VERSION);
        sqliteConfig.setSharedCache(true);
        return sqliteConfig;
    }
}
