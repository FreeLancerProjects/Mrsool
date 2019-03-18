package com.appzone.mrsool.language;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.appzone.mrsool.preferences.Preferences;

import java.util.Locale;

public class Language_Helper {

    public static Context setLocality(Context context, String defLang) {

        Preferences preferences = Preferences.getInstance();
        String lang = preferences.getLanguage(context,defLang);
        preferences.SaveLanguage(context,lang);

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N)
        {
            return createConfiguration(context,lang);
        }else
            {
                return updateResource(context,lang);
            }
    }


    @SuppressWarnings("deprecation")
    private static Context updateResource(Context context,String lang)
    {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;

        resources.updateConfiguration(configuration,resources.getDisplayMetrics());

        return context;

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static Context createConfiguration(Context context , String lang)
    {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        return context.createConfigurationContext(configuration);
    }

}
