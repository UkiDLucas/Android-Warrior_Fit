package com.warriorfitapp.mobile.model.v2.factory;

import android.database.Cursor;

import com.warriorfitapp.model.v2.Program;

/**
 * @author Andrii Kovalov
 */
public class ProgramCursorFactory implements com.warriorfitapp.model.v2.ModelFactory<Program, Cursor> {

    private static final ProgramCursorFactory INSTANCE = new ProgramCursorFactory();

    public static ProgramCursorFactory getInstance() {
        return INSTANCE;
    }

    private ProgramCursorFactory() {
    }

    @Override
    public com.warriorfitapp.model.v2.Program create(Cursor cursor) {
        com.warriorfitapp.model.v2.Program program = new com.warriorfitapp.model.v2.Program();

        program.setId(cursor.getLong(cursor.getColumnIndex(com.warriorfitapp.db.sqlite.schema.table.ProgramTable.COLUMN_ID)));
        program.setActive(cursor.getInt(cursor.getColumnIndex(com.warriorfitapp.db.sqlite.schema.table.ProgramTable.COLUMN_ACTIVE)) == 1);
        program.setPremium(cursor.getInt(cursor.getColumnIndex(com.warriorfitapp.db.sqlite.schema.table.ProgramTable.COLUMN_PREMIUM)) == 1);
        program.setAuthorId(cursor.getLong(cursor.getColumnIndex(com.warriorfitapp.db.sqlite.schema.table.ProgramTable.COLUMN_AUTHOR_ID)));
        program.setName(cursor.getString(cursor.getColumnIndex(com.warriorfitapp.db.sqlite.schema.table.ProgramTable.COLUMN_NAME)));
        program.setDescription(cursor.getString(cursor.getColumnIndex(com.warriorfitapp.db.sqlite.schema.table.ProgramTable.COLUMN_DESCRIPTION)));

        return program;
    }
}
