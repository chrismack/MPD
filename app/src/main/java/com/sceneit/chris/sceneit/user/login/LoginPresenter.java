package com.sceneit.chris.sceneit.user.login;

import android.content.Intent;

import com.sceneit.chris.sceneit.MainModel;
import com.sceneit.chris.sceneit.main.MainActivity;
import com.sceneit.chris.sceneit.user.UserContract;
import com.sceneit.chris.sceneit.user.UserPresenter;
import com.sceneit.chris.sceneit.user.register.RegisterView;

/**
 * Created by Chris on 24/02/2018.
 */

public class LoginPresenter implements LoginContract.ILoginPresenter{

    private LoginView view;
    private LoginModel model;
    private UserPresenter userPresenter;
    private MainModel mainModel = MainModel.getInstance();

    public LoginPresenter(LoginView view) {
        this.view = view;
        this.model = new LoginModel();
        this.userPresenter = new UserPresenter();
    }

    @Override
    public int getReqReadContacts() {
        return this.model.getRequestReadContacts();
    }

    @Override
    public String[] getDummyCredentials() {
        return this.model.getDummyCredentials();
    }

    @Override
    public void registerUser() {
        Intent intent = new Intent(this.view.getApplicationContext(), RegisterView.class);
        this.view.startActivity(intent);
    }

    @Override
    public void finish() {
        mainModel.setCurrentUser(mainModel.getAuth().getCurrentUser());
        Intent intent = new Intent(this.view.getApplicationContext(), MainActivity.class);
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
