package com.example.familymapclient;

import com.google.android.gms.maps.model.Marker;

import java.util.Comparator;

import Models.Event;

public class EventComparator implements Comparator<Marker> {


    @Override
    public int compare(Marker marker, Marker t1) {
        Event t1event = (Event) t1.getTag();
        Event markerEvent = (Event) marker.getTag();
        return t1event.getYear() - markerEvent.getYear();
    }
}
