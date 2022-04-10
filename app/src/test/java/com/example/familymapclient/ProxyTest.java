package com.example.familymapclient;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.Assert.*;

/**
 * Tests:
 * Login method
 * Registering a new user
 * Retrieving people related to a logged in/registered user
 * Retrieving events related to a logged in/registered user
 * Calculates family relationships (i.e., spouses, parents, children)
 * Filters events according to the current filter settings
 * Chronologically sorts a person’s individual events (birth first, death last, etc.)
 * Correctly searches for people and events (for your Search Activity)
 */
public class ProxyTest {

    @BeforeEach
    public void setUp() {
        //call clear on server?
    }

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void loginPass() {
        //Login with credentials already in db
    }

    @Test
    public void loginFail() {
        //login with incorrect credentials
    }

    @Test
    public void registerPass() {
        //brand new user
    }

    @Test
    public void registerFail() {
        //user already in the db
    }

    @Test
    public void retrievePeoplePass() {
        //User already in DB, getting their info (check is accurate?)
    }

    @Test
    public void retrievePeopleFail() {
        // User not registered in DB
    }

    @Test
    public void retrieveEventsPass() {
        //User in DB, getting their events (check is accurate?)
    }

    @Test
    public void retrieveEventsFail() {
        //User not registered in DB
    }
}