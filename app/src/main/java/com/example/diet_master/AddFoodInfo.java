package com.example.diet_master;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AddFoodInfo extends LinearLayout {

    TextView tvNcal, tvNcarb, tvNprotein, tvNfat, tvFoodname, tvCal, tvCarb, tvProtein, tvFat;
    ImageView ivFoodImg;


    public AddFoodInfo(Context context) {
        super(context);

        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.add_foodinfo, this, true);

        tvNcal = (TextView)findViewById(R.id.TV_nameCalory);
        tvNcarb = (TextView)findViewById(R.id.TV_nameCarb);
        tvNprotein = (TextView)findViewById(R.id.TV_nameProtein);
        tvNfat = (TextView)findViewById(R.id.TV_nameFat);

        tvFoodname = (TextView)findViewById(R.id.TV_itemFoodname);
        tvCal = (TextView)findViewById(R.id.TV_itemCalory);
        tvCarb = (TextView)findViewById(R.id.TV_itemCarb);
        tvProtein = (TextView)findViewById(R.id.TV_itemProtein);
        tvFat = (TextView)findViewById(R.id.TV_itemFat);

        ivFoodImg = (ImageView)findViewById(R.id.IV_foodImg);

        tvNcal.setText("칼로리");
        tvNcarb.setText("탄수화물");
        tvNprotein.setText("단백질");
        tvNfat.setText("지방");
    }

}
