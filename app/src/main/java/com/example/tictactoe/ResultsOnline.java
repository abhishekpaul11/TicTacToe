package com.example.tictactoe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ResultsOnline extends AppCompatActivity {

    SharedPreferences win,loss,tie,nam,id;
    SharedPreferences.Editor edit;
    TextView name,tag;
    ImageView symbol;
    Button menu,back;
    private Dialog d,msg;
    private boolean rep = false,flag = true;
    int w,l,t;
    String ID;
    static FirebaseDatabase database = FirebaseDatabase.getInstance();
    static DatabaseReference players = database.getReference("players");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_online_res);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        name = findViewById(R.id.textView4);
        tag = findViewById(R.id.textView5);
        symbol = findViewById(R.id.imageView2);
        menu = findViewById(R.id.button29);
        back = findViewById(R.id.button30);
        d = new Dialog(this);
        d.setContentView(R.layout.message);
        msg = new Dialog(this);
        msg.setContentView(R.layout.warning);

        Window win2 = d.getWindow();
        WindowManager.LayoutParams wlp = win2.getAttributes();
        win2.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
        wlp.gravity = Gravity.CENTER;
        win2.setBackgroundDrawable(new ColorDrawable(Color.argb(225,255,255,255)));
        win2.setAttributes(wlp);

        Window win1 = msg.getWindow();
        WindowManager.LayoutParams wlp1 = win1.getAttributes();
        win1.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
        wlp1.gravity = Gravity.CENTER;
        win1.setBackgroundDrawable(new ColorDrawable(Color.argb(225,255,255,255)));
        win1.setAttributes(wlp1);

        final MediaPlayer draw = MediaPlayer.create(this,R.raw.draw);
        final MediaPlayer winn = MediaPlayer.create(this,R.raw.win);

        win = getSharedPreferences("win", MODE_PRIVATE);
        loss = getSharedPreferences("loss", MODE_PRIVATE);
        tie = getSharedPreferences("tie", MODE_PRIVATE);
        nam = getSharedPreferences("name",MODE_PRIVATE);
        id = getSharedPreferences("id",MODE_PRIVATE);

        w = win.getInt("win",0);
        l = loss.getInt("loss",0);
        t = tie.getInt("tie",0);
        String name_o = nam.getString("name","");
        ID = id.getString("id","");

        players.child(ID).child("Status").setValue("Online");

        String winner = getIntent().getExtras().getString("winner");
        final String pl1 = getIntent().getExtras().getString("pl1");
        final String pl2 = getIntent().getExtras().getString("pl2");
        String p1 = (pl1.length()>18)?pl1.substring(0,15)+"...":pl1;
        String p2 = (pl2.length()>18)?pl2.substring(0,15)+"...":pl2;

        players.child(ID).child("Online").child("Request").addValueEventListener(new ValueEventListener() {
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
                                finish();
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

        if (winner.equals("-1")) {
            draw.start();
            name.setTextColor(Color.BLACK);
            name.setText("Match Drawn");
            tag.setText("!!!");
            symbol.setImageResource(R.drawable.draw);
            symbol.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 400, getResources().getDisplayMetrics());
            symbol.getLayoutParams().width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 500, getResources().getDisplayMetrics());
            edit = tie.edit();
            edit.putInt("tie",t+1);
            edit.apply();
        }
        else if(winner.equals("1")){
            winn.start();
            name.setText(p1);
            name.setTextColor(Color.rgb(255, 51, 51));
            symbol.setImageResource(R.drawable.medal);
            tag.setText("wins");
            if(name_o.equals(pl1)) {
                edit = win.edit();
                edit.putInt("win", w + 1);
                edit.apply();
            }
            else{
                edit = loss.edit();
                edit.putInt("loss",l+1);
                edit.apply();
            }
        }
        else{
            winn.start();
            name.setText(p2);
            name.setTextColor(Color.rgb(0, 153, 255));
            symbol.setImageResource(R.drawable.medal);
            tag.setText("wins");
            if(name_o.equals(pl2)) {
                edit = win.edit();
                edit.putInt("win", w + 1);
                edit.apply();
            }
            else{
                edit = loss.edit();
                edit.putInt("loss",l+1);
                edit.apply();
            }
        }
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Menu.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    @Override
    public void onResume(){
        super.onResume();
        flag = true;
        rep = false;
        players.child(ID).child("Status").setValue("Online");
    }
    @Override
    public void onPause(){
        super.onPause();
        flag = false;
        players.child(ID).child("Status").setValue("Offline");
    }
}
