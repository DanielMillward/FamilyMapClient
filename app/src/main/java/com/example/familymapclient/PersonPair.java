package com.example.familymapclient;

import java.util.ArrayList;

import Models.Person;

public class PersonPair {
    ArrayList<Person> persons;
    ArrayList<PersonCard> personCards;

    public PersonPair(ArrayList<Person> persons, ArrayList<PersonCard> personCards) {
        this.persons = persons;
        this.personCards = personCards;
    }

    public ArrayList<Person> getPersons() {
        return persons;
    }

    public void setPersons(ArrayList<Person> persons) {
        this.persons = persons;
    }

    public ArrayList<PersonCard> getPersonCards() {
        return personCards;
    }

    public void setPersonCards(ArrayList<PersonCard> personCards) {
        this.personCards = personCards;
    }
}
