package com.appzone.mrsool.activities_fragments.activity_sign_in.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.appzone.mrsool.R;
import com.appzone.mrsool.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.appzone.mrsool.activities_fragments.activity_sign_in.fragments.Fragment_Chooser_Login;
import com.appzone.mrsool.activities_fragments.activity_sign_in.fragments.Fragment_Complete_Profile;
import com.appzone.mrsool.activities_fragments.activity_sign_in.fragments.Fragment_Language;
import com.appzone.mrsool.activities_fragments.activity_sign_in.fragments.Fragment_Phone;
import com.appzone.mrsool.activities_fragments.terms_conditions.TermsConditionsActivity;
import com.appzone.mrsool.language.Language_Helper;
import com.appzone.mrsool.preferences.Preferences;

import java.util.Locale;

import io.paperdb.Paper;

public class SignInActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private Fragment_Language fragment_language;
    private Fragment_Chooser_Login fragment_chooser_login;
    private Fragment_Phone fragment_phone;
    private Fragment_Complete_Profile fragment_complete_profile;
    private Preferences preferences;
    private String phone = "";
    private String current_lang;

    @Override
    protected void attachBaseContext(Context base) {
        Paper.init(base);
        current_lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        super.attachBaseContext(Language_Helper.setLocality(base,current_lang));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        fragmentManager = getSupportFragmentManager();
        preferences = Preferences.getInstance();
        setUpFragment();
    }

    private void setUpFragment()
    {
        int state = preferences.getFragmentState(this);
        switch (state)
        {
            case 0:
                DisplayFragmentLanguage();
                break;
            case 1:
                DisplayFragmentChooserLogin();
                break;

        }
    }
    public void DisplayFragmentLanguage()
    {
        preferences.saveLoginFragmentState(this,0);

        if (fragment_language == null)
        {
            fragment_language = Fragment_Language.newInstance();
        }
        fragmentManager.beginTransaction().add(R.id.fragment_sign_in_container,fragment_language,"fragment_language").addToBackStack("fragment_language").commit();
    }
    public void DisplayFragmentChooserLogin()
    {
        this.phone = "";
        preferences.saveLoginFragmentState(this,1);

        if (fragment_chooser_login == null)
        {
            fragment_chooser_login = Fragment_Chooser_Login.newInstance();
        }


        if (fragment_chooser_login.isAdded())
        {
            fragmentManager.beginTransaction().show(fragment_chooser_login).commit();
        }else
            {
                fragmentManager.beginTransaction().add(R.id.fragment_sign_in_container,fragment_chooser_login,"fragment_chooser_login").addToBackStack("fragment_chooser_login").commit();

            }

    }
    public void DisplayFragmentPhone()
    {
        this.phone = "";

        if (fragment_chooser_login!=null&&fragment_chooser_login.isAdded())
        {
            fragmentManager.beginTransaction().hide(fragment_chooser_login).commit();
        }
        if (fragment_phone == null)
        {
            fragment_phone = Fragment_Phone.newInstance();
        }

        if (fragment_phone.isAdded())
        {
            fragmentManager.beginTransaction().show(fragment_phone).commit();
        }else
            {
                fragmentManager.beginTransaction().add(R.id.fragment_sign_in_container,fragment_phone,"fragment_phone").addToBackStack("fragment_phone").commit();

            }



    }

    public void DisplayFragmentCompleteProfile(String phone)
    {
        if (fragment_chooser_login!=null&&fragment_chooser_login.isAdded())
        {
            fragmentManager.beginTransaction().hide(fragment_chooser_login).commit();
        }
        if (fragment_phone!=null&&fragment_phone.isAdded())
        {
            fragmentManager.beginTransaction().hide(fragment_phone).commit();
        }
        if (fragment_complete_profile == null)
        {
            fragment_complete_profile = Fragment_Complete_Profile.newInstance();
        }
        if (fragment_complete_profile.isAdded())
        {
            fragmentManager.beginTransaction().show(fragment_complete_profile).commit();

        }else
            {
                fragmentManager.beginTransaction().add(R.id.fragment_sign_in_container,fragment_complete_profile,"fragment_complete_profile").addToBackStack("fragment_complete_profile").commit();

            }


    }
    public void NavigateToClientHomeActivity(boolean skip)
    {
        Intent intent = new Intent(this, ClientHomeActivity.class);
        startActivity(intent);

        if (!skip)
        {
            finish();
        }

    }
    public void NavigateToTermsActivity()
    {
        Intent intent = new Intent(this, TermsConditionsActivity.class);
        startActivity(intent);

    }
    public void RefreshActivity()
    {
        preferences.saveLoginFragmentState(this,1);
        recreate();
    }

    public void signIn(String m_phone) {

        this.phone = m_phone;

    }

    public void signUpWithImage(String m_name, String m_email, Uri uri) {

    }

    public void signUpWithoutImage(String m_name, String m_email) {

    }

    @Override
    public void onBackPressed() {
        Back();
    }

    public void Back()
    {
        if (fragment_complete_profile!=null&&fragment_complete_profile.isVisible())
        {
            DisplayFragmentPhone();
        }else if (fragment_phone!=null&&fragment_phone.isVisible())
        {
            Log.e("phone","phone");
            fragmentManager.popBackStack("fragment_phone",FragmentManager.POP_BACK_STACK_INCLUSIVE);
            DisplayFragmentChooserLogin();
        }else
            {
                finish();
            }
    }


}
