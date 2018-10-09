# RxJavaRoom
An application integrating RxJava and Room Database


It’s not a difficult task for an Android developer to convert raw data into a structured database for internal storage. This is done using the most reliable language — SQL. The inbuilt SQLite core library is within the Android OS. It will handle CRUD (Create, Read, Update and Delete) operations required for a database. Java classes and interfaces for SQLite are provided by the android.database. SQLite maintains an effective database management system. But this conventional method has its own disadvantages.

* You have to write long repetitive code, which will be time-consuming as well as prone to mistakes.
* It is very difficult to manage SQL queries for a complex relational database.

To overcome this, Google has introduced Room Persistence Library. This acts as an abstraction layer for the existing SQLite APIs. All the required packages, parameters, methods, and variables are imported into an Android project by using simple annotations.

1. Create a data model class for the database table and annotate its table name and primary key.

```
@Entity(tableName = Constants.TABLE_NAME_BOOKS)
public class Book {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String title;
    private String description;
    private String author;
    private String imageUrl;
    
    .....
}
```

2. Create an interface class for Database access. Create abstract methods for CRUD operations. Add custom SQL query as a method.

```
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
```

3. Create a Database class for database implementation.

```
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
```

