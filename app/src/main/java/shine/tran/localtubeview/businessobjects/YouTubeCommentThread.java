package shine.tran.localtubeview.businessobjects;

import com.google.api.services.youtube.model.Comment;
import com.google.api.services.youtube.model.CommentThread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A Thread of comments.  A thread is made up of a top-level comments and 0 or more reply comments.
 */
public class YouTubeCommentThread {

    /**
     * Top-level comment.
     */
    private YouTubeComment comment;
    /**
     * Replies.
     */
    private List<YouTubeComment> repliesList = new ArrayList<>();

    public YouTubeCommentThread(CommentThread commentThread) {
        if (isCommentThreadOkay(commentThread)) {
            this.comment = new YouTubeComment(commentThread.getSnippet().getTopLevelComment());

            if (hasAnyReplies(commentThread)) {
                List<Comment> commentRepliesList = commentThread.getReplies().getComments();
                Collections.reverse(commentRepliesList);    // reverse as the newest comments are put at the front of the list -- so we need to invert it

                for (com.google.api.services.youtube.model.Comment comment : commentRepliesList) {
                    repliesList.add(new YouTubeComment(comment));
                }
            }
        }

    }

    private boolean isCommentThreadOkay(CommentThread commentThread) {
        return (commentThread.getSnippet() != null
                && commentThread.getSnippet().getTopLevelComment() != null);
    }


    private boolean hasAnyReplies(CommentThread commentThread) {
        return (commentThread.getReplies() != null
                && commentThread.getReplies().size() > 0);
    }


    public YouTubeComment getTopLevelComment() {
        return comment;
    }

    public List<YouTubeComment> getRepliesList() {
        return repliesList;
    }

    public int getTotalReplies() {
        return repliesList.size();
    }

}
