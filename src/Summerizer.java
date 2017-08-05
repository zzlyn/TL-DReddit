package src;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Summerizer {

	private final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";

	public static void main(String[] args) throws Exception {

		Summerizer http = new Summerizer();

		System.out.println("Testing 1 - Send Http GET request");

		String jsonString = http.sendGet("https://www.reddit.com/r/DFO/comments/6rpb5i/an_accurate_summary_of_the_players_role_in_the/");

		// Analysis
		JSONArray json = (JSONArray) new JSONParser().parse(jsonString);

		JSONObject dataJson = (JSONObject) new JSONParser().parse(json.get(1).toString());

		JSONArray childrenData = (JSONArray) new JSONParser()
				.parse(((JSONObject) dataJson.get("data")).get("children").toString());

		for (int k = 0; k < childrenData.size(); k++) {
			System.out.println("Post: " + ((JSONObject) ((JSONObject)childrenData.get(k)).get("data")).get("body"));
			depackageReplies((JSONObject) ((JSONObject) childrenData.get(k)).get("replies"));
		}
	}
	
	
	//Iteratively collect all replies to this top post
	//Draw a tree picture, might want to iterative from the top post instead of first reply layer
	private static void depackageReplies(JSONObject jsonObject) {
		System.out.println(jsonObject.toString());
		
		int layers = 0;
		
		//iterative solution to print all replies
		JSONObject replies = null;
		while(jsonObject.get("replies") != ""){
			System.out.println(++layers);
		}
		
		
	}

	// HTTP GET request
	private String sendGet(String url) throws Exception {

		URL obj = new URL(url + ".json?");
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		// add request header
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print result
		System.out.println(response.toString());

		return response.toString();

	}

}