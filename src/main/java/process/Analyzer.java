package process;

import java.util.ArrayList;

import com.aylien.textapi.TextAPIClient;
import com.aylien.textapi.TextAPIException;
import com.aylien.textapi.parameters.SentimentParams;
import com.aylien.textapi.parameters.SummarizeParams;
import com.aylien.textapi.responses.Sentiment;
import com.aylien.textapi.responses.Summarize;

public class Analyzer {

	/**
	 * Analyze the post result in JSON
	 * 
	 * @return analysis result
	 */
	public String analyze(String jsonString) {

		// Parse JSON string to content object, collect all comments
		JsonPostContent jsonContent = new JsonPostContent(jsonString);
		jsonContent.parseComments();
		
		// Process contents in the array list
		String responseText = null;
		try {
			responseText = processText(jsonContent.getComments());
		} catch (TextAPIException e) {
			e.printStackTrace();
			System.out.println("Unexpected error while analyzing content: " + e.getMessage());
			return null;
		}

		return responseText;
	}

	/**
	 * 
	 * @param Concatenated
	 *            comments
	 * @return Sentimental analysis + summarization results
	 * @throws TextAPIException
	 */
	private String processText(ArrayList<String> text) throws TextAPIException {

		// Concatenate comments into a union String
		String stringText = String.join(". ", text);

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
