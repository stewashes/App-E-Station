package com.example.e_station;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private static final int RC_SIGN_IN = 100;
    private ValidazioneInput validazioneInput;

    private FirebaseAuth mAuth;

    private GoogleSignInClient mGoogleSignInClient;

    private static String TAG = "LoginFragment";

    private Button conferma_accedi;
    private TextView goToRegistrati;

    private TextInputLayout textInputEmail;
    private TextInputLayout textInputPassword;

    private StringBuilder erroreMail;
    private StringBuilder errorePassword;

    private SharedPreferences prefs;

    private View view;

    StartActivity s;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Log.i(TAG, "OnCreateView");

        s = (StartActivity) getActivity();

        // Inflate the activity_start_twopane for this fragment
        view = inflater.inflate(R.layout.login_fragment, container, false);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("50288546273-q4meiotkrnueondjfp17e67attge7afi.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        erroreMail = new StringBuilder();
        errorePassword = new StringBuilder();

        initUI(view);

        return view;
    }

    private void initUI(View view) {
        textInputEmail = (TextInputLayout) view.findViewById(R.id.ti_email);
        textInputPassword = (TextInputLayout) view.findViewById(R.id.ti_password);
        conferma_accedi = (Button) view.findViewById(R.id.btn_conferma_login);

        Configuration configuration = getActivity().getResources().getConfiguration();
        int smallScreenWi = configuration.smallestScreenWidthDp;

        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            goToRegistrati = (TextView) view.findViewById(R.id.tv_registrati);
            goToRegistrati.setOnClickListener(this);
        }

        if (orientation == Configuration.ORIENTATION_LANDSCAPE && smallScreenWi < 600) {
            goToRegistrati = (TextView) view.findViewById(R.id.tv_registrati);
            goToRegistrati.setOnClickListener(this);
        }

        conferma_accedi.setOnClickListener(this);

        SignInButton signInButton = view.findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);


        // per mettere la stringa Accedi con Google nel tasto di google perchè non si può dall xml
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText("ACCEDI CON GOOGLE");
            }
        }

        signInButton.setOnClickListener(this);
    }

    @Override
    public void onStart() {

        super.onStart();

        Log.i(TAG, "onStart");
        updateUI();
    }

    private void updateUI() {

        Log.i(TAG, "updateUI");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String email = user.getEmail();

            Log.i(TAG, "updateUI - utente presente");

            Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
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
                case (R.id.btn_conferma_login):
                    Log.i(TAG, "Cliccato Conferma");
                    confermaInput();
                    break;
                case (R.id.tv_registrati):
                    goToRegister();
                    break;
                case (R.id.sign_in_button):
                    Log.i(TAG, "Cliccato Google");
                    signIn();
                    break;
            }

        } else {
            Log.i(TAG, "Non connesso");
            Toast.makeText(getContext(), "Non connesso ad internet", Toast.LENGTH_LONG).show();
        }

    }

    private void signIn() {
        Log.i(TAG, "Sign In");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }

                        // ...
                    }
                });
    }

    private void confermaInput() {

        String email = textInputEmail.getEditText().getText().toString().trim();
        String password = textInputPassword.getEditText().getText().toString().trim();

        erroreMail.delete(0, erroreMail.length());
        errorePassword.delete(0, errorePassword.length());

        textInputEmail.setError(erroreMail);
        textInputPassword.setError(errorePassword);

        validazioneInput = new ValidazioneInput(email, password);
        boolean bEmail = validazioneInput.validazioneEmail(erroreMail);
        boolean bPassword = validazioneInput.validazionePassword(errorePassword);

        if (!bEmail | !bPassword) {
            textInputEmail.setError(erroreMail);
            textInputPassword.setError(errorePassword);
            // return;
        } else {
            loginUser(email, password);
        }
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i(TAG, "signInWithEmail:success");
                            Toast.makeText(getActivity(), "Authentication success", Toast.LENGTH_SHORT).show();

                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.i(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                            updateUI();
                        }

                        // ...
                    }
                });
    }


    private void goToRegister() {

        Log.i(TAG, "OnClickRegistrati");

        FragmentManager f = getActivity().getSupportFragmentManager();
        f.beginTransaction().replace(R.id.fragment, s.RegisterFragment).commit();

        s.id = 0;

    }

    @Override
    public void onPause() {

        Log.i(TAG, "OnPause");

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("EmailLog", textInputEmail.getEditText().getText().toString());
        editor.putString("PasswordLog", textInputPassword.getEditText().getText().toString());
        editor.commit();

        super.onPause();
    }

    @Override
    public void onResume() {

        Log.i(TAG, "OnResume");

        textInputEmail.getEditText().setText(prefs.getString("EmailLog", ""));
        textInputPassword.getEditText().setText(prefs.getString("PasswordLog", ""));
        super.onResume();
    }
}
