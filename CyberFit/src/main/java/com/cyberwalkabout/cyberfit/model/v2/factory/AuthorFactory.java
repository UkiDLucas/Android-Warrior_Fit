package com.cyberwalkabout.cyberfit.model.v2.factory;

import android.database.Cursor;

import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.AuthorTable;
import com.cyberwalkabout.cyberfit.model.v2.Author;
import com.cyberwalkabout.cyberfit.model.v2.ModelFactory;

/**
 * @author Andrii Kovalov
 */
public class AuthorFactory implements ModelFactory<Author, Cursor> {

    private static final AuthorFactory INSTANCE = new AuthorFactory();

    public static AuthorFactory getInstance() {
        return INSTANCE;
    }

    private AuthorFactory() {
    }

    @Override
    public Author create(Cursor cursor) {
        Author author = new Author();

        if (!cursor.isNull(cursor.getColumnIndex(AuthorTable.COLUMN_ID))) {
            author.setId(cursor.getLong(cursor.getColumnIndex(AuthorTable.COLUMN_ID)));
        }

        author.setName(cursor.getString(cursor.getColumnIndex(AuthorTable.COLUMN_NAME)));
        return author;
    }
}
