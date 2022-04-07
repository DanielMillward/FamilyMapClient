package com.example.familymapclient;

import android.os.Bundle;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import Models.AuthToken;
import Models.Event;
import Models.Person;

public class SearchActivity extends AppCompatActivity {

    SearchView searchView;
    Map<String, Person> personMap;
    ArrayList<Person> displayedPersons;
    ArrayList<Event> displayedEvents;
    androidx.recyclerview.widget.RecyclerView recyclerView;
    PersonAdapterClass adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchView = (SearchView) findViewById(R.id.searchBar);
        personMap = (Map<String, Person>) getIntent().getExtras().getSerializable("personMap");
        displayedEvents = (ArrayList<Event>) getIntent().getExtras().getSerializable("displayedEvents");

        Person[] personArray = personMap.values().toArray(new Person[0]);
        displayedPersons = new ArrayList<>();
        displayedPersons.addAll(Arrays.asList(personArray));

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
        adapter.updateList(newList);

    }

    private void initializeRecyclerView() {
        // initialize with blank thing since start with no letters
        ArrayList<PersonCard> personCards = new ArrayList<>();
        adapter = new PersonAdapterClass(personCards);

        // set the manager for the recycler view and other stuff
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }
}