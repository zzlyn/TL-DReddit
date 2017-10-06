package webInterface;

import java.io.IOException;
import java.io.OutputStream;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import process.Analyzer;

/**
 * General handler for handling client requests
 * 
 * Improve this to a more generic java restful server
 */
public class CustomHttpHandler implements HttpHandler {

	private PostRetriever jPostRetriever = new PostRetriever();
	private Analyzer postAnalyzer = new Analyzer();

	/**
	 * Handle every HTTP request from Chrome client side
	 */
	public void handle(HttpExchange httpExchange) throws IOException {

		// Get queried URL from request
		String query = httpExchange.getRequestURI().getQuery();
		String postUrl = getQueryComponent(query, "postUrl");

		// Retrieve requested post in JSON string format
		String postJsonString = jPostRetriever.getJsonPost(postUrl);

		// Analyze requested content and build a response
		String response = postAnalyzer.analyze(postJsonString);

		httpExchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

		// remove this? test it out
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
		System.out.println("<--data written to client-->\n" + response);
		os.close();
	}

	/**
	 * Get specific component in URL query
	 */
	public String getQueryComponent(String query, String fieldName) {

		for (String param : query.split("&")) {
			String pair[] = param.split("=");
			if (pair.length > 1) {
				if (pair[0].equals(fieldName))
					return pair[1];
			}
		}
		return null;
	}
}