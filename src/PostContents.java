package src;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * input a reddit url, get the json version of the post
 * 
 * @author yannan.lin
 *
 */
public class PostContents {
	
	
	// User agent for http clinet
	private String USER_AGENT;

	
	// URL for json retrieve
	private String jsonUrl;

	public PostContents(String url) throws Exception {
		jsonUrl = url + ".json?";
		USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";
	}
	
	/**
	 * Send http get request to reddit post url, expect to get json formatted string in return
	 * @return Json formatted string for entire post
	 * @throws Exception
	 */
	public String sendGet() throws Exception {

		System.out.println("~~~~Testing 1 - Send Http GET request");

		URL obj = new URL(jsonUrl);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		// add request header
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();
		System.out.println("~~~~Sending 'GET' request to URL : " + jsonUrl);
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
