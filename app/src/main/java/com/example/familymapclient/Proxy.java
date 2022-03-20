package com.example.familymapclient;

import android.util.Log;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
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

    //private static final String LOG_TAG = "ClientProxy";

    public UserDataModel getLoginRegisterData(boolean isLogin, String server, String port, LoginRequest loginRequest, RegisterRequest registerRequest) {
        try {
            //Viewmodel has an object of events and object of persons?
            System.out.println("Calling getting data...");
            String baseAddress = server + ":" + port;
            String serverAddress;
            if (isLogin) {
                serverAddress = baseAddress + "/user/login";
            } else {
                serverAddress = baseAddress + "/user/register";
            }
            //Write our response body
            //serverAddress = "http://google.com";
            System.out.println("Server address is " + serverAddress);
            URL serverURL = new URL(serverAddress);
            HttpURLConnection connection = (HttpURLConnection) serverURL.openConnection();
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
            System.out.println("Closing resources...");
            //finish up writing response body
            outStreamWriter.flush();
            outStreamWriter.close();
            //send request
            System.out.println("About to connect....");
            connection.connect();
            System.out.println("Connected!");
            //we got a good response
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                //TODO: Put below in its own function

                // Like before, but get inputStream
                InputStream responseBody = connection.getInputStream();

                // Read the response bytes
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = responseBody.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, length);
                }
                //turn into string
                String responseData = outputStream.toString();
                System.out.println("The response to the login or register request:");
                System.out.println(responseData);
                //turn the string JSON into the object we need
                if (isLogin) {
                    //ask server for person and event data, add that to viewmodel and return
                    LoginResult loginResult = (LoginResult) gson.fromJson(responseData, LoginResult.class);
                    return askServerForData(loginResult.getAuthtoken(), baseAddress);

                } else {
                    //ask server for person and event data, add that to viewmodel and return
                    RegisterResult registerResult = (RegisterResult) gson.fromJson(responseData,RegisterResult.class);
                    return askServerForData(registerResult.getAuthtoken(), baseAddress);
                }

            } else {
                //didn't get a 200, something was wrong about our input, do toast
                System.out.println("Got a response, but not a 200: " + connection.getResponseCode());
                return new UserDataModel(false, null, null);
            }
        } catch (Exception e) {
            //something went wrong with connecting to the server
            System.out.println("Couldn't connect to the server! message below for type " + e.getClass());
            System.out.println(e.getMessage());
        }
        return null;
    }

    private UserDataModel askServerForData(String authtoken, String serverAddress) {
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
                String eventsJSON = getJsonFromResponse(connection);
                loginEventResult = gson.fromJson(eventsJSON, EventArray.class).getList();
            } else {
                System.out.println("Connected, but got an error getting events for person :(");
                return new UserDataModel(false, null,null);
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
                String personJSON = getJsonFromResponse(newConnection);
                loginPersonResult = gson.fromJson(personJSON, PersonArray.class).getList();
            } else {
                return new UserDataModel(false, null, null);
            }

            /*
            Third part: Making the UserData and sending it out
             */
            UserDataModel output = new UserDataModel(true, loginEventResult, loginPersonResult);
            return output;
        } catch (Exception e) {
            //something went wrong with connecting to the server
            System.out.println(e.getMessage());

        }
        return null;
    }

    private String getJsonFromResponse(HttpURLConnection connection) throws IOException {
        InputStream responseBody = connection.getInputStream();

        // Read the response bytes
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = responseBody.read(buffer)) != -1) {
            outputStream.write(buffer, 0, length);
        }
        //turn into string
        String responseData = outputStream.toString();
        return responseData;
    }
}

