package com.example.tictactoe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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

public class MainActivity extends AppCompatActivity {

    private Button[] btn = new Button[9];
    private int[] ids = new int[]{R.id.button1,R.id.button2,R.id.button3,R.id.button6,R.id.button5,R.id.button4,R.id.button7,R.id.button8,R.id.button9};
    private int CROSS = R.drawable.tick ,CIRCLE = R.drawable.circle;
    private int symbol = CROSS;
    private int[][] grid = new int[][]{{-1,-2,3},{4,5,6},{7,8,9}};
    private String pl1;
    private String pl2;
    private boolean flip;
    private String player;
    private TextView name,turn;
    private int count = 0,x=10,curr = -1;
    private int[] bug = new int[9];
    private Button reset,undo;
    private ConstraintLayout layout,lout;
    private float UP = 0f, DOWN;
    private float orientation = UP;
    private boolean flag,rep;
    private Dialog msg,d;
    SharedPreferences id;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference players = database.getReference("players");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        flip = getIntent().getExtras().getBoolean("flip");
        pl1 = getIntent().getExtras().getString("player1");
        pl2 = getIntent().getExtras().getString("player2");
        DOWN = (flip)?180f:0f;

        id = getSharedPreferences("id",MODE_PRIVATE);
        String s = id.getString("id","");
        if(!s.equals("")) players.child(s).child("Status").setValue("Online");

        final MediaPlayer click = MediaPlayer.create(this,R.raw.click);
        name = findViewById(R.id.textView);
        name.setTextColor(Color.RED);
        player = pl1;
        name.setTextColor(Color.rgb(255, 51, 51));
        name.setText(player);
        reset = findViewById(R.id.button10);
        reset.setBackgroundResource(R.drawable.reset);
        undo = findViewById(R.id.button11);
        undo.setBackgroundResource(R.drawable.undo);
        layout = findViewById(R.id.layout);
        msg = new Dialog(this);
        msg.setContentView(R.layout.warning);
        lout = msg.findViewById(R.id.constraintlayout);
        turn = findViewById(R.id.textView2);
        turn.setText("starts");
        flag = true;
        rep = false;
        d = new Dialog(this);
        d.setContentView(R.layout.message);

        Window win = msg.getWindow();
        WindowManager.LayoutParams wlp = win.getAttributes();
        win.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
        wlp.gravity = Gravity.CENTER;
        win.setBackgroundDrawable(new ColorDrawable(Color.argb(225,255,255,255)));
        win.setAttributes(wlp);

        Window win1 = d.getWindow();
        WindowManager.LayoutParams wlp1 = win1.getAttributes();
        win1.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
        wlp1.gravity = Gravity.CENTER;
        win1.setBackgroundDrawable(new ColorDrawable(Color.argb(225,255,255,255)));
        win1.setAttributes(wlp1);

        for(int i=0;i<9;i++){
            btn[i] = findViewById(ids[i]);
            final int j = i;
            btn[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(bug[j]!=1){
                        click.start();
                        curr = j;
                        count++;
                        btn[j].setBackgroundResource(symbol);
                        grid[j / 3][j % 3] = (symbol == CROSS) ? 1 : 2;
                        if (gameOver()!=-1 || count==9) {
                            bug = new int[]{1,1,1,1,1,1,1,1,1};
                            curr = -1;
                            new CountDownTimer(1000,1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                }

                                @Override
                                public void onFinish() {
                                    Intent i = new Intent(getApplicationContext(),Results.class);
                                    i.putExtra("winner",gameOver()+"");
                                    i.putExtra("player1",pl1);
                                    i.putExtra("player2",pl2);
                                    i.putExtra("flip",flip);
                                    i.putExtra("source",false);
                                    startActivity(i);
                                    finish();
                                }
                            }.start();
                        }
                        else {
                            turn.setText("turn");
                            symbol = (symbol == CIRCLE) ? CROSS : CIRCLE;
                            player = (symbol == CROSS) ? pl1 : pl2;
                            if (symbol==CROSS) {
                                name.setTextColor(Color.rgb(255, 51, 51));
                            } else {
                                name.setTextColor(Color.rgb(0, 153, 255));
                            }
                            name.setText(player+"'s");
                            bug[j] = 1;
                            orientation = (orientation == DOWN) ? UP : DOWN;
                            layout.setRotation(orientation);
                        }
                    }
                }
            });
        }
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(curr!=-1) {
                    count--;
                    btn[curr].setBackgroundColor(Color.TRANSPARENT);
                    grid[curr / 3][curr % 3] = x++;
                    symbol = (symbol == CIRCLE) ? CROSS : CIRCLE;
                    player = (symbol == CROSS) ? pl1 : pl2;
                    if (symbol==CROSS) {
                        name.setTextColor(Color.rgb(255, 51, 51));
                    } else {
                        name.setTextColor(Color.rgb(0, 153, 255));
                    }
                    name.setText(player+"'s");
                    bug[curr] = 0;
                    curr = -1;
                    orientation = (orientation==DOWN)?UP:DOWN;
                    layout.setRotation(orientation);
                }
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(gameOver()!=-1 || count==9)) {
                    lout.setRotation(orientation);
                    msg.setCancelable(true);
                    TextView tt = msg.findViewById(R.id.textView23);
                    tt.setText("WARNING");
                    TextView t = msg.findViewById(R.id.textView24);
                    t.setText("Are you sure?");
                    msg.show();
                    Button yes = msg.findViewById(R.id.button20);
                    Button no = msg.findViewById(R.id.button21);
                    yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            msg.cancel();
                            for (int i = 0; i < 9; i++) {
                                btn[i] = findViewById(ids[i]);
                                btn[i].setBackgroundColor(Color.TRANSPARENT);
                            }
                            player = pl1;
                            name.setTextColor(Color.RED);
                            name.setText(player);
                            turn.setText("starts");
                            symbol = CROSS;
                            int num = 10;
                            for (int k = 0; k < 3; k++) {
                                for (int j = 0; j < 3; j++) {
                                    grid[k][j] = num++;
                                }
                            }
                            bug = new int[9];
                            count = 0;
                            if (orientation == DOWN) {
                                orientation = UP;
                                layout.setRotation(orientation);
                            }
                        }
                    });
                    no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            msg.cancel();
                        }
                    });
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
    @Override
    public void onBackPressed() {
        TextView tt = msg.findViewById(R.id.textView23);
        tt.setText("WARNING");
        TextView t = msg.findViewById(R.id.textView24);
        t.setText("Are you sure?");
        Button tick = msg.findViewById(R.id.button20);
        Button cross = msg.findViewById(R.id.button21);
        msg.setCancelable(true);
        msg.show();
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msg.dismiss();
            }
        });
        tick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msg.dismiss();
                finish();
            }
        });
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
