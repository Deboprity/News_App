package com.example.android.newsapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class NewsAdapter extends ArrayAdapter<News> {

    private static final String TAG = NewsAdapter.class.getSimpleName();

    public NewsAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    public NewsAdapter(@NonNull Context context, ArrayList<News> newsDetails) {
        super(context, 0, newsDetails);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Log.d(TAG, "getView: started");
        
        final News news ;
        View listView = convertView;

        if(listView == null){
            listView = LayoutInflater.from(getContext()).inflate(R.layout.news_list_item, parent, false);
        }

        news = getItem(position);

        TextView sectionName = (TextView) listView.findViewById(R.id.section_info);
        TextView webTitle = (TextView) listView.findViewById(R.id.news_info);
        TextView webPublicationDate = (TextView) listView.findViewById(R.id.date_info);


        webTitle.setText(news.getWebTitle());
        webPublicationDate.setText(news.getWebPublicationDate());
        sectionName.setText(news.getSectionName());

        final String url = news.getWebURL();

        // Set the proper background color on the magnitude circle.
        // Fetch the background from the TextView, which is a GradientDrawable.
        GradientDrawable sectionCircle = (GradientDrawable) sectionName.getBackground();

        // Get the appropriate background color based on the current earthquake magnitude
        int sectionColor = QueryUtils.getSectionColor(getContext(), news.getSectionName());

        // Set the color on the magnitude circle
        sectionCircle.setColor(sectionColor);

        listView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: started");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                getContext().startActivity(intent);
                Log.d(TAG, "onClick: ended");
            }
        });

        Log.d(TAG, "getView: ended");
        
        return listView;
    }
}
