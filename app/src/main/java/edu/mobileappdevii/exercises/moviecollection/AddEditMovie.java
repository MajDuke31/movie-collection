// AddEditMovie.java
// Activity for adding a new entry to or
// editing an existing entry in the Movie Collection app.
package edu.mobileappdevii.exercises.moviecollection;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import java.sql.SQLException;

public class AddEditMovie extends AppCompatActivity {
    // EditTexts for movie information
    private EditText titleEditText;
    private EditText yearEditText;
    private EditText directorEditText;
    private EditText mpRatingEditText;
    private EditText runtimeEditText;

    private long rowID; // Id of movie being edited, if any

    // Called when the Activity is first started
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Call super's onCreate
        setContentView(R.layout.add_movie); // Inflate the UI

        titleEditText = (EditText) findViewById(R.id.titleEditText);
        yearEditText = (EditText) findViewById(R.id.yearEditText);
        directorEditText = (EditText) findViewById(R.id.directorEditText);
        mpRatingEditText = (EditText) findViewById(R.id.mpRatingEditText);
        runtimeEditText = (EditText) findViewById(R.id.runtimeEditText);

        Bundle extras = getIntent().getExtras(); // Get Bundle of extras

        if (extras != null)
        {
            rowID = extras.getLong("row_id");
            titleEditText.setText(extras.getString("title"));
            yearEditText.setText(extras.getString("year"));
            directorEditText.setText(extras.getString("director"));
            mpRatingEditText.setText(extras.getString("mp_rating"));
            runtimeEditText.setText(extras.getString("runtime"));
        } // End if

        // Set event listener for the Save Movie Button
        Button saveMovieButton = (Button) findViewById(R.id.saveMovieButton);
        saveMovieButton.setOnClickListener(saveMovieButtonClicked);
    } // End method onCreate

    // Responds to event generated when user clicks the Done button
    private OnClickListener saveMovieButtonClicked = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (titleEditText.getText().length() != 0)
            {
                AsyncTask<Object, Object, Object> saveMovieTask =
                        new AsyncTask<Object, Object, Object>()
                        {
                            @Override
                            protected Object doInBackground(Object... params) {
                                try {
                                    saveMovie(); // Save movie to the database
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                return null;
                            } // End method doInBackground

                            @Override
                            protected void onPostExecute(Object result)
                            {
                                finish(); // Return to the previous activity
                            } // End method onPostExecute
                        }; // End AsyncTask

                // Save the movie to the database using a separate thread
                saveMovieTask.execute((Object[]) null);
            } // End if
            else
            {
                // Create a new AlertDialog Builder
                AlertDialog.Builder builder = new AlertDialog.Builder(AddEditMovie.this);

                // Set dialog title & message, and provide Button to dismiss
                builder.setTitle("Movie Title is Required");
                builder.setMessage("You must enter a movie title");
                builder.setPositiveButton("OK", null);
                builder.show(); // Display the Dialog
            } // End else
        } // End OnClickListener saveMovieButtonClicked
    };

    // Saves movie information to the database
    private void saveMovie() throws SQLException {
        // Get DatabaseConnector to interact with the SQLite database
        DatabaseConnector databaseConnector = new DatabaseConnector(this);

        if (getIntent().getExtras() == null)
        {
            // Insert the movie information into the database - new movie
            databaseConnector.insertMovie(
                    titleEditText.getText().toString(),
                    yearEditText.getText().toString(),
                    directorEditText.getText().toString(),
                    mpRatingEditText.getText().toString(),
                    runtimeEditText.getText().toString());
        } // End if
        else // Edit the movie
        {
            databaseConnector.updateMovie(rowID,
                    titleEditText.getText().toString(),
                    yearEditText.getText().toString(),
                    directorEditText.getText().toString(),
                    mpRatingEditText.getText().toString(),
                    runtimeEditText.getText().toString());
        } // End else
    } // End class SaveMovie
} // End class AddEditMovie
