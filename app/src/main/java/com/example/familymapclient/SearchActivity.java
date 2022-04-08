package com.example.familymapclient;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

import Models.AuthToken;
import Models.Event;
import Models.Person;

public class SearchActivity extends AppCompatActivity {

    SearchView searchView;
    PersonBinaryTree personTree;
    ArrayList<Person> displayedPersons;
    ArrayList<Event> displayedEvents;
    androidx.recyclerview.widget.RecyclerView recyclerView;
    PersonAdapterClass adapter;
    FullUser userInfo;
    UserDataModel userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchView = (SearchView) findViewById(R.id.searchBar);
        personTree = (PersonBinaryTree) getIntent().getExtras().getSerializable("personTree");
        displayedEvents = (ArrayList<Event>) getIntent().getExtras().getSerializable("displayedEvents");
        displayedPersons = new ArrayList<>();
        displayedPersons = personTree.getAllDisplayed(personTree);
        userInfo = (FullUser)  getIntent().getExtras().getSerializable("userData");
        userData = userInfo.getUserData();

        recyclerView = findViewById(R.id.searchRecycle);
        initializeRecyclerView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //use string to find what to use
                showNewPeople(s);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)  {
            Intent data = new Intent();
            setResult(RESULT_OK, data);
            finish();
        }

        return true;
    }


    private void showNewPeople(String s) {
        //make arraylist of people in personCard form, call recyclerView.updateList(newthing)

        ArrayList<PersonCard> newList = new ArrayList<>();

        // running a for loop to compare elements.
        for (Person person : displayedPersons) {
            // checking if the entered string matched with any item of our recycler view.
            if (person.getFirstName().toLowerCase().contains(s.toLowerCase()) || person.getLastName().toLowerCase().contains(s.toLowerCase())) {
                // got a match, add it to the thing
                newList.add(new PersonCard(person.getFirstName(), person.getLastName(), "", person.getGender()));
            }
        }
        //now add the events
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
                newList.add(new PersonCard(eventPseudoName, eventPseudoLastName, eventPseudoTitle, "e"));
            }
        }
        adapter.updateList(newList);

    }

    private void initializeRecyclerView() {
        // initialize with blank thing since start with no letters
        ArrayList<PersonCard> personCards = new ArrayList<>();
        adapter = new PersonAdapterClass(personCards, userInfo);

        // set the manager for the recycler view and other stuff
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }
}