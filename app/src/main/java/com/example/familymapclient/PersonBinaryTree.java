package com.example.familymapclient;

import java.io.Serializable;
import java.util.ArrayList;

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

    private PersonBinaryTree findChildFromParentID(String currPersonID) {
        if (left == null || right == null) {
            return null;
        }
        if (left.getPerson().getPersonID().equals(currPersonID)) {
            return this;
        }
        if (right.getPerson().getPersonID().equals(currPersonID)) {
            return this;
        }

        PersonBinaryTree dadTree = left.findChildFromParentID(currPersonID);
        PersonBinaryTree momTree = right.findChildFromParentID(currPersonID);
        if (dadTree != null) return dadTree;
        if (momTree != null) return momTree;
        return null;
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
        getAllRecursive(this, output);
        return output;
    }

    private void getAllRecursive(PersonBinaryTree tree, ArrayList<Person> output) {
        if (tree == null || tree.getPerson() == null) {
            return;
        } else {
            getAllRecursive(tree.left, output);
            if (tree.isDisplayed()) {
                output.add(tree.getPerson());
            }
            getAllRecursive(tree.right, output);
        }
    }


}
