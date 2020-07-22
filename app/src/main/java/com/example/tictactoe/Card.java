package com.example.tictactoe;

import java.util.Comparator;

public class Card {
    private String name;
    private int w, l, d;
    private String status;
    private int avatar;
    private String id;
    private boolean extra;

    public Card(String s, int w, int l, int d, int avatar, String status, String id, boolean e) {
        this.name = s;
        this.w = w;
        this.l = l;
        this.d = d;
        this.avatar = avatar;
        this.status = status;
        this.id = id;
        this.extra = e;
    }

    public static Comparator<Card> name_compare = new Comparator<Card>() {

        public int compare(Card c1, Card c2) {
            String name = c1.getName();
            String name1 = c2.getName();
            return name.compareToIgnoreCase(name1);
        }
    };

    public static Comparator<Card> win_compare = new Comparator<Card>() {

        public int compare(Card c1, Card c2) {
            int w1 = c1.getW();
            int w2 = c2.getW();
            return w2-w1;
        }
    };

    public String getName() {
        return name;
    }
    public String getStatus() {
        return status;
    }
    public String getId(){
        return id;
    }
    public int getW(){
        return w;
    }
    public int getL(){
        return l;
    }
    public int getD(){
        return d;
    }
    public int getAvatar(){
        return avatar;
    }
    public boolean getExtra(){
        return extra;
    }
}
