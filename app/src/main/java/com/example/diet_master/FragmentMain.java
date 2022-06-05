package com.example.diet_master;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

public class FragmentMain extends Fragment {

    private static final String TAG = "FragmentMain";
    MainActivity mainActivity;
    AddFoodInfo addFoodInfo;

    // rootView
    View rootView;

    // Date
    TextView tDate;
    Date date;
    SimpleDateFormat dateFormat;
    String sDate;


    // Calory
    TextView tvRecoCal, tvMyCal, tvUseCal, tvCarb, tvProtein, tvFat, tvRecoCalinPG, tvRecoCarb, tvRecoProtein, tvRecoFat;    // fragment_main
    TextView tvItemFoodname, tvItemCal, tvItemCarb, tvItemProtein, tvItemFat, tvItemThreemeal;
    String dMyCal, dCarb, dProtein, dFat;   // 내 영양소 from FoodInfo
    String dRecoCal, dRecoCarb, dRecoProtein, dRecoFat, dBasicRate;     // 권장from UserInfo
    ProgressBar pgMyCal, pgCarb, pgProtein, pgFat;
    LinearLayout lLayout;

    // Database
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbReference, dbUserInfo, dbFoodInfo;
    private FirebaseAuth auth;  // firebase auth
    private FirebaseUser currentUser;  //firebase user
    String uid;
    DailyInfo dInfo;



    SimpleDateFormat dateFormat4DB;
    String dbDate;


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

        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        //mainActivity = (MainActivity)getActivity();

        // DialyInfo
        tvMyCal = rootView.findViewById(R.id.TV_myCal);
        tvCarb = rootView.findViewById(R.id.TV_carb);
        tvProtein = rootView.findViewById(R.id.TV_protein);
        tvFat = rootView.findViewById(R.id.TV_fat);
        tvRecoCal = rootView.findViewById(R.id.TV_recoCal);
        tvUseCal = rootView.findViewById(R.id.TV_useCal);
        tvRecoCalinPG = rootView.findViewById(R.id.TV_recoCalinPG);
        tvRecoCarb = rootView.findViewById(R.id.TV_recoCarb);
        tvRecoProtein = rootView.findViewById(R.id.TV_recoProtein);
        tvRecoFat = rootView.findViewById(R.id.TV_recoFat);


        pgMyCal = rootView.findViewById(R.id.PG_myCal);
        pgCarb = rootView.findViewById(R.id.PG_carb);
        pgProtein = rootView.findViewById(R.id.PG_protein);
        pgFat = rootView.findViewById(R.id.PG_fat);

        // CurrentUser
        currentUser = auth.getInstance().getCurrentUser();
        uid = auth.getInstance().getUid();

        dbReference = FirebaseDatabase.getInstance().getReference();

        //dbUserInfo = dbReference.child("UserInfo").child(uid);
        //dbFoodInfo = dbReference.child("FoodInfo").child(uid);

        // addLayout
        lLayout = rootView.findViewById(R.id.Linear_root);

        //Date
        tDate = rootView.findViewById(R.id.TV_Date);
        long curTime = System.currentTimeMillis();
        date = new Date(curTime);
        showDate();

        addFoodInfo();
        showDateInfo();

        return rootView;
    }

    public void FragmentMain() {

    }

    // Date 표시
    public void showDate() {

        dateFormat = new SimpleDateFormat("yyyy" + "년" + "MM" + "월" + "dd" + "일");
        sDate = dateFormat.format(date);
        tDate.setText(sDate);
    }


    // DailyInfo(일일섭취 칼로리, 3대영양소 표시)
    public void showDateInfo() {
        // Today date
        dateFormat4DB = new SimpleDateFormat("yyMMdd");
        dbDate = dateFormat4DB.format(date);
        //Log.d(TAG, "dataFormat : " + dbDate);

        dbReference.child("FoodInfo").child("Uid").child("220527").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dInfo = dataSnapshot.getValue(DailyInfo.class);

                // Get DailyInfo
                dMyCal = dInfo.getDailyCalory();
                dCarb = dInfo.getDailyCarb();
                dProtein = dInfo.getDailyProtein();
                dFat = dInfo.getDailyFat();

                // DailyInfo setText
                tvMyCal.setText(dMyCal);
                tvCarb.setText(dCarb + "g");
                tvProtein.setText(dProtein + "g");
                tvFat.setText(dFat + "g");

                // Progressbar setMy
                pgMyCal.setProgress(Integer.parseInt(dMyCal));
                pgCarb.setProgress(Integer.parseInt(dCarb));
                pgProtein.setProgress(Integer.parseInt(dProtein));
                pgFat.setProgress(Integer.parseInt(dFat));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        dbReference.child("UserInfo").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dInfo = dataSnapshot.getValue(DailyInfo.class);

                dRecoCal = dInfo.getRecoCal();
                dRecoCarb = dInfo.getRecoCarb();
                dRecoProtein = dInfo.getRecoProtein();
                dRecoFat = dInfo.getRecoFat();
                dBasicRate = dInfo.getBasicRate();

                // set 권장, 기초대사량(소모칼로리로 변경 요)
                tvRecoCalinPG.setText(dRecoCal + " Kcal");
                tvRecoCal.setText(dRecoCal + "Kcal");
                tvUseCal.setText(dBasicRate + "Kcal");

                // setText 권장3대 영양소
                tvRecoCarb.setText(" / " + dRecoCarb + "g");
                tvRecoProtein.setText(" / " + dRecoProtein + "g");
                tvRecoFat.setText(" / " + dRecoFat + "g");

                // Progressbar setMax
                pgMyCal.setMax(Integer.parseInt(dRecoCal));
                pgCarb.setMax(Integer.parseInt((dRecoCarb)));
                pgProtein.setMax(Integer.parseInt(dRecoProtein));
                pgFat.setMax(Integer.parseInt(dRecoFat));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void showDateInfo2() {
        dbReference.child("FoodInfo").child(uid).child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dInfo = dataSnapshot.getValue(DailyInfo.class);

                dRecoCal = dInfo.getRecoCal();
                dRecoCarb = dInfo.getRecoCarb();
                dRecoProtein = dInfo.getRecoProtein();
                dRecoFat = dInfo.getRecoFat();
                dBasicRate = dInfo.getBasicRate();

                // set 권장, 기초대사량(소모칼로리로 변경 요)
                tvRecoCal.setText(dRecoCal);
                tvUseCal.setText(dBasicRate);

                // Progressbar setMax
                pgMyCal.setMax(Integer.parseInt(dRecoCal));
                //pgMyCal.set(dRecoCal);
                pgCarb.setMax(Integer.parseInt((dRecoCarb)));
                pgProtein.setMax(Integer.parseInt(dRecoProtein));
                pgFat.setMax(Integer.parseInt(dRecoFat));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addFoodInfo() {

        String[] threeMeal = {"Breakfast","Lunch", "Dinner"};

        for(String three : threeMeal ) {
            //Log.d(TAG, "Three : " + three);
            dbReference.child("FoodInfo").child("Uid").child("220527").child(three).orderByKey().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    AddThreeMeal itemThreemeal = new AddThreeMeal(getContext());
                    tvItemThreemeal = itemThreemeal.findViewById(R.id.TV_threemeal);
                    tvItemThreemeal.setText(three);
                    lLayout.addView(itemThreemeal);

                    for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        AddFoodInfo item = new AddFoodInfo(getContext());
                        String key = postSnapshot.getKey();
                        //ArrayList value = new ArrayList((Integer) postSnapshot.getValue());
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


