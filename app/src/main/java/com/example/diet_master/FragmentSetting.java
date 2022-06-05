package com.example.diet_master;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class FragmentSetting extends Fragment {

    private static final String TAG = "FragmentMain";
    MainActivity mainActivity;

    // rootView
    View rootView;

    // View
    TextView tvNickname, tvSetMyInfo, tvGoogleFit;
    Button btLogout;
    FrameLayout fLayout_1, fLayout_2;

    // Firebase
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbReference;
    private FirebaseAuth auth;          // firebase auth
    private FirebaseUser currentUser;   //firebase user
    String uid;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_setting, container, false);

        // View
        tvNickname = rootView.findViewById(R.id.TV_nickname);
        btLogout = rootView.findViewById(R.id.BT_logout);
        tvSetMyInfo = rootView.findViewById(R.id.TV_Menu1);
        tvGoogleFit = rootView.findViewById(R.id.TV_Menu2);
        fLayout_1 = rootView.findViewById(R.id.fLayout1);
        fLayout_2 = rootView.findViewById(R.id.fLayout2);

        // db
        dbReference = database.getReference();
        uid = auth.getInstance().getCurrentUser().getUid();



        setNickname();

        // 로그아웃 버튼
        btLogout.setText("로그아웃");
        btLogout.setTextSize(11);
        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        // 개인정보 수정 메뉴
        tvSetMyInfo.setText("개인정보 수정");
        fLayout_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ModifyMyInfo.class);
                startActivity(intent);
            }
        });

        // 구글 피트니스 연동
        tvGoogleFit.setText("구글 피트니스 연동");
        fLayout_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        return rootView;
    }

    // 설정 닉네임 표시
    public void setNickname() {
        dbReference.child("UserInfo").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserAccount uAccount = dataSnapshot.getValue(UserAccount.class);
                String nickname = uAccount.getNickname();

                String str = nickname + " 님!\n반갑습니다.";
                SpannableString spannableString = new SpannableString(str);

                int start = str.indexOf(nickname);
                int end = start + nickname.length();

                spannableString.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                tvNickname.setText(spannableString);
                tvNickname.setTextSize(16);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();

        Toast.makeText(getContext(), "...로그아웃...", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getContext(), LoginActivity.class));
        //mainActivity.finish();
    }

    public void showSettingMenu() {
        String[] menu = {"개인정보수정", "구글피트니스 연동"};

    }
}


