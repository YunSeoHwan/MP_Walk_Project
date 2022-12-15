package com.example.afinal;

public class User {     // 근처 사용자 Class
    String name;
    double km = 0;

    User(){}

    public User(String name, double km) {
        this.name = name;
        this.km = km;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getKm() {
        return km;
    }

    public void setKm(double km) {
        this.km = km;
    }
}
