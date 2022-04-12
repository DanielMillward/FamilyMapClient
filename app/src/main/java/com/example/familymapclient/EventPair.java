package com.example.familymapclient;

import java.util.ArrayList;

import Models.Event;
import Models.Person;

public class EventPair {
    ArrayList<Event> events;
    ArrayList<PersonCard> personCards;

    public EventPair(ArrayList<Event> events, ArrayList<PersonCard> personCards) {
        this.events = events;
        this.personCards = personCards;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    public ArrayList<PersonCard> getPersonCards() {
        return personCards;
    }

    public void setPersonCards(ArrayList<PersonCard> personCards) {
        this.personCards = personCards;
    }
}
