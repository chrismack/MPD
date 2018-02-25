package com.sceneit.chris.sceneit.user.register;

import android.content.Intent;

import com.sceneit.chris.sceneit.user.UserPresenter;
import com.sceneit.chris.sceneit.user.login.LoginView;

/**
 * Created by Chris on 24/02/2018.
 */

public class RegisterPresenter implements RegisterContract.IRegisterPresenter {

    private RegisterView view;
    private RegisterModel model;
    private UserPresenter userPresenter;

    public RegisterPresenter(RegisterView view) {
        this.view = view;
        this.model = new RegisterModel();
        this.userPresenter = new UserPresenter();
    }

    @Override
    public boolean isPasswordSetValid(String password, String confirmation) {
        return this.model.passwordsMatch(password, confirmation);
    }

    @Override
    public void finish() {
        Intent intent = new Intent(this.view.getApplicationContext(), LoginView.class);
        this.view.startActivity(intent);
    }

    @Override
    public boolean isEmailValid(String email) {
        return this.userPresenter.isEmailValid(email);
    }

    @Override
    public boolean isPasswordValid(String password) {
        return this.userPresenter.isPasswordValid(password);
    }
}
