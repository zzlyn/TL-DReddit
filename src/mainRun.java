package src;

import java.net.URI;
import java.util.ArrayList;

import com.aylien.textapi.TextAPIClient;
import com.aylien.textapi.parameters.SentimentParams;
import com.aylien.textapi.parameters.SummarizeParams;
import com.aylien.textapi.responses.Sentiment;
import com.aylien.textapi.responses.Summarize;

public class mainRun {

	public static void main(String[] args) throws Exception {

		// specify post url
		String postUrl = "https://www.reddit.com/r/DFO/comments/6rpb5i/an_accurate_summary_of_the_players_role_in_the/";

		// Initialize http get client
		PostContents httpContents = new PostContents(postUrl);

		// Send get request and get json string
		String jsonString = httpContents.sendGet();

		// Parse all post bodies in json format into array list object
		JsonContent jsonPost = new JsonContent(jsonString);

		// Process contents in the array list
		// change this to another class
		// processText(jsonPost.getComments());
		processText(jsonPost.getComments());
	}

	private static void processText(ArrayList<String> text) throws Exception {
		String stringText = String.join(". ", text);
		
		// Sentimental Analysis
		TextAPIClient client = new TextAPIClient("4601a828", "9a6eeba16455f86d493446218c494fab");
		SentimentParams.Builder builder = SentimentParams.newBuilder();
		builder.setText(stringText);
		Sentiment sentiment = client.sentiment(builder.build());
		System.out.println(sentiment);
		
		
		// Summarization
		SummarizeParams.Builder sumBuilder = SummarizeParams.newBuilder();
		sumBuilder.setText(stringText);
		sumBuilder.setTitle(" ");
		Summarize summerize = client.summarize(sumBuilder.build());
		for (String sentence : summerize.getSentences()) {
			System.out.println(sentence);
		}
	}

}
