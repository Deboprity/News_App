package com.example.android.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<News>> {

    private static final String TAG = NewsLoader.class.getName();

    private static final String JSON_RESP_OBJ_NAME = "response";
    private static final String JSON_RESULT_NAME = "results";
    private static final String JSON_SECTION_NAME = "sectionName";
    private static final String JSON_WEB_TITLE_NAME = "webTitle";
    private static final String JSON_WEB_PUBLICATION_DATE_NAME = "webPublicationDate";
    private static final String JSON_WEB_URL_NAME = "webUrl";

    /** Query URL */
    private String mUrl;

    NewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }


    @Override
    protected void onStartLoading() {
        Log.d(TAG, "onStartLoading: started");
        forceLoad();
        Log.d(TAG, "onStartLoading: ended");
    }


    @Override
    public List<News> loadInBackground() {
        Log.d(TAG, "loadInBackground: started");
        // Don't perform the request if there are no URLs, or the first URL is null.
        if (mUrl == null) {
            return null;
        }

        ArrayList<News> newsList = new ArrayList<>();

        // Create URL object
        URL url = QueryUtils.createUrl(mUrl);

        String JSONresponse;

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            JSONresponse = makeHttpRequest(url);

            JSONObject root = new JSONObject(JSONresponse);
            
            JSONObject responseObj = root.getJSONObject(JSON_RESP_OBJ_NAME);

            JSONArray newsArray = responseObj.getJSONArray(JSON_RESULT_NAME);

            Log.d(TAG, "No. of news returned : "+newsArray.length());
            for (int i = 0; i < newsArray.length(); i++) {
                JSONObject currentEarthquake = newsArray.getJSONObject(i);

                String sectionName = currentEarthquake.optString(JSON_SECTION_NAME);
                String webTitle = currentEarthquake.optString(JSON_WEB_TITLE_NAME);
                String webPublicationDate = currentEarthquake.optString(JSON_WEB_PUBLICATION_DATE_NAME);
                String webURL = currentEarthquake.optString(JSON_WEB_URL_NAME);

                News news = new News(sectionName, webTitle, webPublicationDate, webURL);

                newsList.add(news);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(TAG, "Problem parsing the news JSON results", e);
        } catch (IOException e) {
            Log.e(TAG, "IOException : ", e);
        }

        Log.d(TAG, "loadInBackground: ends");
        // Return the list of earthquakes
        return newsList;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(60000 /* milliseconds */);
            urlConnection.setConnectTimeout(100000 /* milliseconds */);
            urlConnection.connect();

            Boolean success = false;

            int responseCode  = urlConnection.getResponseCode();

            Log.d(TAG, "makeHttpRequest: responseCode :: "+responseCode);

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (responseCode == 200) {
                success = true;
                Log.d(TAG, "makeHttpRequest: success response");
            } else if (responseCode == HttpURLConnection.HTTP_MOVED_PERM || responseCode == HttpURLConnection.HTTP_MOVED_TEMP || responseCode == HttpURLConnection.HTTP_SEE_OTHER) {
                // get redirect url from "location" header field
                String newUrl = urlConnection.getHeaderField("Location");

                urlConnection = (HttpURLConnection) new URL(newUrl).openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(60000 /* milliseconds */);
                urlConnection.setConnectTimeout(100000 /* milliseconds */);
                urlConnection.connect();
                Log.d(TAG, "makeHttpRequest: newUrl :: "+newUrl);
                success = true;
            }else {
                Log.e(TAG, "Error response code: " + urlConnection.getResponseCode());
            }
            if(success) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
        } catch (IOException e) {
            Log.e(TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }
        }
        Log.d(TAG, "makeHttpRequest: jsonResponse :: "+jsonResponse);
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        Log.d(TAG, "readFromStream: ended");
        return output.toString();
    }

}
