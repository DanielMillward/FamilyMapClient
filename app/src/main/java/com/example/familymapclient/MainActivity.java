package com.example.familymapclient;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import RequestResult.LoginRequest;
import RequestResult.RegisterRequest;

public class MainActivity extends AppCompatActivity {

    private static final String USER_DATA_KEY = "UserDataKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button loginButton = findViewById(R.id.loginButton);
        //What to do if user presses login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    // When we get a message back, this handleMessage deals with it
                    // This is called after the rest of the try block is done
                    Handler uiThreadMessageHandler = new Handler() {
                        @Override
                        public void handleMessage(Message message) {
                            super.handleMessage(message);
                            UserDataModel userData = (UserDataModel) message.obj;
                            if (userData.WasSuccess()) {
                                //switch to map fragment, put data somewhere safe
                            } else {
                                // put toast up with error
                            }
                        }
                    };
                    //set variables to pass to proxy
                    String server = "localhost";
                    String port = "8080";
                    LoginRequest loginRequest = new LoginRequest("username", "password");
                    //get data on separate thread
                    GetUserDataTask task = new GetUserDataTask(uiThreadMessageHandler,true, server, port, loginRequest, null);
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.submit(task);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });
    }


    private static class GetUserDataTask implements Runnable {

        private final Handler messageHandler;
        private final boolean isLogin;
        private final String server;
        private final String port;
        private final LoginRequest loginRequest;
        private final RegisterRequest registerRequest;

        public GetUserDataTask(Handler messageHandler, boolean isLogin, String server, String port, LoginRequest loginRequest, RegisterRequest registerRequest) {
            this.messageHandler = messageHandler;
            this.isLogin = isLogin;
            this.server = server;
            this.port = port;
            this.loginRequest = loginRequest;
            this.registerRequest = registerRequest;
        }

        @Override
        public void run() {
            Proxy httpProxy = new Proxy();
            //send off request to server
            UserDataModel userData = httpProxy.getLoginRegisterData(isLogin, server, port, loginRequest,registerRequest);
            //send off the data to the activity
            sendMessage(userData);
        }

        private void sendMessage(UserDataModel userData) {
            Message message = Message.obtain();
            //set obj parameter of message to the userdata
            message.obj = userData;
            //actually send the message
            messageHandler.sendMessage(message);
        }
    }
}