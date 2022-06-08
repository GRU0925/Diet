package com.example.diet_master;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AddTipThumbnail extends LinearLayout {

    //TextView tVTipName;
    ImageView ivThumbnail;

    public AddTipThumbnail(Context context) {
        super(context);
        init(context);
    }
    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.add_tipthumbnail, this, true);

        //tVTipName = (TextView)findViewById(R.id.TV_tipName);
        ivThumbnail = (ImageView)findViewById(R.id.IV_thumbnail);


    }
}

