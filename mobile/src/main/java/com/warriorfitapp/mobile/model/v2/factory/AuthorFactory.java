package com.warriorfitapp.mobile.model.v2.factory;

import android.database.Cursor;

import com.warriorfitapp.model.v2.ModelFactory;

/**
 * @author Andrii Kovalov
 */
public class AuthorFactory implements ModelFactory<com.warriorfitapp.model.v2.Author, Cursor> {

    private static final AuthorFactory INSTANCE = new AuthorFactory();

    public static AuthorFactory getInstance() {
        return INSTANCE;
    }

    private AuthorFactory() {
    }

    @Override
    public com.warriorfitapp.model.v2.Author create(Cursor cursor) {
        com.warriorfitapp.model.v2.Author author = new com.warriorfitapp.model.v2.Author();

        if (!cursor.isNull(cursor.getColumnIndex(com.warriorfitapp.db.sqlite.schema.table.AuthorTable.COLUMN_ID))) {
            author.setId(cursor.getLong(cursor.getColumnIndex(com.warriorfitapp.db.sqlite.schema.table.AuthorTable.COLUMN_ID)));
        }

        author.setName(cursor.getString(cursor.getColumnIndex(com.warriorfitapp.db.sqlite.schema.table.AuthorTable.COLUMN_NAME)));
        return author;
    }
}
