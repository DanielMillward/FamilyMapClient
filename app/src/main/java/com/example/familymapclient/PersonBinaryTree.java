package com.example.familymapclient;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Models.Person;

public class PersonBinaryTree implements Serializable {
    PersonBinaryTree left;
    PersonBinaryTree right;
    Person person;
    boolean isDisplayed;

    public PersonBinaryTree(Person person, boolean isDisplayed) {
        this.person = person;
        this.isDisplayed = isDisplayed;
    }



    public PersonBinaryTree getLeft() {
        return left;
    }

    public PersonBinaryTree setLeft(PersonBinaryTree left) {
        this.left = left;
        return this.left;
    }

    public PersonBinaryTree getRight() {
        return right;
    }

    public PersonBinaryTree setRight(PersonBinaryTree right) {
        this.right = right;
        return this.right;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public boolean isDisplayed() {
        return isDisplayed;
    }

    public void setDisplayed(boolean displayed) {
        isDisplayed = displayed;
    }

    public PersonBinaryTree findSpouseOfPersonFromID(PersonBinaryTree tree, String currPersonID) {
        //assumes we are in the root
        if (tree == null || tree.getPerson().getPersonID().equals(currPersonID)) {
            return null;
        }
        if (tree.getLeft() != null && tree.getLeft().getPerson().getPersonID().equals(currPersonID)) {
            System.out.println("Found spouse! " + tree.getRight().getPerson().getPersonID());
            return tree.getRight();
        }
        if (tree.getRight() != null && tree.getRight().getPerson().getPersonID().equals(currPersonID)) {
            System.out.println("Found spouse! " + tree.getLeft().getPerson().getPersonID());
            return tree.getLeft();
        }
        PersonBinaryTree temp = findSpouseOfPersonFromID(tree.getLeft(), currPersonID);
        if (temp != null) {
            return temp;
        }
        temp = findSpouseOfPersonFromID(tree.getRight(), currPersonID);
        return temp;

    }

    public PersonBinaryTree findChildFromParentID(String currPersonID, PersonBinaryTree tree) {
        if (tree == null || tree.left == null || tree.right == null) {
            return null;
        }
        if (tree.getLeft().getPerson().getPersonID().equals(currPersonID)) {
            return tree;
        }
        if (tree.getRight().getPerson().getPersonID().equals(currPersonID)) {
            return tree;
        }

        PersonBinaryTree dadTree = tree.getLeft().findChildFromParentID(currPersonID, tree.getLeft());
        PersonBinaryTree momTree = tree.getRight().findChildFromParentID(currPersonID, tree.getRight());
        if (dadTree != null) return dadTree;
        return momTree;
    }

    public PersonBinaryTree getSubtreeGivenID(String personID, PersonBinaryTree tree) {
        if (tree == null) return null;
        if (tree.getPerson().getPersonID().equals(personID)) {
            return tree;
        }
        if (tree.getLeft() != null && tree.getLeft().getPerson().getPersonID().equals(personID)) {
            return tree.getLeft();
        }
        if (tree.getRight() != null && tree.getRight().getPerson().getPersonID().equals(personID)) {
            return tree.getRight();
        }
        PersonBinaryTree temp = getSubtreeGivenID(personID, tree.getLeft());
        if (temp != null) {
            return temp;
        }
        temp = getSubtreeGivenID(personID, tree.getRight());
        return temp;
    }

    public PersonBinaryTree findNodeFromID(String personID, PersonBinaryTree tree) {
        if (tree.getPerson().getPersonID().equals(personID)) {
            return tree;
        }
        if (tree.left == null || tree.right == null) {
            return null;
        }
        PersonBinaryTree temp = findNodeFromID(personID, tree.getLeft());
        if (temp != null) {
            return temp;
        }
        temp = findNodeFromID(personID, tree.getRight());
        return temp;
    }

    public ArrayList<Person> getAllDisplayed(PersonBinaryTree personTree) {
        ArrayList<Person> output = new ArrayList<>();
        getAllDisplayedRecursive(this, output);
        return output;
    }

    private void getAllDisplayedRecursive(PersonBinaryTree tree, ArrayList<Person> output) {
        if (tree == null || tree.getPerson() == null) {
            return;
        } else {
            getAllDisplayedRecursive(tree.left, output);
            if (tree.isDisplayed()) {
                output.add(tree.getPerson());
            }
            getAllDisplayedRecursive(tree.right, output);
        }
    }


    public ArrayList<Person> getAll(PersonBinaryTree personTree) {
        ArrayList<Person> output = new ArrayList<>();
        getAllRecursive(this, output);
        return output;
    }

    private void getAllRecursive(PersonBinaryTree tree, ArrayList<Person> output) {
        if (tree == null || tree.getPerson() == null) {
            return;
        } else {
            getAllRecursive(tree.left, output);
            output.add(tree.getPerson());
            getAllRecursive(tree.right, output);
        }
    }

    public PersonPair getRelatives(Person activePerson, PersonBinaryTree personTree) {
        //returns relatives found in order: father, mother, spouse, child
        //The second arraylist is just the PersonCard version of the first

        ArrayList<PersonCard> personCards = new ArrayList<>();
        ArrayList<Person> tempPersons = new ArrayList<>();
        PersonBinaryTree activeTree = personTree.findNodeFromID(activePerson.getPersonID(), personTree);
        //father
        PersonBinaryTree fatherTree = activeTree.getLeft();
        //mother
        PersonBinaryTree motherTree = activeTree.getRight();
        //spouse
        PersonBinaryTree spouseTree = activeTree.findSpouseOfPersonFromID(personTree, activePerson.getPersonID());
        //children
        PersonBinaryTree childTree = activeTree.findChildFromParentID(activePerson.getPersonID(), personTree);

        if (fatherTree != null) {
            personCards.add(new PersonCard(fatherTree.getPerson().getFirstName(), fatherTree.getPerson().getLastName(), "father", "m", fatherTree.getPerson()));
            tempPersons.add(fatherTree.getPerson());
        }
        if (motherTree != null) {
            personCards.add(new PersonCard(motherTree.getPerson().getFirstName(), motherTree.getPerson().getLastName(), "mother", "f", motherTree.getPerson()));
            tempPersons.add(motherTree.getPerson());
        }
        if (spouseTree != null) {
            personCards.add(new PersonCard(spouseTree.getPerson().getFirstName(), spouseTree.getPerson().getLastName(), "spouse", spouseTree.getPerson().getGender(), spouseTree.getPerson()));
            tempPersons.add(spouseTree.getPerson());
        }
        if (childTree != null) {
            personCards.add(new PersonCard(childTree.getPerson().getFirstName(), childTree.getPerson().getLastName(), "child", childTree.getPerson().getGender(),childTree.getPerson()));
            tempPersons.add(childTree.getPerson());
        }
        PersonPair output = new PersonPair(tempPersons, personCards);
        return output;
    }

    public void setPersonTreeNodesFalse(String nodeType, PersonBinaryTree tree) {
        //goes through tree and deletes the respective nodes based on the settings
        if (tree == null) return;
        //To do: might be able to combine father/mother since code is basically the same and starts are different
        //goes through and sets every child to false
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
            //goes through and sets every male child to false
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

    public PersonBinaryTree fillPersonBinaryTree(ArrayList<Person> persons, String firstName, String lastName) {
        Map<String, Person> personMap = new HashMap<>();

        Person user = null;
        String fatherID;
        String motherID;

        for (Person person : persons) {
            //find original user
            if (person.getFirstName().equals(firstName) && person.getLastName().equals(lastName)) {
                user = person;
                fatherID = person.getFatherID();
                motherID = person.getMotherID();
            }
            //turn entire person list into a map!
            personMap.put(person.getPersonID(), person);
        }

        PersonBinaryTree personBinaryTree = new PersonBinaryTree(user, true);
        addParents(personBinaryTree, personMap);
        return personBinaryTree;
    }

    private void addParents(PersonBinaryTree tree, Map<String, Person> personMap) {
        //If the person doesn't have parents, don't add parents
        if (tree.getPerson().getFatherID() == null || tree.getPerson().getMotherID() == null) {
            return;
        } else {
            //Find the person object for the father/mother, add as parent nodes
            Person father = personMap.get(tree.getPerson().getFatherID());
            PersonBinaryTree fatherTree = tree.setTreeLeft(new PersonBinaryTree(father, true), tree);
            addParents(fatherTree, personMap);
            Person mother = personMap.get(tree.getPerson().getMotherID());
            PersonBinaryTree motherTree = tree.setTreeRight(new PersonBinaryTree(mother, true), tree);
            addParents(motherTree, personMap);
        }
    }

    private PersonBinaryTree setTreeRight(PersonBinaryTree personBinaryTree, PersonBinaryTree tree) {
        tree.right = personBinaryTree;
        return tree.right;
    }

    private PersonBinaryTree setTreeLeft(PersonBinaryTree personBinaryTree, PersonBinaryTree tree) {
        tree.left = personBinaryTree;
        return tree.left;
    }
}
