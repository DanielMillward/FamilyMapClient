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
    PersonBinaryTree tempTree = new PersonBinaryTree(null, false);

    Event birthEvent;
    Event weddingEvent;
    Event deathEvent;

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

        birthEvent = new Event("birthEvent", "elon", "elon_musk",
                69, 42, "funny", "number", "birth", 1969);
        weddingEvent = new Event("weddingEvent", "elon", "elon_musk",
                69, 42, "non", "regular", "marriage", 1989);
        deathEvent = new Event("deathEvent", "elon", "elon_musk",
                69, 42, "other", "number", "death", 2010);
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

        PersonBinaryTree fatherFilter = tempTree.fillPersonBinaryTree(retrievedPersons, "Sheila", "Parker");
        fatherFilter.setPersonTreeNodesFalse("father", fatherFilter.getLeft());
        //iterate through father, make sure all things
        recurseOnSide(fatherFilter.getLeft(), false);
        //iterate through mother, make sure still all good
        recurseOnSide(fatherFilter.getRight(), true);
    }


    @Test
    public void filterFatherEventsFail() {
        //Has no dad tree, check doesn't crash
        PersonBinaryTree fatherFilterFail = tempTree.fillPersonBinaryTree(retrievedPersons, "Sheila", "Parker");
        fatherFilterFail.left = null;
        fatherFilterFail.setPersonTreeNodesFalse("father", fatherFilterFail.left);
        //iterate through father, make sure all things
        recurseOnSide(fatherFilterFail.getLeft(), false);
        //iterate through mother, make sure still all good
        recurseOnSide(fatherFilterFail.getRight(), true);
    }

    @Test
    public void filterMotherEventsPass() {
        //Regular mom tree
        PersonBinaryTree motherFilter = tempTree.fillPersonBinaryTree(retrievedPersons, "Sheila", "Parker");
        motherFilter.setPersonTreeNodesFalse("mother", motherFilter.getRight());
        //iterate through mother
        recurseOnSide(motherFilter.getRight(), false);
        //iterate through father
        recurseOnSide(motherFilter.getLeft(), true);
    }

    @Test
    public void filterMotherEventsFail() {
        //no mom tree
        PersonBinaryTree motherFilterFail = tempTree.fillPersonBinaryTree(retrievedPersons, "Sheila", "Parker");
        motherFilterFail.right = null;
        motherFilterFail.setPersonTreeNodesFalse("mother", motherFilterFail.right);
        //iterate through father, make sure all things
        recurseOnSide(motherFilterFail.getRight(), false);
        //iterate through mother, make sure still all good
        recurseOnSide(motherFilterFail.getLeft(), true);
    }

    @Test
    public void filterMaleEventsPass() {
        //Regular tree, run through & check
        PersonBinaryTree maleFilter = tempTree.fillPersonBinaryTree(retrievedPersons, "Sheila", "Parker");
        maleFilter.setPersonTreeNodesFalse("male", maleFilter);
        //iterate through whole tree, make sure it's good
        recurseWhole(maleFilter, true);
    }

    @Test
    public void filterMaleEventsFail() {
        //Tree of only mother's side
        PersonBinaryTree maleFilter = tempTree.fillPersonBinaryTree(retrievedPersons, "Sheila", "Parker");
        maleFilter.left = null;
        maleFilter.setPersonTreeNodesFalse("male", maleFilter);
        //iterate through whole tree, make sure it's good
        recurseWhole(maleFilter, true);
    }

    @Test
    public void filterFemaleEventsPass() {
        //Regular tree, run through & check
        PersonBinaryTree femaleFilter = tempTree.fillPersonBinaryTree(retrievedPersons, "Sheila", "Parker");
        femaleFilter.setPersonTreeNodesFalse("female", femaleFilter);
        //iterate through whole tree, make sure it's good
        recurseWhole(femaleFilter, false);

    }

    @Test
    public void filterFemaleEventsFail() {
        //tree of only dad's side
        PersonBinaryTree femaleFilter = tempTree.fillPersonBinaryTree(retrievedPersons, "Sheila", "Parker");
        femaleFilter.right = null;
        femaleFilter.setPersonTreeNodesFalse("female", femaleFilter);
        //iterate through whole tree, make sure it's good
        recurseWhole(femaleFilter, false);
    }

    @Test
    public void sortEventsChronologicallyPass() {
        //Have a birth, marriage, and death event
        //use helper sortEvents
        GeneralHelper generalHelper = GeneralHelper.getInstance();
        ArrayList<Event> eventList = new ArrayList<>();
        //add out of order
        eventList.add(birthEvent);
        eventList.add(deathEvent);
        eventList.add(weddingEvent);

        ArrayList<PersonCard> personCardList = new ArrayList<>();
        personCardList.add(new PersonCard("Elon", "Musk", "1969", "m", birthEvent));
        personCardList.add(new PersonCard("Elon", "Musk", "2010", "m", deathEvent));
        personCardList.add(new PersonCard("Elon", "Musk", "1989", "m", weddingEvent));

        EventPair sorted = generalHelper.sortEvents(eventList, personCardList);
        assertEquals(1969, sorted.events.get(0).getYear());
        assertEquals(1989, sorted.events.get(1).getYear());
        assertEquals(2010, sorted.events.get(2).getYear());

        assertEquals("1969", sorted.personCards.get(0).getTitle());
        assertEquals("1989", sorted.personCards.get(1).getTitle());
        assertEquals("2010", sorted.personCards.get(2).getTitle());

    }

    @Test
    public void sortEventsChronologicallyTwo() {
        //only a birth event
        GeneralHelper generalHelper = GeneralHelper.getInstance();
        ArrayList<Event> eventList = new ArrayList<>();

        eventList.add(birthEvent);

        ArrayList<PersonCard> personCardList = new ArrayList<>();
        personCardList.add(new PersonCard("Elon", "Musk", "1969", "m", birthEvent));

        EventPair sorted = generalHelper.sortEvents(eventList, personCardList);
        assertEquals(1969, sorted.events.get(0).getYear());
        assertEquals(1, sorted.events.size());
        assertEquals("1969", sorted.personCards.get(0).getTitle());
        assertEquals(1, sorted.personCards.size());
    }

    @Test
    public void searchForPeoplePass() {
        //Regular search with person results
        GeneralHelper generalHelper = GeneralHelper.getInstance();
        ArrayList<PersonCard> foundPersons = generalHelper.findMatchingPersons(fullUser.getUserData(), "h");

        assertEquals(5, foundPersons.size());
        ArrayList<String> personsWithH = new ArrayList<>();
        personsWithH.add("Sheila_Parker");
        personsWithH.add("Davis_Hyer");
        personsWithH.add("Betty_White");
        personsWithH.add("Ken_Rodham");
        personsWithH.add("Mrs_Rodham");

        for (PersonCard personCard : foundPersons) {
            Person person = personCard.person;
            assertTrue(personsWithH.contains(person.getPersonID()));
        }

    }


    @Test
    public void searchForPeopleFail() {
        //search that yields no person results
        GeneralHelper generalHelper = GeneralHelper.getInstance();
        ArrayList<PersonCard> foundPersons = generalHelper.findMatchingPersons(fullUser.getUserData(), "some Random string");
        assertEquals(0, foundPersons.size());
    }

    @Test
    public void searchForEventsPass() {
        //regular search with event results
        //helper findmatchingevents
        GeneralHelper generalHelper = GeneralHelper.getInstance();
        ArrayList<PersonCard> foundEvents = generalHelper.findMatchingEvents(fullUser.getUserData().getPersons(),
                fullUser.getUserData().getEvents(), "asteroids");

        assertEquals(2, foundEvents.size());
        ArrayList<String> asteroidEvents = new ArrayList<>();
        asteroidEvents.add("Sheila_Asteroids");
        asteroidEvents.add("Other_Asteroids");

        for (PersonCard personCard : foundEvents) {
            Event event = personCard.event;
            assertTrue(asteroidEvents.contains(event.getEventID()));
        }
    }

    @Test
    public void searchForEventsFail() {
        //search with no event results
        GeneralHelper generalHelper = GeneralHelper.getInstance();
        ArrayList<PersonCard> foundEvents = generalHelper.findMatchingEvents(fullUser.getUserData().getPersons(),
                fullUser.getUserData().getEvents(), "nothing should match this");
        assertEquals(0, foundEvents.size());
    }

    public void recurseOnSide(PersonBinaryTree tree, boolean shouldBeTrue) {
        if (tree != null) {
            assertEquals(shouldBeTrue, tree.isDisplayed());
            if (tree.left != null) {
                recurseOnSide(tree.getLeft(), shouldBeTrue);
            }
            if (tree.right != null) {
                recurseOnSide(tree.getRight(), shouldBeTrue);
            }
        }
    }

    public void recurseWhole(PersonBinaryTree tree, boolean settingIsMale) {
        if (settingIsMale && tree.getPerson().getGender().equals("m")) {
            assertFalse(tree.isDisplayed());
        } else if (!settingIsMale && tree.getPerson().getGender().equals("f")) {
            assertFalse(tree.isDisplayed());
        } else {
            assertTrue(tree.isDisplayed());
        }
        if (tree.left != null) {
            recurseWhole(tree.getLeft(), settingIsMale);
        }
        if (tree.right != null) {
            recurseWhole(tree.getRight(), settingIsMale);
        }
    }

}
