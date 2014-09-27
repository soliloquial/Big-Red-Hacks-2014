import java.io.*;
import java.net.*;
import java.util.regex.*;

public class ImageRecognize {
    private static Pattern httpPattern =
	Pattern.compile("style=\"font-style:italic\">(.+)</a>");

    private static String httpGetUrl(URL url) throws IOException {
	HttpURLConnection conn;
	BufferedReader rd;
	String line;
	String result = "";

	conn = (HttpURLConnection) url.openConnection();
	conn.setRequestMethod("GET");
	conn.setFollowRedirects(true);
	rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	while ((line = rd.readLine()) != null) {
	}
	rd.close();

	String s = conn.getHeaderField("Location");
	URL loc = new URL(s);
	conn = (HttpURLConnection) loc.openConnection();
	conn.setRequestMethod("GET");
	conn.setFollowRedirects(false);
	rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	while ((line = rd.readLine()) != null) {
	    result += line;
	}
	rd.close();

	System.err.println("" + conn.getResponseCode());
	System.out.println(result);
	
	return result;
    }
	
    public static String recognize(URL imageUrl) throws IOException {
	URL interpolatedUrl = new URL("http://images.google.com/searchbyimage"
				      + "?image_url=" + imageUrl);
	System.err.println(interpolatedUrl);
	String result = httpGetUrl(interpolatedUrl);
	Matcher matcher = httpPattern.matcher(result);
	int start = matcher.start(1);
	int end = matcher.end(1);
	String description = matcher.group(1);
	return description;
    }

    public static void main(String args[]) {
	try {
	    URL image = new URL("http://countryjam.com/files/2014/09/Coke_Script_Logo.png");

	    System.out.println(recognize(image));
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
