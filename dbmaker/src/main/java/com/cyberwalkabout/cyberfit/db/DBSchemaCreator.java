package com.cyberwalkabout.cyberfit.db;

import com.cyberwalkabout.cyberfit.db.sqlite.schema.DBSchema;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Andrii Kovalov
 */
public interface DBSchemaCreator {
    void createSchema(DBSchema dbSchema, Connection connection) throws SQLException;
}
