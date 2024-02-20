package com.xorazmapp.sayhundomladastur;

public class User {

    String ism;
    String fam;


    public User(String ism, String fam) {
        this.ism = ism;
        this.fam = fam;
    }

    public String getIsm() {
        return ism;
    }

    public void setIsm(String ism) {
        this.ism = ism;
    }

    public String getFam() {
        return fam;
    }

    public void setFam(String fam) {
        this.fam = fam;
    }


}
