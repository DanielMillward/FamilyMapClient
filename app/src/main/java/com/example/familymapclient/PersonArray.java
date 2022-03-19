package com.example.familymapclient;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import Models.Person;

public class PersonArray {
    @SerializedName("data")
    private ArrayList<Person> personList;

    public ArrayList<Person> getList() {
        return personList;
    }
}