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

public class PlayOnline extends AppCompatActivity {

    static FirebaseDatabase database = FirebaseDatabase.getInstance();
    static DatabaseReference players = database.getReference("players");
    SharedPreferences ID,nam,Loss;
    SharedPreferences.Editor edit;
    int n,count = 0,time,pin,num,los;
    String name,id,name_own,ID_own,names,names1;
    private int win,loss,tie,win_own,tie_own,loss_own,pos,selfping;
    boolean flag,check1 = false,crun = false,dev = false,nb = false,nxt = false;
    CountDownTimer ct,choose,c,net1,ping,ping_own,net;
    Dialog msg,ch,ch1;
    private boolean play,a = true,b = true,bool_c = true,check = false,read = true,flaag = false,debug = false,magic = false;
    private String pl1,pl2;
    private int[] bug = new int[]{0,0,0,0,0,0,0,0,0};
    private TextView player,turn,countdown,message;
    private Button[] btn = new Button[9];
    private int[] ids = new int[]{R.id.button1,R.id.button2,R.id.button3,R.id.button6,R.id.button5,R.id.button4,R.id.button7,R.id.button8,R.id.button9};
    private int CROSS = R.drawable.tick ,CIRCLE = R.drawable.circle;
    private int symbol,symbol_own;
    private int[][] grid = new int[][]{{-1,-2,3},{4,5,6},{7,8,9}};
    MediaPlayer click;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        click = MediaPlayer.create(this,R.raw.click);
        msg = new Dialog(this);
        msg.setContentView(R.layout.message);
        ch = new Dialog(this);
        ch.setContentView(R.layout.warning);
        ch1 = new Dialog(this);
        ch1.setContentView(R.layout.warning);
        player = findViewById(R.id.textView);
        turn = findViewById(R.id.textView2);
        countdown = findViewById(R.id.textView39);
        message = findViewById(R.id.textView41);
        for(int i=0;i<9;i++){
            btn[i] = findViewById(ids[i]);
        }

        Loss = getSharedPreferences("loss",MODE_PRIVATE);
        los = Loss.getInt("loss",0);

        Window win = msg.getWindow();
        WindowManager.LayoutParams wlp = win.getAttributes();
        win.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
        wlp.gravity = Gravity.CENTER;
        win.setBackgroundDrawable(new ColorDrawable(Color.argb(225,255,255,255)));
        win.setAttributes(wlp);

        Window win1 = ch.getWindow();
        WindowManager.LayoutParams wlp1 = win1.getAttributes();
        win1.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
        wlp1.gravity = Gravity.CENTER;
        win1.setBackgroundDrawable(new ColorDrawable(Color.argb(225,255,255,255)));
        win1.setAttributes(wlp1);

        Window win2 = ch1.getWindow();
        WindowManager.LayoutParams wlp2 = win2.getAttributes();
        win2.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
        wlp2.gravity = Gravity.CENTER;
        win2.setBackgroundDrawable(new ColorDrawable(Color.argb(225,255,255,255)));
        win2.setAttributes(wlp2);

        ID = getSharedPreferences("id",MODE_PRIVATE);
        nam = getSharedPreferences("name",MODE_PRIVATE);
        name_own = nam.getString("name","");
        ID_own = ID.getString("id","");
        flag = getIntent().getExtras().getBoolean("flag");
        id = getIntent().getExtras().getString("id");
        name = getIntent().getExtras().getString("name");
        players.child(ID_own).child("Online").child("Ping").setValue(1);
        players.child(ID_own).child("Online").child("Reply").setValue("");
        players.child(ID_own).child("Online").child("Request").setValue("");
        players.child(id).child("Online").child("Reply").setValue("");
        players.child(id).child("Online").child("Request").setValue("");
        players.child(ID_own).child("Status").setValue("Online");


