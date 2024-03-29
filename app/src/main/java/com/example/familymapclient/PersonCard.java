package com.example.familymapclient;

import Models.Event;
import Models.Person;

public class PersonCard {
    String firstName;
    String lastName;
    String gender;
    String title;
    Event event;
    Person person;

    public PersonCard(String firstName, String lastName, String title, String gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.title = title;
        this.gender = gender;
    }

    public PersonCard(String firstName, String lastName, String title, String gender, Event event) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.title = title;
        this.gender = gender;
        this.event = event;
        this.person = null;
    }

    public PersonCard(String firstName, String lastName, String title, String gender, Person person) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.title = title;
        this.gender = gender;
        this.person = person;
        this.event = null;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
