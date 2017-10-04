package webInterface;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import process.Analyzer;

/**
 * General handler for handling client requests
 * 
 * Improve this to a more generic java restful server
 * 
 * @author yannan.lin
 *
 */
public class CustomHttpHandler implements HttpHandler {

	private JsonPostRetriever jsonGetHelper = new JsonPostRetriever();
	private Analyzer postAnalyzer = new Analyzer();

	/**
	 * Handle every HTTP request from Chrome client side
	 */
	public void handle(HttpExchange httpExchange) throws IOException {

		// Get queried URL from request
		String query = httpExchange.getRequestURI().getQuery();
		HashMap<String, String> components = (HashMap<String, String>) queryToMap(query);
		String postUrl = components.get("postUrl");

		// Retrieve requested post in JSON string format
		String jsonString = jsonGetHelper.getJsonString(postUrl);

		// Analyze requested content and build a response
		String response = postAnalyzer.analyze(jsonString);

		httpExchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

		if (httpExchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
			httpExchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
			httpExchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
			httpExchange.sendResponseHeaders(204, -1);
			return;
		}

		// Send response back to client
		httpExchange.sendResponseHeaders(200, response.length());
		OutputStream os = httpExchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}

	/**
	 * Parse query URL to a map
	 */
	public Map<String, String> queryToMap(String query) {
		Map<String, String> result = new HashMap<String, String>();
		for (String param : query.split("&")) {
			String pair[] = param.split("=");
			if (pair.length > 1) {
				result.put(pair[0], pair[1]);
			} else {
				result.put(pair[0], "");
			}
		}
		return result;
	}
}