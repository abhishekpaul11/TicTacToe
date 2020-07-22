package com.example.tictactoe;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

public class Stats extends AppCompatActivity {

    SharedPreferences id;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference players = database.getReference("players");
    private String ID;
    static boolean flag = true, rep = false;
    private Dialog d,msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_stats);

        id = getSharedPreferences("id",MODE_PRIVATE);
        String s = id.getString("id","");
        ID = s;
        rep = false;
        if(!s.equals("")) players.child(s).child("Status").setValue("Online");
        d = new Dialog(this);
        msg = new Dialog(this);
        d.setContentView(R.layout.message);
        msg.setContentView(R.layout.warning);

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

        final TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("ONLINE"));
        tabLayout.addTab(tabLayout.newTab().setText("OFFLINE"));
        tabLayout.addTab(tabLayout.newTab().setText("SOLO"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final ViewPager viewPager = findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        LinearLayout linearLayout = (LinearLayout)((ViewGroup) tabLayout.getChildAt(0)).getChildAt(0);
        TextView tabTextView = (TextView) linearLayout.getChildAt(1);
        tabTextView.setTypeface(tabTextView.getTypeface(), Typeface.BOLD);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                LinearLayout linearLayout = (LinearLayout)((ViewGroup) tabLayout.getChildAt(0)).getChildAt(tab.getPosition());
                TextView tabTextView = (TextView) linearLayout.getChildAt(1);
                tabTextView.setTypeface(tabTextView.getTypeface(), Typeface.BOLD);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                LinearLayout linearLayout = (LinearLayout)((ViewGroup) tabLayout.getChildAt(0)).getChildAt(tab.getPosition());
                TextView tabTextView = (TextView) linearLayout.getChildAt(1);
                tabTextView.setTypeface(Typeface.DEFAULT);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                LinearLayout linearLayout = (LinearLayout)((ViewGroup) tabLayout.getChildAt(0)).getChildAt(tab.getPosition());
                TextView tabTextView = (TextView) linearLayout.getChildAt(1);
                tabTextView.setTypeface(tabTextView.getTypeface(), Typeface.BOLD);
            }
        });

        players.child(s).child("Online").child("Request").addValueEventListener(new ValueEventListener() {
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