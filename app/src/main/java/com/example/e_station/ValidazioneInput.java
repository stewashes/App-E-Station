package com.example.e_station;

import android.util.Patterns;

public class ValidazioneInput {

    private String email;
    private String username;
    private String password;

    public ValidazioneInput(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public ValidazioneInput(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public boolean validazioneEmail(StringBuilder errore) {
        if (email.isEmpty()) {
            // Log.i("RegisterFragment", "invio stringa email");
            String err = "Il campo Email non può essere vuoto";
            errore.append(err);
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            String err = "Inserire Email valida";
            errore.append(err);
            return false;
        } else {
            return true;
        }
    }


    public boolean validazioneUsername(StringBuilder errore) {
        if (username.isEmpty()) {
            // Log.i("RegisterFragment", "invio stringa username");
            String err = "Il campo Username non può essere vuoto";
            errore.append(err);
            return false;
        } else if (username.length() > 10) {
            String err = "Il campo Username è troppo lungo";
            errore.append(err);
            return false;
        } else {
            return true;
        }
    }


    public boolean validazionePassword(StringBuilder errore) {
        if (password.isEmpty()) {
            // Log.i("RegisterFragment", "invio stringa password");
            String err = "Il campo Password non può essere vuoto";
            errore.append(err);
            return false;
        } else if (password.length() < 6 | password.length() > 15) {
            String err = "Lughezza password richiesta: 7 - 14";
            errore.append(err);
            return false;
        } else {
            return true;
        }
    }
}
