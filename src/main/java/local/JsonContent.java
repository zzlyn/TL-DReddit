package local;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Collection of all the comments/replies in the post
 * 
 * @author yannan.lin
 *
 */
public class JsonContent {

	private ArrayList<String> comments;

	public JsonContent(String jsonString) {
		comments = new ArrayList<>();

		try {
			analyseResponseString(jsonString);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * for every child of the top layer json array (layer 1 comments), retrieve
	 * all of its comments
	 * 
	 * @param response
	 * @throws ParseException
	 */
	private void analyseResponseString(String response) throws ParseException {

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

	/**
	 * Recursively retrieve all comments embedded/childed in this comment
	 * 
	 * @param jsonObject
	 */
	private void depackageReplies(JSONObject jsonObject) {
		System.out.println(jsonObject.get("body"));
		comments.add((String) jsonObject.get("body"));
		JSONObject replies = null;
		try {
			replies = (JSONObject) jsonObject.get("replies");
		} catch (ClassCastException e) {
			// this comment has no replies
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

	public ArrayList<String> getComments() {
		return comments;
	}

}