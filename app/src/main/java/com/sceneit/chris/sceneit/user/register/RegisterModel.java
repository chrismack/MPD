package com.sceneit.chris.sceneit.user.register;

/**
 * Created by Chris on 24/02/2018.
 */

public class RegisterModel {

    public RegisterModel() {
    }

    public boolean passwordsMatch(String password, String confirmation) {
        return password.equals(confirmation);
    }
}
