package com.example.afinal;

public class UserAccount {
    private String idToken;     // Firebase Uid(고유정보 토큰)
    private String emailId;     // 이메일 아이디
    private String password;    // 비밀번호
    private String name;

    // firebase DB를 쓰기위한 생성자
    public UserAccount(String name, String emailId, String password) {
        this.name = name;
        this.emailId = emailId;
        this.password = password;
    }
    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
