package com.example.e_station;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterFragment extends Fragment implements View.OnClickListener {

    StartActivity s;

    private ValidazioneInput validazioneInput;
    private FirebaseAuth mAuth;

    private static String TAG = "RegisterFragment";

    private Button conferma;
    private TextView goToAccedi;

    private TextInputLayout textInputEmail;
    private TextInputLayout textInputUsername;
    private TextInputLayout textInputPassword;
    private StringBuilder erroreMail;
    private StringBuilder erroreUsername;
    private StringBuilder errorePassword;

    private SharedPreferences prefs;

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.i(TAG, "OnCreateView");

        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        mAuth = FirebaseAuth.getInstance();

        erroreMail = new StringBuilder();
        erroreUsername = new StringBuilder();
        errorePassword = new StringBuilder();

        s = (StartActivity) getActivity();

        view = inflater.inflate(R.layout.register_fragment, container, false);

        initUI(view);

        return view;
    }

    private void initUI(View view) {

        textInputEmail = (TextInputLayout) view.findViewById(R.id.ti_email);
        textInputUsername = (TextInputLayout) view.findViewById(R.id.ti_username);
        textInputPassword = (TextInputLayout) view.findViewById(R.id.ti_password);
        conferma = (Button) view.findViewById(R.id.btn_conferma);

        Configuration configuration = getActivity().getResources().getConfiguration();
        int smallScreenWi = configuration.smallestScreenWidthDp;

        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            goToAccedi = (TextView) view.findViewById(R.id.tv_accedi);
            goToAccedi.setOnClickListener(this);
        }

        if (orientation == Configuration.ORIENTATION_LANDSCAPE && smallScreenWi < 600) {
            goToAccedi = (TextView) view.findViewById(R.id.tv_accedi);
            goToAccedi.setOnClickListener(this);
        }

        conferma.setOnClickListener(this);

    }

    @Override
    public void onStart() {
        super.onStart();
        updateUI();
    }

    private void updateUI() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String email = user.getEmail();

            Log.i(TAG, "updateUI - utente presente");

            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra("msg", email);
            startActivity(intent);
            getActivity().finish();
        }
    }

    @Override
    public void onClick(View v) {

        Log.i(TAG, "OnClickConferma");

        if (ConnectivityHelper.controllaConnessione(getContext())) {
            Log.i(TAG, "connesso");
            switch (v.getId()) {
                case (R.id.btn_conferma):
                    confermaInput();
                    break;
                case (R.id.tv_accedi):
                    goToLogin();
            }
        } else {
            Log.i(TAG, "Non connesso");
            Toast.makeText(getContext(), "Non connesso ad internet", Toast.LENGTH_LONG).show();
        }

    }

    public void confermaInput() {

        // Log.i(TAG, "cliccato conferma");
        String email = textInputEmail.getEditText().getText().toString().trim();
        String username = textInputUsername.getEditText().getText().toString().trim();
        String password = textInputPassword.getEditText().getText().toString().trim();

        erroreMail.delete(0, erroreMail.length());
        erroreUsername.delete(0, erroreUsername.length());
        errorePassword.delete(0, errorePassword.length());

        textInputEmail.setError(erroreMail);
        textInputUsername.setError(erroreUsername);
        textInputPassword.setError(errorePassword);

        validazioneInput = new ValidazioneInput(email, username, password);
        boolean bEmail = validazioneInput.validazioneEmail(erroreMail);
        boolean bUsername = validazioneInput.validazioneUsername(erroreUsername);
        boolean bPassword = validazioneInput.validazionePassword(errorePassword);

        if (!bEmail | !bUsername | !bPassword) {
            textInputEmail.setError(erroreMail);
            textInputUsername.setError(erroreUsername);
            textInputPassword.setError(errorePassword);
            // return;
        } else {
            createFirebaseUser(email, username, password);
        }
    }

    private void createFirebaseUser(String email, final String username, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.i(TAG, "createUserWithEmail:success");

                            setUsername(username);

                            Toast.makeText(getActivity(), "Authentication success.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI();
                        } else {
                            Log.i(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI();
                        }

                        // ...
                    }
                });
    }

    public void setUsername(final String username) {
        FirebaseUser user = mAuth.getCurrentUser();
        UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build();

        user.updateProfile(changeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.i(TAG, mAuth.getCurrentUser().getDisplayName());
                } else {
                    Log.i(TAG, "Nome non caricato");
                }
            }
        });
    }

    public void goToLogin() {

        Log.i(TAG, "OnClickLogin");

        FragmentManager f = getActivity().getSupportFragmentManager();
        f.beginTransaction().replace(R.id.fragment, s.LoginFragment).commit();

        s.id = 1;
    }

    @Override
    public void onPause() {

        Log.i(TAG, "OnPause");

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Email", textInputEmail.getEditText().getText().toString());
        editor.putString("Username", textInputUsername.getEditText().getText().toString());
        editor.putString("Password", textInputPassword.getEditText().getText().toString());
        editor.commit();

        super.onPause();
    }

    @Override
    public void onResume() {

        Log.i(TAG, "OnResume");

        textInputEmail.getEditText().setText(prefs.getString("Email", ""));
        textInputUsername.getEditText().setText(prefs.getString("Username", ""));
        textInputPassword.getEditText().setText(prefs.getString("Password", ""));
        super.onResume();
    }
}

