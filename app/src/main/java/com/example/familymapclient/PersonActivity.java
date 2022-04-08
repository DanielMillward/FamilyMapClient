package com.example.familymapclient;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import Models.Event;
import Models.Person;

public class PersonActivity extends AppCompatActivity {

    PersonBinaryTree personTree;
    ArrayList<Person> displayedPersons;
    ArrayList<Event> displayedEvents;
    Event activeEvent;

    ExpandableListView expandableList;
    ExpandableListAdapter expandableListAdapter;
    ArrayList<String> bigTitleList;
    HashMap<String, ArrayList<PersonCard>> listOfLists;

    TextView firstName;
    TextView lastName;
    TextView gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        activeEvent = (Event) getIntent().getExtras().getSerializable("activeEvent");
        personTree = (PersonBinaryTree) getIntent().getExtras().getSerializable("personTree");
        displayedEvents = (ArrayList<Event>) getIntent().getExtras().getSerializable("displayedEvents");
        displayedPersons = new ArrayList<>();
        displayedPersons = personTree.getAll(personTree);

        firstName = (TextView) findViewById(R.id.personFirstName);
        lastName = (TextView) findViewById(R.id.personLastName);
        gender = (TextView) findViewById(R.id.personGender);
        setTopInfo();

        expandableList = (ExpandableListView) findViewById(R.id.expandableListView);
        //TODO: Make map of titles with their data in PersonCard form
        listOfLists = makeMapOfExpandableData();
        bigTitleList = new ArrayList<String>(listOfLists.keySet());
        expandableListAdapter = new MyExpanderAdapter(this, bigTitleList, listOfLists);
        expandableList.setAdapter(expandableListAdapter);

        expandableList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                return false;
            }
        });
        expandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                return false;
            }
        });
    }

    private void setTopInfo() {
        Person activePerson = null;
        for (Person person : displayedPersons) {
            if (person.getPersonID().equals(activeEvent.getPersonID())) {
                activePerson = person;
            }
        }
        if (activePerson != null) {
            firstName.setText(activePerson.getFirstName());
            lastName.setText(activePerson.getLastName());
            if (activePerson.getGender().equals("m")) {
                gender.setText("male");
            } else if (activePerson.getGender().equals("f")) {
                gender.setText("female");
            }
        }
    }

    private HashMap<String, ArrayList<PersonCard>> makeMapOfExpandableData() {
        ArrayList<PersonCard> eventCards = new ArrayList<>();
        ArrayList<Event> tempEvents = new ArrayList<>();
        ArrayList<PersonCard> personCards = new ArrayList<>();
        //Finding the person on the tree
        Person activePerson = null;
        for (Person person : displayedPersons) {
            if (person.getPersonID().equals(activeEvent.getPersonID())) {
                activePerson = person;
            }
        }
        //Part 1 - adding the life events
        if (activePerson != null) {
            for (Event event : displayedEvents) {
                if (event.getPersonID().equals(activePerson.getPersonID())) {
                    String eventPseudoName = event.getEventType().toUpperCase() + ": " +
                            event.getCity() + ", " + event.getCountry();
                    String eventPseudoLastName = " (" + Integer.toString(event.getYear()) + ")";
                    String eventPseudoTitle = activePerson.getFirstName() + " " + activePerson.getLastName();
                    eventCards.add(new PersonCard(eventPseudoName, eventPseudoLastName, eventPseudoTitle, "e"));
                    tempEvents.add(event);
                }
            }
        }
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

        //Part 2 - adding the person expander
        if (activePerson != null) {
            PersonBinaryTree activeTree = personTree.findNodeFromID(activePerson.getPersonID(), personTree);
            //father
            PersonBinaryTree fatherTree = activeTree.getLeft();
            //mother
            PersonBinaryTree motherTree = activeTree.getRight();
            //spouse
            PersonBinaryTree spouseTree = activeTree.findSpouseOfPersonFromID(personTree, activePerson.getPersonID());
            //children
            PersonBinaryTree childTree = activeTree.findChildFromParentID(activePerson.getPersonID(), personTree);

            if (fatherTree != null) {
                personCards.add(new PersonCard(fatherTree.getPerson().getFirstName(), fatherTree.getPerson().getLastName(), "father", "m"));
            }
            if (motherTree != null) {
                personCards.add(new PersonCard(motherTree.getPerson().getFirstName(), motherTree.getPerson().getLastName(), "mother", "f"));
            }
            if (spouseTree != null) {
                personCards.add(new PersonCard(spouseTree.getPerson().getFirstName(), spouseTree.getPerson().getLastName(), "spouse", spouseTree.getPerson().getGender()));
            }
            if (childTree != null) {
                personCards.add(new PersonCard(childTree.getPerson().getFirstName(), childTree.getPerson().getLastName(), "child", childTree.getPerson().getGender()));
            }
        }

        //part 3 - actually making the map
        HashMap<String, ArrayList<PersonCard>> output = new HashMap<>();
        output.put("Life Events", eventCards);
        output.put("Family", personCards);
        if (output.size() > 0) {
            return output;
        }
        return null;
    }

}