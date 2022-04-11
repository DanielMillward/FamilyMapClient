package com.example.familymapclient;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.Assert.*;

import android.util.Pair;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import Models.Event;
import Models.Person;
import Models.User;
import RequestResult.LoginRequest;

public class GeneralTest {

    PersonBinaryTree tree;
    String testData;
    String serverAddress = "http://localhost:8080";
    String testDataFile = "C:\\Users\\dfmil\\AndroidStudioProjects\\FamilyMapClient\\app\\src\\test\\java\\com\\example\\familymapclient\\testData.json";

    ArrayList<Person> retrievedPersons;
    ArrayList<Event> retrievedEvents;

    TestHelper testHelper;

    Person sheilaPerson;
    Person sheilaParent;

    FullUser fullUser;

    @Before
    public void generalSetUp() {
        Proxy proxy = new Proxy();
        try {
            testHelper = new TestHelper();
            boolean gotCleared = testHelper.clearDatabase(serverAddress);
            testData = testHelper.getDataFromFile(testDataFile);
            testHelper.addTestDataToDatabase(testData, serverAddress);
            proxy = new Proxy();
            if (!gotCleared) {
                throw new IOException("Error clearing or getting Data");
            }
        } catch (IOException e) {
            System.out.println("Error clearing database:");
            System.out.println(e.getMessage());
        }
        User user = new User("sheila", "parker", "sheila@parker.com",
                "Sheila", "Parker", "f", "Sheila_Parker");
        sheilaPerson = new Person("Sheila_Parker", "sheila",
                "Sheila", "Parker", "f", "Blaine_McGary", "Betty_White", "Davis_Hyer");
        LoginRequest loginRequest = new LoginRequest(user.getUsername(), user.getPassword());
        fullUser = proxy.getLoginRegisterData(true, "http://localhost", "8080",
                loginRequest, null);

        retrievedPersons = fullUser.getUserData().getPersons();
        retrievedEvents = fullUser.getUserData().getEvents();

        PersonBinaryTree tempTree = new PersonBinaryTree(null, false);
        tree = tempTree.fillPersonBinaryTree(retrievedPersons, "Sheila", "Parker");
        sheilaParent = tree.getLeft().getPerson();
    }

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
        //binarytree relations
        PersonPair personRelatives = tree.getRelatives(fullUser.getUserData(), sheilaParent, tree, sheilaPerson);
        ArrayList<Person> fatherRelatives = personRelatives.getPersons();
        System.out.println("temp");
        Person father = fatherRelatives.get(0);
        Person mother = fatherRelatives.get(1);
        Person spouse = fatherRelatives.get(2);
        Person child = fatherRelatives.get(3);
        assertEquals("Ken_Rodham", father.getPersonID());
        assertEquals("Mrs_Rodham", mother.getPersonID());
        assertEquals("Betty_White", spouse.getPersonID());
        assertEquals("Sheila_Parker", child.getPersonID());
    }

    @Test
    public void getFamilyRelationsTwo() {
        //No spouse
        PersonPair personRelatives = tree.getRelatives(fullUser.getUserData(),sheilaPerson, tree, sheilaPerson);
        ArrayList<Person> sheilaRelatives = personRelatives.getPersons();
        assertEquals(3, sheilaRelatives.size());
        Person father = sheilaRelatives.get(0);
        Person mother = sheilaRelatives.get(1);
        Person spouse = sheilaRelatives.get(2);
        assertEquals("Blaine_McGary", father.getPersonID());
        assertEquals("Betty_White", mother.getPersonID());
        assertEquals("Davis_Hyer", spouse.getPersonID());
        //No parents

    }

    @Test
    public void filterFatherEventsPass() {
        //Has regular dad tree, search through and make sure everyone on father side is 0
        //use SetPersonTreeNodes
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
        //use helper sortEvents
    }

    @Test
    public void sortEventsChronologicallyFail() {
        //only a birth event
    }

    @Test
    public void searchForPeoplePass() {
        //Regular search with person results
        //helper findmatchingpersons
    }

    @Test
    public void searchForPeopleFail() {
        //search that yields no person results
    }

    @Test
    public void searchForEventsPass() {
        //regular search with event results
        //helper findmatchingevents
    }

    @Test
    public void searchForEventsFail() {
        //search with no event results
    }

}
