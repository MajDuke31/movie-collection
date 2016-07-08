// DatabaseConnector.java
// Provides easy connection and creation of Movies database.
package edu.mobileappdevii.exercises.moviecollection;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;

public class DatabaseConnector {
    // Database name
    private static final String DATABASE_NAME = "MovieCollection";
    private SQLiteDatabase database; // Database object
    private DatabaseOpenHelper databaseOpenHelper; // Database helper

    // Public constructor for DatabaseConnector
    public DatabaseConnector(Context context)
    {
        // Create a new DatabaseOpenHelper
        databaseOpenHelper = new DatabaseOpenHelper(context, DATABASE_NAME, null, 1);
    } // End DatabaseConnector constructor

    // Open the database connection
    public void open() throws SQLException
    {
        // Create or open a database for reading/writing
        database = databaseOpenHelper.getWritableDatabase();
    } // End method open

    // Close the database connection
    public void close()
    {
        if (database != null) {
            database.close(); // Close the database connection
        }
    } // End method close

    // Inserts a new movie in the database
    public void insertMovie(String title, String year, String director,
                            String mpRating, String runtime) throws SQLException {
        ContentValues newMovie = new ContentValues();
        newMovie.put("title", title);
        newMovie.put("year", year);
        newMovie.put("director", director);
        newMovie.put("mp_rating", mpRating);
        newMovie.put("runtime", runtime);

        open(); // Open the database
        database.insert("movies", null, newMovie);
        close(); // Close the database
    } // End method insertMovie

    public void updateMovie(long id, String title, String year, String director,
                            String mpRating, String runtime) throws SQLException {
        ContentValues editMovie = new ContentValues();
        editMovie.put("title", title);
        editMovie.put("year", year);
        editMovie.put("director", director);
        editMovie.put("mp_rating", mpRating);
        editMovie.put("runtime", runtime);

        open(); // Open the database
        database.update("movies", editMovie, "_id=" + id, null);
        close(); // Close the database
    } // End method updateMovie

    // Return a Cursor with all movie information in the database
    public Cursor getAllMovies()
    {
        return database.query("movies", new String[] {"_id", "title"},
                null, null, null, null, "title");
    } // End method getAllMovies

    // Get a Cursor containing all information about the movie specified
    // by the given id
    public Cursor getOneMovie(long id)
    {
        return database.query("movies", null, "_id=" + id, null, null, null, null);
    } // End method getOneMovie

    // Delete the movie specified by the given id
    public void deleteMovie(long id) throws SQLException
    {
        open(); // Open the database
        database.delete("movies", "_id=" + id, null);
        close(); // Close the database
    } // End method deleteMovie

    private class DatabaseOpenHelper extends SQLiteOpenHelper
    {
        // Public constructor
        public DatabaseOpenHelper(Context context, String name,
                                  SQLiteDatabase.CursorFactory factory, int version)
        {
            super(context, name, factory, version);
        } // End DatabaseOpenHelper constructor

        // Creates the movies table when the database is created
        @Override
        public void onCreate(SQLiteDatabase db)
        {
            // Query to create a new table named movies
            String createQuery = "CREATE TABLE movies" +
                    "(_id integer primary key autoincrement," +
                    "title TEXT, year TEXT," +
                    "director TEXT, mp_rating TEXT," +
                    "runtime TEXT);";
            db.execSQL(createQuery); // Execute the query
        } // End method onCreate

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
        } // End method onUpgrade
    } // End class DatabaseOpenHelper
} // End class DatabaseConnector
