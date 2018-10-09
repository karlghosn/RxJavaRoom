package com.example.rxjavaroom.db;

import android.content.Context;

import com.example.rxjavaroom.model.Book;
import com.example.rxjavaroom.util.Constants;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Book.class}, version = 1)
public abstract class BookDatabase extends RoomDatabase {

    public abstract BookDao getBookDao();

    private static BookDatabase bookDB;

    // synchronized is use to avoid concurrent access in multithred environment
    public static /*synchronized*/ BookDatabase getInstance(Context context) {
        if (null == bookDB) {
            bookDB = buildDatabaseInstance(context);
        }
        return bookDB;
    }

    private static BookDatabase buildDatabaseInstance(Context context) {
        return Room.databaseBuilder(context,
                BookDatabase.class,
                Constants.DB_NAME).allowMainThreadQueries().build();
    }

    public void cleanUp() {
        bookDB = null;
    }
}