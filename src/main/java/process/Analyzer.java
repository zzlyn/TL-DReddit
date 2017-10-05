package process;

import java.util.ArrayList;
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

	/**
	 * Accepts post content in JSON and returns final analysis in text
	 * 
	 * @return analysis result
	 */
	public String analyze(String jsonString) {

		// Parse JSON string to content object, collect all comments
		JsonPostProcessor jsonProcessor = new JsonPostProcessor(jsonString);
		try {
			jsonProcessor.parsePost();
		} catch (ParseException e) {
			e.printStackTrace();
			System.out.println("Error parsing JSON String: " + jsonString);
			return parseErrorMessage;
		}

		// Process contents in the array list
		String responseText = null;
		try {
			responseText = processText(jsonProcessor.getComments());
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
	private String processText(ArrayList<String> comments) throws TextAPIException {

		// Concatenate comments into a union String
		String stringText = String.join(". ", comments);

		String resultResponse = "";

		// Sentimental Analysis
		TextAPIClient client = new TextAPIClient("4601a828", "9a6eeba16455f86d493446218c494fab");
		SentimentParams.Builder builder = SentimentParams.newBuilder();
		builder.setText(stringText);
		Sentiment sentiment = client.sentiment(builder.build());
		System.out.println(sentiment);
		resultResponse += sentiment.toString();

		// Summarization
		SummarizeParams.Builder sumBuilder = SummarizeParams.newBuilder();
		sumBuilder.setText(stringText);
		sumBuilder.setTitle(" ");
		Summarize summerize = client.summarize(sumBuilder.build());

		for (String sentence : summerize.getSentences()) {
			System.out.println(sentence);
			resultResponse += "\n" + sentence;
		}
		return resultResponse;
	}

}
