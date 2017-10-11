package DAO;

public class AnalyticResponse {
	private String title;
	private String keywords;
	private String sentiments;
	private String summary;

	private StringBuilder builder = null;

	public AnalyticResponse(String title, String keywords, String sentiments, String summary) {
		builder = new StringBuilder();
		this.title = title;
		this.keywords = keywords;
		this.sentiments = sentiments;
		this.summary = summary;
	}

	public String constructResponse() {
		if (builder == null)
			return null;

		builder.append("<h3>" + title + "</h3>");
		builder.append("<h4>Sentiments: </h4>" + "<p>" + sentiments + "</p>");
		builder.append("<h4>Summary: </h4>" + "<p>" + summary + "</p>");
		builder.append("<h4>Keywords: </h4>" + "<p>" + keywords + "</p>");

		return builder.toString();

	}

}
