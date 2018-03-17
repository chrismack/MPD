package com.sceneit.chris.sceneit.user.register;

import com.sceneit.chris.sceneit.user.UserContract;

/**
 * Created by Chris on 24/02/2018.
 */

public interface RegisterContract {

    interface IRegisterPresenter extends UserContract.IUserPresenter {
        boolean isPasswordSetValid(String password, String confirmation);

        void finish();

        void setupUser();
    }

    interface IRegisterView {
        void attemptRegister();

        void finsih();
    }
}
