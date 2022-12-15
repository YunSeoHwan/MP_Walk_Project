package com.example.afinal;

public class UserWalk {     // 사용자 산책정보 받아오는 Class
    private int walk = 0;
    private double km = 0;
    private int kcal = 0;

    public UserWalk(){

    }
    public UserWalk(int walk, double km, int kcal) {
        this.walk = walk;
        this.km = km;
        this.kcal = kcal;
    }

    public int getWalk() {
        return walk;
    }

    public void setWalk(int walk) {
        this.walk = walk;
    }

    public double getKm() {
        return km;
    }

    public void setKm(double km) {
        this.km = km;
    }

    public int getKcal() {
        return kcal;
    }

    public void setKcal(int kcal) {
        this.kcal = kcal;
    }
}
