package com.sceneit.chris.sceneit.user;

/**
 * Created by Chris on 24/02/2018.
 */

public class UserPresenter implements UserContract.IUserPresenter{

    private UserModel model;

    public UserPresenter() {
        model = new UserModel();
    }

    @Override
    public boolean isEmailValid(String email) {
        return this.model.isEmailValid(email);
    }

    @Override
    public boolean isPasswordValid(String password) {
        return this.model.isPasswordValid(password);
    }
}
