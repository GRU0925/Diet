package com.example.diet_master;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class AddYoloFood extends LinearLayout {

    TextView calory, carb, protein, fat, tvYoloCalory, tvYoloCarb, tvYoloProtein, tvYoloFat, tvYoloName;
    ImageView yoloImage;
    Spinner yoloAmount;

    public AddYoloFood(Context context) {
        super(context);

        init(context);
    }


    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //inflater.inflate(R.layout.add_yolofood,this,true);

        calory = (TextView)findViewById(R.id.TV_calory);
        carb = (TextView)findViewById(R.id.TV_carb);
        protein = (TextView)findViewById(R.id.TV_protein);
        fat = (TextView)findViewById(R.id.TV_fat);

        tvYoloName = (TextView)findViewById(R.id.TV_yoloFoodname);
        tvYoloCalory = (TextView)findViewById(R.id.TV_yoloCalory);
        tvYoloCarb = (TextView)findViewById(R.id.TV_yoloCarb);
        tvYoloProtein = (TextView)findViewById(R.id.TV_yoloProtein);
        tvYoloFat = (TextView)findViewById(R.id.TV_yoloFat);

        yoloAmount = (Spinner)findViewById(R.id.SP_yoloAmount);

        yoloImage = (ImageView)findViewById(R.id.IV_yoloImg);

        calory.setText("칼로리");
        carb.setText("탄수화물");
        protein.setText("단백질");
        fat.setText("지방");

    }
}
