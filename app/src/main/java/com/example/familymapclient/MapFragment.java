package com.example.familymapclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Models.Event;
import Models.Person;
import Models.User;

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



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // here you have the reference of your button

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
        System.out.println("Recieved user data success is " + userData.wasSuccess() +
                " with events length " + userData.getEvents().size() + " and people length " + userData.getPersons().size());


        String message = "Welcome, " + userInfo.getUserFirstName() + " " + userInfo.getUserLastName()+ "!";
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();

        SupportMapFragment mapFragment= (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fillPersonBinaryTree(userData.getPersons(), userInfo.getUserFirstName(), userInfo.getUserLastName());
    }

    private void fillPersonBinaryTree(ArrayList<Person> persons, String firstName, String lastName) {
        Person user = null;
        String fatherID;
        String motherID;


        for (Person person : persons) {
            //find original user
            if (person.getFirstName() == firstName && person.getLastName() == lastName) {
                user = person;
                fatherID = person.getFatherID();
                motherID = person.getMotherID();
            }
            //convert arraylist to dictionary for easier access
            personMap.put(person.getPersonID(), person);
        }

        personBinaryTree = new PersonBinaryTree(user, true);
        addParents(personBinaryTree);
    }

    private void addParents(PersonBinaryTree tree) {
        if (tree.getPerson().getFatherID() == null || tree.getPerson().getMotherID() == null) {
            return;
        } else {
            Person father = personMap.get(tree.getPerson().getFatherID());
            PersonBinaryTree fatherTree = tree.setLeft(new PersonBinaryTree(father, true));
            addParents(fatherTree);
            Person mother = personMap.get(tree.getPerson().getMotherID());
            PersonBinaryTree motherTree = tree.setRight(new PersonBinaryTree(mother, true));
            addParents(motherTree);
        }
    }


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MapFragment() {
        // Required empty public constructor
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    //The one that actually does the work!
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        GoogleMap map = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng sydney= new LatLng(-34,  151);

        map.animateCamera(CameraUpdateFactory.newLatLng(sydney));
        System.out.println("About to add stuff to map!");
        addEventsToMap(map);
        map.setOnMarkerClickListener(new MyMarkerListener());
    }

    private void addEventsToMap(GoogleMap map) {
        //Use data to put all events there
        //use setTag to pass in the event object
        LatLng sydney= new LatLng(-34,  151);
        map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));



        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        boolean displayFatherSide = sharedPref.getBoolean("FATHERS_SIDE", false);
        boolean displayMotherSide = sharedPref.getBoolean("MOTHERS_SIDE", false);
        boolean displayMale = sharedPref.getBoolean("MALE_EVENTS", true);
        boolean displayFemale = sharedPref.getBoolean("FEMALE_EVENTS", true);

        //TODO: Based on these bools and the tree previously made, decide which ones are displayed
        System.out.println("About to delete some nodes");
        if (!displayFatherSide) {
            //set the isDisplayed of the person node to false
            setPersonTreeNodesFalse("father", personBinaryTree.getLeft());
        }
        if (!displayMotherSide) {
            setPersonTreeNodesFalse("mother", personBinaryTree.getRight());
        }
        if (!displayMale) {
            setPersonTreeNodesFalse("male", personBinaryTree);
        }
        if (!displayFemale) {
            setPersonTreeNodesFalse("female", personBinaryTree);
        }

        //get the events for the persons that are to be displayed
        ArrayList<Event> displayEvents = new ArrayList<>();
        getDisplayEvents(personBinaryTree, displayEvents, userData.getEvents());


        //events already with a color
        Map<String, Float> usedEvents = new HashMap<>();
        int colorCounter = 0;
        //make list of events that the settings tell us to use
        ArrayList<Event> displayedEvents = new ArrayList<>();

        for (Event event : displayEvents){
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

    private void getDisplayEvents(PersonBinaryTree tree, ArrayList<Event> output, ArrayList<Event> events) {
        //go through tree, if it's to be displayed then for every event for this person add to output
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

    private void setPersonTreeNodesFalse(String nodeType, PersonBinaryTree tree) {
        //goes through tree and deletes the respective nodes based on the settings
        if (tree == null) return;
        if (nodeType.equals("father")) {
            tree.setDisplayed(false);
            if (tree.left != null && tree.right != null) {
                tree.getLeft().setDisplayed(false);
                tree.getRight().setDisplayed(false);
                System.out.println("Disabled person " + tree.getPerson().getFirstName() + " " + tree.getPerson().getLastName());
                setPersonTreeNodesFalse("father", tree.getLeft());
                setPersonTreeNodesFalse("father", tree.getRight());
            }
        } else if (nodeType.equals("mother")) {
            tree.setDisplayed(false);
            if (tree.left != null && tree.right != null) {
                tree.getLeft().setDisplayed(false);
                tree.getRight().setDisplayed(false);
                System.out.println("Disabled person " + tree.getPerson().getFirstName() + " " + tree.getPerson().getLastName());
                setPersonTreeNodesFalse("mother", tree.getLeft());
                setPersonTreeNodesFalse("mother", tree.getRight());
            }
        } else if (nodeType.equals("male")) {
            if (tree.getPerson().getGender().equals("m")) {
                tree.setDisplayed(false);
            }
            if (tree.getRight() != null) {
                if (tree.getRight().getPerson().getGender().equals("m")) {
                    tree.getRight().setDisplayed(false);
                }
                setPersonTreeNodesFalse("male", tree.getRight());
            }
            if (tree.getLeft() != null) {
                if (tree.getLeft().getPerson().getGender().equals("m")) {
                    tree.getLeft().setDisplayed(false);
                }
                setPersonTreeNodesFalse("male", tree.getLeft());
            }
        } else if (nodeType.equals("female")) {
            if (tree.getPerson().getGender().equals("f")) {
                tree.setDisplayed(false);
            }
            if (tree.getRight() != null) {
                if (tree.getRight().getPerson().getGender().equals("f")) {
                    tree.getRight().setDisplayed(false);
                }
                setPersonTreeNodesFalse("female", tree.getRight());
            }
            if (tree.getLeft() != null) {
                if (tree.getLeft().getPerson().getGender().equals("f")) {
                    tree.getLeft().setDisplayed(false);
                }
                setPersonTreeNodesFalse("female", tree.getLeft());
            }
        }
    }


    private class MyMarkerListener implements GoogleMap.OnMarkerClickListener {


        @Override
        public boolean onMarkerClick(@NonNull Marker marker) {
            //use the tag of the marker to tell what event object it was
            //pass the shared preferences to the thingy
            return false;
        }
    }
}