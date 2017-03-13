package shine.tran.localtubeview.businessobjects;

/**
 * A YouTube comment.
 */
public class YouTubeComment {

	private String author;
	private String comment;
	private String datePublished;
	private String likeCount;

	public YouTubeComment(com.google.api.services.youtube.model.Comment comment) {
		if (comment.getSnippet() != null) {
			this.author = comment.getSnippet().getAuthorDisplayName();
			this.comment = comment.getSnippet().getTextDisplay();
			this.datePublished = new PrettyTimeEx().format(comment.getSnippet().getPublishedAt());
			this.likeCount = comment.getSnippet().getLikeCount().toString();
		}
	}

	public String getAuthor() {
		return author;
	}

	public String getComment() {
		return comment;
	}

	public String getDatePublished() {
		return datePublished;
	}

	public String getLikeCount() {
		return likeCount;
	}

}
