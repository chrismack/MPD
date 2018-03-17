package com.sceneit.chris.sceneit.user.register;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.sceneit.chris.sceneit.MainModel;
import com.sceneit.chris.sceneit.R;
import com.sceneit.chris.sceneit.user.login.LoginView;

public class RegisterView extends AppCompatActivity implements RegisterContract.IRegisterView {

    private RegisterPresenter presenter;
    private static final MainModel mainModel = MainModel.getInstance();

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;

    private boolean registerSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.reg_email);
        mPasswordView = (EditText) findViewById(R.id.reg_password);

        Button register = findViewById(R.id.user_register_button);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        this.presenter = new RegisterPresenter(this);
    }

    @Override
    public void attemptRegister() {
        final FirebaseAuth auth = mainModel.getAuth();

        String email, password;
        email = this.mEmailView.getText().toString();
        password = this.mPasswordView.getText().toString();

        this.registerSuccess = true;

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mainModel.setCurrentUser(auth.getCurrentUser());
                            presenter.finish();
                        } else {
                            registerSuccess = false;
                            Log.w("", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Error Registering email", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    @Override
    public void finsih() {
        this.presenter.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, LoginView.class);
        startActivity(intent);
    }
}
