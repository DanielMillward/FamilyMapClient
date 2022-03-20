package com.example.familymapclient;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import RequestResult.LoginRequest;
import RequestResult.RegisterRequest;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // here you have the reference of your button
        Button loginButton = (Button) view.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                System.out.println("Clicked the login Button");
                boolean canAttemptLogin = checkForRequiredInfo(true);

                if (canAttemptLogin) {
                    attemptConnect(true);
                    attemptLogin(view);
                } else {
                    showToastIncomplete(view, "Did you put all the required info in?");
                }
            }
        });

        Button registerButton = (Button) view.findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                System.out.println("Clicked the register Button");
                boolean canAttemptRegister = checkForRequiredInfo(false);

                if (canAttemptRegister) {
                    attemptConnect(false);
                } else {
                    showToastIncomplete(view, "Did you put all the required info in?");
                }
            }
        });
    }

    private void attemptConnect(boolean isLogin) {
        //try to login and switch to map view
        System.out.println("Attempting to login or register....");

        EditText host = (EditText) getView().findViewById(R.id.hostEditText);
        EditText port = (EditText) getView().findViewById(R.id.portEditText);
        EditText username = (EditText) getView().findViewById(R.id.usernameEditText);
        EditText password = (EditText) getView().findViewById(R.id.passwordEditText);
        EditText firstName = (EditText) getView().findViewById(R.id.firstNameEditText);
        EditText lastName = (EditText) getView().findViewById(R.id.lastNameEditText);
        RadioGroup gender = (RadioGroup) getView().findViewById(R.id.genderGroup);
        EditText email = (EditText) getView().findViewById(R.id.emailEditText);


        String textHost = "http://" + host.getText().toString();
        String textPort = port.getText().toString();
        String textUsername = username.getText().toString();
        String textPassword = password.getText().toString();
        String textFirstName = firstName.getText().toString();
        String textLastName = lastName.getText().toString();
        String textEmail = email.getText().toString();
        String textGender = null;
        if (gender.getCheckedRadioButtonId() == R.id.maleButton) {
            textGender = "m";
        } else {
            textGender = "f";
        }

        try {

            // When we get a message back, this handleMessage deals with it
            // This is called after the rest of the try block is done
            Handler uiThreadMessageHandler = new Handler() {
                @Override
                public void handleMessage(Message message) {
                    super.handleMessage(message);
                    UserDataModel userData = (UserDataModel) message.obj;
                    if (userData != null) {
                        if (userData.wasSuccess()) {
                            //switch to map fragment, put data somewhere safe
                            System.out.println("Got all the data! Now to do map fragment");
                            Fragment fragment = new MapFragment();

                            Bundle bundle = new Bundle();
                            bundle.putSerializable("userData", userData);
                            fragment.setArguments(bundle);

                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            transaction.replace(R.id.fragmentContainer, fragment);
                            transaction.addToBackStack(null);
                            transaction.commit();
                        } else {
                            // put toast up with error
                            System.out.println("Connected, but Failed for some reason!");
                            showToastIncomplete(getView(), "An error occured. Please contact customer support with the following error code: ISUCK");
                        }
                    } else {
                        System.out.println("userData was null!");
                        showToastIncomplete(getView(), "An error occured. Please contact customer support with the following error code: IMA NOBODY");
                    }
                }
            };
            //set variables to pass to proxy
            //TODO: in cmd type ipconfig, put one of the addresses shown there, NOT 127.0.0.1!
            // Try using 10.0.2.2
            // If can't connect, you might have to allow an exception for the port in windows defender

                    /*
                    If doesn't work, in cmd type ipconfig and look for:

                    Ethernet adapter Ethernet 2:

                   Connection-specific DNS Suffix  . :
                   Link-local IPv6 Address . . . . . : fe80::7037:a8b7:c36:f487%58
                   IPv4 Address. . . . . . . . . . . : 192.168.249.92
                            ^^^ This one ^^^^
                   Subnet Mask . . . . . . . . . . . : 255.255.255.0
                   Default Gateway . . . . . . . . . : 192.168.249.144

                    IGNORE BELOW?
                    Or if can't find that, look for:

                    Ethernet adapter vEthernet (WSL):

                   Connection-specific DNS Suffix  . :
                   Link-local IPv6 Address . . . . . : fe80::acac:3de3:c0f:c367%53
                   IPv4 Address. . . . . . . . . . . : 172.22.128.1
                            ^^^ This one ^^^
                   Subnet Mask . . . . . . . . . . . : 255.255.240.0
                   Default Gateway . . . . . . . . . :
                     */
            LoginRequest loginRequest = new LoginRequest(textUsername, textPassword);
            RegisterRequest registerRequest = new RegisterRequest(textUsername, textPassword, textEmail, textFirstName, textLastName, textGender);
            //get data on separate thread
            GetUserDataTask task;
            if (isLogin) {
                task = new GetUserDataTask(uiThreadMessageHandler,true, textHost, textPort, loginRequest, null);
            } else {
                task = new GetUserDataTask(uiThreadMessageHandler,false, textHost, textPort, null, registerRequest);
            }
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(task);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        return view;
    }

    private boolean checkForRequiredInfo(boolean isLogin) {
        EditText host = (EditText) getView().findViewById(R.id.hostEditText);
        EditText port = (EditText) getView().findViewById(R.id.portEditText);
        EditText username = (EditText) getView().findViewById(R.id.usernameEditText);
        EditText password = (EditText) getView().findViewById(R.id.passwordEditText);
        EditText firstName = (EditText) getView().findViewById(R.id.firstNameEditText);
        EditText lastName = (EditText) getView().findViewById(R.id.lastNameEditText);
        EditText email = (EditText) getView().findViewById(R.id.emailEditText);
        RadioGroup gender = (RadioGroup) getView().findViewById(R.id.genderGroup);

        boolean hasHost = host.getText().toString().equals("");
        boolean hasPort = port.getText().toString().equals("");
        boolean hasUsername = username.getText().toString().equals("");
        boolean hasPassword = password.getText().toString().equals("");
        boolean hasFirstName = firstName.getText().toString().equals("");
        boolean hasLastName = lastName.getText().toString().equals("");
        boolean hasEmail = email.getText().toString().equals("");
        int hasGender = gender.getCheckedRadioButtonId();
        if (isLogin) {
            if (!hasHost && !hasPort && !hasUsername && !hasPassword) {
                return true;
            } else {
                return false;
            }
        } else {
            if (!hasHost && !hasPort && !hasUsername && !hasPassword && !hasFirstName && !hasLastName && !hasEmail && (hasGender > 0)) {
                return true;
            } else {
                return false;
            }
        }
    }

    private void showToastIncomplete(View v, String message) {
        //show that the thing is not complete
        System.out.println("Incomplete data! showing toast....");
        Toast.makeText(getActivity(), message,
                Toast.LENGTH_LONG).show();
    }

    private void attemptLogin(View v) {


    }



    //Called when button is clicked (on a new background thread)
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
            System.out.println("Task is being run");
            Proxy httpProxy = new Proxy();
            System.out.println("Proxy made");
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