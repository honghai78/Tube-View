package shine.tran.localtubeview.businessobjects.db;

/**
 * YouTube channels subscriptions table.
 */
public class SubscriptionsTable {

	public static final String TABLE_NAME = "Subs";
	public static final String COL_ID  = "_id";
	public static final String COL_CHANNEL_ID = "Channel_Id";
	public static final String COL_LAST_VISIT_TIME = "Last_Visit_Time";
	public static final String TABLE_SEARCH_NAME = "StringDataSearch";
	public static final String COL_STRING  = "stringData";

	public static String getCreateStatement() {
		return "CREATE TABLE " + TABLE_NAME + " (" +
				COL_ID         + " INTEGER PRIMARY KEY ASC, " +
				COL_CHANNEL_ID + " TEXT UNIQUE NOT NULL, " +
				COL_LAST_VISIT_TIME + " TIMESTAMP DEFAULT (strftime('%s', 'now')) " +
		" )";
	}
	public static String getCreateSearchDataString()
	{
		return "Create table "+ TABLE_SEARCH_NAME +" ("+COL_STRING+")";
	};

}
