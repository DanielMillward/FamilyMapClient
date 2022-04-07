package com.example.familymapclient;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class FullUser implements Serializable, Parcelable {
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

    public static final Creator<FullUser> CREATOR = new Creator<FullUser>() {
        @Override
        public FullUser createFromParcel(Parcel in) {
            return new FullUser(in);
        }

        @Override
        public FullUser[] newArray(int size) {
            return new FullUser[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userFirstName);
        parcel.writeString(userLastName);
    }
}
