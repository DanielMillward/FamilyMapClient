package com.example.familymapclient;

import androidx.lifecycle.ViewModel;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

import Models.Event;
import Models.Person;

public class UserDataModel extends ViewModel implements Serializable {

    private boolean wasSuccess;
    private ArrayList<Event> events;
    private ArrayList<Person> persons;

    public UserDataModel(boolean wasSuccess, ArrayList<Event> events, ArrayList<Person> persons) {
        this.wasSuccess = wasSuccess;
        this.events = events;
        this.persons = persons;
    }

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
}
