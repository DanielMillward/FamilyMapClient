package com.example.familymapclient;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
        GeneralHelper helper = GeneralHelper.getInstance();

        newList.addAll(helper.findMatchingPersons(userData, s));
        newList.addAll(helper.findMatchingEvents(displayedPersons, displayedEvents, s));

        adapter.updateList(newList);

    }

    private void initializeRecyclerView() {
        // initialize with blank thing since start with no letters
        ArrayList<PersonCard> personCards = new ArrayList<>();
        adapter = new PersonAdapterClass(personCards, userInfo, displayedEvents, personTree, getApplicationContext(), cardActivityLauncher);

        // set the manager for the recycler view and other stuff
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }

    ActivityResultLauncher<Intent> cardActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                }
            });

}