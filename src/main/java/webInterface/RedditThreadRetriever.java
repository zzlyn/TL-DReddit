package webInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Get Reddit thread in JSON String format
 */
public class RedditThreadRetriever {

	// User agent for HTTP Client
	private String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";

	/**
	 * Send HTTP get request to Reddit thread URL, expect to get JSON formatted
	 * string in return
	 * 
	 * @return JSON formatted string for entire thread
	 */
	public String getThreadJson(String baseUrl) throws IOException {

		String jsonUrl = baseUrl + ".json?";

		System.out.println("~~~~Sending 'GET' request to URL : " + jsonUrl);

		// Establish connection
		URL url = new URL(jsonUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		// Configurations
		connection.setRequestMethod("GET");
		connection.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = connection.getResponseCode();

		System.out.println("~~~~Response Code : " + responseCode);

		// Reads & parses all text received
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		return response.toString();

	}
}
