package com.example.familymapclient;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.Assert.*;

public class GeneralTest {

    @BeforeEach
    public void setUp() {

    }

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void getFamilyRelationsPass() {
        //Has all 4 (mother/father/spouse/child)
    }

    @Test
    public void getFamilyRelationsFail() {
        //Only spouse and child
        //Only parents
    }

    @Test
    public void filterFatherEventsPass() {
        //Has regular dad tree, search through and make sure everyone on father side is 0
    }

    @Test
    public void filterFatherEventsFail() {
        //Has no dad tree, check doesn't crash
    }

    @Test
    public void filterMotherEventsPass() {
        //Regular mom tree
    }

    @Test
    public void filterMotherEventsFail() {
        //no mom tree
    }

    @Test
    public void filterMaleEventsPass() {
        //Regular tree, run through & check
    }

    @Test
    public void filterMaleEventsFail() {
        //Tree of only the mom's side mothers
    }

    @Test
    public void filterFemaleEventsPass() {
        //regular tree
    }

    @Test
    public void filterFemaleEventsFail() {
        //tree of only dad's side fathers
    }

    @Test
    public void sortEventsChronologicallyPass() {
        //Have a birth, marriage, and death event
    }

    @Test
    public void sortEventsChronologicallyFail() {
        //only a birth event
    }

    @Test
    public void searchForPeoplePass() {
        //Regular search with person results
    }

    @Test
    public void searchForPeopleFail() {
        //search that yields no person results
    }

    @Test
    public void searchForEventsPass() {
        //regular search with event results
    }

    @Test
    public void searchForEventsFail() {
        //search with no event results
    }
}
