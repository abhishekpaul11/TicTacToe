package com.example.tictactoe;

import android.app.Dialog;
import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Stat_Offline extends Fragment {

    private TextView pl1,pl2,draw,last;
    private Button reset;
    private Dialog msg;
    SharedPreferences p1,p2,d,l,id;
    SharedPreferences.Editor editor;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference players = database.getReference("players");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stat_offline, container, false);
        pl1 = view.findViewById(R.id.textView31);
        pl2 = view.findViewById(R.id.textView32);
        draw = view.findViewById(R.id.textView35);
        last = view.findViewById(R.id.textView37);
        reset = view.findViewById(R.id.button23);
        msg = new Dialog(getContext());
        msg.setContentView(R.layout.warning);

        Window win = msg.getWindow();
        WindowManager.LayoutParams wlp = win.getAttributes();
        win.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
        wlp.gravity = Gravity.CENTER;
        win.setBackgroundDrawable(new ColorDrawable(Color.argb(225,255,255,255)));
        win.setAttributes(wlp);

        p1 = getActivity().getSharedPreferences("pl1", Context.MODE_PRIVATE);
        p2 = getActivity().getSharedPreferences("pl2",Context.MODE_PRIVATE);
        d = getActivity().getSharedPreferences("tie_p", Context.MODE_PRIVATE);
        l = getActivity().getSharedPreferences("last_p",Context.MODE_PRIVATE);
        id = getActivity().getSharedPreferences("id",Context.MODE_PRIVATE);

        pl1.setText(p1.getInt("pl1",0)+"");
        pl2.setText(p2.getInt("pl2",0)+"");
        draw.setText(d.getInt("tie_p",0)+"");
        last.setText(l.getString("last_p","NA"));

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView t = msg.findViewById(R.id.textView23);
                t.setText("RESET STATS");
                msg.show();
                Button yes = msg.findViewById(R.id.button20);
                Button no = msg.findViewById(R.id.button21);
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pl1.setText("0");
                        pl2.setText("0");
                        draw.setText("0");
                        last.setText("NA");
                        editor = p1.edit();
                        editor.putInt("pl1",0);
                        editor.apply();
                        editor = p2.edit();
                        editor.putInt("pl2",0);
                        editor.apply();
                        editor = d.edit();
                        editor.putInt("tie_p",0);
                        editor.apply();
                        editor = l.edit();
                        editor.putString("last_p","NA");
                        editor.apply();
                        msg.dismiss();
                        if(haveNetworkConnection()){
                            players.child(id.getString("id","")).child("Offline").child("Player 1").setValue(0);
                            players.child(id.getString("id","")).child("Offline").child("Player 2").setValue(0);
                            players.child(id.getString("id","")).child("Offline").child("Tie").setValue(0);
                            players.child(id.getString("id","")).child("Offline").child("Last").setValue("NA");
                        }
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
