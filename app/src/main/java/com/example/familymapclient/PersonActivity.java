package com.example.familymapclient;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    ArrayList<Event> sortedDisplayedEvents;
    ArrayList<Person> sortedDisplayedPersons;

    FullUser userInfo;
    UserDataModel userData;
    Person userPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        activeEvent = (Event) getIntent().getExtras().getSerializable("activeEvent");
        personTree = (PersonBinaryTree) getIntent().getExtras().getSerializable("personTree");
        displayedEvents = (ArrayList<Event>) getIntent().getExtras().getSerializable("displayedEvents");
        userInfo= (FullUser) getIntent().getExtras().getSerializable("userData");
        userData = userInfo.getUserData();
        displayedPersons = new ArrayList<>();
        displayedPersons = personTree.getAllDisplayed(personTree);

        for (Person person : displayedPersons) {
            if (person.getFirstName().equals(userInfo.getUserFirstName()) && person.getLastName().equals(userInfo.getUserLastName())) {
                userPerson = person;
            }
        }

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
                if (activeEvent != null) {
                    //Find what the active event (or person) is
                    if (i == 1) {
                        //it was an actual event
                        Bundle myBundle = new Bundle();
                        //add people with whether they're displayed or not
                        myBundle.putSerializable("displayedEvents", (Serializable) displayedEvents);
                        myBundle.putSerializable("personTree", (Serializable) personTree);
                        myBundle.putSerializable("userData", userInfo);
                        Intent eventIntent = new Intent(getApplicationContext(), EventActivity.class);
                        Event actualClickedEvent = sortedDisplayedEvents.get(i1);
                        myBundle.putSerializable("pastClickedEvent", actualClickedEvent);
                        eventIntent.putExtras(myBundle);
                        searchActivityLauncher.launch(eventIntent);
                    } else if (i == 0) {
                        //clicked on a person, assume makeMap func is already called
                        Bundle myBundle = new Bundle();
                        //add people with whether they're displayed or not
                        myBundle.putSerializable("displayedEvents", (Serializable) displayedEvents);
                        myBundle.putSerializable("personTree", (Serializable) personTree);
                        myBundle.putSerializable("userData", userInfo);
                        Intent personIntent = new Intent(getApplicationContext(), PersonActivity.class);
                        Person clickedPerson = sortedDisplayedPersons.get(i1);
                        Event clickedEvent = null;
                        //get an event to pass on
                        for (Event event : userData.getEvents()) {
                            if (event.getPersonID().equals(clickedPerson.getPersonID())) {
                                clickedEvent = event;
                            }
                        }
                        myBundle.putSerializable("activeEvent", clickedEvent);
                        personIntent.putExtras(myBundle);
                        searchActivityLauncher.launch(personIntent);
                    }


                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)  {
            //Intent intent= new Intent(this, MainActivity.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //startActivity(intent);
            Intent data = new Intent();
            setResult(RESULT_OK, data);
            finish();
        }

        return true;
    }

    ActivityResultLauncher<Intent> searchActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                }
            });

    private void setTopInfo() {
        Person activePerson = null;
        for (Person person : userData.getPersons()) {
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
        for (Person person : userData.getPersons()) {
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
                    eventCards.add(new PersonCard(eventPseudoName, eventPseudoLastName, eventPseudoTitle, "e", event));
                    tempEvents.add(event);
                }
            }
        }
        GeneralHelper helper = GeneralHelper.getInstance();
        Pair<ArrayList<Event>, ArrayList<PersonCard>> sortEvents = helper.sortEvents(tempEvents, eventCards);
        tempEvents = sortEvents.first;
        eventCards = sortEvents.second;


        ArrayList<Person> tempPersons = new ArrayList<>();
        //Part 2 - adding the person expander
        if (activePerson != null) {
            PersonPair personRelatives = personTree.getRelatives(userData, activePerson, personTree, userPerson);
           tempPersons = personRelatives.getPersons();
           personCards = personRelatives.getPersonCards();
        }

        //part 3 - actually making the map
        HashMap<String, ArrayList<PersonCard>> output = new HashMap<>();
        output.put("Life Events", eventCards);
        output.put("Family", personCards);

        sortedDisplayedEvents = tempEvents;
        sortedDisplayedPersons = tempPersons;

        if (output.size() > 0) {
            return output;
        }
        return null;
    }

}