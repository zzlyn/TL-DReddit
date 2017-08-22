package src;

import java.util.ArrayList;

import com.aylien.textapi.TextAPIClient;
import com.aylien.textapi.parameters.SentimentParams;
import com.aylien.textapi.parameters.SummarizeParams;
import com.aylien.textapi.responses.Sentiment;
import com.aylien.textapi.responses.Summarize;

public class Analyzer {

	public static String urlListen(String postUrl) throws Exception {

		// Initialize http get client
		PostContents httpContents = new PostContents(postUrl);

		// Send get request and get json string
		String jsonString = httpContents.sendGet();

		// Parse all post bodies in json format into array list object
		JsonContent jsonPost = new JsonContent(jsonString);

		// Process contents in the array list
		// change this to another class
		// processText(jsonPost.getComments());
		String responseText = processText(jsonPost.getComments());

		System.out.println(responseText);
		
		return responseText;
	}

	private static String processText(ArrayList<String> text) throws Exception {
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
