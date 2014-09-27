package com.example.spencer.brh2014;

import org.json.*;

import java.net.*;
import java.io.*;
import java.net.*;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by pniedzielski on 9/27/14.
 */
public class Translation {
    public String english;
    public URL imageUrl;
    public String translation;

    private static final String TRANSLATE_URL = "";

    private static URL makeTranslateUrl(String languageCode, URL imageURL) {
	return new URL(TRANSLATE_URL + "?lang=" + languageCode + "&url=" +
		       imageURL);
    }

    public static List<Translation> doTranslate(String languageCode,
						URL imageURL)
	throws Exception {

	URL url = makeTranslateUrl(languageCode, imageURL);
	HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	conn.setRequestMethod("GET");
	conn.connect();

	BufferedReader rd;
	String line;
	String result = "";
	rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	while ((line = rd.readLine()) != null) {
	    result += line;
	}
	rd.close();

	List<Translation> list = new ArrayList<Translation>();
        JSONArray translations = new JSONArray(result);
	for (int i = 0; i < translations.length(); ++i) {
	    JSONObject o = translations.getJSONObject(i);
	    Translation t = new Translation();
	    t.english = o.getString("original");
	    t.translation = o.getString("translation");
	    t.imageUrl = new URL(o.getString("imageUrl"));
	    list.add(t);
	}	
	return list;
    }
}
