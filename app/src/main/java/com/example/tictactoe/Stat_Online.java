package com.example.tictactoe;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.Context.MODE_PRIVATE;

public class Stat_Online extends Fragment {

    private Button delete, logout,reset,edi;
    private TextView id, name, date, w,l,d;
    private Dialog msg,net,msg1;
    private ImageView img;
    private boolean f = true;
    SharedPreferences uid,nam,dat,winner,loss,tie,image,win_c,loss_c,tie_c,last_c,last_p,tie_p,pl1,pl2;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference players = database.getReference("players");
    SharedPreferences.Editor e;
    private int[] avatars = new int[]{R.drawable.avatar1,R.drawable.avatar2,R.drawable.avatar3,R.drawable.avatar4,R.drawable.avatar5,R.drawable.avatar6,R.drawable.avatar7,R.drawable.avatar8,R.drawable.avatar9};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.stat_online, container, false);
        delete = view.findViewById(R.id.del);
        logout = view.findViewById(R.id.logout);
        reset = view.findViewById(R.id.reset);
        edi = view.findViewById(R.id.edit);
        id = view.findViewById(R.id.textView15);
        name = view.findViewById(R.id.textView16);
        date = view.findViewById(R.id.textView17);
        w = view.findViewById(R.id.textView20);
        l = view.findViewById(R.id.textView22);
        d = view.findViewById(R.id.textView25);
        img = view.findViewById(R.id.imageView18);
        msg = new Dialog(getContext());
        msg.setContentView(R.layout.warning);
        msg1 = new Dialog(getContext());
        msg1.setContentView(R.layout.warning);
        net = new Dialog(getContext());
        net.setContentView(R.layout.message);
        edi = view.findViewById(R.id.edit);

        uid = getActivity().getSharedPreferences("id", MODE_PRIVATE);
        nam = getActivity().getSharedPreferences("name", MODE_PRIVATE);
        dat = getActivity().getSharedPreferences("date", MODE_PRIVATE);
        winner = getActivity().getSharedPreferences("win", MODE_PRIVATE);
        loss = getActivity().getSharedPreferences("loss", MODE_PRIVATE);
        tie = getActivity().getSharedPreferences("tie", MODE_PRIVATE);
        image = getActivity().getSharedPreferences("avatar", MODE_PRIVATE);
        win_c = getActivity().getSharedPreferences("you",MODE_PRIVATE);
        loss_c = getActivity().getSharedPreferences("charlie",MODE_PRIVATE);
        tie_c = getActivity().getSharedPreferences("tie_c",MODE_PRIVATE);
        last_c = getActivity().getSharedPreferences("last_c",MODE_PRIVATE);
        pl1 = getActivity().getSharedPreferences("pl1",MODE_PRIVATE);
        pl2 = getActivity().getSharedPreferences("pl2",MODE_PRIVATE);
        tie_p = getActivity().getSharedPreferences("tie_p",MODE_PRIVATE);
        last_p = getActivity().getSharedPreferences("last_p",MODE_PRIVATE);

        id.setText(uid.getString("id","-"));
        name.setText(nam.getString("name","-"));
        date.setText(dat.getString("date","NA"));
        img.setImageResource(avatars[image.getInt("avatar",0)]);
        String iid = uid.getString("id","-");

        players.child(iid).child("Stats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(f){
                    f = false;
                    w.setText(dataSnapshot.child("Win").getValue(Integer.class) + "");
                    l.setText(dataSnapshot.child("Loss").getValue(Integer.class) + "");
                    d.setText(dataSnapshot.child("Tie").getValue(Integer.class) + "");
                    e = winner.edit();
                    e.putInt("win", dataSnapshot.child("Win").getValue(Integer.class));
                    e.apply();
                    e = loss.edit();
                    e.putInt("loss", dataSnapshot.child("Loss").getValue(Integer.class));
                    e.apply();
                    e = tie.edit();
                    e.putInt("tie", dataSnapshot.child("Tie").getValue(Integer.class));
                    e.apply();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        if(!haveNetworkConnection()){
            w.setText(winner.getInt("win",0)+"");
            l.setText(loss.getInt("loss",0)+"");
            d.setText(tie.getInt("tie",0)+"");
        }

        Window win = msg.getWindow();
        WindowManager.LayoutParams wlp = win.getAttributes();
        win.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
        wlp.gravity = Gravity.CENTER;
        win.setBackgroundDrawable(new ColorDrawable(Color.argb(225,255,255,255)));
        win.setAttributes(wlp);

        Window win2 = msg1.getWindow();
        WindowManager.LayoutParams wlp2 = win.getAttributes();
        win2.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
        wlp2.gravity = Gravity.CENTER;
        win2.setBackgroundDrawable(new ColorDrawable(Color.argb(225,255,255,255)));
        win2.setAttributes(wlp2);

        Window win1 = net.getWindow();
        WindowManager.LayoutParams wlp1 = win1.getAttributes();
        win1.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
        wlp1.gravity = Gravity.CENTER;
        win1.setBackgroundDrawable(new ColorDrawable(Color.argb(225,255,255,255)));
        win1.setAttributes(wlp);

        edi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),Register.class);
                startActivity(i);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!haveNetworkConnection()){
                    TextView t = net.findViewById(R.id.textView23);
                    t.setText("ERROR");
                    TextView txt = net.findViewById(R.id.textView24);
                    txt.setText("No Network");
                    net.show();
                }
                else {
                    TextView t = msg.findViewById(R.id.textView23);
                    t.setText("LOGOUT");
                    msg.show();
                    Button yes = msg.findViewById(R.id.button20);
                    Button no = msg.findViewById(R.id.button21);
                    yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            players.child(uid.getString("id","")).child("Offline").child("Player 1").setValue(pl1.getInt("pl1",0));
                            players.child(uid.getString("id","")).child("Offline").child("Player 2").setValue(pl2.getInt("pl2",0));
                            players.child(uid.getString("id","")).child("Offline").child("Tie").setValue(tie_p.getInt("tie_p",0));
                            players.child(uid.getString("id","")).child("Offline").child("Last").setValue(last_p.getString("last_p","NA"));
                            players.child(uid.getString("id","")).child("Charlie").child("You").setValue(win_c.getInt("you",0));
                            players.child(uid.getString("id","")).child("Charlie").child("Charlie").setValue(loss_c.getInt("charlie",0));
                            players.child(uid.getString("id","")).child("Charlie").child("Tie").setValue(tie_c.getInt("tie_c",0));
                            players.child(uid.getString("id","")).child("Charlie").child("Last").setValue(last_c.getString("last_c","NA"));
                            players.child(uid.getString("id","")).child("Status").setValue("Offline");
                            e = nam.edit();
                            e.putString("name","");
                            e.apply();
                            e = uid.edit();
                            e.putString("id","");
                            e.apply();
                            msg.dismiss();
                            Toast.makeText(getContext(),"Logged Out Successfully",Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getContext(), Login.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i.putExtra("msg","log");
                            startActivity(i);
                        }
                    });
                    no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            msg.dismiss();
                        }
                    });
                }
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!haveNetworkConnection()){
                    TextView t = net.findViewById(R.id.textView23);
                    t.setText("ERROR");
                    TextView txt = net.findViewById(R.id.textView24);
                    txt.setText("No Network");
                    net.show();
                }
                else {
                    TextView t = msg.findViewById(R.id.textView23);
                    t.setText("RESET STATS");
                    msg.show();
                    Button yes = msg.findViewById(R.id.button20);
                    Button no = msg.findViewById(R.id.button21);
                    yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            players.child(id.getText().toString()).child("Stats").child("Win").setValue(0);
                            players.child(id.getText().toString()).child("Stats").child("Loss").setValue(0);
                            players.child(id.getText().toString()).child("Stats").child("Tie").setValue(0);
                            e = winner.edit();
                            e.putInt("win",0);
                            e.apply();
                            e = loss.edit();
                            e.putInt("loss",0);
                            e.apply();
                            e = tie.edit();
                            e.putInt("tie",0);
                            e.apply();
                            msg.dismiss();
                            w.setText("0");
                            l.setText("0");
                            d.setText("0");
                            Toast.makeText(getContext(),"Stat Reset Successfully",Toast.LENGTH_SHORT).show();
                        }
                    });
                    no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            msg.dismiss();
                        }
                    });
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!haveNetworkConnection()){
                    TextView t = net.findViewById(R.id.textView23);
                    t.setText("ERROR");
                    TextView txt = net.findViewById(R.id.textView24);
                    txt.setText("No Network");
                    net.show();
                }
                else {
                    TextView t = msg1.findViewById(R.id.textView23);
                    t.setText("DELETE ACCOUNT");
                    msg1.show();
                    Button yes1 = msg1.findViewById(R.id.button20);
                    Button no1 = msg1.findViewById(R.id.button21);
                    yes1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            e = nam.edit();
                            e.putString("name","");
                            e.apply();
                            e = uid.edit();
                            e.putString("id","");
                            e.apply();
                            Stats.flag = false;
                            players.child(id.getText().toString()).removeValue();
                            msg1.dismiss();
                            Toast.makeText(getContext(),"Account Deleted Successfully",Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getContext(), Greet.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        }
                    });
                    no1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            msg1.dismiss();
                        }
                    });
                }
            }
        });

        return view;
    }
    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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
}
