package DAO;

import java.util.ArrayList;

public class RedditThread {
	private String title;
	private ArrayList<String> comments;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ArrayList<String> getComments() {
		return comments;
	}

	public void setComments(ArrayList<String> comments) {
		this.comments = comments;
	}
}
