package com.pstech.ramayanmanka108;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
//import org.acra.*;
//import org.acra.annotation.*;
//import org.acra.config.CoreConfigurationBuilder;
//import org.acra.config.ToastConfigurationBuilder;
//import org.acra.data.StringFormat;

/**
 * Created by pagrawal on 26-01-2018.
 */

//@AcraMailSender(mailTo = "peeyush.agrawal31@gmail.com")
public class MainApplication extends Application {


    private SharedPreferences sharedPreferences;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        // The following line triggers the initialization of ACRA
//        ACRA.init(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

//        CoreConfigurationBuilder builder = new CoreConfigurationBuilder(this);
//        builder.setBuildConfigClass(BuildConfig.class).setReportFormat(StringFormat.JSON);
//        builder.getPluginConfigurationBuilder(ToastConfigurationBuilder.class).setResText(R.string.acra_toast_text);
//        builder.getPluginConfigurationBuilder(ToastConfigurationBuilder.class).setEnabled(true);

//        ACRA.init(this, builder);

    }

    public boolean isKeepAlwaysOn() {
        return sharedPreferences.getBoolean(getResources().getString(R.string.keep_screen_alive), true);
    }
    public void setKeepAlwaysOn(boolean flag) {
        sharedPreferences.edit().putBoolean(getResources().getString(R.string.keep_screen_alive), flag).commit();
    }

    public boolean isBackgroundPlayOn() {
        return sharedPreferences.getBoolean(getResources().getString(R.string.backgroud_play), true);
    }

    public boolean isResumePlayerOn() {
        return sharedPreferences.getBoolean(getResources().getString(R.string.resume_from_last_location), true);
    }

}
