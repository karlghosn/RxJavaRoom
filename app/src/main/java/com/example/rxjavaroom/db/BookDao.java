package com.example.rxjavaroom.db;

import com.example.rxjavaroom.model.Book;
import com.example.rxjavaroom.util.Constants;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import io.reactivex.Single;

@Dao
public interface BookDao {
    @Query("SELECT * FROM " + Constants.TABLE_NAME_BOOKS)
    Single<List<Book>> getBooks();

    @Insert
    void insertBook(Book book);

    @Update
    void updateBook(Book book);

    @Delete
    void deleteBook(Book book);
}
