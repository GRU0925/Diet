package com.example.diet_master;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ModifyMyInfo extends AppCompatActivity {
    private static final String TAG = "SignUp";

    FragmentSetting fragmentSetting;

    //
    private EditText etNick, etAge, etHeight, etWeight; // 사용자 정보 입력 필드
    private RadioGroup radioGroup;
    private RadioButton rbMale, rbFemale;
    private Spinner spIndex;
    private Button btModifyInfo; //회원가입 버튼

    // Firebase
    private FirebaseAuth auth;  // firebase auth
    private FirebaseUser currentUser;  //firebase user
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbReference;
    String uid;

    // String
    String stNick, stAge, stHeight, stWeight, stSex, stActIndex;
    String[] spItems = {"비활동적", "저활동적", "활동적", "매우 활동적"};
    String recoCal, recoCarb, recoProtein, recoFat, basicRate;
    //BasicRate = 기초대사량

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modifymyinfo);

        etNick = findViewById(R.id.ET_mNickname);
        etAge = findViewById(R.id.ET_mAge);
        etHeight = findViewById(R.id.ET_mHeight);
        etWeight = findViewById(R.id.ET_mWeight);

        radioGroup = findViewById(R.id.mRadioGroup);
        rbMale = findViewById(R.id.RB_mMale);
        rbFemale = findViewById(R.id.Rb_mFemale);

        spIndex = findViewById(R.id.SP_mIndex);

        btModifyInfo = findViewById(R.id.BT_modifyInfo);

        currentUser = auth.getInstance().getCurrentUser();
        uid = auth.getInstance().getUid();


        selectSex();
        selectActIndex();

        btModifyInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModifyMyInfo();
            }
        });

    }

    // SignUp-----------------------------------------------------------------------------------------
    public void ModifyMyInfo() {
        stNick = etNick.getText().toString();
        stAge = etAge.getText().toString();
        stHeight = etHeight.getText().toString();
        stWeight = etWeight.getText().toString();

        calculateRecoCal();
        selectRecoProtainNfat();
        calculateBasicRate();

        if(stNick.equals("") || stAge.equals("") || stHeight.equals("") || stWeight.equals("") || (rbMale.isChecked() == false && rbFemale.isChecked() == false)){
            //
            Toast.makeText(ModifyMyInfo.this, "빈칸을 다 채워 주세요!", Toast.LENGTH_SHORT).show();
        }
        else {
            //nickname, age, height, weight;
            database.getReference().child("UserInfo").child(uid).child("uid").setValue(uid);
            database.getReference().child("UserInfo").child(uid).child("nickname").setValue(stNick);
            database.getReference().child("UserInfo").child(uid).child("age").setValue(stAge);
            database.getReference().child("UserInfo").child(uid).child("height").setValue(stHeight);
            database.getReference().child("UserInfo").child(uid).child("weight").setValue(stWeight);
            database.getReference().child("UserInfo").child(uid).child("sex").setValue(stSex);
            database.getReference().child("UserInfo").child(uid).child("activityindex").setValue(stActIndex);

            database.getReference().child("UserInfo").child(uid).child("recoCal").setValue(recoCal);        // 권장칼로리
            database.getReference().child("UserInfo").child(uid).child("recoCarb").setValue("130");         // 권장탄수화물
            database.getReference().child("UserInfo").child(uid).child("recoProtein").setValue(recoProtein);// 권장 단백질
            database.getReference().child("UserInfo").child(uid).child("recoFat").setValue(recoFat);        // 권장 지방
            database.getReference().child("UserInfo").child(uid).child("basicRate").setValue(basicRate);    //기초대사량

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            finish();
        }
    }


    // 성별(radio button in radio group)--------------------------------------------------------------
    public void selectSex() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.RB_mMale) {
                    //Toast.makeText(ModifyMyInfo.this, "male", Toast.LENGTH_SHORT).show();
                    stSex = "male"; // 라디오 버튼의 text값을 string에 담아줌
                } else if(i == R.id.Rb_mFemale){
                    //Toast.makeText(ModifyMyInfo.this, "female", Toast.LENGTH_SHORT).show();
                    stSex = "female"; // 라디오 버튼의 text값을 string에 담아줌
                }
            }
        });
    }


    // 활동지수 선택(spinner)--------------------------------------------------------------------------
    public void selectActIndex() {
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spIndex.setAdapter(adapter);
        spIndex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 남: 1.0, 1.1, 1.25, 1.48 / 여:1.0, 1.2, 1.27, 1.45
                switch (spItems[position]) {
                    case "비활동적":
                        stActIndex = "1.0";
                        break;
                    case "저활동적":
                        stActIndex = (stSex == "male")? "1.1" : "1.2";
                        break;
                    case "활동적":
                        stActIndex = (stSex == "male")? "1.25" : "1.27";
                        break;
                    case "매우 활동적":
                        stActIndex = (stSex == "male")? "1.48" : "1.45";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /// 권장칼로리 계산--------------------------------------------------------------------------------
    public void calculateRecoCal() {
        double height = Double.parseDouble(stHeight) / 100;

        if( Integer.parseInt(stAge) <= 2) {
            recoCal = String.format("%.0f", 89 * Double.parseDouble(stWeight) - 100);
        }
        if( Integer.parseInt(stAge) >= 3 && Integer.parseInt(stAge) <= 19) {
            if(stSex == "male") {
                recoCal = String.format("%.0f", 88.5 - 61.9 * Double.parseDouble(stAge) + Double.parseDouble(stActIndex) * (26.7 * Double.parseDouble(stWeight) + 903 * height));
            }
            else {
                recoCal = String.format("%.0f", 135.3 - 30.8 * Double.parseDouble(stAge) + Double.parseDouble(stActIndex) * (10 * Double.parseDouble(stWeight) + 934 * height));
            }
        }
        if( Integer.parseInt(stAge) >= 20) {
            if(stSex == "male") {
                recoCal = String.format("%.0f", 662 - 9.53 * Double.parseDouble(stAge) + Double.parseDouble(stActIndex) * (15.91 * Double.parseDouble(stWeight) + 539.6 * height));
            }
            else {
                recoCal = String.format("%.0f", 354 - 6.91 * Double.parseDouble(stAge) + Double.parseDouble(stActIndex) * (9.36 * Double.parseDouble(stWeight) + 726 * height));
            }
        }
    }
    // 단백질, 지방 계산-------------------------------------------------------------------------------
    public void selectRecoProtainNfat() {
        // 단백질 30% 지방 20%
        // 단백질 x 0.6 = 지방

        if(stSex == "male") {
            if( Integer.parseInt(stAge) <= 2) {
                recoProtein = "20";
                recoFat = "12";
            }
            else if( Integer.parseInt(stAge) >= 3 && Integer.parseInt(stAge) <= 5) {
                recoProtein = "25";
                recoFat = "15";
            }
            else if( Integer.parseInt(stAge) >= 6 && Integer.parseInt(stAge) <= 8) {
                recoProtein = "35";
                recoFat = "21";
            }
            else if( Integer.parseInt(stAge) >= 9 && Integer.parseInt(stAge) <= 11) {
                recoProtein = "50";
                recoFat = "30";
            }
            else if( Integer.parseInt(stAge) >= 12 && Integer.parseInt(stAge) <= 14) {
                recoProtein = "60";
                recoFat = "36";
            }
            else if( Integer.parseInt(stAge) >= 15 && Integer.parseInt(stAge) <= 49) {
                recoProtein = "65";
                recoFat = "39";
            }
            else if( Integer.parseInt(stAge) >= 50) {
                recoProtein = "60";
                recoFat = "36";
            }
        }
        else {
            if( Integer.parseInt(stAge) <= 2) {
                recoProtein = "20";
                recoFat = "12";
            }
            else if( Integer.parseInt(stAge) >= 3 && Integer.parseInt(stAge) <= 5) {
                recoProtein = "25";
                recoFat = "15";
            }
            else if( Integer.parseInt(stAge) >= 6 && Integer.parseInt(stAge) <= 8) {
                recoProtein = "35";
                recoFat = "21";
            }
            else if( Integer.parseInt(stAge) >= 9 && Integer.parseInt(stAge) <= 11) {
                recoProtein = "45";
                recoFat = "27";
            }
            else if( Integer.parseInt(stAge) >= 12 && Integer.parseInt(stAge) <= 29) {
                recoProtein = "55";
                recoFat = "33";
            }
            else if( Integer.parseInt(stAge) >= 30) {
                recoProtein = "50";
                recoFat = "30";
            }
        }
    }

    // 기초대사량--------------------------------------------------------------------------------------
    public void calculateBasicRate() {
        double height = Double.parseDouble(stHeight) / 100;

        if( Integer.parseInt(stAge) <= 18) {
            if(stSex == "male") {
                basicRate = String.format("%.0f", 68 - 43.3 * Double.parseDouble(stAge) + 712 * height + 19.2 * Double.parseDouble(stWeight));
            }
            else {
                basicRate = String.format("%.0f", 189 - 17.6 * Double.parseDouble(stAge) + 625 * height + 7.9 * Double.parseDouble(stWeight));
            }
        }
        if( Integer.parseInt(stAge) >= 19) {
            if(stSex == "male") {
                basicRate = String.format("%.0f", 204 - 4 * Double.parseDouble(stAge) + 450.5 * height + 11.69 * Double.parseDouble(stWeight));
            }
            else {
                basicRate = String.format("%.0f", 255 - 2.35 * Double.parseDouble(stAge) + 361.6 * height + 9.39 * Double.parseDouble(stWeight));
            }
        }
    }
}
