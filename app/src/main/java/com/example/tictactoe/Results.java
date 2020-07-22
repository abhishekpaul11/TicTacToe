package com.example.tictactoe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

public class Results extends AppCompatActivity {

    private TextView name,tag;
    private ImageView symbol;
    private Button again,menu;
    private boolean flag,rep;
    private Dialog d,msg;
    SharedPreferences id,win_c,loss_c,tie_c,last_c,p1,p2,tie_p,last_p;
    SharedPreferences.Editor editor;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference players = database.getReference("players");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_results);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        id = getSharedPreferences("id",MODE_PRIVATE);
        win_c = getSharedPreferences("you",MODE_PRIVATE);
        loss_c = getSharedPreferences("charlie",MODE_PRIVATE);
        tie_c = getSharedPreferences("tie_c",MODE_PRIVATE);
        last_c = getSharedPreferences("last_c",MODE_PRIVATE);
        p1 = getSharedPreferences("pl1",MODE_PRIVATE);
        p2 = getSharedPreferences("pl2",MODE_PRIVATE);
        tie_p = getSharedPreferences("tie_p",MODE_PRIVATE);
        last_p = getSharedPreferences("last_p",MODE_PRIVATE);

        String s = id.getString("id","");
        if(!s.equals("")) players.child(s).child("Status").setValue("Online");

        String winner = getIntent().getExtras().getString("winner");
        final String pl1 = getIntent().getExtras().getString("player1");
        final String pl2 = getIntent().getExtras().getString("player2");
        final boolean flip = getIntent().getExtras().getBoolean("flip");
        final boolean source = getIntent().getExtras().getBoolean("source");

        final MediaPlayer draw = MediaPlayer.create(this,R.raw.draw);
        final MediaPlayer win = MediaPlayer.create(this,R.raw.win);

        name = findViewById(R.id.textView4);
        tag = findViewById(R.id.textView5);
        symbol = findViewById(R.id.imageView2);
        again = findViewById(R.id.button);
        menu = findViewById(R.id.button12);
        rep = false;
        flag = true;
        d = new Dialog(this);
        msg = new Dialog(this);
        d.setContentView(R.layout.message);
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

        if(winner.equals("-1")){
            draw.start();
            name.setTextColor(Color.BLACK);
            name.setText("Match Drawn");
            tag.setText("!!!");
            symbol.setImageResource(R.drawable.draw);
            symbol.getLayoutParams().height= (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 400, getResources().getDisplayMetrics());
            symbol.getLayoutParams().width = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 500, getResources().getDisplayMetrics());
            if(source && !id.getString("id","").equals("")){
                editor = tie_c.edit();
                editor.putInt("tie_c",tie_c.getInt("tie_c",0)+1);
                editor.apply();
                editor = last_c.edit();
                editor.putString("last_c","Drawn");
                editor.apply();
                if(haveNetworkConnection()){
                    players.child(id.getString("id","")).child("Charlie").child("Tie").setValue(tie_c.getInt("tie_c",0));
                    players.child(id.getString("id","")).child("Charlie").child("Last").setValue("Drawn");
                }
            }
            else if(!id.getString("id","").equals("")){
                editor = tie_p.edit();
                editor.putInt("tie_p",tie_p.getInt("tie_p",0)+1);
                editor.apply();
                editor = last_p.edit();
                editor.putString("last_p","Drawn");
                editor.apply();
                if(haveNetworkConnection()){
                    players.child(id.getString("id","")).child("Offline").child("Tie").setValue(tie_p.getInt("tie_p",0));
                    players.child(id.getString("id","")).child("Offline").child("Last").setValue("Drawn");
                }
            }
        }
        else if(winner.equals("1")){
            win.start();
            name.setText(pl1);
            name.setTextColor(Color.rgb(255, 51, 51));
            if(source){
                if(pl1.equals("Charlie")) {
                    if(!id.getString("id","").equals("")){
                        editor = loss_c.edit();
                        editor.putInt("charlie",loss_c.getInt("charlie",0)+1);
                        editor.apply();
                        editor = last_c.edit();
                        editor.putString("last_c","Charlie");
                        editor.apply();
                        if(haveNetworkConnection()){
                            players.child(id.getString("id","")).child("Charlie").child("Charlie").setValue(loss_c.getInt("charlie",0));
                            players.child(id.getString("id","")).child("Charlie").child("Last").setValue("Charlie");
                        }
                    }
                    symbol.setImageResource(R.drawable.bot);
                    tag.setText("wins");
                }
                else{
                    if(!id.getString("id","").equals("")){
                        editor = win_c.edit();
                        editor.putInt("you",win_c.getInt("you",0)+1);
                        editor.apply();
                        editor = last_c.edit();
                        editor.putString("last_c","You");
                        editor.apply();
                        if(haveNetworkConnection()){
                            players.child(id.getString("id","")).child("Charlie").child("You").setValue(win_c.getInt("you",0));
                            players.child(id.getString("id","")).child("Charlie").child("Last").setValue("You");
                        }
                    }
                    symbol.setImageResource(R.drawable.medal);
                    tag.setText("win");
                }
            }
            else{
                if(!id.getString("id","").equals("")){
                    editor = p1.edit();
                    editor.putInt("pl1",p1.getInt("pl1",0)+1);
                    editor.apply();
                    editor = last_p.edit();
                    editor.putString("last_p","Player 1");
                    editor.apply();
                    if(haveNetworkConnection()){
                        players.child(id.getString("id","")).child("Offline").child("Player 1").setValue(p1.getInt("pl1",0));
                        players.child(id.getString("id","")).child("Offline").child("Last").setValue("Player 1");
                    }
                }
                symbol.setImageResource(R.drawable.medal);
                tag.setText("wins");
            }
        }
        else{
            win.start();
            name.setText(pl2);
            name.setTextColor(Color.rgb(0, 153, 255));
            if(source){
                if(pl2.equals("Charlie")) {
                    if(!id.getString("id","").equals("")){
                        editor = loss_c.edit();
                        editor.putInt("charlie",loss_c.getInt("charlie",0)+1);
                        editor.apply();
                        editor = last_c.edit();
                        editor.putString("last_c","Charlie");
                        editor.apply();
                        if(haveNetworkConnection()){
                            players.child(id.getString("id","")).child("Charlie").child("Charlie").setValue(loss_c.getInt("charlie",0));
                            players.child(id.getString("id","")).child("Charlie").child("Last").setValue("Charlie");
                        }
                    }
                    symbol.setImageResource(R.drawable.bot);
                    tag.setText("wins");
                }
                else{
                    if(!id.getString("id","").equals("")){
                        editor = win_c.edit();
                        editor.putInt("you",win_c.getInt("you",0)+1);
                        editor.apply();
                        editor = last_c.edit();
                        editor.putString("last_c","You");
                        editor.apply();
                        if(haveNetworkConnection()){
                            players.child(id.getString("id","")).child("Charlie").child("You").setValue(win_c.getInt("you",0));
                            players.child(id.getString("id","")).child("Charlie").child("Last").setValue("You");
                        }
                    }
                    symbol.setImageResource(R.drawable.medal);
                    tag.setText("win");
                }
            }
            else{
                if(!id.getString("id","").equals("")){
                    editor = p2.edit();
                    editor.putInt("pl2",p2.getInt("pl2",0)+1);
                    editor.apply();
                    editor = last_p.edit();
                    editor.putString("last_p","Player 2");
                    editor.apply();
                    if(haveNetworkConnection()){
                        players.child(id.getString("id","")).child("Offline").child("Player 2").setValue(p2.getInt("pl2",0));
                        players.child(id.getString("id","")).child("Offline").child("Last").setValue("Player 2");
                    }
                }
                symbol.setImageResource(R.drawable.medal);
                tag.setText("wins");
            }
        }

        again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(source) {
                    Intent i = new Intent(getApplicationContext(), Solo.class);
                    startActivity(i);
                    finish();
                }
                else{
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    i.putExtra("player1", pl2);
                    i.putExtra("player2", pl1);
                    i.putExtra("flip", flip);
                    startActivity(i);
                    finish();
                }
            }
        });
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final String ID = id.getString("id","");
        if(!ID.equals("")) {
            players.child(ID).child("Online").child("Request").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (flag) {
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
    }
    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected()) {
                    haveConnectedWifi = true;
                }
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
    @Override
    public void onPause(){
        super.onPause();
        rep = false;
        flag = false;
        id = getSharedPreferences("id",MODE_PRIVATE);
        String s = id.getString("id","");
        if(!s.equals("")) players.child(s).child("Status").setValue("Offline");
    }
    @Override
    public void onResume(){
        super.onResume();
        flag = true;
        id = getSharedPreferences("id",MODE_PRIVATE);
        String s = id.getString("id","");
        if(!s.equals("")) players.child(s).child("Status").setValue("Online");
    }
}
