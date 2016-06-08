package com.warriorfitapp.db.sqlite.schema.table;

/**
 * @author Andrii Kovalov
 */
public abstract class DBTable implements com.warriorfitapp.db.sqlite.schema.DBEntity {
    public static final String COLUMN_ID = "_id";

    private String name;

    public DBTable(String name) {
        this.name = name;
    }

    public abstract String[] getColumnNames();

    public String qualifiedColumnName(String columnName) {
        return getName() + "." + columnName;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getDropSqlStatement() {
        return "drop table if exists " + name;
    }

    @Override
    public String toString() {
        return name;
    }

    public String foreignKeyConstraint(String colName, String foreignTable) {
        return "foreign key (" + colName + ") references " + foreignTable + "(" + COLUMN_ID + ")";
    }
}
