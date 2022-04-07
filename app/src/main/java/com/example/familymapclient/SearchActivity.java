package com.example.familymapclient;

import android.os.Bundle;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.Map;

import Models.AuthToken;
import Models.Event;
import Models.Person;

public class SearchActivity extends AppCompatActivity {

    SearchView searchView;
    FullUser userInfo;
    UserDataModel userData;
    Map<String, Person> personMap;
    ArrayList<Event> displayedEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchView = (SearchView) findViewById(R.id.searchBar);
        personMap = (Map<String, Person>) getIntent().getExtras().getSerializable("personMap");
        displayedEvents = (ArrayList<Event>) getIntent().getExtras().getSerializable("displayedEvents");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //use string to find what to use
                return false;
            }
        });
    }
}