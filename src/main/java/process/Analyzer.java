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

/**
 * Analyzes JSON content by traversing the comment tree and using analysis APIs
 */
public class Analyzer {

	// Responses when errors are encountered
	private String parseErrorMessage = "Error parsing post content in JSON.";
	private String analysisErrorMessage = "Error sending analysis request.";
	private String htmlSkip = "<br><br>";

	// NLP client
	private TextAPIClient client = null;

	/**
	 * Initialize API client with properties file
	 */
	public Analyzer() throws FileNotFoundException, IOException {
		Properties clientProps = new Properties();
		clientProps.load(new FileInputStream("clientAPI.properties"));
		client = new TextAPIClient(clientProps.getProperty("applicationId"), clientProps.getProperty("applicationKey"));
	}

	/**
	 * Accepts post content in JSON and returns final analysis in text
	 * 
	 * @return analysis result
	 */
	public String analyze(String jsonString) {

		// Parse JSON string to content object, collect all comments
		JsonThreadProcessor jThreadProcessor = new JsonThreadProcessor(jsonString);
		try {
			jThreadProcessor.parsePost();
		} catch (ParseException e) {
			e.printStackTrace();
			System.out.println("Error parsing JSON String: " + jsonString);
			return parseErrorMessage;
		}

		// Process contents in the array list
		String responseText = null;
		try {
			responseText = constructResponse(jThreadProcessor.getComments());
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
	private String constructResponse(ArrayList<String> comments) throws TextAPIException {

		// Concatenate comments into a union String
		String commentText = String.join(". ", comments);

		StringBuilder responseBuilder = new StringBuilder();

		// Key Word extraction
		buildKeyEntities(responseBuilder, commentText);

		// Sentimental Analysis
		buildSentiments(responseBuilder, commentText);

		// Summarization
		buildSummary(responseBuilder, commentText);

		return responseBuilder.toString();
	}

	private void buildKeyEntities(StringBuilder builder, String targetText) throws TextAPIException {
		builder.append("Key Words:" + htmlSkip + "[");
		EntitiesParams.Builder epBuilder = EntitiesParams.newBuilder();
		epBuilder.setText(targetText);
		Entities entities = client.entities(epBuilder.build());
		for (Entity entity : entities.getEntities()) {
			for (String sf : entity.getSurfaceForms()) {
				builder.append(sf + ", ");
			}
		}
		builder.append("]" + htmlSkip);
	}

	private void buildSentiments(StringBuilder builder, String targetText) throws TextAPIException {
		builder.append("Sentiments:" + htmlSkip);
		SentimentParams.Builder spBuilder = SentimentParams.newBuilder();
		spBuilder.setText(targetText);
		Sentiment sentiment = client.sentiment(spBuilder.build());
		builder.append(sentiment.toString());
		builder.append(htmlSkip);
	}

	private void buildSummary(StringBuilder builder, String targetText) throws TextAPIException {
		builder.append("Summarization:" + htmlSkip);
		SummarizeParams.Builder sumBuilder = SummarizeParams.newBuilder();
		sumBuilder.setText(targetText);
		sumBuilder.setTitle(" ");
		Summarize summerize = client.summarize(sumBuilder.build());
		for (String sentence : summerize.getSentences()) {
			builder.append("- " + sentence + htmlSkip);
		}
	}

}
