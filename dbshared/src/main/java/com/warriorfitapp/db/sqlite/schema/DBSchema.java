package com.warriorfitapp.db.sqlite.schema;

import com.warriorfitapp.db.sqlite.schema.table.DBTable;
import com.warriorfitapp.db.sqlite.schema.table.ExerciseSessionTable;
import com.warriorfitapp.db.sqlite.schema.table.ExerciseTable;
import com.warriorfitapp.db.sqlite.schema.table.LocationInfoTable;
import com.warriorfitapp.db.sqlite.schema.table.ProgramTable;
import com.warriorfitapp.db.sqlite.schema.table.SubscribedProgramTable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrii Kovalov
 */
public class DBSchema {
    public static final String DEFAULT_NAME = "cyberfit_v2";
    public static final String DEFAULT_EXT = ".db";
    // always increase version after schema update
    public static final int VERSION = 10;

    public static final int PRE_RELEASE_1_VERSION = 3;
    public static final int RELEASE_1_VERSION = 9; // released on 24/09/2015

    private List<DBTable> tables = new ArrayList<>();

    private static volatile DBSchema DEFAULT_SCHEMA;

    public synchronized static DBSchema defaultSchema() {
        if (DEFAULT_SCHEMA == null) {

            List<DBTable> tables = new ArrayList<>();
            tables.add(com.warriorfitapp.db.sqlite.schema.table.AndroidMetadataTable.instance());
            tables.add(com.warriorfitapp.db.sqlite.schema.table.UserTable.instance());
            tables.add(com.warriorfitapp.db.sqlite.schema.table.SocialProfileTable.instance());
            tables.add(com.warriorfitapp.db.sqlite.schema.table.AuthorTable.instance());
            tables.add(ProgramTable.instance());
            tables.add(ExerciseTable.instance());
            tables.add(com.warriorfitapp.db.sqlite.schema.table.ExerciseToProgramTable.instance());
            tables.add(com.warriorfitapp.db.sqlite.schema.table.SelectedProgramTable.instance());
            tables.add(SubscribedProgramTable.instance());
            tables.add(com.warriorfitapp.db.sqlite.schema.table.FavoriteExerciseTable.instance());
            tables.add(ExerciseSessionTable.instance());
            tables.add(LocationInfoTable.instance());

            DEFAULT_SCHEMA = new DBSchema(tables);
        }

        return DEFAULT_SCHEMA;
    }

    public List<DBTable> getTables() {
        return tables;
    }

    private DBSchema(List<DBTable> tables) {
        this.tables = tables;
    }
}
