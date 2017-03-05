package com.example.android.booksapi;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays information about a Book.
 */
public class MainActivity extends AppCompatActivity {

    // Tag for the log messages
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    // URL to query the Google Books API for book information
    private static String GBOOKS_REQUEST_URL = "";

    // Adapter for the list of Books
    private BookAdapter mAdapter;

    // TextView that is displayed when the list is empty
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the content of the activity to use the activity_main.xml layout file
        setContentView(R.layout.activity_main);

        // Find a reference to the {@link ListView} in the layout
        ListView bookListView = (ListView) findViewById(R.id.list);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        bookListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of books as input
        mAdapter = new BookAdapter(this, new ArrayList<Book>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        bookListView.setAdapter(mAdapter);

        // Find the view that shows the search button
        Button searchButton = (Button) findViewById(R.id.btn_search);
        // Set a click listener to launch the network query when the search
        // button is clicked
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitSearch(view);
            }
        });

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected book.
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current book that was clicked on
                Book currentBook = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri bookUri = Uri.parse(currentBook.getBookLink());

                // Create a new intent to view the book URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });
    }

    /**
     * This method is called when the Search button is clicked
     */
    private void submitSearch(View view) {
        // Find the view that shows the search text entered by the user
        EditText searchEditText = (EditText) findViewById(R.id.et_subject);
        // Get the text from that view replacing any spaces with "+"
        String subject = searchEditText.getText().toString().replaceAll(" ", "+");
        // Dismiss the keyboard
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);

        // Ensure the user entered some text
        if (subject == null || subject.equals("")) {
            Toast.makeText(this, getString(R.string.no_query_text), Toast.LENGTH_LONG).show();
        } else {
            // Now we can clear the EditText field
            searchEditText.setText("");

            // Set the Query URL with the search text. Limit the results to 10.
            GBOOKS_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?q=" + subject + "&maxResults=10\n";
        }

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Start the AsyncTask to fetch the book data
            BookAsyncTask task = new BookAsyncTask();
            task.execute(GBOOKS_REQUEST_URL);
        } else {
            // Otherwise, display error

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    /**
     * {@link AsyncTask} to perform the network request on a background thread, and then
     * update the UI with the list of books in the response.
     *
     * AsyncTask has three generic parameters: the input type, a type used for progress updates, and
     * an output type. Our task will take a String URL, and return an Book. We won't do
     * progress updates, so the second generic is just Void.
     *
     * We'll only override two of the methods of AsyncTask: doInBackground() and onPostExecute().
     * The doInBackground() method runs on a background thread, so it can run long-running code
     * (like network activity), without interfering with the responsiveness of the app.
     * Then onPostExecute() is passed the result of doInBackground() method, but runs on the
     * UI thread, so it can use the produced data to update the UI.
     */
    private class BookAsyncTask extends AsyncTask<String, Void, List<Book>> {

        /**
         * This method runs on a background thread and performs the network request.
         * We should not update the UI from a background thread, so we return a list of
         * {@link Book}s as the result.
         */
        @Override
        protected List<Book> doInBackground(String... urls) {
            // Don't perform the request if there are no URLs, or the first URL is null
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            List<Book> result = QueryUtils.fetchBookData(urls[0]);
            return result;
        }

        /**
         * This method runs on the main UI thread after the background work has been
         * completed. This method receives as input, the return value from the doInBackground()
         * method. First we clear out the adapter, to get rid of book data from a previous
         * query to Google Books API. Then we update the adapter with the new list of books,
         * which will trigger the ListView to re-populate its list items.
         */
        @Override
        protected void onPostExecute(List<Book> data) {
            // Clear the adapter of previous book data
            mAdapter.clear();

            // If there is a valid list of {@link Book}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (data != null && !data.isEmpty()) {
                mAdapter.addAll(data);
            }
            if(mAdapter.isEmpty()) {
                // Set empty state text to display "No books found."
                mEmptyStateTextView.setText(R.string.no_books);

            }
        }
    }
}
