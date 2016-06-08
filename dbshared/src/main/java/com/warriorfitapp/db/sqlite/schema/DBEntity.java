package com.warriorfitapp.db.sqlite.schema;

/**
 * @author Andrii Kovalov
 */
public interface DBEntity {
    String getCreateSqlStatement();

    String getDropSqlStatement();
}
