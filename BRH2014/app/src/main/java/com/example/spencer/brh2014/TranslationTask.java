package com.example.spencer.brh2014;

import android.os.AsyncTask;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by spencer on 9/27/14.
 */
public class TranslationTask extends AsyncTask<Void, Void, List<Translation>> {
    URL imageURL;
    String langCode;

    public TranslationTask(String langCode, String imageURL) {
        try {
            this.imageURL = new URL(imageURL);
            Log.d("Translator","Translating " + imageURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        this.langCode = langCode;
    }
    @Override
    protected List<Translation> doInBackground(Void... voids) {
        try {
            return Translation.doTranslate(langCode, imageURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<Translation>();
    }
}
