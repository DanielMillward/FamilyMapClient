package com.example.familymapclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import Models.Event;

public class EventActivity extends AppCompatActivity {

    UserDataModel userData;
    FullUser userInfo;
    Event pastClickedEvent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        userInfo= (FullUser) getIntent().getExtras().getSerializable("userData");
        userData = userInfo.getUserData();
        pastClickedEvent = (Event) getIntent().getExtras().getSerializable("pastClickedEvent");

        //add mapFragment to the fragmentContainer
        if (savedInstanceState == null) {
            //Make a bundle and put the userData in it
            Bundle bundleData = new Bundle();
            bundleData.putSerializable("userData", userInfo);
            bundleData.putBoolean("isEventActivity", true);
            bundleData.putSerializable("pastClickedEvent", pastClickedEvent);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Fragment newFragment = new MapFragment();
            newFragment.setArguments(bundleData);
            transaction.replace(R.id.eventFragmentContainer, newFragment);
            transaction.commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)  {
            Intent data = new Intent();
            setResult(RESULT_OK, data);
            finish();
        }
        return true;
    }
}