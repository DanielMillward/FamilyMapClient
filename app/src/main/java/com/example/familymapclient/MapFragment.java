package com.example.familymapclient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import Models.Event;
import Models.Person;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback{
    FullUser userInfo;
    UserDataModel userData;
    String firstName;
    String lastName;
    float markerColors[];
    PersonBinaryTree personBinaryTree;
    Map<String, Person> personMap;
    ArrayList<Marker> displayedMarkers;
    ArrayList<Polyline> displayedLines;
    private GoogleMap map;

    boolean displayFatherSide;
    boolean displayMotherSide;
    boolean displayMale;
    boolean displayFemale;
    boolean displaySpouseLines;
    boolean displayLifeStoryLines;
    boolean displayFamilyTreeLines;
    TextView eventInfoText;
    TextView personInfoText;
    ImageView personPicture;

    Event activeEvent;

    boolean isEventActivity;
    Event pastClickedEvent;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // here you have the reference of your button


        activeEvent = null;

        eventInfoText = (TextView) getView().findViewById(R.id.eventInfoText);
        personInfoText = (TextView) getView().findViewById(R.id.personInfoText);
        personPicture = (ImageView) getView().findViewById(R.id.personPic);

        displayedLines = new ArrayList<>();

        getStoredPreferences();


        personMap = new HashMap<>();
        displayedMarkers = new ArrayList<>();

        markerColors = new float[10];
        markerColors[0] = BitmapDescriptorFactory.HUE_RED;
        markerColors[1] = BitmapDescriptorFactory.HUE_BLUE;
        markerColors[2] = BitmapDescriptorFactory.HUE_CYAN;
        markerColors[3] = BitmapDescriptorFactory.HUE_GREEN;
        markerColors[4] = BitmapDescriptorFactory.HUE_MAGENTA;
        markerColors[5] = BitmapDescriptorFactory.HUE_ORANGE;
        markerColors[6] = BitmapDescriptorFactory.HUE_AZURE;
        markerColors[7] = BitmapDescriptorFactory.HUE_ROSE;
        markerColors[8] = BitmapDescriptorFactory.HUE_VIOLET;
        markerColors[9] = BitmapDescriptorFactory.HUE_YELLOW;


        Bundle bundle = getArguments();
        userInfo= (FullUser) bundle.getSerializable("userData");
        userData = userInfo.getUserData();
        isEventActivity = (boolean) bundle.getBoolean("isEventActivity");
        pastClickedEvent = (Event) bundle.getSerializable("pastClickedEvent");

        System.out.println("Recieved user data success is " + userData.wasSuccess() +
                " with events length " + userData.getEvents().size() + " and people length " + userData.getPersons().size());


        String message = "Welcome, " + userInfo.getUserFirstName() + " " + userInfo.getUserLastName()+ "!";
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();

        SupportMapFragment mapFragment= (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        PersonBinaryTree tempTree = new PersonBinaryTree(null, false);
        personBinaryTree = tempTree.fillPersonBinaryTree(userData.getPersons(), userInfo.getUserFirstName(), userInfo.getUserLastName());

        //set onclick listener for bottom view thing
        androidx.gridlayout.widget.GridLayout explainBox = getView().findViewById(R.id.eventInfo);

        explainBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToPersonActivity(view);
            }
        });

    }

    public void goToPersonActivity(View view) {
        if (activeEvent != null) {
            Intent searchIntent = new Intent(getActivity().getApplicationContext(), PersonActivity.class);
            Bundle myBundle = new Bundle();
            //add people with whether they're displayed or not
            myBundle.putSerializable("personTree", (Serializable) personBinaryTree);
            myBundle.putSerializable("activeEvent", activeEvent);
            myBundle.putSerializable("userData", userInfo);
            //add useable events
            ArrayList<Event> displayedEvents = new ArrayList<>();
            for (Marker marker : displayedMarkers) {
                Event currEvent = (Event) marker.getTag();
                displayedEvents.add(currEvent);
            }
            myBundle.putSerializable("displayedEvents", (Serializable) displayedEvents);
            myBundle.putSerializable("userData", userInfo);
            searchIntent.putExtras(myBundle);
            searchActivityLauncher.launch(searchIntent);
        }
    }



    private void getStoredPreferences() {
        SharedPreferences sharedPref = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        displayFatherSide = sharedPref.getBoolean("FATHERS_SIDE", true);
        displayMotherSide = sharedPref.getBoolean("MOTHERS_SIDE", false);
        displayMale = sharedPref.getBoolean("MALE_EVENTS", true);
        displayFemale = sharedPref.getBoolean("FEMALE_EVENTS", true);
        displaySpouseLines = sharedPref.getBoolean("SPOUSE_LINES", true);
        displayLifeStoryLines = sharedPref.getBoolean("LIFE_STORY_LINES", true);
        displayFamilyTreeLines = sharedPref.getBoolean("FAMILY_TREE_LINES", true);
    }





    public MapFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //saying we're using the map_menu xml file for our options menu
        inflater.inflate(R.menu.map_menu, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //use for menu items
        int id = item.getItemId();
        switch (id) {
            //case R.id.action_settings:
                // do stuff, like showing settings fragment
           //     return true;
            case R.id.settings_icon:
                Intent intent = new Intent(getActivity().getApplicationContext(), SettingsActivity.class);
                settingsActivityLauncher.launch(intent);
                return true;
            case R.id.search_icon:
                Intent searchIntent = new Intent(getActivity().getApplicationContext(), SearchActivity.class);
                Bundle myBundle = new Bundle();
                myBundle.putString("key", "value");
                myBundle.putSerializable("personTree", (Serializable) personBinaryTree);
                ArrayList<Event> displayedEvents = new ArrayList<>();
                for (Marker marker : displayedMarkers) {
                    Event currEvent = (Event) marker.getTag();
                    displayedEvents.add(currEvent);
                }
                myBundle.putSerializable("displayedEvents", (Serializable) displayedEvents);
                myBundle.putSerializable("userData", userInfo);
                searchIntent.putExtras(myBundle);
                searchActivityLauncher.launch(searchIntent);
                return true;
        }

        return super.onOptionsItemSelected(item); // important line
    }

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> settingsActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    resetMap(result);
                }
            });

    ActivityResultLauncher<Intent> searchActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //resetMap(result);
                }
            });

    private void resetMap(ActivityResult result) {
        activeEvent = null;
        System.out.println("OK1!!!! " + result.getResultCode());
        //delete displayedMarkers
        System.out.println("OK!!!!");
        for (Marker marker : displayedMarkers) {
            marker.remove();
        }
        //delete displayedLines
        for (Polyline line : displayedLines) {
            line.remove();
        }
        //redraw
        getStoredPreferences();
        addEventsToMap(map);
    }

    public void openSettings() {
        //The one you actually plug & play where you need

    }


    //The one that actually does the work!
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng sydney= new LatLng(-34,  151);

        map.animateCamera(CameraUpdateFactory.newLatLng(sydney));
        System.out.println("About to add stuff to map!");
        addEventsToMap(map);
        MyMarkerListener mapListener = new MyMarkerListener(googleMap);
        map.setOnMarkerClickListener(mapListener);

        if (isEventActivity && pastClickedEvent != null) {
            if (displayedLines != null) {
                for (Polyline line: displayedLines) {
                    line.remove();
                }
            }

            LatLng desiredLocation = new LatLng(pastClickedEvent.getLatitude(),  pastClickedEvent.getLongitude());
            map.animateCamera(CameraUpdateFactory.newLatLng(desiredLocation));
            Marker desiredMarker = null;
            for (Marker marker : displayedMarkers) {
                Event markerEvent = (Event) marker.getTag();
                assert markerEvent != null;
                if (markerEvent.getEventID().equals(pastClickedEvent.getEventID())) {
                    desiredMarker = marker;
                }
            }
            mapListener.drawEnabledLines(pastClickedEvent, googleMap, desiredMarker);

            Person eventPerson = null;
            for (Person person : userData.getPersons()) {
                if (person.getPersonID().equals(pastClickedEvent.getPersonID())) {
                    eventPerson = person;
                }
            }

            personInfoText.setText(eventPerson.getFirstName() + " " + eventPerson.getLastName());
            eventInfoText.setText(pastClickedEvent.getEventType().toUpperCase() + ": " + pastClickedEvent.getCity()+ ", " + pastClickedEvent.getCountry() + " (" + pastClickedEvent.getYear()+ ")");
            if (eventPerson.getGender().equals("m")) {
                personPicture.setImageResource(R.drawable.boy_pic_3);
            } else if (eventPerson.getGender().equals("f")){
                personPicture.setImageResource(R.drawable.girl_pic_3);
            }

        }
    }

    private void addEventsToMap(GoogleMap map) {
        //Use data to put all events there
        //use setTag to pass in the event object
        //LatLng sydney= new LatLng(-34,  151);
        //map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));


        //TODO: Based on these bools and the tree previously made, decide which ones are displayed
        System.out.println("About to delete some nodes");
        resetTreeDisplay(personBinaryTree);
        displayedMarkers.clear();
        displayedLines.clear();
        if (!displayFatherSide) {
            //set the isDisplayed of the person node to false
            personBinaryTree.setPersonTreeNodesFalse("father", personBinaryTree.getLeft());
        }
        if (!displayMotherSide) {
            personBinaryTree.setPersonTreeNodesFalse("mother", personBinaryTree.getRight());
        }
        if (!displayMale) {
            personBinaryTree.setPersonTreeNodesFalse("male", personBinaryTree);
        }
        if (!displayFemale) {
            personBinaryTree.setPersonTreeNodesFalse("female", personBinaryTree);
        }

        //get the events for the persons that are to be displayed
        ArrayList<Event> displayEvents = new ArrayList<>();
        getDisplayEvents(personBinaryTree, displayEvents, userData.getEvents());
        //Add spouse events of root
        if (personBinaryTree.getPerson().getSpouseID() != null) {
            for (Event event : userData.getEvents()) {
                if (event.getPersonID().equals(personBinaryTree.getPerson().getSpouseID())) {
                    if (personBinaryTree.getPerson().getGender().equals("m") && displayFemale) {
                        displayEvents.add(event);
                    } else if ((personBinaryTree.getPerson().getGender().equals("f") && displayMale)) {
                        displayEvents.add(event);
                    }
                }
            }
        }

        //events already with a color
        Map<String, Float> usedEvents = new HashMap<>();
        int colorCounter = 0;
        //make list of events that the settings tell us to use
        ArrayList<Event> displayedEvents = new ArrayList<>();

        for (Event event : displayEvents) {
            //if event Type has not been assigned a color already, set a new one/reuse, else use set
            if (usedEvents.containsKey(event.getEventType())) {
                //already have a color, assign it
                Marker  marker = map.addMarker(new MarkerOptions().
                        position(new LatLng(event.getLatitude(), event.getLongitude())).
                        icon(BitmapDescriptorFactory.defaultMarker(usedEvents.get(event.getEventType()))));
                marker.setTag(event);
                displayedMarkers.add(marker);
            } else {
                //put new color next to it
                if (colorCounter >= markerColors.length) {
                    colorCounter = 0;
                }
                usedEvents.put(event.getEventType(), markerColors[colorCounter]);
                Marker  marker = map.addMarker(new MarkerOptions().
                        position(new LatLng(event.getLatitude(), event.getLongitude())).
                        icon(BitmapDescriptorFactory.defaultMarker(usedEvents.get(event.getEventType()))));
                marker.setTag(event);
                displayedMarkers.add(marker);
                colorCounter++;
            }
        }

    }

    private void resetTreeDisplay(PersonBinaryTree tree) {
        tree.setDisplayed(true);
        if (tree.left != null) {
            resetTreeDisplay(tree.getLeft());
        }
        if (tree.right != null) {
            resetTreeDisplay(tree.getRight());
        }
    }

    private void getDisplayEvents(PersonBinaryTree tree, ArrayList<Event> output, ArrayList<Event> events) {
        //if person is displayed, then add every matching event to the output
        if (tree.isDisplayed()) {
            for (Event event : events) {
                if (event.getPersonID().equals(tree.getPerson().getPersonID())) {
                    output.add(event);
                }
            }
        }
        if (tree.getLeft() != null) {
            getDisplayEvents(tree.getLeft(), output, events);
        }
        if (tree.getRight() != null) {
            getDisplayEvents(tree.getRight(), output, events);
        }

    }




    private class MyMarkerListener implements GoogleMap.OnMarkerClickListener {
        GoogleMap myMap;
        public MyMarkerListener(@NonNull GoogleMap googleMap) {
            myMap = googleMap;
        }

        @Override
        public boolean onMarkerClick(@NonNull Marker marker) {
            //use the tag of the marker to tell what event object it was, use displayedMarkers
            Event currEvent = (Event) marker.getTag();
            activeEvent = currEvent;
            myMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
            assert currEvent != null;
            System.out.println("Clicked on marker of " + currEvent.getEventType() + "for " + currEvent.getPersonID());
            drawEnabledLines(currEvent, myMap, marker);

            setInfoText(currEvent);

            return false;
        }

        private void setInfoText(Event currEvent) {
            PersonBinaryTree personNode = null;
            if (currEvent.getPersonID().equals(personBinaryTree.getPerson().getSpouseID())) {
                for (Person person : userData.getPersons()) {
                    if (person.getPersonID().equals(personBinaryTree.getPerson().getSpouseID())) {
                        personNode = new PersonBinaryTree(person, true);
                    }
                }
            } else {
                personNode = personBinaryTree.findNodeFromID(currEvent.getPersonID(), personBinaryTree);
            }

            String firstName = personNode.getPerson().getFirstName();
            String lastName = personNode.getPerson().getLastName();
            String eventType = currEvent.getEventType();
            String eventCity = currEvent.getCity();
            String eventCountry = currEvent.getCountry();
            String eventYear = Integer.toString(currEvent.getYear());

            personInfoText.setText(firstName + " " + lastName);
            eventInfoText.setText(eventType.toUpperCase() + ": " + eventCity + ", " + eventCountry + " (" + eventYear + ")");
            if (personNode.getPerson().getGender().equals("m")) {
                personPicture.setImageResource(R.drawable.boy_pic_3);
            } else if (personNode.getPerson().getGender().equals("f")){
                personPicture.setImageResource(R.drawable.girl_pic_3);
            }
        }

        private void drawEnabledLines(Event currEvent, GoogleMap myMap, Marker marker) {
            //clear previously displayed lines
            if (displayedLines != null) {
                for (Polyline line: displayedLines) {
                    line.remove();
                }
            }


            String currPersonID = currEvent.getPersonID();

            if (displaySpouseLines) {
                System.out.println("Printing spouse lines");
                drawSpouseLines(currPersonID, marker);
            }
            if (displayFamilyTreeLines) {
                drawFamilyTreeLines(currEvent, marker);
            }
            if (displayLifeStoryLines) {
                drawLifeStoryLines(currEvent);
            }
        }

        private void drawFamilyTreeLines(Event currEvent, Marker marker) {
            PersonBinaryTree currPersonSubTree = personBinaryTree.getSubtreeGivenID(currEvent.getPersonID(), personBinaryTree);
            recurseDrawFamilyTree(currEvent, currPersonSubTree, 20, marker);
        }

        private void recurseDrawFamilyTree(Event currEvent, PersonBinaryTree currPersonSubTree, float width, Marker currMarker) {
            if (currPersonSubTree == null) return;
            if (currPersonSubTree.left != null && currPersonSubTree.right != null) {
                int oldestDadYear = 9999999;
                int oldestMomYear = 9999999;
                Marker oldestDadMarker = null;
                Marker oldestMomMarker = null;
                //find the oldest marker for the parents
                for (Marker marker : displayedMarkers) {
                    Event currMarkerEvent = (Event) marker.getTag();
                    if (currMarkerEvent == null) continue;
                    if (currMarkerEvent.getPersonID().equals(currPersonSubTree.getLeft().getPerson().getPersonID())) {
                        //event is a dad event
                        if (currMarkerEvent.getYear() < oldestDadYear) {
                            oldestDadYear = currMarkerEvent.getYear();
                            oldestDadMarker = marker;
                        }
                    }
                    if (currMarkerEvent.getPersonID().equals(currPersonSubTree.getRight().getPerson().getPersonID())) {
                        //event is a mom event
                        if (currMarkerEvent.getYear() < oldestMomYear) {
                            oldestMomYear = currMarkerEvent.getYear();
                            oldestMomMarker = marker;
                        }
                    }
                }
                if (oldestDadMarker != null) {
                    drawLineGivenMarkers(myMap, currMarker, oldestDadMarker,0xff000000, width);
                }
                //draw lines to parents
                if (oldestMomMarker != null) {
                    drawLineGivenMarkers(myMap, currMarker, oldestMomMarker,0xff000000, width);
                }


                //call same thing on parents
                recurseDrawFamilyTree(currEvent, currPersonSubTree.getLeft(), width * (2/3f), oldestDadMarker);
                recurseDrawFamilyTree(currEvent, currPersonSubTree.getRight(), width * (2/3f), oldestMomMarker);
            }
        }

        private void drawLifeStoryLines(Event currEvent) {
            ArrayList<Marker> personMarkers = new ArrayList<>();
            //Find marker events relating to selected person
            for (Marker marker : displayedMarkers) {
                Event markerEvent = (Event) marker.getTag();
                if (markerEvent.getPersonID().equals(currEvent.getPersonID())) {
                    personMarkers.add(marker);
                }
            }
            //sort based on year
            Collections.sort(personMarkers, new EventComparator());
            //Now have sorted markers based on year. Just draw the connecting event markers!
            for (int i = 0; i < personMarkers.size()-1; ++i) {
                drawLineGivenMarkers(myMap, personMarkers.get(i), personMarkers.get(i+1),0xff0000ff, 10F );
            }
        }

        private void drawSpouseLines(String currPersonID, Marker currMarker) {
            if (currPersonID.equals(personBinaryTree.getPerson().getSpouseID())){
                int oldestYear = 2022;
                Marker oldestMarker = null;
                for (Marker marker : displayedMarkers) {
                    //Iterate through all markers, if it belongs to spouse, and is oldest, set as marker

                    Event markerEvent = (Event) marker.getTag();
                    if (markerEvent.getPersonID().equals(personBinaryTree.getPerson().getPersonID())) {
                        if (markerEvent.getYear()  < oldestYear) {
                            oldestYear = markerEvent.getYear();
                            oldestMarker = marker;
                        }
                    }
                }
                drawLineGivenMarkers(myMap, currMarker, oldestMarker, 0xffff0000, 10F);
            }

            PersonBinaryTree currTree= personBinaryTree.findSpouseOfPersonFromID(userData, personBinaryTree, currPersonID, personBinaryTree.getPerson());

            Person currPersonSpouse = null;
            if (currTree == null) {
                return;
            } else {
                currPersonSpouse = currTree.getPerson();

                System.out.println("Spouse of " + currPersonID+ " is " + currPersonSpouse.getPersonID());
            }

            int oldestYear = 2022;
            Marker oldestMarker = null;
            if (currPersonSpouse != null) {
                for (Marker marker : displayedMarkers) {
                    //Iterate through all markers, if it belongs to spouse, and is oldest, set as marker
                    Event markerEvent = (Event) marker.getTag();
                    System.out.println("Comparing " + markerEvent.getPersonID() + " with " + currPersonSpouse.getPersonID());
                    if (markerEvent.getPersonID().equals(currPersonSpouse.getPersonID())) {
                        if (markerEvent.getYear()  < oldestYear) {
                            oldestYear = markerEvent.getYear();
                            oldestMarker = marker;
                        }
                    }
                }
                if (currMarker != null && oldestMarker != null) {
                    drawLineGivenMarkers(myMap, currMarker, oldestMarker, 0xffff0000, 10F);
                }

            }


        }

        private void drawLineGivenMarkers(GoogleMap map, Marker currMarker, Marker oldestMarker, int color, float width) {
            if (currMarker == null) {
                System.out.println("Uh oh");
                return;
            }
            LatLng start = new LatLng(currMarker.getPosition().latitude, currMarker.getPosition().longitude);
            if (oldestMarker == null) {
                System.out.println("huh");
                return;
            }
            LatLng end = new LatLng(oldestMarker.getPosition().latitude, oldestMarker.getPosition().longitude);
            System.out.println("Drawing line between " + start.toString() + " and " + end.toString());

            PolylineOptions options = new PolylineOptions().add(start).add(end).color(color).width(width);
            Polyline line = map.addPolyline(options);
            displayedLines.add(line);
        }
    }


}