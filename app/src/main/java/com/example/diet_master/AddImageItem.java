package com.example.diet_master;

import android.net.Uri;

public class AddImageItem {

    private String TipName;
    private Uri uri;

    public String getTipName() {
        return TipName;
    }

    public void setTipName(String TipName) {
        TipName = TipName;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public void AddImageItem(String TipName, Uri uri) {
        this.TipName = TipName;
        this.uri = uri;
    }
}