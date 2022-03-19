package com.example.familymapclient;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import Models.Event;

public class EventArray {
    @SerializedName("data")
    private ArrayList<Event> eventList;

    public ArrayList<Event> getList() {
        return eventList;
    }
}