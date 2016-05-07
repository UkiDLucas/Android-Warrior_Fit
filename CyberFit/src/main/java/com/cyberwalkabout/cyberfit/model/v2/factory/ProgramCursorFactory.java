package com.cyberwalkabout.cyberfit.model.v2.factory;

import android.database.Cursor;

import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.ProgramTable;
import com.cyberwalkabout.cyberfit.model.v2.ModelFactory;
import com.cyberwalkabout.cyberfit.model.v2.Program;

/**
 * @author Andrii Kovalov
 */
public class ProgramCursorFactory implements ModelFactory<Program, Cursor> {

    private static final ProgramCursorFactory INSTANCE = new ProgramCursorFactory();

    public static ProgramCursorFactory getInstance() {
        return INSTANCE;
    }

    private ProgramCursorFactory() {
    }

    @Override
    public Program create(Cursor cursor) {
        Program program = new Program();

        program.setId(cursor.getLong(cursor.getColumnIndex(ProgramTable.COLUMN_ID)));
        program.setActive(cursor.getInt(cursor.getColumnIndex(ProgramTable.COLUMN_ACTIVE)) == 1);
        program.setPremium(cursor.getInt(cursor.getColumnIndex(ProgramTable.COLUMN_PREMIUM)) == 1);
        program.setAuthorId(cursor.getLong(cursor.getColumnIndex(ProgramTable.COLUMN_AUTHOR_ID)));
        program.setName(cursor.getString(cursor.getColumnIndex(ProgramTable.COLUMN_NAME)));
        program.setDescription(cursor.getString(cursor.getColumnIndex(ProgramTable.COLUMN_DESCRIPTION)));

        return program;
    }
}
