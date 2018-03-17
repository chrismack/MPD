package com.sceneit.chris.sceneit.main.data;

import java.util.List;

/**
 * Created by Chris on 15/03/2018.
 */

public class Users {

    private String uid;
    private String name = "Anon";
    private List<String> following;
    private List<String> followers;
    private String FGM;

    public Users() {
    }

    public Users(String uid, String name, List<String> following, List<String> followers, String FGM) {
        this.uid = uid;
        this.name = name;
        this.following = following;
        this.followers = followers;
        this.FGM = FGM;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getFollowing() {
        return following;
    }

    public void setFollowing(List<String> following) {
        this.following = following;
    }

    public List<String> getFollowers() {
        return followers;
    }

    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }

    public String getFGM() {
        return FGM;
    }

    public void setFGM(String FGM) {
        this.FGM = FGM;
    }
}
