package com.artioml.yandex;

import android.app.Application;

import com.yandex.metrica.YandexMetrica;

public class App extends Application {

    public static final String API_KEY = "147f2e7e-da3f-4b01-980f-5e46cfcf1fb3";

    @Override public void onCreate() {
        super.onCreate();

        YandexMetrica.activate(getApplicationContext(), API_KEY);
        YandexMetrica.enableActivityAutoTracking(this);
    }

}
