package com.example.diet_master;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
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
    String year, month, day, calenderDate;

    // Calory
    TextView tvItemFoodname, tvItemCal, tvItemCarb, tvItemProtein, tvItemFat, tvItemThreemeal;
    LinearLayout lLayout;
    ImageView ivItemFood;


    // Database
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbReference;
    private FirebaseAuth auth;  // firebase auth
    private FirebaseUser currentUser;  //firebase user
    String uid;
    DailyInfo dInfo = null;

    // Storage
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

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
        Log.d(TAG,"uid=============" + uid);
        dbReference = FirebaseDatabase.getInstance().getReference();

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener()
        {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int cYear, int cMonth, int cDay)
            {
                TV_Date.setText(String.format("%d/%d/%d", cYear, cMonth + 1, cDay));

                // change year
                year = String.valueOf(cYear);

                // chagne month
                if(cMonth+1 < 10) {
                    month = String.format("%02d", cMonth +1);
                }
                else { month = String.valueOf(cMonth + 1);}

                // chagne day format
                if(cDay < 10) {
                    day = String.format("%02d", cDay);
                }
                else { day = String.valueOf(cDay);}

                String date = year + month + day;

                // change dateFormat yyyyMMdd to yyMMdd
                try {
                    SimpleDateFormat getDate = new SimpleDateFormat("yyyyMMdd");
                    SimpleDateFormat newDate = new SimpleDateFormat("yyMMdd");
                    calenderDate = newDate.format(getDate.parse(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                addFoodInfo();
            }
        });
        return rootView;
    }


    public void showDate() {
        dateFormat = new SimpleDateFormat("yyyy" + "/" + "MM" + "/" + "dd");
        sDate = dateFormat.format(date);
        TV_Date.setText(sDate);
    }



    public void addFoodInfo() {

        String[] threeMeal = {"Breakfast", "Lunch", "Dinner"};
        lLayout.removeAllViewsInLayout();

        for (String three : threeMeal) {
            dbReference.child("FoodInfo").child("Uid").child(calenderDate).child(three).orderByKey().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    AddThreeMeal itemThreemeal = new AddThreeMeal(getContext());
                    tvItemThreemeal = itemThreemeal.findViewById(R.id.TV_threemeal);
                    tvItemThreemeal.setText(three);
                    lLayout.addView(itemThreemeal);

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        AddFoodInfo item = new AddFoodInfo(getContext());
                        String key = postSnapshot.getKey();
                        HashMap<String, String> value = (HashMap<String, String>) postSnapshot.getValue();

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

                        // ImageView
                        storage.getReferenceFromUrl(storageRef + "FoodInfo/" + uid + "/" + calenderDate + "/" + three + "/" + key + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                ivItemFood = item.findViewById(R.id.IV_foodImg);
                                Glide.with(getContext()).load(uri).into(ivItemFood);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
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
