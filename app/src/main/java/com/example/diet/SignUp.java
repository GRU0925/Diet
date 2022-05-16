package com.example.diet;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignUp extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
    }

    final String[] SP_index = {"활동량을 선택하십시오", "활동량 적음(25)", "활동량 보통(35)", "활동량 많음(40)"};

    Spinner spinner = (Spinner)findViewById(R.id.spinner1);

    ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, SP_index);
    /*adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
    spinner.setAdapter(adapter);

    spiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            Toast.makeText(getApplicationContext(), "Selected Country: "+categories[position] , Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }1
    });*/
}
