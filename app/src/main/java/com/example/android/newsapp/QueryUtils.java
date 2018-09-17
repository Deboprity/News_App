package com.example.android.newsapp;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {

    private static final String TAG = QueryUtils.class.getSimpleName();

    private QueryUtils() {
    }

    /**
     * Returns new URL object from the given string URL.
     */
    public static URL createUrl(String stringUrl) {
        URL url = null;

        //stringUrl = stringUrl + "&starttime="+"2018-01-01"+"&endtime="+getCurrentDate()+"&minmagnitude=6.2";
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(TAG, "Error with creating URL", exception);
            return null;
        }
        Log.d(TAG, "createUrl: "+stringUrl);
        return url;
    }

    public static int getSectionColor(Context context, String secName) {
        Log.d(TAG, "getSectionColor: started");
        int sectionColorResourceId = 0;
        switch (secName) {
            case "News":
                sectionColorResourceId = R.color.News;
                break;
            case "Politics":
                sectionColorResourceId = R.color.Politics;
                break;
            case "Business":
                sectionColorResourceId = R.color.Business;
                break;
            case "Media":
                sectionColorResourceId = R.color.Media;
                break;
            case "World news":
                sectionColorResourceId = R.color.WorldNews;
                break;
            case "Opinion":
                sectionColorResourceId = R.color.Opinion;
                break;
            case "Science":
                sectionColorResourceId = R.color.Science;
                break;
            case "Society":
                sectionColorResourceId = R.color.Society;
                break;
            case "Technology":
                sectionColorResourceId = R.color.Technology;
                break;
            default:
                sectionColorResourceId = R.color.Default;
                break;
        }

        Log.d(TAG, "getMagnitudeColor: ends");
        return ContextCompat.getColor(context, sectionColorResourceId);
    }





}