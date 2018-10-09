package com.example.rxjavaroom.activities;

import android.content.DialogInterface;
import android.os.Bundle;

import com.example.rxjavaroom.adapters.BooksAdapter;
import com.example.rxjavaroom.R;
import com.example.rxjavaroom.db.BookDao;
import com.example.rxjavaroom.db.BookDatabase;
import com.example.rxjavaroom.model.Book;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements BooksAdapter.OnItemClickListener {
    private final List<Book> books = new ArrayList<>();
    private BookDao bookDao;
    private CompositeDisposable disposable;
    private BookDatabase bookDatabase;
    private BooksAdapter booksAdapter;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        disposable = new CompositeDisposable();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        bookDatabase = BookDatabase.getInstance(MainActivity.this);
        bookDao = bookDatabase.getBookDao();

        booksAdapter = new BooksAdapter(this, books);
        booksAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(booksAdapter);

        setData();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewDialog(false, null, 0);
            }
        });
    }

    private void viewDialog(final boolean isEdit, final Book myBook, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);
        View alertView = getLayoutInflater().inflate(R.layout.edit_item_dialog, null);
        final EditText titleEt = alertView.findViewById(R.id.title);
        final EditText authorEt = alertView.findViewById(R.id.author);
        final EditText descriptionEt = alertView.findViewById(R.id.description);

        if (isEdit) {
            titleEt.setText(myBook.getTitle());
            titleEt.setSelection(titleEt.getText().length());
            authorEt.setText(myBook.getAuthor());
            authorEt.setSelection(authorEt.getText().length());
            descriptionEt.setText(myBook.getDescription());
            descriptionEt.setSelection(descriptionEt.getText().length());
        }


        builder.setView(alertView);
        builder.setTitle(R.string.add_book);

        if (isEdit)
            builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    bookDao.deleteBook(myBook);
                    books.remove(myBook);
                    booksAdapter.notifyItemRemoved(position);
                }
            });


        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final Book book = myBook == null ? new Book() : myBook;
                book.setTitle(titleEt.getText().toString());
                book.setAuthor(authorEt.getText().toString());
                book.setDescription(descriptionEt.getText().toString());
                if (!isEdit) {
                    String[] imagesArr = getResources().getStringArray(R.array.images);
                    int pos = generateRandom();
                    book.setImageUrl(imagesArr[pos]);
                    bookDao.insertBook(book);
                    books.add(book);
                    booksAdapter.notifyItemInserted(books.size() - 1);
                } else {
                    book.setImageUrl(myBook.getImageUrl());
                    bookDao.updateBook(book);
                    books.set(position, book);
                    booksAdapter.notifyItemChanged(position);
                }

            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setData() {
        disposable.add(bookDao.getBooks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<Book>>() {
                    @Override
                    public void onSuccess(List<Book> bookList) {
                        books.clear();
                        books.addAll(bookList);
                        booksAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                    }
                }));
    }

    private int generateRandom() {
        Random random = new Random();
        int max = 4;
        int min = 0;
        return random.nextInt(max - min + 1) + min;
    }

    @Override
    public void onItemClick(Book book, int position) {
        viewDialog(true, book, position);
    }

    @Override
    protected void onDestroy() {
        bookDatabase.cleanUp();
        disposable.dispose();
        super.onDestroy();
    }
}
