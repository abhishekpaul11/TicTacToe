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

public class Solo extends AppCompatActivity {

    private TextView name,turn,tt,txt;
    private Button[] btn = new Button[9];
    private int[] ids = new int[]{R.id.button1,R.id.button2,R.id.button3,R.id.button6,R.id.button5,R.id.button4,R.id.button7,R.id.button8,R.id.button9};
    private Button undo,reset,can,cross,tick;
    private String[][] grid = new String[][]{{"1","2","3"},{"4","5","6"},{"7","8","9"}};
    private int[] bug = new int[9];
    private int count = 0;
    private boolean possible = false,flag,rep;
    MediaPlayer click;
    Dialog d,msg;
    private String pl1,pl2;
    private String symbol;
    final Computer cmp = new Computer();
    SharedPreferences id;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference players = database.getReference("players");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        id = getSharedPreferences("id",MODE_PRIVATE);
        String s = id.getString("id","");
        flag = true;
        rep = false;
        if(!s.equals("")) players.child(s).child("Status").setValue("Online");
        name = findViewById(R.id.textView);
        name.setText("");
        turn = findViewById(R.id.textView2);
        turn.setText("");
        undo = findViewById(R.id.button11);
        undo.setBackgroundResource(R.drawable.undo);
        reset = findViewById(R.id.button10);
        reset.setBackgroundResource(R.drawable.reset);
        d = new Dialog(this);
        d.setContentView(R.layout.warning);
        tt = d.findViewById(R.id.textView23);
        tt.setText("LET'S PLAY");
        txt = d.findViewById(R.id.textView24);
        txt.setText("Pick your symbol");
        msg = new Dialog(this);
        msg.setContentView(R.layout.message);
        TextView t = msg.findViewById(R.id.textView23);
        TextView text = msg.findViewById(R.id.textView24);
        t.setText("ERROR");
        text.setText("Oops!!!\n Charlie doesn't give you a second chance");

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

