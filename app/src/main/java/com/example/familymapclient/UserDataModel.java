package com.example.familymapclient;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.lifecycle.ViewModel;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

import Models.Event;
import Models.Person;

public class UserDataModel extends ViewModel implements Serializable, Parcelable {

    private boolean wasSuccess;
    private ArrayList<Event> events;
    private ArrayList<Person> persons;

    public UserDataModel(boolean wasSuccess, ArrayList<Event> events, ArrayList<Person> persons) {
        this.wasSuccess = wasSuccess;
        this.events = events;
        this.persons = persons;
    }

    protected UserDataModel(Parcel in) {
        wasSuccess = in.readByte() != 0;
    }

    public static final Creator<UserDataModel> CREATOR = new Creator<UserDataModel>() {
        @Override
        public UserDataModel createFromParcel(Parcel in) {
            return new UserDataModel(in);
        }

        @Override
        public UserDataModel[] newArray(int size) {
            return new UserDataModel[size];
        }
    };

    public boolean wasSuccess() {
        return wasSuccess;
    }

    public void setWasSuccess(boolean wasSuccess) {
        this.wasSuccess = wasSuccess;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    public ArrayList<Person> getPersons() {
        return persons;
    }

    public void setPersons(ArrayList<Person> persons) {
        this.persons = persons;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte((byte) (wasSuccess ? 1 : 0));


    }
}
