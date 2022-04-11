package com.example.familymapclient;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.Assert.*;

import android.util.Pair;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import Models.Event;
import Models.Person;
import Models.User;
import RequestResult.LoginRequest;
import RequestResult.LoginResult;
import RequestResult.RegisterRequest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

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

    String testData;
    String serverAddress = "http://localhost:8080";
    String testDataFile = "C:\\Users\\dfmil\\AndroidStudioProjects\\FamilyMapClient\\app\\src\\test\\java\\com\\example\\familymapclient\\testData.json";

    Proxy proxy;

    LoginRequest loginRequest;
    RegisterRequest registerRequest;

    User user;

    TestHelper testHelper;

    @BeforeEach
    public void setUp() {
        //clear database and set testData as the json to load (if needed)
        try {
            testHelper = new TestHelper();
            boolean gotCleared = testHelper.clearDatabase(serverAddress);
            testData = testHelper.getDataFromFile(testDataFile);

            proxy = new Proxy();
            if (!gotCleared) {
                throw new IOException("Error clearing or getting Data");
            }
        } catch (IOException e) {
            System.out.println("Error clearing database:");
            System.out.println(e.getMessage());
        }


        user = new User("sheila", "parker", "sheila@parker.com",
                "Sheila", "Parker", "f", "Sheila_Parker");
        loginRequest = new LoginRequest(user.getUsername(), user.getPassword());
        registerRequest = new RegisterRequest(user.getUsername(), user.getPassword(),
                user.getEmail(), user.getFirstName(), user.getLastName(), user.getGender());
    }


    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void loginPass() throws IOException {
        //Login with credentials already in db
        testHelper.addTestDataToDatabase(testData, serverAddress);
        FullUser fullUser = proxy.getLoginRegisterData(true, "http://localhost", "8080",
                loginRequest, null);
        Assertions.assertEquals(user.getFirstName(), fullUser.getUserFirstName());
        Assertions.assertEquals(user.getLastName(), fullUser.getUserLastName());
    }

    @Test
    public void loginFail() throws IOException {
        //login with incorrect credentials
        testHelper.addTestDataToDatabase(testData, serverAddress);
        LoginRequest badLogin = new LoginRequest(user.getUsername(),"a wrong password");
        FullUser fullUser = proxy.getLoginRegisterData(true, "http://localhost", "8080",
               badLogin, null);
        Assertions.assertNull(fullUser.userFirstName);
        Assertions.assertNull(fullUser.userLastName);
        Assertions.assertNull(fullUser.userData);
    }

    @Test
    public void registerPass() {
        //brand new user
        FullUser fullUser = proxy.getLoginRegisterData(false, "http://localhost", "8080",
                null, registerRequest);
        Assertions.assertEquals(user.getFirstName(), fullUser.getUserFirstName());
        Assertions.assertEquals(user.getLastName(), fullUser.getUserLastName());
    }

    @Test
    public void registerFail() throws IOException {
        //user already in the db
        testHelper.addTestDataToDatabase(testData, serverAddress);
        FullUser fullUser = proxy.getLoginRegisterData(false, "http://localhost", "8080",
                null, registerRequest);
        Assertions.assertNull(fullUser.userFirstName);
        Assertions.assertNull(fullUser.userLastName);
        Assertions.assertNull(fullUser.userData);
    }

    @Test
    public void retrievePeoplePass() throws IOException {
        //User already in DB, getting their info (check is accurate?)
        testHelper.addTestDataToDatabase(testData, serverAddress);
        FullUser fullUser = proxy.getLoginRegisterData(true, "http://localhost", "8080",
                loginRequest, null);
        ArrayList<Person> retrievedPersons = fullUser.getUserData().getPersons();
        for (Person person : retrievedPersons) {
            Assertions.assertEquals(person.getAssociatedUsername(), user.getUsername());
            boolean isInUserList = testHelper.comparePersonAgainstSheilaList(person);
            Assertions.assertTrue(isInUserList);
        }
    }


    @Test
    public void retrievePeopleFail() {
        // User not registered in DB
        LoginRequest badLogin = new LoginRequest(user.getUsername(),"a wrong password");
        FullUser retrieveFail = proxy.getLoginRegisterData(true, "http://localhost", "8080",
                badLogin, null);
        Assertions.assertNull(retrieveFail.userFirstName);
        Assertions.assertNull(retrieveFail.userLastName);
        Assertions.assertNull(retrieveFail.userData);
    }

    @Test
    public void retrieveEventsPass() throws IOException {
        //User in DB, getting their events (check is accurate?)
        testHelper.addTestDataToDatabase(testData, serverAddress);
        FullUser fullUser = proxy.getLoginRegisterData(true, "http://localhost", "8080",
                loginRequest, null);
        ArrayList<Event> retrievedEvents = fullUser.getUserData().getEvents();
        for (Event event : retrievedEvents) {
            Assertions.assertEquals(event.getAssociatedUsername(), user.getUsername());
            boolean isInUserList = testHelper.compareEventAgainstSheilaList(event);
            Assertions.assertTrue(isInUserList);
        }
    }


    @Test
    public void retrieveEventsFail() {
        //User not registered in DB
        LoginRequest badLogin = new LoginRequest(user.getUsername(),"a wrong password");
        FullUser retrieveFail = proxy.getLoginRegisterData(true, "http://localhost", "8080",
                badLogin, null);
        Assertions.assertNull(retrieveFail.userData);
    }


}