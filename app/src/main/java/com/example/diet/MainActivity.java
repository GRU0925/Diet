package com.example.diet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    Button btnMain, btnCalender, btnTip, btnSetting;

    private Fragment fragmentMain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnMain = (Button)findViewById(R.id.BT_Main);
        btnCalender = (Button)findViewById(R.id.BT_calender);
        btnTip = (Button)findViewById(R.id.BT_tip);
        btnSetting = (Button)findViewById(R.id.BT_setting);

        fragmentMain = new Fragment();

        /*
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.fmLayout, new FragmentMain());
        fragmentTransaction.add(R.id.fmLayout, new FragmentCalender());
        fragmentTransaction.commit();
        */

        btnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callFragmentMain();
            }
        });

        btnCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callFragmentCalender();
            }
        });


        /*
        // 음식 추가 페이지로 넘어가기
        ImageButton btnmanager = (ImageButton) findViewById(R.id.btnmanager);
        btnmanager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), YoloActivity.class);
                startActivityForResult(intent,0);
            }
        });
        */

    }

    public void callFragmentMain() {

        Fragment fr = new com.example.diet.FragmentMain();

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fmLayout, fr);
        fragmentTransaction.commit();
    }

    public void callFragmentCalender() {

        Fragment fr = new com.example.diet.FragmentCalender();

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fmLayout, fr);
        fragmentTransaction.commit();
    }
}