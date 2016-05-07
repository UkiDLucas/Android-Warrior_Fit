package com.cyberwalkabout.cyberfit.db.sqlite;

import com.cyberwalkabout.cyberfit.db.DBContentPopulator;
import com.cyberwalkabout.cyberfit.db.DBException;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.UserTable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Andrii Kovalov
 */
public class InitialDataPopulator implements DBContentPopulator {
    private static final Logger LOG = LoggerFactory.getLogger(InitialDataPopulator.class);

    @Override
    public void populateDB(Connection connection) throws DBException {
        insertDefaultLocalUser(connection);
    }

    private void insertDefaultLocalUser(Connection connection) {
        String sql = "insert into " + UserTable.TABLE_NAME +
                " ("
                + UserTable.COLUMN_ACCOUNT_TYPE + ", "
                + UserTable.COLUMN_ACTIVE
                + ") values (?,?)";

        try {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, "LOCAL");
                statement.setInt(2, 1);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            LOG.error("Couldn't insert entry into '" + UserTable.TABLE_NAME + "' table", e);
        }
    }
}
