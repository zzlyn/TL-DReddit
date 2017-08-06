package src;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Summerizer {

	private final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";

	public static void main(String[] args) throws Exception {

		Summerizer http = new Summerizer();

		String jsonString = http.sendGet(
				"https://www.reddit.com/r/DFO/comments/6rpb5i/an_accurate_summary_of_the_players_role_in_the/");

		analyseResponseString(jsonString);

	}

	private static void analyseResponseString(String response) throws ParseException {

		System.out.println("\nAnalyzing response .......");

		// Analysis
		JSONArray json = (JSONArray) new JSONParser().parse(response);

		JSONObject dataJson = (JSONObject) new JSONParser().parse(json.get(1).toString());

		JSONArray childrenData = (JSONArray) new JSONParser()
				.parse(((JSONObject) dataJson.get("data")).get("children").toString());

		for (int k = 0; k < childrenData.size(); k++) {
			JSONObject firstLayerComment = (JSONObject) ((JSONObject) childrenData.get(k)).get("data");
			System.out.println("Post: ");
			depackageReplies(firstLayerComment);
		}
	}

	// Iteratively collect all replies to this top post
	// Draw a tree picture, might want to iterative from the top post instead of
	// first reply layer
	private static void depackageReplies(JSONObject jsonObject) {
		System.out.println(jsonObject.get("body"));
		JSONObject replies = null;
		try {
			replies = (JSONObject) jsonObject.get("replies");
		} catch (ClassCastException e) {
			System.out.println("no replies, bottom\n");
			return;
		}

		// this comment has replies
		JSONArray childrens = (JSONArray) (((JSONObject) replies.get("data")).get("children"));
		// System.out.println(childrens.size());
		for (int con = 0; con < childrens.size(); con++) {
			JSONObject dataOut = (JSONObject) childrens.get(con);
			JSONObject dataItself = (JSONObject) dataOut.get("data");
			depackageReplies(dataItself);
		}

	}

	// HTTP GET request
	private String sendGet(String url) throws Exception {
		
		System.out.println("~~~~Testing 1 - Send Http GET request");
		
		
		URL obj = new URL(url + ".json?");
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		// add request header
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();
		System.out.println("~~~~Sending 'GET' request to URL : " + url);
		System.out.println("~~~~Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print result
		// System.out.println(response.toString());

		return response.toString();

	}

}