// ViewMovie.java
// Activity for viewing a single movie.
package edu.mobileappdevii.exercises.moviecollection;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import java.sql.SQLException;

public class ViewMovie extends AppCompatActivity
{
    // The TextView's representing each field of each record in the movie database
    private TextView titleTextView;
    private TextView yearTextView;
    private TextView directorTextView;
    private TextView mpRatingTextView;
    private TextView runtimeTextView;
    private long rowID; // Holds the ID of the current row

    // Called when the activity is first created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_movie);

        // Get the TextViews
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        yearTextView = (TextView) findViewById(R.id.yearTextView);
        directorTextView = (TextView) findViewById(R.id.directorTextView);
        mpRatingTextView = (TextView) findViewById(R.id.mpRatingTextView);
        runtimeTextView = (TextView) findViewById(R.id.runtimeTextView);

        // Get the selected contact's row ID
        Bundle extras = getIntent().getExtras();
        rowID = extras.getLong(MovieCollection.ROW_ID);
    } // End method onCreate

    // Called when the activity is first created
    @Override
    protected void onResume()
    {
        super.onResume();

        // Create new LoadContactTask and execute it
        new LoadMovieTask().execute(rowID);
    } // End method onResume

    // Performs database query outside GUI thread
    private class LoadMovieTask extends AsyncTask<Long, Object, Cursor> {
        private DatabaseConnector databaseConnector = new DatabaseConnector(ViewMovie.this);

        // Perform the database access
        @Override
        protected Cursor doInBackground(Long... params) {
            try {
                databaseConnector.open();

                // Get a cursor containing all data on given entry
                return databaseConnector.getOneMovie(params[0]);
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        } // End method doInBackground

        // Use the Cursor returned from doInBackground
        @Override
        protected void onPostExecute(Cursor result)
        {
            super.onPostExecute(result);

            result.moveToFirst(); // Move to the first item

            // Get the column index for each data item
            int titleIndex = result.getColumnIndex("title");
            int yearIndex = result.getColumnIndex("year");
            int directorIndex = result.getColumnIndex("director");
            int mpRatingIndex = result.getColumnIndex("mp_rating");
            int runtimeIndex = result.getColumnIndex("runtime");

            // Fill TextViews with the retrieved data
            titleTextView.setText(result.getString(titleIndex));
            yearTextView.setText(result.getString(yearIndex));
            directorTextView.setText(result.getString(directorIndex));
            mpRatingTextView.setText(result.getString(mpRatingIndex));
            runtimeTextView.setText(result.getString(runtimeIndex));

            result.close(); // Close the result cursor
            databaseConnector.close(); // Close database connection
        } // End method onPostExecute
    } // End class LoadMovieTask

    // Create the Activity's menu from a menu resource XML file
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.view_movie_menu, menu);
        return true;
    } // End method onCreateOptionsMenu

    // Handle choice from options menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) // Switch based on selected MenuItem's ID
        {
            case R.id.editItem:
                // Create an Intent to launch the AddEditMovie Activity
                Intent addEditMovie = new Intent(this, AddEditMovie.class);

                // Pass the selecte movie's data as extras with the Intent
                addEditMovie.putExtra(MovieCollection.ROW_ID, rowID);
                addEditMovie.putExtra("title", titleTextView.getText());
                addEditMovie.putExtra("year", yearTextView.getText());
                addEditMovie.putExtra("director", directorTextView.getText());
                addEditMovie.putExtra("mp_rating", mpRatingTextView.getText());
                addEditMovie.putExtra("runtime", runtimeTextView.getText());
                startActivity(addEditMovie); // Start the Activity
                return true;
            case R.id.deleteItem:
                deleteMovie(); // Delete the displayed movie
                return true;
            default:
                return super.onOptionsItemSelected(item);
        } // End switch
    } // End method onOptionsItemSelected

    // Delete a movie
    private void deleteMovie() {
        // Create a new AlertDialog Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewMovie.this);

        builder.setTitle("Are You Sure?"); // Title bar string
        builder.setMessage("This will permanently delete the movie"); // Message to display

        // Provide an OK button that simply dismisses the dialog
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int button) {
                final DatabaseConnector databaseConnector = new DatabaseConnector(ViewMovie.this);

                // Create an AsyncTask that deletes the movie in another
                // thread, then calls finish after the deletion
                AsyncTask<Long, Object, Object> deleteTask = new AsyncTask<Long, Object, Object>() {
                    @Override
                    protected Object doInBackground(Long... params) {
                        try {
                            databaseConnector.deleteMovie(params[0]);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        return null;
                    } // End method doInBackground

                    @Override
                    protected void onPostExecute(Object result)
                    {
                        finish(); // Return to the MovieCollection Activity
                    } // End method onPostExecute
                }; // End new AsyncTask

                // Execute the AsyncTask to delete movie at rowID
                deleteTask.execute(rowID);
            } // End anonymous inner class
        }); // End call to method setPositiveButton

        builder.setNegativeButton("Cancel", null);
        builder.show(); // Display the dialog
    } // End method deleteMovie
} // End class ViewMovie
