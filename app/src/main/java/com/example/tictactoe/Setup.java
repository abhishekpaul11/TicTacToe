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
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Setup extends AppCompatActivity {

    private EditText pl1,pl2;
    private Button play,flip;
    private ImageView bg;
    private int count = 0;
    private boolean flp = false,flag,rep;
    private Dialog d,msg;
    SharedPreferences id;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference players = database.getReference("players");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_setup);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        id = getSharedPreferences("id",MODE_PRIVATE);
        String s = id.getString("id","");
        if(!s.equals("")) players.child(s).child("Status").setValue("Online");
        pl1 = findViewById(R.id.textView6);
        pl1.setOnEditorActionListener(new DoneOnEditorActionListener());
        pl2 = findViewById(R.id.textView8);
        pl2.setOnEditorActionListener(new DoneOnEditorActionListener());
        play = findViewById(R.id.button18);
        flip = findViewById(R.id.button16);
        bg = findViewById(R.id.imageView3);
        d = new Dialog(this);
        d.setContentView(R.layout.message);
        msg = new Dialog(this);
        msg.setContentView(R.layout.warning);
        flag = true;
        rep = false;

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

        flip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                if(count%2!=0) {
                    bg.setBackgroundColor(Color.argb(80,0, 153, 255));
                    Toast.makeText(getApplicationContext(), "Flip n Play activated", Toast.LENGTH_SHORT).show();
                    flp = true;
                }
                else{
                    bg.setBackgroundColor(Color.argb(0,116, 237, 83));
                    Toast.makeText(getApplicationContext(), "Flip n Play de-activated", Toast.LENGTH_SHORT).show();
                    flp = false;
                }
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pl1.getText().toString().equals("") || pl2.getText().toString().equals("")){
                    TextView msg = d.findViewById(R.id.textView24);
                    msg.setText("Incomplete Details !!!");
                    TextView txt = d.findViewById(R.id.textView23);
                    txt.setText("WARNING");
                    d.show();
                }
                else if(!isValid(pl1.getText().toString()) || !isValid(pl2.getText().toString())){
                    TextView msg = d.findViewById(R.id.textView24);
                    msg.setText("Invalid Entry !!!");
                    TextView txt = d.findViewById(R.id.textView23);
                    txt.setText("WARNING");
                    d.show();
                }
                else{
                    Intent i = new Intent(getApplicationContext(),MainActivity.class);
                    i.putExtra("player1",process(pl1.getText().toString()));
                    i.putExtra("player2",process(pl2.getText().toString()));
                    i.putExtra("flip",flp);
                    startActivity(i);
                    finish();
                }
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
    public boolean isValid(String s){
        for(int i =0 ;i<s.length();i++){
            if(Character.isLetter(s.charAt(i))) return true;
        }
        return false;
    }
    public String process(String s){
        String str = "";
        s = s.trim();
        int IN = 1, OUT = 0;
        int state = OUT;
        for(int i = 0;i<s.length();i++){
            char c = s.charAt(i);
            if(!Character.isWhitespace(c) && state==OUT){
                state = IN;
                str+=c;
            }
            else if(!Character.isWhitespace(c) && state==IN){
                str+=c;
            }
            else if(Character.isWhitespace(c) && state==IN){
                state = OUT;
                str+=" ";
            }
        }
        return (str.length()>18)?str.substring(0,15)+"...":str;
    }
    @Override
    public void onPause(){
        super.onPause();
        flag = false;
        rep = false;
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
class DoneOnEditorActionListener implements EditText.OnEditorActionListener {
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
        if(actionId == EditorInfo.IME_ACTION_DONE){
            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(),0);
            v.clearFocus();
            return true;
        }
        return false;
    }
}
