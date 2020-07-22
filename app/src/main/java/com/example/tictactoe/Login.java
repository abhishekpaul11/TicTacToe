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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    private EditText id, pass;
    private Button login,guest,register;
    private CheckBox box,me;
    private Dialog d;
    private boolean flag;
    private String msg;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference players = database.getReference("players");
    SharedPreferences nam,p,ID, avatar,date,winner,loss,tie,win_c,loss_c,tie_c,last_c,pl1,pl2,tie_p,last_p,me_id,me_pass;
    SharedPreferences.Editor edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        me_id = getSharedPreferences("me_id",MODE_PRIVATE);
        me_pass = getSharedPreferences("me_pass",MODE_PRIVATE);
        nam = getSharedPreferences("name",MODE_PRIVATE);
        ID = getSharedPreferences("id",MODE_PRIVATE);
        p = getSharedPreferences("pass",MODE_PRIVATE);
        avatar = getSharedPreferences("avatar",MODE_PRIVATE);
        date = getSharedPreferences("date",MODE_PRIVATE);
        winner = getSharedPreferences("win",MODE_PRIVATE);
        loss = getSharedPreferences("loss",MODE_PRIVATE);
        tie = getSharedPreferences("tie",MODE_PRIVATE);
        win_c = getSharedPreferences("you",MODE_PRIVATE);
        loss_c = getSharedPreferences("charlie",MODE_PRIVATE);
        tie_c = getSharedPreferences("tie_c",MODE_PRIVATE);
        last_c = getSharedPreferences("last_c",MODE_PRIVATE);
        pl1 = getSharedPreferences("pl1",MODE_PRIVATE);
        pl2 = getSharedPreferences("pl2",MODE_PRIVATE);
        tie_p = getSharedPreferences("tie_p",MODE_PRIVATE);
        last_p = getSharedPreferences("last_p",MODE_PRIVATE);
        msg = getIntent().getExtras().getString("msg");

        flag = false;
        me = findViewById(R.id.checkBox3);
        if(!me_id.getString("me_id","").equals("")) me.setChecked(true);
        guest = findViewById(R.id.login4);
        id = findViewById(R.id.textView30);
        id.setText(me_id.getString("me_id",""));
        box = findViewById(R.id.checkBox2);
        id.setOnEditorActionListener(new DoneOnEditorActionListener());
        pass = findViewById(R.id.textView33);
        pass.setText(me_pass.getString("me_pass",""));
        pass.setOnEditorActionListener(new DoneOnEditorActionListener());
        login = findViewById(R.id.login2);
        d = new Dialog(this);
        d.setContentView(R.layout.message);

        guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Logged in as Guest",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(),Menu.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        if(msg.equals("log")) {
            register = findViewById(R.id.login5);
            register.setText("REGISTER");
            register.setBackgroundColor(Color.rgb(0,153,255));
            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), Register.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            });
        }

        box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    pass.setTransformationMethod(null);
                }
                else{
                    pass.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        Window win = d.getWindow();
        WindowManager.LayoutParams wlp = win.getAttributes();
        win.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
        wlp.gravity = Gravity.CENTER;
        win.setBackgroundDrawable(new ColorDrawable(Color.argb(225,255,255,255)));
        win.setAttributes(wlp);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String uid = id.getText().toString();
                final String password = pass.getText().toString();
                if(uid.equals("") || password.equals("")){
                    TextView t = d.findViewById(R.id.textView24);
                    t.setText("Incomplete Details !!!");
                    d.show();
                }
                else if(!isValidPass(password) || !isValid(uid)){
                    TextView t = d.findViewById(R.id.textView24);
                    t.setText("Invalid Entry !!!");
                    d.show();
                }
                else{
                    if(!haveNetworkConnection()){
                        TextView t = d.findViewById(R.id.textView24);
                        t.setText("No Network !!!");
                        TextView txt = d.findViewById(R.id.textView23);
                        txt.setText("ERROR");
                        d.show();
                    }
                    else {
                        if(me.isChecked()){
                            edit = me_id.edit();
                            edit.putString("me_id",id.getText().toString());
                            edit.apply();
                            edit = me_pass.edit();
                            edit.putString("me_pass",pass.getText().toString());
                            edit.apply();
                        }
                        else{
                            edit = me_id.edit();
                            edit.putString("me_id","");
                            edit.apply();
                            edit = me_pass.edit();
                            edit.putString("me_pass","");
                            edit.apply();
                        }
                        flag = true;
                        TextView t = d.findViewById(R.id.textView24);
                        t.setText("Logging in...");
                        TextView txt = d.findViewById(R.id.textView23);
                        txt.setText("MESSAGE");
                        d.setCancelable(false);
                        d.show();
                        players.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists() && dataSnapshot.hasChild(process(uid)) && flag){
                                    if(dataSnapshot.child(process(uid)).child("Password").getValue(String.class).equals(password)){
                                        flag = false;
                                        edit = nam.edit();
                                        edit.putString("name", dataSnapshot.child(process(uid)).child("Name").getValue(String.class));
                                        edit.apply();
                                        edit = p.edit();
                                        edit.putString("pass", dataSnapshot.child(process(uid)).child("Password").getValue(String.class));
                                        edit.apply();
                                        edit = ID.edit();
                                        edit.putString("id", process(uid));
                                        edit.apply();
                                        edit = avatar.edit();
                                        edit.putInt("avatar", dataSnapshot.child(process(uid)).child("Avatar").getValue(Integer.class));
                                        edit.apply();
                                        edit = date.edit();
                                        edit.putString("date", dataSnapshot.child(process(uid)).child("Date Joined").getValue(String.class));
                                        edit.apply();
                                        edit = winner.edit();
                                        edit.putInt("win", dataSnapshot.child(process(uid)).child("Stats").child("Win").getValue(Integer.class));
                                        edit.apply();
                                        edit = loss.edit();
                                        edit.putInt("loss", dataSnapshot.child(process(uid)).child("Stats").child("Loss").getValue(Integer.class));
                                        edit.apply();
                                        edit = tie.edit();
                                        edit.putInt("tie", dataSnapshot.child(process(uid)).child("Stats").child("Tie").getValue(Integer.class));
                                        edit.apply();
                                        edit = win_c.edit();
                                        edit.putInt("you", dataSnapshot.child(process(uid)).child("Charlie").child("You").getValue(Integer.class));
                                        edit.apply();
                                        edit = loss_c.edit();
                                        edit.putInt("charlie", dataSnapshot.child(process(uid)).child("Charlie").child("Charlie").getValue(Integer.class));
                                        edit.apply();
                                        edit = tie_c.edit();
                                        edit.putInt("tie_c", dataSnapshot.child(process(uid)).child("Charlie").child("Tie").getValue(Integer.class));
                                        edit.apply();
                                        edit = last_c.edit();
                                        edit.putString("last_c", dataSnapshot.child(process(uid)).child("Charlie").child("Last").getValue(String.class));
                                        edit.apply();
                                        edit = pl1.edit();
                                        edit.putInt("pl1", dataSnapshot.child(process(uid)).child("Offline").child("Player 1").getValue(Integer.class));
                                        edit.apply();
                                        edit = pl2.edit();
                                        edit.putInt("pl2", dataSnapshot.child(process(uid)).child("Offline").child("Player 2").getValue(Integer.class));
                                        edit.apply();
                                        edit = tie_p.edit();
                                        edit.putInt("tie_p", dataSnapshot.child(process(uid)).child("Offline").child("Tie").getValue(Integer.class));
                                        edit.apply();
                                        edit = last_p.edit();
                                        edit.putString("last_p", dataSnapshot.child(process(uid)).child("Offline").child("Last").getValue(String.class));
                                        edit.apply();
                                        Toast.makeText(getApplicationContext(), "Logged In Successfully", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(getApplicationContext(), Menu.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(i);
                                        d.dismiss();
                                        finish();
                                    }
                                    else {
                                        d.dismiss();
                                        d.setCancelable(true);
                                        TextView txt = d.findViewById(R.id.textView23);
                                        txt.setText("ERROR");
                                        TextView t = d.findViewById(R.id.textView24);
                                        t.setText("Invalid Password !!!");
                                        d.show();
                                    }
                                }
                                else if(flag){
                                    d.dismiss();
                                    d.setCancelable(true);
                                    TextView txt = d.findViewById(R.id.textView23);
                                    txt.setText("ERROR");
                                    TextView t = d.findViewById(R.id.textView24);
                                    t.setText("Invalid ID !!!");
                                    d.show();
                                    flag = false;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                }
            }
        });

    }
    public boolean isValid(String s){
        for(int i =0 ;i<s.length();i++){
            if(Character.isLetter(s.charAt(i))) return true;
        }
        return false;
    }
    public boolean isValidPass(String s){
        for(int i =0 ;i<s.length();i++){
            if(Character.isLetterOrDigit(s.charAt(i))) return true;
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
        return str;
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
    public void onBackPressed(){
        super.onBackPressed();
        if(me.isChecked()){
            edit = me_id.edit();
            edit.putString("me_id",id.getText().toString());
            edit.apply();
            edit = me_pass.edit();
            edit.putString("me_pass",pass.getText().toString());
            edit.apply();
        }
        else{
            edit = me_id.edit();
            edit.putString("me_id","");
            edit.apply();
            edit = me_pass.edit();
            edit.putString("me_pass","");
            edit.apply();
        }
    }
}
