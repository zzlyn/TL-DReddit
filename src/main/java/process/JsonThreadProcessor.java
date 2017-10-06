package process;

import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Parse comments recursively, later change to iteratively
 */
public class JsonThreadProcessor {

	private ArrayList<String> comments = new ArrayList<>();

	private String jsonString;

	public JsonThreadProcessor(String jContent) {
		jsonString = jContent;
	}

	/**
	 * for every child of the top layer JSON array (layer 1 comments), retrieve
	 * all of its comments
	 * 
	 * @param response
	 */
	public void parsePost() throws ParseException {

		// Process original post
		JSONArray json = (JSONArray) new JSONParser().parse(jsonString);

		JSONObject dataJson = (JSONObject) new JSONParser().parse(json.get(1).toString());

		JSONArray childrenData = (JSONArray) new JSONParser()
				.parse(((JSONObject) dataJson.get("data")).get("children").toString());

		// Process comments & replies
		for (int k = 0; k < childrenData.size(); k++) {
			JSONObject firstLayerComment = (JSONObject) ((JSONObject) childrenData.get(k)).get("data");
			parseReplies(firstLayerComment);
		}
	}

	/**
	 * Recursively retrieve all comments embedded/children in this comment
	 */
	private void parseReplies(JSONObject jsonObject) {

		if (jsonObject.get("body") == null) {
			return;
		}

		comments.add((String) jsonObject.get("body"));
		JSONObject replies = null;

		try {
			replies = (JSONObject) jsonObject.get("replies");
		} catch (ClassCastException e) {
			return;
		}

		// this comment has replies
		JSONArray childrens = (JSONArray) (((JSONObject) replies.get("data")).get("children"));

		for (int con = 0; con < childrens.size(); con++) {
			JSONObject dataOut = (JSONObject) childrens.get(con);
			JSONObject dataItself = (JSONObject) dataOut.get("data");
			parseReplies(dataItself);
		}

	}

	/**
	 * return array of comments
	 */
	public ArrayList<String> getComments() {
		return comments;
	}

}