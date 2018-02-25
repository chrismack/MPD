package com.sceneit.chris.sceneit.user;

/**
 * Created by Chris on 24/02/2018.
 */

public class UserModel {

    public UserModel() {
    }

    public boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    public boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 6;
    }
}
