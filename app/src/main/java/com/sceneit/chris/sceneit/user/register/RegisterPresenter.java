package com.sceneit.chris.sceneit.user.register;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.sceneit.chris.sceneit.MainModel;
import com.sceneit.chris.sceneit.main.MainActivity;
import com.sceneit.chris.sceneit.main.data.Users;
import com.sceneit.chris.sceneit.user.UserPresenter;
import com.sceneit.chris.sceneit.user.login.LoginView;

import java.util.ArrayList;

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
        setupUser();
    }

    @Override
    public void setupUser() {
        MainModel mainModel = MainModel.getInstance();
        Users user = new Users(mainModel.getCurrentUser().getUid(), "Anon", new ArrayList<String>(), new ArrayList<String>(), "");
        mainModel.getDataStore().collection(MainModel.USER_COLLECTION).add(user)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        Intent intent = new Intent(view.getApplicationContext(), LoginView.class);
                        view.startActivity(intent);
                    }
                });
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
