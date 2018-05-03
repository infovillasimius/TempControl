package com.estimote.tempControl;

import android.app.Application;

import com.estimote.coresdk.common.config.EstimoteSDK;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;


public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        EstimoteSDK.initialize(getApplicationContext(), "tempcontrol-kxy", "f416fd75f835a9c1dfa98b8541bd723d");

        // uncomment to enable debug-level logging
        // it's usually only a good idea when troubleshooting issues with the Estimote SDK
        EstimoteSDK.enableDebugLogging(true);


    }


}
