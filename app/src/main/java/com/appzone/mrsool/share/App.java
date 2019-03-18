package com.appzone.mrsool.share;

import android.app.Application;
import android.content.Context;

import com.appzone.mrsool.language.Language_Helper;

public class App extends Application {
    @Override
    protected void attachBaseContext(Context base) {

        super.attachBaseContext(Language_Helper.setLocality(base,"ar"));


    }
}
