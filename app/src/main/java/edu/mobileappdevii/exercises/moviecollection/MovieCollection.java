package edu.mobileappdevii.exercises.moviecollection;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.sql.SQLException;

public class MovieCollection extends AppCompatActivity {
    // Used as a key in key-value pair that's passed between activities
    public static final String ROW_ID = "row_id";
    private ListView movieListView;
    private SimpleCursorAdapter movieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_collection);
        movieListView = (ListView) findViewById(R.id.listView);
        movieListView.setOnItemClickListener(viewMovieListener);
        movieListView.setBackgroundColor(Color.BLACK);
        // Display message on empty list
        TextView emptyText = (TextView) View.inflate(this, R.layout.movie_list_empty_item, null);
        emptyText.setVisibility(View.GONE);

        ((ViewGroup) movieListView.getParent()).addView(emptyText);

        // Map each movie's title to a text view in the listview layout
        String[] from = new String[] { "title" };
        int[] to = new int[] { R.id.movieTextView };
        movieAdapter = new SimpleCursorAdapter(MovieCollection.this,
                R.layout.movie_list_item, null, from, to, 0);

        movieListView.setAdapter(movieAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Create a new GetContactsTask and execute it
        // on background thread gets all rows and
        // populates the list view
        new GetMoviesTask().execute((Object[]) null);
    }

    @Override
    protected void onStop() {
        Cursor cursor = movieAdapter.getCursor();
        // If not null deactivate it
        if (cursor != null) {
            cursor.close();
        }
        movieAdapter.changeCursor(null);
        super.onStop();
    }

    // Performs database query outside of GUI thread
    private class GetMoviesTask extends AsyncTask<Object, Object, Cursor> {
        private DatabaseConnector databaseConnector = new DatabaseConnector(MovieCollection.this);

        // Perform database access
        // Runs on the background thread
        @Override
        protected Cursor doInBackground(Object... params) {
            try {
                databaseConnector.open();
                return databaseConnector.getAllMovies();
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }

        // Use the cursor returned from doInBackground
        // Runs on the main UI thread
        @Override
        protected void onPostExecute(Cursor result) {
            movieAdapter.changeCursor(result);
            databaseConnector.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_collection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Create a new intent to launch AddEditMovieActivity
        Intent addNewMovie = new Intent(MovieCollection.this, AddEditMovie.class);
        startActivity(addNewMovie);

        return super.onOptionsItemSelected(item);
    }

    private OnItemClickListener viewMovieListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // Create an Intent to launch the ViewMovie Activity
            Intent viewMovie = new Intent(MovieCollection.this, ViewMovie.class);
            // Pass the selected movie's row ID as an extra to the Intent
            viewMovie.putExtra(ROW_ID, id);
            startActivity(viewMovie);
        }
    };
}
