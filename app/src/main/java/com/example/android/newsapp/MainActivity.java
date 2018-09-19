package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    public static final String TAG = MainActivity.class.getSimpleName();

    private static final String REQUEST_URL = "http://content.guardianapis.com/search?q=debates&api-key=06c0e722-0793-47ab-aa2a-40c40749af63";

    TextView mEmptyStateTextView;
    View loadingIndicator;
    public ListView newsListView;
    NewsAdapter newsAdapter;
    RelativeLayout searchLayout;
    EditText searchTextBox;
    Button searchButton;
    String searchString;

    /**
     * Constant value for the earthquake loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int NEWS_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(TAG, "onCreate: started");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initControls();

        // Create a new adapter that takes an empty list of news as input
        newsAdapter = new NewsAdapter(this, new ArrayList<News>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        newsListView.setAdapter(newsAdapter);

        // Get a reference to the LoaderManager, in order to interact with loaders.
        LoaderManager loaderManager = getLoaderManager();
        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        loaderManager.initLoader(NEWS_LOADER_ID, null, this);

        Log.d(TAG, "onCreate: ended");
    }

    private void initControls() {
        mEmptyStateTextView = findViewById(R.id.empty_view);
        loadingIndicator = findViewById(R.id.loading_indicator);
        // Find a reference to the {@link ListView} in the layout
        newsListView = findViewById(R.id.list);

        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if(!isConnected){
            loadingIndicator.setVisibility(View.GONE);
            mEmptyStateTextView.setVisibility(View.VISIBLE);
            mEmptyStateTextView.setText(R.string.no_news);
            return;
        }
        setNewsListView(newsListView);

        searchLayout = findViewById(R.id.search_overlay);
        searchTextBox = findViewById(R.id.search_edit_text);
        searchButton = findViewById(R.id.search_button);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_search:
                showSearchOverlay();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSearchOverlay() {
        searchLayout.setVisibility(View.VISIBLE);
        newsListView.setVisibility(View.GONE);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchString = searchTextBox.getText().toString();
                if(TextUtils.isEmpty(searchString)){
                    Toast.makeText(getApplicationContext(), "Enter a word to search", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader: started");

        /*SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));
*/
        Uri baseUri = Uri.parse(REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
/*

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", "10");
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);
*/

        Log.d(TAG, "onCreateLoader: ended");
        return new NewsLoader(this, uriBuilder.toString());
    }


    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> newsList) {
        Log.d(TAG, "onLoadFinished: started");

        // Hide loading indicator because the data has been loaded
        loadingIndicator.setVisibility(View.GONE);

        // Clear the adapter of previous earthquake data
        newsAdapter.clear();

        if (newsList == null || newsList.size() == 0) {
            // Set empty state text to display "No earthquakes found."
            mEmptyStateTextView.setVisibility(View.VISIBLE);
            mEmptyStateTextView.setText(R.string.no_news);
            return;
        }else{
            Log.d(TAG, "onPostExecute: earthquakes.size() :: "+newsList.size());
            newsAdapter.addAll(newsList);
        }
        // Find a reference to the {@link ListView} in the layout
        ListView newsListView = getNewsListView();

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        newsListView.setAdapter(newsAdapter);
        Log.d(TAG, "onLoadFinished: ended");
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        Log.d(TAG, "onLoaderReset: started");
        // Clear the adapter of previous earthquake data
        newsAdapter.clear();
        Log.d(TAG, "onLoaderReset: ended");
    }

    public ListView getNewsListView() {
        return newsListView;
    }

    public void setNewsListView(ListView newsListView) {
        this.newsListView = newsListView;
    }
}
