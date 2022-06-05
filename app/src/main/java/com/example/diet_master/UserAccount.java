package com.example.diet_master;

/**
 * 사용자 계정 정보 모델 클래스
 */

public class UserAccount {
    private String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    private String nickname; //닉네임
    private String age; //나이
    private String height; //신장
    private String weight;  //체중
    private String radiogroup; //성별
    private String idToken; // Firebase UID (사용자 고유 토큰 정보)

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public UserAccount(){}

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getRadiogroup() {
        return radiogroup;
    }

    public void setRadiogroup(String radiogroup) {
        this.radiogroup = radiogroup;
    }

    public UserAccount(String nickname, String age, String height, String weight, String radiogroup, String idToken, String uid) {
        this.nickname = nickname;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.radiogroup = radiogroup;
        this.idToken = idToken;
        this.uid = uid;
    }
}
