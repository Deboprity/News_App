package com.example.android.newsapp;

public class News {

    private String sectionName;
    private String webTitle;
    private String webPublicationDate;
    private String webURL;

    public News(String sectionName, String webTitle, String webPublicationDate, String webURL) {
        this.sectionName = sectionName;
        this.webTitle = webTitle;
        this.webPublicationDate = webPublicationDate;
        this.webURL = webURL;
    }

    public String getSectionName() {
        return sectionName;
    }

    public String getWebTitle() {
        return webTitle;
    }

    public String getWebPublicationDate() {
        return webPublicationDate;
    }

    public String getWebURL() {
        return webURL;
    }

}
