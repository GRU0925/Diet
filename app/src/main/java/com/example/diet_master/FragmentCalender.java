package com.example.diet_master;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class FragmentCalender extends Fragment {

    private static final String TAG = "FragmentCalender";
    // rootView
    View rootView;

    // Date
    TextView TV_Date;
    Date date;
    SimpleDateFormat dateFormat;
    String sDate;

    // Calory
    TextView tvItemFoodname, tvItemCal, tvItemCarb, tvItemProtein, tvItemFat, tvItemThreemeal;
    LinearLayout lLayout;

    // Database
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbReference;
    private FirebaseAuth auth;  // firebase auth
    private FirebaseUser currentUser;  //firebase user
    String uid;
    DailyInfo dInfo = null;

    SimpleDateFormat dateFormat4DB;
    String dbDate;
    CalendarView calendarView;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_calender, container, false);
        lLayout = rootView.findViewById(R.id.Linear_root);
        calendarView = rootView.findViewById(R.id.calendarView);
        TV_Date = rootView.findViewById(R.id.TV_Date);


        long curTime = System.currentTimeMillis();
        date = new Date(curTime);
        showDate();

        uid = auth.getInstance().getUid();


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener()
        {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth)
            {
                TV_Date.setText(String.format("%d"+ "/" + "%d"+ "/"+ "%d", year, month + 1, dayOfMonth));
                addFoodInfo();
            }
        });

        dbReference = FirebaseDatabase.getInstance().getReference();


        //lThreemeal = rootView.findViewById(R.id.Linear_root);


        showDateInfo();


        return rootView;
    }

    public void showDate() {

        dateFormat = new SimpleDateFormat("yyyy" + "/" + "MM" + "/" + "dd");
        sDate = dateFormat.format(date);
        TV_Date.setText(sDate);
    }


    // DailyInfo(일일섭취 칼로리, 3대영양소 표시)
    public void showDateInfo() {
        // Today date
        dateFormat4DB = new SimpleDateFormat("yyMMdd");
        dbDate = dateFormat4DB.format(date);
        Log.d(TAG, "dataFormat : " + dbDate);

        dbReference.child("FoodInfo").child(uid).child(dbDate).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dInfo = dataSnapshot.getValue(DailyInfo.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addFoodInfo() {


        String[] threeMeal = {"Breakfast", "Lunch", "Dinner"};


        for (String three : threeMeal) {
            dbReference.child("FoodInfo").child(uid).child(dbDate).child(three).orderByKey().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    AddThreeMeal itemThreemeal = new AddThreeMeal(getContext());

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        AddFoodInfo item = new AddFoodInfo(getContext());
                        Log.d(TAG, "postSnapshot : " + postSnapshot);
                        String key = postSnapshot.getKey();
                        //ArrayList value = new ArrayList((Integer) postSnapshot.getValue());
                        HashMap<String, String> value = (HashMap<String, String>) postSnapshot.getValue();
                        Log.d(TAG, "value : " + value.get("Carb"));

                        tvItemThreemeal = itemThreemeal.findViewById(R.id.TV_threemeal);
                        tvItemThreemeal.setText(three);
                        lLayout.addView(itemThreemeal);


                        tvItemFoodname = item.findViewById(R.id.TV_itemFoodname);
                        tvItemCal = item.findViewById(R.id.TV_itemCalory);
                        tvItemCarb = item.findViewById(R.id.TV_itemCarb);
                        tvItemProtein = item.findViewById(R.id.TV_itemProtein);
                        tvItemFat = item.findViewById(R.id.TV_itemFat);

                        tvItemFoodname.setText(key);
                        tvItemCal.setText(value.get("Calory"));
                        tvItemCarb.setText(value.get("Carb"));
                        tvItemProtein.setText(value.get("Protein"));
                        tvItemFat.setText(value.get("Fat"));

                        lLayout.addView(item);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}
