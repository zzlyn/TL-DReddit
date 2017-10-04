package process;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Parse comments recursively, later change to iteratively
 * 
 * @author yannan.lin
 *
 */
public class JsonPostContent {

	private ArrayList<String> comments = new ArrayList<>();

	private String jsonString;

	public JsonPostContent(String jContent) {
		jsonString = jContent;
	}

	/**
	 * for every child of the top layer JSON array (layer 1 comments), retrieve
	 * all of its comments
	 * 
	 * @param response
	 * @throws ParseException
	 */
	private void parseTopComment() throws ParseException {

		JSONArray json = (JSONArray) new JSONParser().parse(jsonString);

		JSONObject dataJson = (JSONObject) new JSONParser().parse(json.get(1).toString());

		JSONArray childrenData = (JSONArray) new JSONParser()
				.parse(((JSONObject) dataJson.get("data")).get("children").toString());

		for (int k = 0; k < childrenData.size(); k++) {
			JSONObject firstLayerComment = (JSONObject) ((JSONObject) childrenData.get(k)).get("data");
			System.out.println("Post: ");
			parseReplies(firstLayerComment);
		}
	}

	/**
	 * Recursively retrieve all comments embedded/children in this comment
	 * 
	 * @param jsonObject
	 */
	private void parseReplies(JSONObject jsonObject) {
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
			parseReplies(dataItself);
		}

	}

	/**
	 * Parse the JSON string in this instance into array of comments
	 * 
	 * @return Collection of all comments
	 */
	public void parseComments() {
		try {
			parseTopComment();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * return array of comments
	 * 
	 * @return
	 */
	public ArrayList<String> getComments() {
		return comments;
	}

}