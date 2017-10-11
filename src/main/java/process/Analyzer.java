package process;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.json.simple.parser.ParseException;
import com.aylien.textapi.TextAPIClient;
import com.aylien.textapi.TextAPIException;
import com.aylien.textapi.parameters.*;
import com.aylien.textapi.responses.*;

import DAO.AnalyticResponse;
import DAO.RedditThread;

/**
 * Analyzes JSON content by traversing the comment tree and using analysis APIs
 * 
 * Constructs & returns final analysis result
 */
public class Analyzer {

	// Responses when errors are encountered
	private String parseErrorMessage = "Error parsing post content in JSON.";
	private String analysisErrorMessage = "Error sending analysis request.";

	// Thread parser
	private JsonThreadParser jThreadParser;

	// NLP client
	private TextAPIClient client = null;

	/**
	 * Initialize API client with properties file
	 */
	public Analyzer() throws FileNotFoundException, IOException {
		Properties clientProps = new Properties();
		clientProps.load(new FileInputStream("clientAPI.properties"));
		client = new TextAPIClient(clientProps.getProperty("applicationId"), clientProps.getProperty("applicationKey"));

		jThreadParser = new JsonThreadParser();
	}

	/**
	 * Accepts post content in JSON and returns final analysis in text
	 * 
	 * @return analysis result
	 */
	public String analyze(String jsonString) {

		// Parse JSON string to content object, collect all comments
		RedditThread redThread = new RedditThread();

		try {
			redThread = jThreadParser.parseThread(jsonString);
		} catch (ParseException e) {
			e.printStackTrace();
			System.out.println("Error parsing JSON String: " + jsonString);
			return parseErrorMessage;
		}

		// Process contents in the array list
		String responseText = null;
		try {
			responseText = constructResponse(redThread);
		} catch (TextAPIException e) {
			e.printStackTrace();
			System.out.println("Unexpected error while analyzing content: " + e.getMessage());
			return analysisErrorMessage;
		}

		return responseText;
	}

	/**
	 * Process & analyze the text array collected from JSON post
	 * 
	 * @param Replies
	 * @return Sentimental analysis + summarization results
	 */
	private String constructResponse(RedditThread redThread) throws TextAPIException {

		ArrayList<String> comments = redThread.getComments();

		// Concatenate comments into a union String of sentences
		String commentText = String.join(". ", comments);

		// Key Word extraction
		String keywords = buildKeyEntities(commentText);

		// Sentimental Analysis
		String sentiments = buildSentiments(commentText);

		// Summarization
		String summary = buildSummary(commentText);

		AnalyticResponse response = new AnalyticResponse(redThread.getTitle(), keywords, sentiments, summary);

		return response.constructResponse();
	}

	// Analyze keywords
	private String buildKeyEntities(String targetText) throws TextAPIException {
		StringBuilder builder = new StringBuilder();

		builder.append("[");
		EntitiesParams.Builder epBuilder = EntitiesParams.newBuilder();
		epBuilder.setText(targetText);
		Entities entities = client.entities(epBuilder.build());
		for (Entity entity : entities.getEntities()) {
			for (String sf : entity.getSurfaceForms()) {
				builder.append(sf + ", ");
			}
		}
		builder.append("]");
		return builder.toString();
	}
	
	// Analyze sentiments
	private String buildSentiments(String targetText) throws TextAPIException {

		SentimentParams.Builder spBuilder = SentimentParams.newBuilder();
		spBuilder.setText(targetText);
		Sentiment sentiment = client.sentiment(spBuilder.build());
		return sentiment.toString();

	}

	// Analyze summary
	private String buildSummary(String targetText) throws TextAPIException {
		StringBuilder builder = new StringBuilder();

		SummarizeParams.Builder sumBuilder = SummarizeParams.newBuilder();
		sumBuilder.setText(targetText);
		sumBuilder.setTitle(" ");
		Summarize summerize = client.summarize(sumBuilder.build());
		for (String sentence : summerize.getSentences()) {
			builder.append("- " + sentence + "<br><br>");
		}

		return builder.toString();

	}

}
