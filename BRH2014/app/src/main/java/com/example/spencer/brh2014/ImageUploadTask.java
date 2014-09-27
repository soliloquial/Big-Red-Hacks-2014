package com.example.spencer.brh2014;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.Scanner;
import org.json.JSONObject;

/**
 * Created by spencer on 9/27/14.
 */
public abstract class ImageUploadTask extends AsyncTask<Void, Void, String> {
    private static final String UPLOAD_URL = "https://api.imgur.com/3/image";
    private static final String CLIENT_ID = "1a36c25c8e4c8dc";
    private HttpURLConnection conn;
    private byte[] pictureBytes;

    public ImageUploadTask(byte[] bytes) {
        this.pictureBytes = bytes;
    }

    private String doUpload() {
        try {
            conn = (HttpURLConnection) new URL(UPLOAD_URL).openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Client-ID " + CLIENT_ID);

            OutputStream out = null;
            out = conn.getOutputStream();
            out.write(pictureBytes);
            out.flush();
            out.close();

            InputStream responseIn;

            Log.d("CODE", ""+conn.getResponseCode());
            Log.d("MESSAGE", conn.getResponseMessage());


            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                responseIn = conn.getInputStream();
                return "http://i.imgur.com/" + onInput(responseIn) + ".jpg";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "http://upload.wikimedia.org/wikipedia/commons/thumb/c/ce/Coca-Cola_logo.svg/800px-Coca-Cola_logo.svg.png";
    }

    protected String onInput(InputStream in) throws Exception {
        StringBuilder sb = new StringBuilder();
        Scanner scanner = new Scanner(in);
        while (scanner.hasNext()) {
            sb.append(scanner.next());
        }

        JSONObject root = new JSONObject(sb.toString());
        String id = root.getJSONObject("data").getString("id");
        //String deletehash = root.getJSONObject("data").getString("deletehash");

        return id;
    }

    @Override
    protected String doInBackground(Void... voids) {
        return doUpload();
    }

    @Override
    abstract protected void onPostExecute(String s);
}
