package com.sceneit.chris.sceneit.user;

/**
 * Created by Chris on 24/02/2018.
 */

public interface UserContract {

    interface IUserPresenter {
        boolean isEmailValid(String email);

        boolean isPasswordValid(String password);
    }
}
