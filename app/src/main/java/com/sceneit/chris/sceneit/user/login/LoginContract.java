package com.sceneit.chris.sceneit.user.login;

import com.sceneit.chris.sceneit.user.UserContract;

/**
 * Created by Chris on 24/02/2018.
 */

public interface LoginContract {

    interface ILoginPresenter extends UserContract.IUserPresenter {
        int getReqReadContacts();

        String[] getDummyCredentials();

        void registerUser();

        void finish();
    }

    interface ILoginView {

    }
}
