package com.example.diet_master;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;

//import android.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    // Fragment
    FragmentMain fragmentMain;
    FragmentCalender fragmentCalender;
    FragmentTip fragmentTip;
    FragmentSetting fragmentSetting;
    FragmentManager manager;

    // View
    ImageButton btnMain, btnCalender, btnTip, btnSetting, btnmanager;
    TextView tvTitle;
    Toolbar toolbar;

    // Firebase
    private FirebaseAuth auth;  // firebase auth

    private long backKeyPressedTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Fragment
        fragmentMain = new FragmentMain();
        fragmentCalender = new FragmentCalender();
        fragmentTip = new FragmentTip();
        fragmentSetting = new FragmentSetting();

        // Toolbar
        toolbar = (Toolbar)findViewById(R.id.toolbar);

        // Button
        btnMain = (ImageButton)findViewById(R.id.BT_daily);
        btnCalender = (ImageButton)findViewById(R.id.BT_calender);
        btnTip = (ImageButton)findViewById(R.id.BT_tip);
        btnSetting = (ImageButton)findViewById(R.id.BT_setting);

        // TextView
        tvTitle = (TextView)findViewById(R.id.TV_title);
        tvTitle.setText("일일 정보");

        if( auth.getInstance().getCurrentUser() == null) {
            finish();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }  else {}


        btnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { callFragmentMain(); }
        });

        btnCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { callFragmentCalender(); }
        });

        btnTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { callFragmentTip(); }
        });

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { callFragmentSetting(); }
        });

        // 음식 추가 페이지로 넘어가기
        btnmanager = (ImageButton) findViewById(R.id.btnmanager);
        btnmanager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), YoloActivity.class);
                startActivityForResult(intent,0);
            }
        });
    }


    public void callFragmentMain() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fmLayout, fragmentMain).commit();
        btnmanager.setVisibility(View.VISIBLE);
        toolbar.setVisibility(View.VISIBLE);
        tvTitle.setText("일일 정보");
    }

    public void callFragmentCalender() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fmLayout, fragmentCalender).commit();
        btnmanager.setVisibility(View.INVISIBLE);
        toolbar.setVisibility(View.VISIBLE);
        tvTitle.setText("캘린더");
    }

    public void callFragmentTip() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fmLayout, fragmentTip).commit();
        btnmanager.setVisibility(View.INVISIBLE);
        toolbar.setVisibility(View.VISIBLE);
        tvTitle.setText("팁");
    }

    public void callFragmentSetting() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fmLayout, fragmentSetting).commit();
        btnmanager.setVisibility(View.INVISIBLE);
        toolbar.setVisibility(View.GONE);
    }

    // 뒤로가기 2번 클릭 시 앱 종료
    @Override
    public void onBackPressed() {

        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish();
        }
    }
}