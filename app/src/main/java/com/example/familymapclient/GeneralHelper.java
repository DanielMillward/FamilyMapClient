package com.example.familymapclient;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Collection;

import Models.Event;
import Models.Person;

public class GeneralHelper {

    private static GeneralHelper oneInstance = null;

    private GeneralHelper() {}

    public static GeneralHelper getInstance() {
        if (oneInstance == null) {
            oneInstance = new GeneralHelper();
        }
        return oneInstance;
    }

    public EventPair sortEvents(ArrayList<Event> tempEvents, ArrayList<PersonCard> eventCards) {
        //sorting events by year
        boolean allDone = false;
        PersonCard cardTemp;
        Event eventTemp;
        while(!allDone) {
            allDone = true;
            for (int i = 0; i < tempEvents.size() - 1; ++i) {
                if (tempEvents.get(i).getYear() > tempEvents.get(i+1).getYear()) {
                    cardTemp = eventCards.get(i);
                    eventCards.set(i, eventCards.get(i+1));
                    eventCards.set(i+1, cardTemp);

                    eventTemp = tempEvents.get(i);
                    tempEvents.set(i, tempEvents.get(i+1));
                    tempEvents.set(i+1, eventTemp);

                    allDone = false;
                }
            }
        }
        return new EventPair(tempEvents, eventCards);
    }

    public ArrayList<PersonCard> findMatchingPersons(UserDataModel userData, String s) {
        ArrayList<PersonCard> output = new ArrayList<>();
        // running a for loop to compare elements.
        for (Person person : userData.getPersons()) {
            // checking if the entered string matched with any item of our recycler view.
            if (person.getFirstName().toLowerCase().contains(s.toLowerCase()) || person.getLastName().toLowerCase().contains(s.toLowerCase())) {
                // got a match, add it to the thing
                output.add(new PersonCard(person.getFirstName(), person.getLastName(), "", person.getGender(), person));

            }
        }
        return output;
    }

    public ArrayList<PersonCard> findMatchingEvents(ArrayList<Person> displayedPersons, ArrayList<Event> displayedEvents, String s) {
        ArrayList<PersonCard> output = new ArrayList<>();

        for (Event event: displayedEvents) {
            //countries, cities, event types, and years
            if (event.getEventType().toLowerCase().contains(s.toLowerCase()) ||
                    event.getCountry().toLowerCase().contains(s.toLowerCase()) ||
                    event.getCity().toLowerCase().contains(s.toLowerCase()) ||
                    Integer.toString(event.getYear()).contains(s.toLowerCase())) {
                String eventPseudoName = event.getEventType().toUpperCase() + ": " +
                        event.getCity() + ", " + event.getCountry();
                String eventPseudoLastName = " (" + Integer.toString(event.getYear()) + ")";
                String eventPseudoTitle = "";
                for (Person person : displayedPersons) {
                    if (person.getPersonID().equals(event.getPersonID())) {
                        eventPseudoTitle = person.getFirstName() + " " + person.getLastName();
                    }
                }
                output.add(new PersonCard(eventPseudoName, eventPseudoLastName, eventPseudoTitle, "e", event));
            }
        }
        return output;
    }
}
