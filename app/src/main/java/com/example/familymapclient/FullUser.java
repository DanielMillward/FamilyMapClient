package com.example.familymapclient;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class FullUser implements Serializable{
    String userFirstName;
    String userLastName;
    UserDataModel userData;


    public FullUser(String userFirstName, String userLastName, UserDataModel userData) {
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.userData = userData;
    }

    protected FullUser(Parcel in) {
        userFirstName = in.readString();
        userLastName = in.readString();

    }


    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public UserDataModel getUserData() {
        return userData;
    }

    public void setUserData(UserDataModel userData) {
        this.userData = userData;
    }

}
