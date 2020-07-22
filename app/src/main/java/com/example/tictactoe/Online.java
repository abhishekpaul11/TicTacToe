package com.example.tictactoe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Online extends AppCompatActivity {

    static FirebaseDatabase database = FirebaseDatabase.getInstance();
    static DatabaseReference players = database.getReference("players");
    SharedPreferences id,namesp;
    ArrayList<Card> profile;
    ArrayList<Card> s;
    private RecyclerView view;
    private NameAdapter nameAdapter;
    private TextView load;
    private Button alpha,win,online,cancel,src;
    private EditText search;
    private int version = 1,bug = 0;
    private boolean flag = false;
    private boolean fix = true, res = true, sr = false, a = false, b = false, c = false,abhi;
    private static boolean req = true,rep = false,debug = false;
    private static Dialog d;
    private Dialog net,msg;
    private CountDownTimer con;
    private static String ID,str = "",NAME;
    private static String reply = "";
    private static CountDownTimer ct;
    private static Context cont;
    private static Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_online);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        rep = false;
        abhi = true;
        act = this;
        cont = getApplicationContext();
        profile = new ArrayList<>();
        load = findViewById(R.id.textView13);
        view = findViewById(R.id.recycler);
        alpha = findViewById(R.id.button24);
        win = findViewById(R.id.button25);
        online = findViewById(R.id.button26);
        view.setHasFixedSize(true);
        view.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        id = getSharedPreferences("id",MODE_PRIVATE);
        namesp = getSharedPreferences("name",MODE_PRIVATE);
        search = findViewById(R.id.textView38);
        cancel = findViewById(R.id.button27);
        src = findViewById(R.id.button28);
        ID = id.getString("id","");
        NAME = namesp.getString("name","");
        players.child(ID).child("Status").setValue("Online");
        d = new Dialog(this);
        d.setContentView(R.layout.message);
        net = new Dialog(this);
        net.setContentView(R.layout.message);
        msg = new Dialog(this);
        msg.setContentView(R.layout.warning);

        search.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(),0);
                    search();
                    return true;
                }
                return false;
            }
        });

        Window win2 = net.getWindow();
        WindowManager.LayoutParams wlp1 = win2.getAttributes();
        win2.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
        wlp1.gravity = Gravity.CENTER;
        win2.setBackgroundDrawable(new ColorDrawable(Color.argb(225,255,255,255)));
        win2.setAttributes(wlp1);

        Window win3 = msg.getWindow();
        WindowManager.LayoutParams wlp2 = win3.getAttributes();
        win3.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
        wlp2.gravity = Gravity.CENTER;
        win3.setBackgroundDrawable(new ColorDrawable(Color.argb(225,255,255,255)));
        win3.setAttributes(wlp2);

        con = new CountDownTimer(360000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(!haveNetworkConnection()){
                    net.setCancelable(false);
                    TextView t = net.findViewById(R.id.textView24);
                    t.setText("No Network !!!");
                    TextView txt = net.findViewById(R.id.textView23);
                    txt.setText("ERROR");
                    Button cancel = net.findViewById(R.id.cancel2);
                    cancel.setBackgroundResource(R.drawable.back);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            net.dismiss();
                            con.cancel();
                            finish();
                        }
                    });
                    net.show();
                }
                else {
                    net.dismiss();
                }
            }

            @Override
            public void onFinish() {

            }
        }.start();

        Window win1 = d.getWindow();
        WindowManager.LayoutParams wlp = win1.getAttributes();
        win1.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
        wlp.gravity = Gravity.CENTER;
        win1.setBackgroundDrawable(new ColorDrawable(Color.argb(225,255,255,255)));
        win1.setAttributes(wlp);

        search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    cancel.setBackgroundResource(R.drawable.tick);
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setText("");
                search.clearFocus();
                cancel.setBackgroundColor(Color.TRANSPARENT);
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(),0);
                if(sr){
                    profile = (ArrayList<Card>) s.clone();
                    sr = false;
                    b = false;
                    c = true;
                    list();
                }
            }
        });

        src.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(),0);
                search();
            }
        });

        alpha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alpha.setTextColor(Color.rgb(0,153,255));
                alpha.setTypeface(Typeface.DEFAULT_BOLD);
                win.setTextColor(Color.BLACK);
                win.setTypeface(Typeface.DEFAULT);
                online.setTextColor(Color.BLACK);
                online.setTypeface(Typeface.DEFAULT);
                if(version!=1 && bug==1){
                    version = 1;
                    if(!b)list();
                }
            }
        });

        win.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                win.setTextColor(Color.rgb(0,153,255));
                win.setTypeface(Typeface.DEFAULT_BOLD);
                alpha.setTextColor(Color.BLACK);
                alpha.setTypeface(Typeface.DEFAULT);
                online.setTextColor(Color.BLACK);
                online.setTypeface(Typeface.DEFAULT);
                if(version!=2 && bug==1){
                    version = 2;
                    if(!b)list();
                }
            }
        });

        online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                online.setTextColor(Color.rgb(0,153,255));
                online.setTypeface(Typeface.DEFAULT_BOLD);
                win.setTextColor(Color.BLACK);
                win.setTypeface(Typeface.DEFAULT);
                alpha.setTextColor(Color.BLACK);
                alpha.setTypeface(Typeface.DEFAULT);
                if(version!=3 && bug==1){
                    version = 3;
                    if(!b)list();
                }
            }
        });

        players.child(ID).child("Online").child("Request").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(abhi) {
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
                                rep = false;
                                players.child(ID).child("Online").child("Request").setValue("");
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

        players.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(fix) {
                    profile.clear();
                    load.setText("Loading...");
                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                        if (!dsp.getKey().equals(ID)) {
                            profile.add(new Card(dsp.child("Name").getValue(String.class), dsp.child("Stats").child("Win").getValue(Integer.class), dsp.child("Stats").child("Loss").getValue(Integer.class), dsp.child("Stats").child("Tie").getValue(Integer.class), dsp.child("Avatar").getValue(Integer.class), dsp.child("Status").getValue(String.class), dsp.getKey(), false));
                        }
                    }
                    if (profile.size() == 0 && !sr) {
                        load.setText("No players available");
                        bug = 1;
                        res = true;
                        nameAdapter = new NameAdapter(getApplicationContext(),profile);
                        view.setAdapter(nameAdapter);
                    } else {
                        if (bug == 0) {
                            Toast.makeText(getApplicationContext(), "Loaded Successfully", Toast.LENGTH_SHORT).show();
                            res = true;
                            bug = 1;
                        } else if (bug == 1 && res) {
                            Toast.makeText(getApplicationContext(), "Refreshed Successfully", Toast.LENGTH_SHORT).show();
                        }
                        flag = true;
                        res = true;
                        if(!sr){
                            list();
                        }
                        else {
                            a = true;
                            search();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void search(){
        if(search.getText().toString().equals("") && !a){
            TextView t = d.findViewById(R.id.textView24);
            t.setText("Invalid Entry !!!");
            TextView txt = d.findViewById(R.id.textView23);
            txt.setText("ERROR");
            d.show();
            search.clearFocus();
            if(!sr) cancel.setBackgroundColor(Color.TRANSPARENT);
            return;
        }
        if(sr && !a){
            profile = (ArrayList<Card>) s.clone();
        }
        a = false;
        search.clearFocus();
        sr = true;
        s = (ArrayList<Card>) profile.clone();
        profile.clear();
        if(!search.getText().toString().equals("")) str = search.getText().toString();
        for (Card c : s) {
            if(version!=3) {
                if (c.getName().equalsIgnoreCase(process(str))) {
                    profile.add(c);
                }
            }
            else{
                if (c.getName().equalsIgnoreCase(process(str)) && c.getStatus().equals("Online")) {
                    profile.add(c);
                }
            }
        }
        if (profile.size() == 0) {
            load.setText("No matches found");
            b = true;
        }
        else {
            load.setText("");
            b = false;
        }
        if(profile.size()>5){
            ArrayList<Card> ext = (ArrayList<Card>) profile.clone();
            ext.add(new Card("",0,0,0,0,"","",true));
            nameAdapter = new NameAdapter(getApplicationContext(), ext);
            view.setAdapter(nameAdapter);
        }
        else {
            nameAdapter = new NameAdapter(getApplicationContext(), profile);
            view.setAdapter(nameAdapter);
        }
    }
    public void list(){
        switch (version){
            case 1:
                Collections.sort(profile,Card.name_compare);
                if(profile.size()>5) {
                    ArrayList<Card> ext = (ArrayList<Card>) profile.clone();
                    ext.add(new Card("",0,0,0,0,"","",true));
                    nameAdapter = new NameAdapter(getApplicationContext(), ext);
                    view.setAdapter(nameAdapter);
                }
                else{
                    nameAdapter = new NameAdapter(getApplicationContext(), profile);
                    view.setAdapter(nameAdapter);
                }
                if(!flag && !c){
                    Toast.makeText(getApplicationContext(),"Sorted Successfully",Toast.LENGTH_SHORT).show();
                }
                c = false;
                flag = false;
                if(profile.size()!=0) {
                    load.setText("");
                }
                else{
                    load.setText("No players available");
                }
                break;
            case 2:
                Collections.sort(profile,Card.name_compare);
                Collections.sort(profile,Card.win_compare);
                if(profile.size()>5) {
                    ArrayList<Card> ext = (ArrayList<Card>) profile.clone();
                    ext.add(new Card("",0,0,0,0,"","",true));
                    nameAdapter = new NameAdapter(getApplicationContext(), ext);
                    view.setAdapter(nameAdapter);
                }
                else{
                    nameAdapter = new NameAdapter(getApplicationContext(), profile);
                    view.setAdapter(nameAdapter);
                }
                if(!flag && !c){
                    Toast.makeText(getApplicationContext(),"Sorted Successfully",Toast.LENGTH_SHORT).show();
                }
                c = false;
                flag = false;
                if(profile.size()!=0) {
                    load.setText("");
                }
                else{
                    load.setText("No players available");
                }
                break;
            case 3:
                Collections.sort(profile,Card.name_compare);
                ArrayList<Card> temp = new ArrayList<>();
                for(Card c : profile){
                    if(c.getStatus().equals("Online")){
                        temp.add(c);
                    }
                }
                if(temp.size()==0){
                    load.setText("Oops!!! No online players available");
                }
                else{
                    if(temp.size()>5){
                        temp.add(new Card("",0,0,0,0,"","",true));
                    }
                    load.setText("");
                }
                nameAdapter = new NameAdapter(getApplicationContext(), temp);
                view.setAdapter(nameAdapter);
                if(!flag && !c){
                    Toast.makeText(getApplicationContext(),"Filtered Successfully",Toast.LENGTH_SHORT).show();
                }
                c = false;
                flag = false;
        }
    }
    @Override
    public void onPause(){
        fix = false;
        abhi = false;
        super.onPause();
        id = getSharedPreferences("id",MODE_PRIVATE);
        String s = id.getString("id","");
        if(!s.equals("")) players.child(s).child("Status").setValue("Offline");
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        con.cancel();
        net.dismiss();
    }
    @Override
    public void onResume(){
        fix = true;
        res = false;
        abhi = true;
        super.onResume();
        id = getSharedPreferences("id",MODE_PRIVATE);
        String s = id.getString("id","");
        if(!s.equals("")) players.child(s).child("Status").setValue("Online");
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
    public static void request(final String id,String status,final String name){
        if(status.equals("Offline")){
            TextView t = d.findViewById(R.id.textView24);
            t.setText(name+" is currently offline !!!");
            TextView txt = d.findViewById(R.id.textView23);
            txt.setText("ERROR");
            d.show();
        }
        else {
            debug = true;
            req = true;
            reply = "";
            players.child(id).child("Online").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(req) {
                        String pl = dataSnapshot.child("Ingame").getValue(String.class);
                        if (pl.length() > 15) pl = pl.substring(0, 13) + "...";
                        if (pl.equals("")) {
                            TextView t = d.findViewById(R.id.textView24);
                            t.setText("Requesting...");
                            TextView txt = d.findViewById(R.id.textView23);
                            txt.setText("MESSAGE");
                            d.setCancelable(false);
                            d.show();
                            if(debug){
                                players.child(id).child("Online").child("Request").setValue(ID+"#"+NAME);
                                debug = false;
                            }
                            reply = dataSnapshot.child("Reply").getValue(String.class);
                        } else {
                            TextView t = d.findViewById(R.id.textView24);
                            t.setText(name + " is currently in a game with " + pl + " !!!");
                            TextView txt = d.findViewById(R.id.textView23);
                            txt.setText("ERROR");
                            players.child(id).child("Online").child("Request").setValue("");
                            players.child(id).child("Online").child("Reply").setValue("");
                            req = false;
                            ct.cancel();
                            d.setCancelable(true);
                            d.show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            ct = new CountDownTimer(5000,10) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if(reply.equals("Y")){
                        players.child(id).child("Online").child("Request").setValue("");
                        players.child(id).child("Online").child("Reply").setValue("");
                        req = false;
                        this.cancel();
                        d.dismiss();
                        Toast.makeText(cont,"Request Accepted",Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(cont,PlayOnline.class);
                        i.putExtra("id",id);
                        i.putExtra("name",name);
                        i.putExtra("flag",false);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        cont.startActivity(i);
                        act.finish();
                    }
                    else if(reply.equals("N")){
                        players.child(id).child("Online").child("Request").setValue("");
                        players.child(id).child("Online").child("Reply").setValue("");
                        TextView t = d.findViewById(R.id.textView24);
                        t.setText(name+" has declined your request");
                        TextView txt = d.findViewById(R.id.textView23);
                        txt.setText("ERROR");
                        d.setCancelable(true);
                        d.show();
                        req = false;
                        this.cancel();
                    }
                }
                @Override
                public void onFinish() {
                    TextView t = d.findViewById(R.id.textView24);
                    t.setText("Request timed out");
                    TextView txt = d.findViewById(R.id.textView23);
                    txt.setText("ERROR");
                    d.setCancelable(true);
                    d.show();
                    players.child(id).child("Online").child("Request").setValue("");
                    players.child(id).child("Online").child("Reply").setValue("");
                    req = false;
                }
            }.start();
        }
    }
}
class NameAdapter extends RecyclerView.Adapter<NameAdapter.NameHolder> {

    private int[] avatars = new int[]{R.drawable.avatar1,R.drawable.avatar2,R.drawable.avatar3,R.drawable.avatar4,R.drawable.avatar5,R.drawable.avatar6,R.drawable.avatar7,R.drawable.avatar8,R.drawable.avatar9};
    private Context context;

    private List<Card> list;

    public NameAdapter(Context context, List<Card> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public NameHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card, parent, false);
        return new NameHolder(view);
    }

    @Override
    public void onBindViewHolder(NameHolder holder, int position) {
        final Card nameDetails = list.get(position);
        if(!nameDetails.getExtra()){
            holder.img.setImageResource(avatars[nameDetails.getAvatar()]);
            String n = nameDetails.getName();
            if(n.length()>15) n = n.substring(0,13)+"...";
            holder.nam.setText(n);
            holder.stat.setText("W: " + nameDetails.getW() + "   L: " + nameDetails.getL() + "   D: " + nameDetails.getD());
            holder.status.setText(nameDetails.getStatus().toUpperCase());
            if (nameDetails.getStatus().equals("Offline")) {
                holder.status.setTextColor(Color.rgb(255, 0, 0));
            } else {
                holder.status.setTextColor(Color.rgb(0, 153, 0));
            }
            holder.id.setText("ID: "+nameDetails.getId());
            holder.req.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String s = nameDetails.getName();
                    if(s.length()>15) s = s.substring(0,13)+"...";
                    Online.request(nameDetails.getId(),nameDetails.getStatus(),s);
                }
            });
        }
        else{
            holder.img.setBackgroundColor(Color.TRANSPARENT);
            holder.nam.setText(nameDetails.getName());
            holder.stat.setText("");
            holder.status.setText(nameDetails.getStatus().toUpperCase());
            holder.req.setBackgroundColor(Color.TRANSPARENT);
            holder.id.setText("");
        }
        holder.cv.setCardBackgroundColor(Color.argb(0,255, 255, 255));
    }

    class NameHolder extends RecyclerView.ViewHolder {
        private ImageView img;
        private TextView nam, stat, status,id;
        private Button req;
        private CardView cv;

        NameHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imageView);
            nam = itemView.findViewById(R.id.textView);
            stat = itemView.findViewById(R.id.textView40);
            req = itemView.findViewById(R.id.textView11);
            status = itemView.findViewById(R.id.textView12);
            cv = itemView.findViewById(R.id.cardView);
            id = itemView.findViewById(R.id.textView10);
        }
    }
}