        d.setCancelable(false);
        d.show();
        can = d.findViewById(R.id.cancel);
        can.setBackgroundResource(R.drawable.back);
        can.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
                finish();
            }
        });
        cross = d.findViewById(R.id.button21);
        tick = d.findViewById(R.id.button20);
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
                symbol = "X";
                cmp.setMarker("O");
                play();
            }
        });
        tick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
                symbol = "O";
                cmp.setMarker("X");
                play();
            }
        });

        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msg.show();
                new CountDownTimer(1000,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }
                    @Override
                    public void onFinish() {
                        msg.dismiss();
                    }
                }.start();
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                can.setBackgroundColor(Color.TRANSPARENT);
                tt.setText("RESET");
                txt.setText("Are you sure?");
                d.setCancelable(true);
                d.show();
                can.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
                cross.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        d.dismiss();
                    }
                });
                tick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        d.dismiss();
                        for (int i = 0; i < 9; i++) {
                            btn[i] = findViewById(ids[i]);
                            btn[i].setBackgroundColor(Color.TRANSPARENT);
                        }
                        grid = new String[][]{{"1","2","3"},{"4","5","6"},{"7","8","9"}};
                        bug = new int[9];
                        possible = false;
                        count = 0;
                        play();
                    }
                });
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
                            TextView t = d.findViewById(R.id.textView24);
                            t.setText(disp);
                            TextView txt = d.findViewById(R.id.textView23);
                            txt.setText("GAME REQUEST");
                            can.setBackgroundColor(Color.TRANSPARENT);
                            d.setCancelable(false);
                            d.show();
                            Button y = d.findViewById(R.id.button20);
                            Button no = d.findViewById(R.id.button21);
                            y.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    d.dismiss();
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
                                    d.dismiss();
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
                            d.dismiss();
                            TextView t = msg.findViewById(R.id.textView24);
                            t.setText("Request timed out");
                            TextView txt = msg.findViewById(R.id.textView23);
                            txt.setText("ERROR");
                            msg.setCancelable(true);
                            msg.show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }
    public String gameOver(){
        if(grid[0][0].equals(grid[1][1]) && grid[1][1].equals(grid[2][2])){
            return grid[0][0];
        }
        if (grid[0][2].equals(grid[1][1]) && grid[1][1].equals(grid[2][0])) {
            return grid[0][2];
        }
        for(int i = 0;i<3;i++) {
            if (grid[i][0].equals(grid[i][1]) && grid[i][1].equals(grid[i][2])) {
                return grid[i][0];
            }
            if (grid[0][i].equals(grid[1][i]) && grid[1][i].equals(grid[2][i])){
                return grid[0][i];
            }
        }
        return "-1";
    }
    public void play(){
        click = MediaPlayer.create(this,R.raw.click);
        pl1 = (symbol.equals("X")?"You":"Charlie");
        pl2 = (symbol.equals("X")?"Charlie":"You");
        int n = (int) (Math.random() * 2);
        if(n==1){
            for (int i = 0;i<9;i++){
                btn[i] = findViewById(ids[i]);
            }
            if(!symbol.equals("X")) {
                name.setTextColor(Color.rgb(255, 51, 51));
            }
            else{
                name.setTextColor(Color.rgb(0, 153, 255));
            }
            name.setText("Charlie");
            turn.setText("starts");
            final String pos = cmp.takeTurn(grid,symbol);
            new CountDownTimer(1000,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    count++;
                    click.start();
                    grid[Integer.parseInt(pos.charAt(0)+"")][Integer.parseInt(pos.charAt(2)+"")] = cmp.getMarker();
                    btn[(Integer.parseInt(pos.charAt(0)+"")*3)+(Integer.parseInt(pos.charAt(2)+""))].setBackgroundResource((cmp.getMarker().equals("X"))?R.drawable.tick:R.drawable.circle);
                    bug[(Integer.parseInt(pos.charAt(0) + "") * 3) + (Integer.parseInt(pos.charAt(2) + ""))] = 1;
                    if(symbol.equals("X")) {
                        name.setTextColor(Color.rgb(255, 51, 51));
                    }
                    else{
                        name.setTextColor(Color.rgb(0, 153, 255));
                    }
                    name.setText("Your");
                    turn.setText("turn");
                    possible = true;
                }
            }.start();
        }
        else{
            possible = true;
            if(symbol.equals("X")) {
                name.setTextColor(Color.rgb(255, 51, 51));
            }
            else{
                name.setTextColor(Color.rgb(0, 153, 255));
            }
            name.setText("You");
            turn.setText("start");
        }

        for(int i = 0;i < 9;i++) {
            btn[i] = findViewById(ids[i]);
            final int j = i;
            btn[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bug[j] != 1 && possible) {
                        possible = false;
                        click.start();
                        count++;
                        bug[j] = 1;
                        btn[j].setBackgroundResource((symbol.equals("X")) ? R.drawable.tick : R.drawable.circle);
                        grid[j / 3][j % 3] = symbol;
                        if (!gameOver().equals("-1") || count == 9) {
                            bug = new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1};
                            new CountDownTimer(1000, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                }

                                @Override
                                public void onFinish() {
                                    Intent i = new Intent(getApplicationContext(), Results.class);
                                    String str = (gameOver().equals("-1")) ? "-1" : (gameOver().equals("X")) ? "1" : "2";
                                    i.putExtra("winner", str);
                                    i.putExtra("player1", pl1);
                                    i.putExtra("player2", pl2);
                                    i.putExtra("flip", false);
                                    i.putExtra("source",true);
                                    startActivity(i);
                                    finish();
                                }
                            }.start();
                        }
                        else {
                            final String pos = cmp.takeTurn(grid, symbol);
                            if (!symbol.equals("X")) {
                                name.setTextColor(Color.rgb(255, 51, 51));
                            } else {
                                name.setTextColor(Color.rgb(0, 153, 255));
                            }
                            name.setText("Charlie's");
                            turn.setText("turn");
                            new CountDownTimer(1000, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {

                                }

                                @Override
                                public void onFinish() {
                                    click.start();
                                    count++;
                                    grid[Integer.parseInt(pos.charAt(0) + "")][Integer.parseInt(pos.charAt(2) + "")] = cmp.getMarker();
                                    btn[(Integer.parseInt(pos.charAt(0) + "") * 3) + (Integer.parseInt(pos.charAt(2) + ""))].setBackgroundResource((cmp.getMarker().equals("X")) ? R.drawable.tick : R.drawable.circle);
                                    bug[(Integer.parseInt(pos.charAt(0) + "") * 3) + (Integer.parseInt(pos.charAt(2) + ""))] = 1;
                                    if (!gameOver().equals("-1") || count == 9) {
                                        bug = new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1};
                                        new CountDownTimer(1000, 1000) {
                                            @Override
                                            public void onTick(long millisUntilFinished) {
                                            }

                                            @Override
                                            public void onFinish() {
                                                Intent i = new Intent(getApplicationContext(), Results.class);
                                                String str = (gameOver().equals("-1")) ? "-1" : (gameOver().equals("X")) ? "1" : "2";
                                                i.putExtra("winner", str);
                                                i.putExtra("player1", pl1);
                                                i.putExtra("player2", pl2);
                                                i.putExtra("flip", false);
                                                i.putExtra("source",true);
                                                startActivity(i);
                                                finish();
                                            }
                                        }.start();
                                    } else {
                                        if (symbol.equals("X")) {
                                            name.setTextColor(Color.rgb(255, 51, 51));
                                        } else {
                                            name.setTextColor(Color.rgb(0, 153, 255));
                                        }
                                        name.setText("Your");
                                        possible = true;
                                    }
                                }
                            }.start();
                        }
                    }
                }
            });
        }
    }
    @Override
    public void onBackPressed() {
        can.setBackgroundColor(Color.TRANSPARENT);
        tt.setText("WARNING");
        txt.setText("Are you sure?");
        d.setCancelable(true);
        d.show();
        can.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        tick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
                finish();
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
