package com.example.familymapclient;

import androidx.lifecycle.ViewModel;

import Models.Event;
import Models.Person;

public class UserDataModel extends ViewModel{

    private boolean wasSuccess;
    private Event[] events;
    private Person[] persons;

    public UserDataModel(boolean wasSuccess, Event[] events, Person[] persons) {
        this.wasSuccess = wasSuccess;
        this.events = events;
        this.persons = persons;
    }

    public boolean WasSuccess() {
        return wasSuccess;
    }

    public void setWasSuccess(boolean wasSuccess) {
        this.wasSuccess = wasSuccess;
    }

    public Event[] getEvents() {
        return events;
    }

    public void setEvents(Event[] events) {
        this.events = events;
    }

    public Person[] getPersons() {
        return persons;
    }

    public void setPersons(Person[] persons) {
        this.persons = persons;
    }
}
