package com.example.familymapclient;

import android.util.Log;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import Models.Event;
import Models.Person;
import Models.User;
import RequestResult.LoginRequest;
import RequestResult.LoginResult;
import RequestResult.RegisterRequest;
import RequestResult.RegisterResult;

class Proxy {

    public FullUser getLoginRegisterData(boolean isLogin, String server, String port, LoginRequest loginRequest, RegisterRequest registerRequest) {
        try {
            //Find the right data
            String baseAddress = server + ":" + port;
            String serverAddress;
            if (isLogin) {
                serverAddress = baseAddress + "/user/login";
            } else {
                serverAddress = baseAddress + "/user/register";
            }
            //Write and send our data
            URL serverURL = new URL(serverAddress);
            HttpURLConnection connection = (HttpURLConnection) serverURL.openConnection();
            sendRequest(isLogin, connection, loginRequest, registerRequest);
            Gson gson = new Gson();
            //we got a good response
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String responseData = readDataFromResponse(connection);
                //turn the string JSON into the object we need
                if (isLogin) {
                    //ask server for person and event data, add that to viewmodel and return
                    LoginResult loginResult = (LoginResult) gson.fromJson(responseData, LoginResult.class);
                    return askServerForData(loginResult.getAuthtoken(), baseAddress, loginResult.getPersonID());

                } else {
                    //ask server for person and event data, add that to viewmodel and return
                    RegisterResult registerResult = (RegisterResult) gson.fromJson(responseData,RegisterResult.class);
                    return askServerForData(registerResult.getAuthtoken(), baseAddress, registerResult.getPersonID());
                }

            } else {
                //didn't get a 200, something was wrong about our input, do toast
                System.out.println("Connected to server, but got this code response: " + connection.getResponseCode());
                return new FullUser(null, null, null);
            }
        } catch (Exception e) {
            //something went wrong with connecting to the server
            System.out.println("Couldn't connect to server: " + e.getMessage());
        }
        return null;
    }

    private FullUser askServerForData(String authtoken, String serverAddress, String userID) {
        try {
            /*
            first part: Get event data
             */
            //Make and send an events request
            System.out.println("Using authtoken: " + authtoken + " at address " + serverAddress);
            URL serverURL = new URL(serverAddress + "/event");
            HttpURLConnection connection = (HttpURLConnection) serverURL.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty ("Authorization", authtoken);
            connection.connect();
            Gson gson = new Gson();
            ArrayList<Event> loginEventResult;
            //We got a good result
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                System.out.println("Got response for EVENTS");
                // Like before, but get inputStream
                String eventsJSON = readDataFromResponse(connection);
                loginEventResult = gson.fromJson(eventsJSON, EventArray.class).getList();
            } else {
                System.out.println("Connected, but got an error getting events for person :(");
                return new FullUser(null, null, new UserDataModel(false, null, null));
            }

            /*
            second part: Get person data
             */
            //Make and send a Persons request
            String literalPersonURL = serverAddress + "/person";
            URL personURL = new URL(literalPersonURL);
            System.out.println("making connection to " + literalPersonURL);
            HttpURLConnection newConnection = (HttpURLConnection) personURL.openConnection();
            newConnection.setRequestMethod("GET");
            newConnection.setRequestProperty ("Authorization", authtoken);
            newConnection.connect();
            //got a good result
            ArrayList<Person> loginPersonResult;
            if (newConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Like before, but get inputStream
                System.out.println("Got response for PERSONS");
                String personJSON = readDataFromResponse(newConnection);
                loginPersonResult = gson.fromJson(personJSON, PersonArray.class).getList();
            } else {
                return new FullUser(null, null, new UserDataModel(false, null, null));
            }

            /*
            Third part: Making the UserData and sending it out
             */
            //Actually before that get the user's name
            String userFirstName = null;
            String userLastName = null;
            for (Person person : loginPersonResult) {
                if (person.getPersonID().equals(userID)) {
                    userFirstName = person.getFirstName();
                    userLastName = person.getLastName();
                }
            }

            FullUser output = new FullUser(userFirstName, userLastName, new UserDataModel(true, loginEventResult, loginPersonResult));
            return output;
        } catch (Exception e) {
            //something went wrong with connecting to the server
            System.out.println(e.getMessage());

        }
        return null;
    }


    private String readDataFromResponse(HttpURLConnection connection) throws IOException {
        InputStream responseBody = connection.getInputStream();
        // Read the response bytes
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = responseBody.read(buffer)) != -1) {
            outputStream.write(buffer, 0, length);
        }
        return outputStream.toString();
    }

    private void sendRequest(boolean isLogin, HttpURLConnection connection, LoginRequest loginRequest, RegisterRequest registerRequest) throws IOException {
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        OutputStreamWriter outStreamWriter = new OutputStreamWriter(connection.getOutputStream());
        Gson gson = new Gson();
        //Add different things to request body depending on if we're logging in or registering
        if (isLogin) {
            gson.toJson(loginRequest, outStreamWriter);
        } else {
            gson.toJson(registerRequest, outStreamWriter);
        }
        //finish up writing response body
        outStreamWriter.flush();
        outStreamWriter.close();
        //send request
        connection.connect();
    }

}

