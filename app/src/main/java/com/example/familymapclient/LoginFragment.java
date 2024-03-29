package com.example.familymapclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import RequestResult.LoginRequest;
import RequestResult.RegisterRequest;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment implements OnMapReadyCallback {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    EditText host;
    EditText port;
    EditText username;
    EditText password;
    EditText firstName;
    EditText lastName;
    RadioGroup gender;
    EditText email;

    Button loginButton;
    Button registerButton;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            enableDisableButtons();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private void enableDisableButtons() {
        boolean canLogin = checkForRequiredInfo(true);
        boolean canRegister = checkForRequiredInfo(false);
        if (canLogin) {
            loginButton.setEnabled(true);
        } else {
            loginButton.setEnabled(false);
        }
        if (canRegister) {
            registerButton.setEnabled(true);
        } else {
            registerButton.setEnabled(false);
        }
    }

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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // here you have the reference of your button
        host = (EditText) getView().findViewById(R.id.hostEditText);
        port = (EditText) getView().findViewById(R.id.portEditText);
        username = (EditText) getView().findViewById(R.id.usernameEditText);
        password = (EditText) getView().findViewById(R.id.passwordEditText);
        firstName = (EditText) getView().findViewById(R.id.firstNameEditText);
        lastName = (EditText) getView().findViewById(R.id.lastNameEditText);
        gender = (RadioGroup) getView().findViewById(R.id.genderGroup);
        email = (EditText) getView().findViewById(R.id.emailEditText);

        loginButton = (Button) getView().findViewById(R.id.loginButton);
        registerButton = (Button) getView().findViewById(R.id.registerButton);



        //loginButton.setEnabled(false);
        registerButton.setEnabled(false);

        host.addTextChangedListener(textWatcher);
        port.addTextChangedListener(textWatcher);
        username.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);
        firstName.addTextChangedListener(textWatcher);
        lastName.addTextChangedListener(textWatcher);
        email.addTextChangedListener(textWatcher);

        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                enableDisableButtons();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                System.out.println("Clicked the login Button");
                boolean canAttemptLogin = checkForRequiredInfo(true);

                if (canAttemptLogin) {
                    attemptConnect(true);
                } else {
                    showToastIncomplete(view, "Did you put all the required info in?");
                }
            }
        });


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
                    FullUser userData = (FullUser) message.obj;
                    if (userData != null) {
                        if (userData.getUserData().wasSuccess()) {
                            //switch to map fragment, put data somewhere safe
                            System.out.println("Got all the data! Now to do map fragment");
                            Fragment fragment = new MapFragment();

                            Bundle bundle = new Bundle();
                            bundle.putSerializable("userData", userData);
                            fragment.setArguments(bundle);

                            setDefaultPreferences();

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

    private void setDefaultPreferences() {
        SharedPreferences sharedPref = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("LIFE_STORY_LINES", true);
        editor.putBoolean("FAMILY_TREE_LINES", true);
        editor.putBoolean("SPOUSE_LINES", true);
        editor.putBoolean("FATHERS_SIDE", true);
        editor.putBoolean("MOTHERS_SIDE", true);
        editor.putBoolean("MALE_EVENTS", true);
        editor.putBoolean("FEMALE_EVENTS", true);
        editor.commit();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        return view;
    }

    private boolean checkForRequiredInfo(boolean isLogin) {

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

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Marker"));

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
            //TODO: Turn this into 2 functions, one that logs in and another that gets the data
            FullUser userData = httpProxy.getLoginRegisterData(isLogin, server, port, loginRequest,registerRequest);
            //send off the data to the activity
            sendMessage(userData);
        }

        private void sendMessage(FullUser userData) {
            Message message = Message.obtain();
            //set obj parameter of message to the userdata
            message.obj = userData;
            //actually send the message
            messageHandler.sendMessage(message);
        }
    }
}