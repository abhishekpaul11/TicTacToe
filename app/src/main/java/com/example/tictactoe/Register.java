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
import android.os.CountDownTimer;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Register extends AppCompatActivity {

    private EditText name, pass, uid;
    private Button register, login, left, right,guest;
    private ImageView img;
    private int index = 0;
    private boolean flag = true,bug,rep,flag1;
    private CheckBox box;
    private String ID;
    private Dialog d,msg;
    private int[] avatars = new int[]{R.drawable.avatar1,R.drawable.avatar2,R.drawable.avatar3,R.drawable.avatar4,R.drawable.avatar5,R.drawable.avatar6,R.drawable.avatar7,R.drawable.avatar8,R.drawable.avatar9};
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference players = database.getReference("players");
    SharedPreferences nam, id, avatar,date,winner,loss,tie,win_c,loss_c,tie_c,last_c,pl1,pl2,tie_p,last_p,passwrd;
    SharedPreferences.Editor edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        bug = false;
        flag1 = true;
        rep = false;
        guest = findViewById(R.id.login3);
        box = findViewById(R.id.checkBox);
        name = findViewById(R.id.textView3);
        uid = findViewById(R.id.textView11);
        name.setOnEditorActionListener(new DoneOnEditorActionListener());
        pass = findViewById(R.id.textView29);
        pass.setOnEditorActionListener(new DoneOnEditorActionListener());
        register = findViewById(R.id.button18);
        login = findViewById(R.id.login);
        left = findViewById(R.id.button19);
        right = findViewById(R.id.button22);
        img = findViewById(R.id.imageView6);

        d = new Dialog(this);
        d.setContentView(R.layout.message);

        Window win = d.getWindow();
        WindowManager.LayoutParams wlp = win.getAttributes();
        win.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
        wlp.gravity = Gravity.CENTER;
        win.setBackgroundDrawable(new ColorDrawable(Color.argb(225,255,255,255)));
        win.setAttributes(wlp);

        passwrd = getSharedPreferences("pass",MODE_PRIVATE);
        nam = getSharedPreferences("name",MODE_PRIVATE);
        id = getSharedPreferences("id",MODE_PRIVATE);
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

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index--;
                if(index==-1) index=8;
                img.setImageResource(avatars[index]);
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index++;
                if(index==9) index=0;
                img.setImageResource(avatars[index]);
            }
        });

        if(id.getString("id","").equals("")) {
            guest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Logged in as Guest", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(), Menu.class);
                    startActivity(i);
                    finish();
                }
            });

            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String n = name.getText().toString();
                    final String p = pass.getText().toString();
                    final String u = uid.getText().toString();
                    if (n.equals("") || p.equals("") || u.equals("")) {
                        TextView t = d.findViewById(R.id.textView24);
                        t.setText("Incomplete Details !!!");
                        d.show();
                    }
                    else if(!isValidID(u)){
                        TextView t = d.findViewById(R.id.textView24);
                        t.setText("ID contains invalid characters !!!");
                        d.show();
                    }
                    else if (!isValid(n) || !isValidPass(p) || !isValid(u)) {
                        TextView t = d.findViewById(R.id.textView24);
                        t.setText("Invalid Entry !!!");
                        d.show();
                    } else {
                        if (!haveNetworkConnection()) {
                            TextView t = d.findViewById(R.id.textView24);
                            t.setText("No Network !!!");
                            TextView txt = d.findViewById(R.id.textView23);
                            txt.setText("ERROR");
                            d.show();
                        } else {
                            bug = true;
                            TextView t = d.findViewById(R.id.textView24);
                            t.setText("Registering...");
                            TextView txt = d.findViewById(R.id.textView23);
                            txt.setText("MESSAGE");
                            d.setCancelable(false);
                            d.show();
                            players.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (bug) {
                                        if (dataSnapshot.exists() && dataSnapshot.hasChild(u)) {
                                            if (flag) {
                                                d.dismiss();
                                                d.setCancelable(true);
                                                TextView txt = d.findViewById(R.id.textView23);
                                                txt.setText("ERROR");
                                                TextView t = d.findViewById(R.id.textView24);
                                                t.setText("ID already exists !!!");
                                                d.show();
                                                bug = false;
                                            }
                                        } else {
                                            bug = false;
                                            flag = false;
                                            String up = process(u);
                                            String dt = new SimpleDateFormat("dd MMM ''yy", Locale.getDefault()).format(new Date());
                                            edit = nam.edit();
                                            edit.putString("name", process(n));
                                            edit.apply();
                                            edit = passwrd.edit();
                                            edit.putString("pass", p);
                                            edit.apply();
                                            edit = date.edit();
                                            edit.putString("date", dt);
                                            edit.apply();
                                            edit = id.edit();
                                            edit.putString("id", process(u));
                                            edit.apply();
                                            edit = avatar.edit();
                                            edit.putInt("avatar", index);
                                            edit.apply();
                                            edit = winner.edit();
                                            edit.putInt("win", 0);
                                            edit.apply();
                                            edit = loss.edit();
                                            edit.putInt("loss", 0);
                                            edit.apply();
                                            edit = tie.edit();
                                            edit.putInt("tie", 0);
                                            edit.apply();
                                            edit = win_c.edit();
                                            edit.putInt("you", 0);
                                            edit.apply();
                                            edit = loss_c.edit();
                                            edit.putInt("charlie", 0);
                                            edit.apply();
                                            edit = tie_c.edit();
                                            edit.putInt("tie_c", 0);
                                            edit.apply();
                                            edit = last_c.edit();
                                            edit.putString("last_c", "NA");
                                            edit.apply();
                                            edit = pl1.edit();
                                            edit.putInt("pl1", 0);
                                            edit.apply();
                                            edit = pl2.edit();
                                            edit.putInt("pl2", 0);
                                            edit.apply();
                                            edit = tie_p.edit();
                                            edit.putInt("tie_p", 0);
                                            edit.apply();
                                            edit = last_p.edit();
                                            edit.putString("last_p", "NA");
                                            edit.apply();
                                            players.child(up).child("Name").setValue(process(n));
                                            players.child(up).child("Password").setValue(p);
                                            players.child(up).child("Avatar").setValue(index);
                                            players.child(up).child("Stats").child("Win").setValue(0);
                                            players.child(up).child("Stats").child("Loss").setValue(0);
                                            players.child(up).child("Stats").child("Tie").setValue(0);
                                            players.child(up).child("Charlie").child("You").setValue(0);
                                            players.child(up).child("Charlie").child("Charlie").setValue(0);
                                            players.child(up).child("Charlie").child("Tie").setValue(0);
                                            players.child(up).child("Charlie").child("Last").setValue("NA");
                                            players.child(up).child("Offline").child("Player 1").setValue(0);
                                            players.child(up).child("Offline").child("Player 2").setValue(0);
                                            players.child(up).child("Offline").child("Tie").setValue(0);
                                            players.child(up).child("Offline").child("Last").setValue("NA");
                                            players.child(up).child("Online").child("Request").setValue("");
                                            players.child(up).child("Online").child("Reply").setValue("");
                                            players.child(up).child("Online").child("Ping").setValue(0);
                                            players.child(up).child("Online").child("Ingame").setValue("");
                                            players.child(up).child("Online").child("Position").setValue(-1);
                                            players.child(up).child("Status").setValue("Offline");
                                            players.child(up).child("Date Joined").setValue(dt);
                                            Toast.makeText(getApplicationContext(), "Successfully Registered", Toast.LENGTH_SHORT).show();
                                            Intent i = new Intent(getApplicationContext(), Menu.class);
                                            startActivity(i);
                                            d.dismiss();
                                            finish();
                                        }
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

            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), Login.class);
                    i.putExtra("msg","reg");
                    startActivity(i);
                    name.setText("");
                    pass.setText("");
                    uid.setText("");
                    img.setImageResource(avatars[0]);
                    box.setChecked(false);
                    pass.setTransformationMethod(new PasswordTransformationMethod());
                }
            });
        }
        else{
            msg = new Dialog(this);
            msg.setContentView(R.layout.warning);

            Window win1 = msg.getWindow();
            WindowManager.LayoutParams wlp1 = win1.getAttributes();
            win1.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
            wlp1.gravity = Gravity.CENTER;
            win1.setBackgroundDrawable(new ColorDrawable(Color.argb(225,255,255,255)));
            win1.setAttributes(wlp1);

            ID = id.getString("id","");
            players.child(ID).child("Online").child("Request").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(flag1){
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

            players.child(id.getString("id","")).child("Status").setValue("Online");
            login.setText("");
            login.setBackgroundColor(Color.TRANSPARENT);
            guest.setText("");
            guest.setBackgroundColor(Color.TRANSPARENT);
            register.setText("SAVE");
            uid.setHint("");
            uid.setBackgroundColor(Color.TRANSPARENT);
            uid.getLayoutParams().width = 0;
            uid.getLayoutParams().height = 0;

            name.setText(nam.getString("name",""));
            name.setHint("Enter new name");
            index = avatar.getInt("avatar",0);
            img.setImageResource(avatars[avatar.getInt("avatar",0)]);
            pass.setText(passwrd.getString("pass",""));
            pass.setHint("Enter new password");

            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (name.getText().toString().equals("") || pass.getText().toString().equals("")) {
                        TextView t = d.findViewById(R.id.textView24);
                        t.setText("Incomplete Details !!!");
                        TextView txt = d.findViewById(R.id.textView23);
                        txt.setText("WARNING");
                        d.show();
                    }
                    else if (!isValid(name.getText().toString()) || !isValidPass(pass.getText().toString())) {
                        TextView t = d.findViewById(R.id.textView24);
                        t.setText("Invalid Entry !!!");
                        TextView txt = d.findViewById(R.id.textView23);
                        txt.setText("WARNING");
                        d.show();
                    }
                    else {
                        if (haveNetworkConnection()) {
                            TextView t = d.findViewById(R.id.textView24);
                            t.setText("Saving...");
                            TextView txt = d.findViewById(R.id.textView23);
                            txt.setText("MESSAGE");
                            d.setCancelable(false);
                            d.show();
                            players.child(id.getString("id", "")).child("Name").setValue(process(name.getText().toString()));
                            players.child(id.getString("id", "")).child("Password").setValue(pass.getText().toString());
                            players.child(id.getString("id", "")).child("Avatar").setValue(index);
                            edit = nam.edit();
                            edit.putString("name", process(name.getText().toString()));
                            edit.apply();
                            edit = passwrd.edit();
                            edit.putString("pass", pass.getText().toString());
                            edit.apply();
                            edit = avatar.edit();
                            edit.putInt("avatar", index);
                            edit.apply();
                            Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getApplicationContext(), Menu.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            d.dismiss();
                            startActivity(i);
                        } else {
                            TextView t = d.findViewById(R.id.textView24);
                            t.setText("No Network !!!");
                            TextView txt = d.findViewById(R.id.textView23);
                            txt.setText("ERROR");
                            d.show();
                        }
                    }
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
    public boolean isValidID(String s){
        for(int i =0 ;i<s.length();i++){
            if("#.[]/$".indexOf(s.charAt(i))>=0) return false;
        }
        return true;
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
    public void onPause(){
        super.onPause();
        rep = false;
        flag1 = false;
        id = getSharedPreferences("id",MODE_PRIVATE);
        String s = id.getString("id","");
        if(!s.equals("")) players.child(s).child("Status").setValue("Offline");
    }
    @Override
    public void onResume(){
        super.onResume();
        flag1 = true;
        id = getSharedPreferences("id",MODE_PRIVATE);
        String s = id.getString("id","");
        if(!s.equals("")) players.child(s).child("Status").setValue("Online");
    }
}
