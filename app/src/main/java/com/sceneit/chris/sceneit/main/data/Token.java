package com.sceneit.chris.sceneit.main.data;

import com.google.firebase.iid.FirebaseInstanceIdService;
import com.sceneit.chris.sceneit.MainModel;

/**
 * Created by Chris on 16/03/2018.
 */

public class Token extends FirebaseInstanceIdService {

    private MainModel mainModel = MainModel.getInstance();

    public Token() {
    }

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        mainModel.updateFGM();
    }
}
