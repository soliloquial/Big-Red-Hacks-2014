package com.example.spencer.brh2014;

import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.Scanner;

/**
 * Created by spencer on 9/27/14.
 */
public class ImageUploadTask {
    private static final String UPLOAD_URL = "https://api.imgur.com/3/image";
    private static final String CLIENT_ID = "1a36c25c8e4c8dc";
    private HttpURLConnection conn;

    private String doUpload(byte[] bytes) {
        try {
            conn = (HttpURLConnection) new URL(UPLOAD_URL).openConnection();
            conn.setDoOutput(true);

            conn.setRequestProperty("Authorization", "Client-ID " + CLIENT_ID);

            OutputStream out = null;
            out = conn.getOutputStream();
            out.write(bytes);
            out.flush();
            out.close();

            InputStream responseIn;

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                responseIn = conn.getInputStream();
                return onInput(responseIn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
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
}
