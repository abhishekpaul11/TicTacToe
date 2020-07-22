package com.example.tictactoe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Menu extends AppCompatActivity {

    private Button offline,solo,stats,online;
    private Dialog d,msg;
    private String id;
    private boolean rep = false,flag = true;
    SharedPreferences sp;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference players = database.getReference("players");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_menu);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        sp = getSharedPreferences("id",MODE_PRIVATE);
        id = sp.getString("id","");
        final String ID = id;
        if(!id.equals("")) players.child(id).child("Status").setValue("Online");
        stats = findViewById(R.id.button15);
        online = findViewById(R.id.button14);
        d = new Dialog(this);
        d.setContentView(R.layout.message);
        msg = new Dialog(this);
        msg.setContentView(R.layout.warning);
        final TextView txt = d.findViewById(R.id.textView24);

        Window win = d.getWindow();
        WindowManager.LayoutParams wlp = win.getAttributes();
        win.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
        wlp.gravity = Gravity.CENTER;
        win.setBackgroundDrawable(new ColorDrawable(Color.argb(225,255,255,255)));
        win.setAttributes(wlp);

        Window win1 = msg.getWindow();
        WindowManager.LayoutParams wlp1 = win1.getAttributes();
        win1.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
        wlp1.gravity = Gravity.CENTER;
        win1.setBackgroundDrawable(new ColorDrawable(Color.argb(225,255,255,255)));
        win1.setAttributes(wlp1);

        if(!id.equals("")){
            players.child(id).child("Online").child("Request").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(flag){
                        String string = dataSnapshot.getValue(String.class);
                        if (!string.equals("")) {
                            rep = true;
                            int n = string.indexOf('#');
                            final String id = string.substring(0, n);
                            final String name = string.substring(n + 1);
                            String disp = id + "(" + name + ")";
                            if (disp.length() > 20) disp = disp.substring(0, 17) + "...)";
                            TextView t = msg.findViewById(R.id.textView24);
                            t.setText(disp);
                            TextView txt = msg.findViewById(R.id.textView23);
                            txt.setText("GAME REQUEST");
                            msg.setCancelable(false);
                            msg.show();
                            Button y = msg.findViewById(R.id.button20);
                            Button no = msg.findViewById(R.id.button21);
                            y.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    msg.dismiss();
                                    Toast.makeText(getApplicationContext(), "Request Accepted", Toast.LENGTH_SHORT).show();
                                    players.child(ID).child("Online").child("Reply").setValue("Y");
                                    Intent i = new Intent(getApplicationContext(), PlayOnline.class);
                                    i.putExtra("id",id);
                                    i.putExtra("name",name);
                                    i.putExtra("flag",true);
                                    startActivity(i);
                                }
                            });
                            no.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    msg.dismiss();
                                    Toast.makeText(getApplicationContext(), "Request Denied", Toast.LENGTH_SHORT).show();
                                    players.child(ID).child("Online").child("Reply").setValue("N");
                                    players.child(ID).child("Online").child("Request").setValue("");
                                    rep = false;
                                    new CountDownTimer(500, 500) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {
                                        }

                                        @Override
                                        public void onFinish() {
                                            players.child(ID).child("Online").child("Reply").setValue("");
                                        }
                                    }.start();
                                }
                            });
                        } else if (rep) {
                            msg.dismiss();
                            TextView t = d.findViewById(R.id.textView24);
                            t.setText("Request timed out");
                            TextView txt = d.findViewById(R.id.textView23);
                            txt.setText("ERROR");
                            d.setCancelable(true);
                            d.show();
                        }
                }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }

        offline = findViewById(R.id.button13);
        offline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.setCancelable(false);
                TextView t = d.findViewById(R.id.textView23);
                t.setText("GAME MODE");
                txt.setText("Pass 'n' Play");
                d.show();
                new CountDownTimer(1000,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        d.dismiss();
                        Intent i = new Intent(getApplicationContext(),Setup.class);
                        startActivity(i);
                    }
                }.start();
            }
        });

        solo = findViewById(R.id.button17);
        solo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.setCancelable(false);
                TextView t = d.findViewById(R.id.textView23);
                t.setText("GAME MODE");
                txt.setText("Try and defeat Charlie, the smart, quick and cool bot");
                d.show();
                new CountDownTimer(2000,2000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        d.dismiss();
                        Intent i = new Intent(getApplicationContext(),Solo.class);
                        startActivity(i);
                    }
                }.start();
            }
        });

        stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(id.equals("")){
                    d.setCancelable(true);
                    TextView t = d.findViewById(R.id.textView23);
                    t.setText("ERROR");
                    txt.setText("Stats are not available if you login as guest");
                    d.show();
                }
                else {
                    Intent i = new Intent(getApplicationContext(), Stats.class);
                    startActivity(i);
                }
            }
        });

        online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(id.equals("")){
                    d.setCancelable(true);
                    TextView t = d.findViewById(R.id.textView23);
                    t.setText("ERROR");
                    txt.setText("You can't play online as a guest");
                    d.show();
                }
                else {
                    d.setCancelable(false);
                    TextView t = d.findViewById(R.id.textView23);
                    t.setText("GAME MODE");
                    txt.setText("Challenge friends online");
                    d.show();
                    new CountDownTimer(1000,1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            d.dismiss();
                            Intent i = new Intent(getApplicationContext(),Online.class);
                            startActivity(i);
                        }
                    }.start();
                }
            }
        });
    }
    @Override
    public void onPause(){
        super.onPause();
        flag = false;
        sp = getSharedPreferences("id",MODE_PRIVATE);
        id = sp.getString("id","");
        if(!id.equals("")) players.child(id).child("Status").setValue("Offline");
    }
    @Override
    public void onResume(){
        super.onResume();
        flag = true;
        rep = false;
        sp = getSharedPreferences("id",MODE_PRIVATE);
        id = sp.getString("id","");
        if(!id.equals("")){
            players.child(id).child("Status").setValue("Online");
            players.child(id).child("Online").child("Position").setValue(-1);
            players.child(id).child("Online").child("Ping").setValue(0);
            players.child(id).child("Online").child("Request").setValue("");
            players.child(id).child("Online").child("Reply").setValue("");
            players.child(id).child("Online").child("Ingame").setValue("");
        }
    }
}
