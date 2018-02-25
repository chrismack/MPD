package com.sceneit.chris.sceneit.user.login;

/**
 * Created by Chris on 24/02/2018.
 */

public class LoginModel {
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    public static int getRequestReadContacts() {
        return REQUEST_READ_CONTACTS;
    }

    public static String[] getDummyCredentials() {
        return DUMMY_CREDENTIALS;
    }

}
