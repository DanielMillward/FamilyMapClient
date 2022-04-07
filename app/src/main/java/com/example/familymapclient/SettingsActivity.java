package com.example.familymapclient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity {

    SwitchCompat lifeStorySwitch;
    SwitchCompat familyTreeSwitch;
    SwitchCompat spouseSwitch;
    SwitchCompat fatherSwitch;
    SwitchCompat motherSwitch;
    SwitchCompat maleSwitch;
    SwitchCompat femaleSwitch;

    androidx.gridlayout.widget.GridLayout logoutView;

    boolean displayFatherSide;
    boolean displayMotherSide;
    boolean displayMale;
    boolean displayFemale;
    boolean displaySpouseLines;
    boolean displayLifeStoryLines;
    boolean displayFamilyTreeLines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        lifeStorySwitch = (SwitchCompat) findViewById(R.id.lifeLineSwitch);
        familyTreeSwitch = (SwitchCompat) findViewById(R.id.treeLineSwitch);
        spouseSwitch = (SwitchCompat) findViewById(R.id.spouseLineSwitch);
        fatherSwitch = (SwitchCompat) findViewById(R.id.fatherSideSwitch);
        motherSwitch = (SwitchCompat) findViewById(R.id.motherSideSwitch);
        maleSwitch = (SwitchCompat) findViewById(R.id.maleSwitch);
        femaleSwitch = (SwitchCompat) findViewById(R.id.femaleSwitch);

        logoutView = (androidx.gridlayout.widget.GridLayout) findViewById(R.id.logoutView);

        SharedPreferences sharedPref = getSharedPreferences("settings", Context.MODE_PRIVATE);
        displayFatherSide = sharedPref.getBoolean("FATHERS_SIDE", false);
        displayMotherSide = sharedPref.getBoolean("MOTHERS_SIDE", false);
        displayMale = sharedPref.getBoolean("MALE_EVENTS", true);
        displayFemale = sharedPref.getBoolean("FEMALE_EVENTS", true);
        displaySpouseLines = sharedPref.getBoolean("SPOUSE_LINES", true);
        displayLifeStoryLines = sharedPref.getBoolean("LIFE_STORY_LINES", false);
        displayFamilyTreeLines = sharedPref.getBoolean("FAMILY_TREE_LINES", true);

        lifeStorySwitch.setChecked(displayLifeStoryLines);
        familyTreeSwitch.setChecked(displayFamilyTreeLines);
        spouseSwitch.setChecked(displaySpouseLines);
        fatherSwitch.setChecked(displayFatherSide);
        motherSwitch.setChecked(displayMotherSide);
        maleSwitch.setChecked(displayMale);
        femaleSwitch.setChecked(displayFemale);

        lifeStorySwitch.setOnCheckedChangeListener(new SwitchListener("LIFE_STORY_LINES"));
        familyTreeSwitch.setOnCheckedChangeListener(new SwitchListener("FAMILY_TREE_LINES"));
        spouseSwitch.setOnCheckedChangeListener(new SwitchListener("SPOUSE_LINES"));
        fatherSwitch.setOnCheckedChangeListener(new SwitchListener("FATHERS_SIDE"));
        motherSwitch.setOnCheckedChangeListener(new SwitchListener("MOTHERS_SIDE"));
        maleSwitch.setOnCheckedChangeListener(new SwitchListener("MALE_EVENTS"));
        femaleSwitch.setOnCheckedChangeListener(new SwitchListener("FEMALE_EVENTS"));


    }

    public void logoutUser(View view){
        Intent myIntent = new Intent(this, MainActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(myIntent);
        finish();
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

    private class SwitchListener implements CompoundButton.OnCheckedChangeListener {
        String propertyToChange;

        public SwitchListener(String property) {
            propertyToChange = property;
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            SharedPreferences sharedPref = getSharedPreferences("settings", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            System.out.println("Changed to " + b);
            editor.putBoolean(propertyToChange, b);
            editor.commit();
        }
    }


}