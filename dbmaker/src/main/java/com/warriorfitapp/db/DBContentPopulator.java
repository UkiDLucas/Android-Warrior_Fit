package com.warriorfitapp.db;

import java.sql.Connection;

/**
 * @author Andrii Kovalov
 */
public interface DBContentPopulator {

    void populateDB(Connection connection) throws DBException;
}
