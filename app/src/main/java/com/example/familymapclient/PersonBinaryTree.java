package com.example.familymapclient;

import Models.Person;

public class PersonBinaryTree {
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
}
