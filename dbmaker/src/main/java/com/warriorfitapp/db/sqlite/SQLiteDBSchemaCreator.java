package com.warriorfitapp.db.sqlite;

import com.warriorfitapp.db.DBSchemaCreator;
import com.warriorfitapp.db.sqlite.schema.DBSchema;
import com.warriorfitapp.db.sqlite.schema.table.DBTable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Andrii Kovalov
 */
public class SQLiteDBSchemaCreator implements DBSchemaCreator {
    public static final Logger LOG = LoggerFactory.getLogger(SQLiteDBSchemaCreator.class);

    @Override
    public void createSchema(DBSchema dbSchema, Connection connection) throws SQLException {
        createTables(dbSchema, connection);
    }

    private void createTables(DBSchema dbSchema, Connection connection) throws SQLException {
        LOG.debug("Create tables " + dbSchema.getTables());
        try (Statement statement = connection.createStatement()) {
            // create tables
            for (DBTable dbTable : dbSchema.getTables()) {
                String sql = dbTable.getCreateSqlStatement();
                statement.executeUpdate(sql);
            }
        }
    }
}
