package process;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import DAO.RedditThread;

public class JsonThreadParser {

	/**
	 * Parse thread iteratively
	 * 
	 * @param jsonText
	 * @throws ParseException
	 */
	public RedditThread parseThread(String jsonText) throws ParseException {

		// Queue for traversal, result store replies + title
		Queue<JSONObject> traversalQue = new LinkedList<JSONObject>();
		ArrayList<String> comments = new ArrayList<String>();
		RedditThread result = new RedditThread();

		// Get original post title
		JSONArray jPost = (JSONArray) new JSONParser().parse(jsonText);

		// test
		String origPostTitle = (String) ((JSONObject) ((JSONObject) ((JSONArray) ((JSONObject) ((JSONObject) jPost
				.get(0)).get("data")).get("children")).get(0)).get("data")).get("title");

		result.setTitle(origPostTitle);

		// Get top comments
		JSONObject dataJsonObj = (JSONObject) new JSONParser().parse(jPost.get(1).toString());

		JSONArray childrenData = (JSONArray) new JSONParser()
				.parse(((JSONObject) dataJsonObj.get("data")).get("children").toString());

		// Process comments & replies
		for (int k = 0; k < childrenData.size(); k++) {
			JSONObject topLayerComment = (JSONObject) ((JSONObject) childrenData.get(k)).get("data");
			traversalQue.add(topLayerComment);
		}

		while (!traversalQue.isEmpty()) {

			JSONObject headData = traversalQue.remove();

			comments.add((String) headData.get("body"));

			JSONObject replies = null;

			// "Replies" field can be a JSON object or a String depending on if
			// there are any replies to this comment
			try {
				replies = (JSONObject) headData.get("replies");
			} catch (ClassCastException e) {
				continue;
			}

			if (replies != null) {

				JSONArray nestedReplies = (JSONArray) (((JSONObject) replies.get("data")).get("children"));

				for (int count = 0; count < nestedReplies.size(); count++) {

					JSONObject reply = (JSONObject) nestedReplies.get(count);

					JSONObject replyData = (JSONObject) reply.get("data");

					traversalQue.add(replyData);
				}

			}
		}

		result.setComments(comments);

		return result;

	}

}
