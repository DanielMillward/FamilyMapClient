package com.example.familymapclient;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import Models.Event;
import Models.Person;

public class TestHelper {
    public boolean clearDatabase(String serverAddress) throws IOException {
        URL serverURL = new URL(serverAddress + "/clear");
        HttpURLConnection connection = (HttpURLConnection) serverURL.openConnection();
        connection.setRequestMethod("POST");
        connection.connect();
        //Tested DB before, if we got a 200 then it should be cleared
        return connection.getResponseCode() == HttpURLConnection.HTTP_OK;
    }

    public String getDataFromFile(String fileName) {
        //Helper getting data from file function
        File file = new File(fileName);
        StringBuilder output = new StringBuilder();
        try (FileReader fileReader = new FileReader(file))
        {
            int content;
            while ((content = fileReader.read()) != -1) {
                output.append((char) content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output.toString();
    }

    public void addTestDataToDatabase(String testData, String server) throws IOException {
        //Set up connection
        String serverAddress = server + "/load";
        URL serverURL = new URL(serverAddress);
        HttpURLConnection connection = (HttpURLConnection) serverURL.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        //Write data to body
        OutputStreamWriter outStreamWriter = new OutputStreamWriter(connection.getOutputStream());
        outStreamWriter.write(testData);
        outStreamWriter.flush();
        outStreamWriter.close();
        connection.connect();
        //If not good response, throw IOException
        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("Didn't get a 200 response");
        }
    }

    public boolean comparePersonAgainstSheilaList(Person person) {
        ArrayList<String> acceptedPersonIDs = new ArrayList<>();
        acceptedPersonIDs.add("Sheila_Parker");
        acceptedPersonIDs.add("Davis_Hyer");
        acceptedPersonIDs.add("Blaine_McGary");
        acceptedPersonIDs.add("Betty_White");
        acceptedPersonIDs.add("Ken_Rodham");
        acceptedPersonIDs.add("Mrs_Rodham");
        acceptedPersonIDs.add("Frank_Jones");
        acceptedPersonIDs.add("Mrs_Jones");

        return acceptedPersonIDs.contains(person.getPersonID());
    }

    public boolean compareEventAgainstSheilaList(Event event) {
        ArrayList<String> acceptedEventIDs = new ArrayList<>();
        acceptedEventIDs.add("Sheila_Birth");
        acceptedEventIDs.add("Sheila_Marriage");
        acceptedEventIDs.add("Sheila_Asteroids");
        acceptedEventIDs.add("Other_Asteroids");
        acceptedEventIDs.add("Sheila_Death");
        acceptedEventIDs.add("Davis_Birth");
        acceptedEventIDs.add("Blaine_Birth");
        acceptedEventIDs.add("Betty_Death");
        acceptedEventIDs.add("BYU_graduation");
        acceptedEventIDs.add("Rodham_Marriage");
        acceptedEventIDs.add("Mrs_Rodham_Backflip");
        acceptedEventIDs.add("Mrs_Rodham_Java");
        acceptedEventIDs.add("Jones_Frog");
        acceptedEventIDs.add("Jones_Marriage");
        acceptedEventIDs.add("Mrs_Jones_Barbecue");
        acceptedEventIDs.add("Mrs_Jones_Surf");

        return acceptedEventIDs.contains(event.getEventID());
    }
}
