package com.appzone.mrsool.singletone;

import com.appzone.mrsool.models.UserModel;

public class UserSingleTone {

    private static UserSingleTone instance = null;
    private UserModel userModel;
    private UserSingleTone() {
    }

    public static UserSingleTone getInstance()
    {
        if (instance ==null)
        {
            instance = new UserSingleTone();
        }
        return instance;
    }

    public static void setInstance(UserSingleTone instance) {
        UserSingleTone.instance = instance;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }
    public void clear()
    {
        this.userModel=null;
    }

}
