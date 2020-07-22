package com.example.tictactoe;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class Greet extends AppCompatActivity {

    SharedPreferences name;
    private TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_greet);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        name = getSharedPreferences("name",MODE_PRIVATE);
        txt = findViewById(R.id.textView10);
        String n = name.getString("name","");
        if(n.equals("")){
            txt.setText("Please register to continue...");
            new CountDownTimer(1000,1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    Intent i = new Intent(getApplicationContext(),Register.class);
                    startActivity(i);
                    finish();
                }
            }.start();
        }
        else{
            txt.setText("Hi "+n+" !!!");
            new CountDownTimer(1000,1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    Intent i = new Intent(getApplicationContext(),Menu.class);
                    startActivity(i);
                    finish();
                }
            }.start();
        }

    }
}
