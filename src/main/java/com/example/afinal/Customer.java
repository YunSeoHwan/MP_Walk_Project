package com.example.afinal;

// 근처 사용자 Class
public class Customer {
    String tip;
    String name;
    int resId;

    Customer() {
    }

    public Customer(String tip, String name) {
        this.tip = tip;
        this.name = name;
    }

    public Customer(String tip, String name, int resId) {
        this.tip = tip;
        this.name = name;
        this.resId = resId;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