        players.child(id).child("Online").child("Ping").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                n = dataSnapshot.getValue(Integer.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        ct = new CountDownTimer(5000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                --time;
                countdown.setText(time+"");
                if(n==1){
                    this.cancel();
                    msg.dismiss();
                    Toast.makeText(getApplicationContext(),"Connected Successfully",Toast.LENGTH_SHORT).show();
                    players.child(ID_own).child("Online").child("Ingame").setValue(id);
                    setup();
                }
            }
            @Override
            public void onFinish() {
                players.child(ID_own).child("Online").child("Ping").setValue(0);
                players.child(id).child("Online").child("Ping").setValue(0);
                ch.dismiss();
                msg.dismiss();
                msg.setCancelable(false);
                msg.getWindow().setDimAmount(0.7f);
                TextView a = msg.findViewById(R.id.textView23);
                a.setText("ERROR");
                TextView b = msg.findViewById(R.id.textView24);
                b.setText(name+" has left. Match cancelled!");
                Button can = msg.findViewById(R.id.cancel2);
                can.setBackgroundColor(Color.TRANSPARENT);
                can.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {}
                });
                msg.show();
                new CountDownTimer(1000,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        msg.dismiss();
                        dev = true;
                        finish();
                    }
                }.start();
            }
        }.start();

        message.setText("awaiting opponent...");
        time = 5;
        countdown.setText(time+"");
        msg.setCancelable(false);
        TextView a = msg.findViewById(R.id.textView23);
        a.setText("MESSAGE");
        TextView b = msg.findViewById(R.id.textView24);
        b.setText("Connecting...");
        Button can = msg.findViewById(R.id.cancel2);
        can.setBackgroundResource(R.drawable.back);
        can.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView c = ch1.findViewById(R.id.textView23);
                c.setText("EXIT");
                TextView d = ch1.findViewById(R.id.textView24);
                d.setText("Sure? You might lose.");
                Button y = ch1.findViewById(R.id.button20);
                Button n = ch1.findViewById(R.id.button21);
                y.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ct.cancel();
                        ch1.dismiss();
                        players.child(ID_own).child("Online").child("Ingame").setValue("");
                        players.child(ID_own).child("Online").child("Ping").setValue(0);
                        players.child(id).child("Online").child("Ping").setValue(0);
                        dev = true;
                        finish();
                    }
                });
                n.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ch1.dismiss();
                        msg.getWindow().setDimAmount(0.0f);
                        msg.show();
                    }
                });
                ch1.show();
            }
        });
        msg.getWindow().setDimAmount(0.0f);
        msg.show();
    }
    public void setup(){
        names = (name.length()>18)?name.substring(0,15)+"...":name;
        names1 = (name_own.length()>18)?name_own.substring(0,15)+"...":name_own;
        players.child(id).child("Stats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(a) {
                    win = dataSnapshot.child("Win").getValue(Integer.class);
                    loss = dataSnapshot.child("Loss").getValue(Integer.class);
                    tie = dataSnapshot.child("Tie").getValue(Integer.class);
                    a = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        players.child(ID_own).child("Stats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(b) {
                    win_own = dataSnapshot.child("Win").getValue(Integer.class);
                    loss_own = dataSnapshot.child("Loss").getValue(Integer.class);
                    tie_own = dataSnapshot.child("Tie").getValue(Integer.class);
                    b = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if(flag){
            msg.setCancelable(false);
            TextView a = msg.findViewById(R.id.textView23);
            a.setText("MESSAGE");
            TextView b = msg.findViewById(R.id.textView24);
            b.setText(name+" is choosing symbol");
            msg.getWindow().setDimAmount(0.0f);
            Button can = msg.findViewById(R.id.cancel2);
            can.setBackgroundResource(R.drawable.back);
            can.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView c = ch1.findViewById(R.id.textView23);
                    c.setText("EXIT");
                    TextView d = ch1.findViewById(R.id.textView24);
                    d.setText("Sure? You might lose.");
                    Button y = ch1.findViewById(R.id.button20);
                    Button n = ch1.findViewById(R.id.button21);
                    y.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            choose.cancel();
                            ch1.dismiss();
                            players.child(ID_own).child("Online").child("Ingame").setValue("");
                            players.child(ID_own).child("Online").child("Ping").setValue(0);
                            players.child(id).child("Online").child("Ping").setValue(0);
                            dev = true;
                            finish();
                        }
                    });
                    n.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ch1.dismiss();
                            msg.getWindow().setDimAmount(0.0f);
                            msg.show();
                        }
                    });
                    ch1.show();
                }
            });
            msg.show();
            message.setText("awaiting choice...");
            time = 10;
            countdown.setText(time+"");
            choose = new CountDownTimer(10000,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    --time;
                    countdown.setText(time+"");
                }

                @Override
                public void onFinish() {
                    ch.dismiss();
                    msg.dismiss();
                    msg.setCancelable(false);
                    TextView a = msg.findViewById(R.id.textView23);
                    a.setText("ERROR");
                    TextView b = msg.findViewById(R.id.textView24);
                    b.setText("Match cancelled due to Inactivity!");
                    msg.getWindow().setDimAmount(0.7f);
                    Button can = msg.findViewById(R.id.cancel2);
                    can.setBackgroundColor(Color.TRANSPARENT);
                    can.setOnClickListener(new View.OnClickListener() {
                        @Override
                            public void onClick(View v) {}
                    });
                    msg.show();
                    new CountDownTimer(1000,1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        @Override
                        public void onFinish() {
                            msg.dismiss();
                            players.child(ID_own).child("Online").child("Ingame").setValue("");
                            players.child(ID_own).child("Online").child("Ping").setValue(0);
                            players.child(id).child("Online").child("Ping").setValue(0);
                            dev = true;
                            finish();
                        }
                    }.start();
                }
            }.start();
            players.child(id).child("Online").child("Ping").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    n = dataSnapshot.getValue(Integer.class);
                    if(n!=1 && bool_c){
                        switch (n) {
                            case 2:
                                msg.dismiss();
                                symbol_own = CIRCLE;
                                symbol = CROSS;
                                pl1 = name;
                                pl2 = name_own;
                                player.setTextColor(Color.rgb(255, 51, 51));
                                player.setText(names);
                                play = false;
                                turn.setText("starts");
                                break;
                            case 3:
                                msg.dismiss();
                                symbol_own = CIRCLE;
                                symbol = CROSS;
                                pl1 = name;
                                pl2 = name_own;
                                player.setTextColor(Color.rgb(0, 153, 255));
                                player.setText(names1);
                                play = true;
                                turn.setText("starts");
                                break;
                            case 4:
                                msg.dismiss();
                                symbol_own = CROSS;
                                symbol = CIRCLE;
                                pl1 = name_own;
                                pl2 = name;
                                player.setTextColor(Color.rgb(0, 153, 255));
                                player.setText(names);
                                play = false;
                                turn.setText("starts");
                                break;
                            case 5:
                                msg.dismiss();
                                symbol_own = CROSS;
                                symbol = CIRCLE;
                                pl1 = name_own;
                                pl2 = name;
                                player.setTextColor(Color.rgb(255, 51, 51));
                                player.setText(names1);
                                play = true;
                                turn.setText("starts");
                                break;
                        }
                        bool_c = false;
                        if(n!=0) {
                            choose.cancel();
                            play();
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
        else {
            TextView c = ch.findViewById(R.id.textView23);
            c.setText("MESSAGE");
            TextView d = ch.findViewById(R.id.textView24);
            d.setText("Choose your symbol.");
            Button can = ch.findViewById(R.id.cancel);
            can.setBackgroundResource(R.drawable.back);
            can.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView c = ch1.findViewById(R.id.textView23);
                    c.setText("EXIT");
                    TextView d = ch1.findViewById(R.id.textView24);
                    d.setText("Sure? You might lose.");
                    Button y = ch1.findViewById(R.id.button20);
                    Button n = ch1.findViewById(R.id.button21);
                    y.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            choose.cancel();
                            ch1.dismiss();
                            players.child(ID_own).child("Online").child("Ingame").setValue("");
                            players.child(ID_own).child("Online").child("Ping").setValue(0);
                            players.child(id).child("Online").child("Ping").setValue(0);
                            dev = true;
                            finish();
                        }
                    });
                    n.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ch1.dismiss();
                            ch.getWindow().setDimAmount(0.0f);
                            ch.show();
                        }
                    });
                    ch1.show();
                }
            });
            Button y = ch.findViewById(R.id.button20);
            Button n = ch.findViewById(R.id.button21);
            y.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    symbol_own = CIRCLE;
                    symbol = CROSS;
                    pl1 = name;
                    pl2 = name_own;
                    int rand = 4 + (int)(Math.random()*(2));
                    players.child(ID_own).child("Online").child("Ping").setValue(rand);
                    if(rand==4) {
                        player.setTextColor(Color.rgb(0, 153, 255));
                        player.setText(names1);
                        play = true;
                        turn.setText("starts");
                    }
                    else{
                        player.setTextColor(Color.rgb(255, 51, 51));
                        player.setText(names);
                        play = false;
                        turn.setText("starts");
                    }
                    ch.dismiss();
                    choose.cancel();
                    play();
                }
            });
            n.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    symbol_own = CROSS;
                    symbol = CIRCLE;
                    pl1 = name_own;
                    pl2 = name;
                    int rand = 2 + (int)(Math.random()*(2));
                    players.child(ID_own).child("Online").child("Ping").setValue(rand);
                    if(rand==3) {
                        player.setTextColor(Color.rgb(0, 153, 255));
                        player.setText(names);
                        play = false;
                        turn.setText("starts");
                    }
                    else{
                        player.setTextColor(Color.rgb(255, 51, 51));
                        player.setText(names1);
                        play = true;
                        turn.setText("starts");
                    }
                    ch.dismiss();
                    choose.cancel();
                    play();
                }
            });
            ch.getWindow().setDimAmount(0.0f);
            ch.setCancelable(false);
            ch.show();
            message.setText("awaiting choice...");
            time = 10;
            countdown.setText(time+"");
            choose = new CountDownTimer(10000,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    --time;
                    countdown.setText(time+"");
                }

                @Override
                public void onFinish() {
                    ch.dismiss();
                    msg.setCancelable(false);
                    TextView a = msg.findViewById(R.id.textView23);
                    a.setText("ERROR");
                    TextView b = msg.findViewById(R.id.textView24);
                    b.setText("Match cancelled due to Inactivity!");
                    msg.getWindow().setDimAmount(0.7f);
                    Button can = msg.findViewById(R.id.cancel2);
                    can.setBackgroundColor(Color.TRANSPARENT);
                    can.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {}
                    });
                    msg.show();
                    new CountDownTimer(1000,1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        @Override
                        public void onFinish() {
                            msg.dismiss();
                            dev = true;
                            finish();
                        }
                    }.start();
                }
            }.start();
        }
    }
    public void play(){
        message.setText("");
        countdown.setText("");
        debug = true;
        pin = 6;
        net1 = new CountDownTimer(10000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                --time;
                countdown.setText(time+"");
            }

            @Override
            public void onFinish() {
                msg.setCancelable(false);
                TextView a = msg.findViewById(R.id.textView23);
                a.setText("MESSAGE");
                TextView b = msg.findViewById(R.id.textView24);
                b.setText("No network. Match ended.");
                msg.getWindow().setDimAmount(0.7f);
                Button can = msg.findViewById(R.id.cancel2);
                can.setBackgroundColor(Color.TRANSPARENT);
                can.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {}
                });
                msg.show();
                new CountDownTimer(1000,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        msg.dismiss();
                        dev = true;
                        nb = false;
                        finish();
                    }
                }.start();
            }
        };
        net = new CountDownTimer(360000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(!haveNetworkConnection()){
                    if(!nb){
                        ping_own.cancel();
                        nb = true;
                        msg.setCancelable(false);
                        ping.cancel();
                        c.cancel();
                        check = true;
                        net1.start();
                        TextView a = msg.findViewById(R.id.textView23);
                        a.setText("ERROR");
                        TextView b = msg.findViewById(R.id.textView24);
                        b.setText("You are offline");
                        message.setText("Reconnecting in...");
                        time = 10;
                        countdown.setText(time+"");
                        Button can = msg.findViewById(R.id.cancel2);
                        can.setBackgroundResource(R.drawable.back);
                        can.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                TextView c = ch1.findViewById(R.id.textView23);
                                c.setText("EXIT");
                                TextView d = ch1.findViewById(R.id.textView24);
                                d.setText("Sure? You might lose.");
                                Button y = ch1.findViewById(R.id.button20);
                                Button n = ch1.findViewById(R.id.button21);
                                y.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ch1.dismiss();
                                        dev = true;
                                        finish();
                                    }
                                });
                                n.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ch1.dismiss();
                                        msg.getWindow().setDimAmount(0.0f);
                                        msg.show();
                                    }
                                });
                                ch1.show();
                            }
                        });
                        msg.getWindow().setDimAmount(0.0f);
                        msg.show();
                    }
                }
                else{
                    nb = false;
                    if(check) {
                        check = false;
                        msg.dismiss();
                        ping.start();
                        net1.cancel();
                        ping_own.start();
                        message.setText("");
                        countdown.setText("");
                    }
                }
            }

            @Override
            public void onFinish() {

            }
        }.start();
        ping_own = new CountDownTimer(360000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                players.child(ID_own).child("Online").child("Ping").setValue(pin++);
            }

            @Override
            public void onFinish() {
            }
        }.start();
        c = new CountDownTimer(15000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                crun = true;
                --time;
                countdown.setText(time+"");
            }

            @Override
            public void onFinish(){
                ping.cancel();
                crun = false;
                ch.dismiss();
                msg.dismiss();
                msg.setCancelable(false);
                TextView a = msg.findViewById(R.id.textView23);
                a.setText("MESSAGE");
                TextView b = msg.findViewById(R.id.textView24);
                b.setText("You win. "+names+" has disconnected");
                msg.getWindow().setDimAmount(0.7f);
                Button can = msg.findViewById(R.id.cancel2);
                can.setBackgroundColor(Color.TRANSPARENT);
                can.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {}
                });
                msg.show();
                new CountDownTimer(1000,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        players.child(ID_own).child("Stats").child("Win").setValue(win+1);
                        players.child(id).child("Stats").child("Loss").setValue(loss+1);
                        players.child(ID_own).child("Online").child("Ingame").setValue("");
                        players.child(id).child("Online").child("Ingame").setValue("");
                        players.child(ID_own).child("Online").child("Ping").setValue(0);
                        players.child(id).child("Online").child("Ping").setValue(0);
                        players.child(id).child("Online").child("Position").setValue(-1);
                        players.child(ID_own).child("Online").child("Position").setValue(-1);
                        msg.dismiss();
                        Intent i = new Intent(getApplicationContext(),ResultsOnline.class);
                        i.putExtra("winner",1+"");
                        i.putExtra("pl1",names1);
                        i.putExtra("pl2",name);
                        startActivity(i);
                        dev = true;
                        finish();
                    }
                }.start();
            }
        };
        ping = new CountDownTimer(360000,1500) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(num==n){
                    if(!crun) {
                        message.setText("reconnecting in...");
                        time = 15;
                        countdown.setText(time + "");
                        c.start();
                        flaag = true;
                        msg.setCancelable(false);
                        TextView a = msg.findViewById(R.id.textView23);
                        a.setText("MESSAGE");
                        TextView b = msg.findViewById(R.id.textView24);
                        b.setText(names + " is offline");
                        Button can = msg.findViewById(R.id.cancel2);
                        can.setBackgroundResource(R.drawable.back);
                        can.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                TextView c = ch1.findViewById(R.id.textView23);
                                c.setText("EXIT");
                                TextView d = ch1.findViewById(R.id.textView24);
                                d.setText("Sure? You might lose.");
                                Button y = ch1.findViewById(R.id.button20);
                                Button n = ch1.findViewById(R.id.button21);
                                y.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ch1.dismiss();
                                        ping_own.cancel();
                                        players.child(ID_own).child("Online").child("Ingame").setValue("");
                                        players.child(id).child("Online").child("Ingame").setValue("");
                                        players.child(ID_own).child("Online").child("Ping").setValue(0);
                                        players.child(id).child("Online").child("Ping").setValue(-1);
                                        players.child(id).child("Online").child("Position").setValue(-1);
                                        players.child(ID_own).child("Online").child("Position").setValue(-1);
                                        dev = true;
                                        finish();
                                    }
                                });
                                n.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ch1.dismiss();
                                        msg.getWindow().setDimAmount(0.0f);
                                        msg.show();
                                    }
                                });
                                ch1.show();
                            }
                        });
                        msg.getWindow().setDimAmount(0.0f);
                        msg.show();
                    }
                }
                else if(flaag){
                    if(n==0){
                        msg.dismiss();
                        ping.cancel();
                        c.cancel();
                        msg.setCancelable(false);
                        TextView a = msg.findViewById(R.id.textView23);
                        a.setText("MESSAGE");
                        TextView b = msg.findViewById(R.id.textView24);
                        b.setText("Too bad. " + names + " has left.");
                        msg.getWindow().setDimAmount(0.7f);
                        Button can = msg.findViewById(R.id.cancel2);
                        can.setBackgroundColor(Color.TRANSPARENT);
                        can.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        });
                        msg.show();
                        new CountDownTimer(1000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                            }

                            @Override
                            public void onFinish() {
                                msg.dismiss();
                                players.child(ID_own).child("Online").child("Ping").setValue(0);
                                finish();
                            }
                        }.start();
                    }
                    else {
                        flaag = false;
                        ch1.dismiss();
                        msg.dismiss();
                        crun = false;
                        c.cancel();
                        message.setText("");
                        countdown.setText("");
                    }
                }
                num = n;
            }

            @Override
            public void onFinish() {
            }
        }.start();
        for(int i=0;i<9;i++){
            final int j = i;
            btn[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(play && bug[j]!=1){
                        btn[j].setBackgroundResource(symbol_own);
                        click.start();
                        grid[j / 3][j % 3] = (symbol_own == CROSS) ? 1 : 2;
                        players.child(ID_own).child("Online").child("Position").setValue(j);
                        count++;
                        bug[j] = 1;
                        play = false;
                        turn.setText("turn");
                        if(gameOver()!=-1 || count==9){
                            bug = new int[]{1,1,1,1,1,1,1,1,1};
                            new CountDownTimer(1000,1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                }

                                @Override
                                public void onFinish() {
                                    if(gameOver()==1){
                                        if(symbol==CROSS){
                                            players.child(id).child("Stats").child("Win").setValue(win+1);
                                            players.child(ID_own).child("Stats").child("Loss").setValue(loss_own+1);
                                        }
                                        else{
                                            players.child(ID_own).child("Stats").child("Win").setValue(win_own+1);
                                            players.child(id).child("Stats").child("Loss").setValue(loss+1);
                                        }
                                    }
                                    else if(gameOver()==2){
                                        if(symbol==CROSS){
                                            players.child(ID_own).child("Stats").child("Win").setValue(win_own+1);
                                            players.child(id).child("Stats").child("Loss").setValue(loss+1);
                                        }
                                        else{
                                            players.child(ID_own).child("Stats").child("Loss").setValue(loss_own+1);
                                            players.child(id).child("Stats").child("Win").setValue(win+1);
                                        }
                                    }
                                    else{
                                        players.child(ID_own).child("Stats").child("Tie").setValue(tie_own+1);
                                        players.child(id).child("Stats").child("Tie").setValue(tie+1);
                                    }
                                    ping_own.cancel();
                                    players.child(ID_own).child("Online").child("Ping").setValue(0);
                                    players.child(id).child("Online").child("Ingame").setValue("");
                                    players.child(ID_own).child("Online").child("Ingame").setValue("");
                                    players.child(id).child("Online").child("Ping").setValue(0);
                                    players.child(id).child("Online").child("Position").setValue(-1);
                                    Intent i = new Intent(getApplicationContext(),ResultsOnline.class);
                                    i.putExtra("winner",gameOver()+"");
                                    i.putExtra("pl1",pl1);
                                    i.putExtra("pl2",pl2);
                                    startActivity(i);
                                    dev = true;
                                    finish();
                                }
                            }.start();
                        }
                        else{
                            if(symbol==CROSS){
                                player.setTextColor(Color.rgb(255,51,51));
                            }
                            else {
                                player.setTextColor(Color.rgb(0,153,255));
                            }
                            player.setText(names+"'s");
                        }
                    }
                }
            });
        }
        players.child(id).child("Online").child("Position").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (read) {
                    pos = dataSnapshot.getValue(Integer.class);
                    if (pos != -1) {
                        click.start();
                        btn[pos].setBackgroundResource(symbol);
                        grid[pos / 3][pos % 3] = (symbol == CROSS) ? 1 : 2;
                        count++;
                        bug[pos] = 1;
                        play = true;
                        turn.setText("turn");
                        if (gameOver() != -1 || count == 9) {
                            bug = new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1};
                            new CountDownTimer(1000, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                }

                                @Override
                                public void onFinish() {
                                    if (gameOver() == 1) {
                                        if (symbol == CROSS) {
                                            players.child(id).child("Stats").child("Win").setValue(win + 1);
                                            players.child(ID_own).child("Stats").child("Loss").setValue(loss_own + 1);
                                        } else {
                                            players.child(ID_own).child("Stats").child("Win").setValue(win_own + 1);
                                            players.child(id).child("Stats").child("Loss").setValue(loss + 1);
                                        }
                                    } else if (gameOver() == 2) {
                                        if (symbol == CROSS) {
                                            players.child(ID_own).child("Stats").child("Win").setValue(win_own + 1);
                                            players.child(id).child("Stats").child("Loss").setValue(loss + 1);
                                        } else {
                                            players.child(ID_own).child("Stats").child("Loss").setValue(loss_own + 1);
                                            players.child(id).child("Stats").child("Win").setValue(win + 1);
                                        }
                                    } else {
                                        players.child(ID_own).child("Stats").child("Tie").setValue(tie_own + 1);
                                        players.child(id).child("Stats").child("Tie").setValue(tie + 1);
                                    }
                                    ping_own.cancel();
                                    players.child(ID_own).child("Online").child("Ping").setValue(0);
                                    players.child(id).child("Online").child("Ingame").setValue("");
                                    players.child(ID_own).child("Online").child("Ingame").setValue("");
                                    players.child(id).child("Online").child("Ping").setValue(0);
                                    players.child(id).child("Online").child("Position").setValue(-1);
                                    Intent i = new Intent(getApplicationContext(), ResultsOnline.class);
                                    i.putExtra("winner", gameOver() + "");
                                    i.putExtra("pl1", pl1);
                                    i.putExtra("pl2", pl2);
                                    startActivity(i);
                                    dev = true;
                                    finish();
                                }
                            }.start();
                        } else {
                            if (symbol == CIRCLE) {
                                player.setTextColor(Color.rgb(255, 51, 51));
                            } else {
                                player.setTextColor(Color.rgb(0, 153, 255));
                            }
                            player.setText(names1 + "'s");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

        });
    }
    public int gameOver(){
        if(grid[0][0]==grid[1][1] && grid[1][1]==grid[2][2]){
            return grid[0][0];
        }
        if (grid[0][2]==grid[1][1] && grid[1][1]==grid[2][0]) {
            return grid[0][2];
        }
        for(int i = 0;i<3;i++) {
            if (grid[i][0] == grid[i][1] && grid[i][1] == grid[i][2]) {
                return grid[i][0];
            }
            if (grid[0][i] == grid[1][i] && grid[1][i] == grid[2][i]){
                return grid[0][i];
            }
        }
        return -1;
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
        TextView c = ch1.findViewById(R.id.textView23);
        c.setText("EXIT");
        TextView d = ch1.findViewById(R.id.textView24);
        d.setText("Sure? You might lose.");
        Button y = ch1.findViewById(R.id.button20);
        Button n = ch1.findViewById(R.id.button21);
        y.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ch1.dismiss();
                dev = true;
                finish();
            }
        });
        n.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ch.dismiss();
            }
        });
        ch1.show();
    }
    @Override
    public void onPause(){
        super.onPause();
        players.child(ID_own).child("Status").setValue("Offline");
        if(debug && !dev) {
            ping.cancel();
            ping_own.cancel();
            net.cancel();
            check1 = true;
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        players.child(ID_own).child("Status").setValue("Online");
        if(check1) {
            nxt = true;
            net.start();
            ping.start();
        }
        players.child(ID_own).child("Online").child("Ping").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (check1 && nxt) {
                    check1 = false;
                    nxt = false;
                    selfping = dataSnapshot.getValue(Integer.class);
                    if (selfping <= 0) {
                        msg.setCancelable(false);
                        TextView a = msg.findViewById(R.id.textView23);
                        a.setText("MESSAGE");
                        TextView b = msg.findViewById(R.id.textView24);
                        if (selfping == -1) {
                            b.setText("Too bad. " + names + " has left.");
                        } else {
                            b.setText("You lose. Turn timed out.");
                            edit = Loss.edit();
                            edit.putInt("loss",los+1);
                            edit.apply();
                        }
                        msg.getWindow().setDimAmount(0.7f);
                        Button can = msg.findViewById(R.id.cancel2);
                        can.setBackgroundColor(Color.TRANSPARENT);
                        can.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        });
                        msg.show();
                        new CountDownTimer(1000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                            }

                            @Override
                            public void onFinish() {
                                msg.dismiss();
                                dev = true;
                                finish();
                            }
                        }.start();
                    }
                    else{
                        ping_own.start();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        read = false;
        crun = false;
        if(debug) {
            c.cancel();
            ping.cancel();
            ping_own.cancel();
            net.cancel();
            net1.cancel();
        }
    }
}
