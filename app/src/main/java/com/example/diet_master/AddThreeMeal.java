package com.example.diet_master;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AddThreeMeal extends LinearLayout {

    TextView tvThreeMeal;

    public AddThreeMeal(Context context) {
        super(context);

        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.add_threemeal, this, true);

        tvThreeMeal = (TextView)findViewById(R.id.TV_threemeal);
    }
}
