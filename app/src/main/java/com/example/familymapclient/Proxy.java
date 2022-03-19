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
                System.out.println("message: " + responseData);

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
            URL serverURL = new URL(serverAddress + "/event");
            HttpURLConnection connection = (HttpURLConnection) serverURL.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty ("Authorization", authtoken);
            connection.connect();
            //We got a good result
            Gson gson = new Gson();
            Event[] loginEventResult;
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Like before, but get inputStream
                String eventsJSON = getJsonFromResponse(connection);
                loginEventResult = (Event[]) gson.fromJson(eventsJSON, Event[].class);
            } else {throw new Exception("Error getting events"); }

            /*
            second part: Get person data
             */
            //Make and send a Persons request
            HttpURLConnection newConnection = (HttpURLConnection) serverURL.openConnection();
            newConnection.setRequestMethod("POST");
            newConnection.setRequestProperty ("Authorization", authtoken);
            newConnection.connect();
            //got a good result
            Person[] loginPersonResult;
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Like before, but get inputStream
                String personJSON = getJsonFromResponse(connection);
                loginPersonResult = (Person[]) gson.fromJson(personJSON, Person[].class);
            } else {throw new Exception("Error getting persons"); }

            /*
            Third part: Making the UserData and sending it out
             */
            UserDataModel output = new UserDataModel(true, loginEventResult, loginPersonResult);
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

