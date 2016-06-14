package shine.tran.tubeview.businessobjects.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import shine.tran.tubeview.businessobjects.YouTubeChannel;

/**
 * A database (DB) that stores user subscriptions (with respect to YouTube channels).
 */
public class SubscriptionsDb extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "subs.db";


    public SubscriptionsDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SubscriptionsTable.getCreateStatement());
        db.execSQL(SubscriptionsTable.getCreateSearchDataString());
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


    public boolean subscribe(String channelId) {
        ContentValues values = new ContentValues();
        values.put(SubscriptionsTable.COL_CHANNEL_ID, channelId);
        values.put(SubscriptionsTable.COL_LAST_VISIT_TIME, System.currentTimeMillis());

        return getWritableDatabase().insert(SubscriptionsTable.TABLE_NAME, null, values) != -1;
    }

    public boolean stringSearch(String searchData) {
        ContentValues values = new ContentValues();
        values.put(SubscriptionsTable.COL_STRING, searchData);
        return getWritableDatabase().insert(SubscriptionsTable.TABLE_SEARCH_NAME, null, values) != -1;
    }

    public boolean unsubscribe(String channelId) {
        int rowsDeleted = getWritableDatabase().delete(SubscriptionsTable.TABLE_NAME,
                SubscriptionsTable.COL_CHANNEL_ID + " = ?",
                new String[]{channelId});

        return (rowsDeleted >= 0);
    }

    public boolean checkStringDataSearch (List<String> list, String string)
    {
        for(int i=0; i<list.size(); i++)
            if(list.get(i).equalsIgnoreCase(string))
                return true;
        return false;
    }

    public List<String> getStringDataSearch()  {
        ArrayList<String> stringDataSearch = new ArrayList<>();
        Cursor c = getReadableDatabase().query(SubscriptionsTable.TABLE_SEARCH_NAME, null, null, null, null, null, null);
        c.moveToFirst();
        try {
            while (!c.isAfterLast()) {
                stringDataSearch.add(c.getString(0));
                c.moveToNext();
            }
        } catch (CursorIndexOutOfBoundsException cursorIndexOutOfBoundsException) {
            Toast.makeText(null,"Read data error!", Toast.LENGTH_LONG).show();
            c.close();
        } finally {
            c.close();
        }
        return stringDataSearch;
    }

    public List<YouTubeChannel> getSubscribedChannels() throws IOException {
        ArrayList<YouTubeChannel> subsChannels = new ArrayList<>();
        Cursor cursor = getReadableDatabase().query(SubscriptionsTable.TABLE_NAME, new String[]{SubscriptionsTable.COL_CHANNEL_ID}, null, null, null, null, SubscriptionsTable.COL_ID + " ASC");

        if (cursor.moveToNext()) {
            int colChannelIdNum = cursor.getColumnIndexOrThrow(SubscriptionsTable.COL_CHANNEL_ID);
            String channelId = null;
            YouTubeChannel channel = null;

            do {
                channelId = cursor.getString(colChannelIdNum);
                channel = new YouTubeChannel();
                channel.init(channelId, true /* = user is subscribed to this channel*/);
                subsChannels.add(channel);
            } while (cursor.moveToNext());
        }

        return subsChannels;
    }


    public boolean isUserSubscribedToChannel(String channelId) throws IOException {
        List<YouTubeChannel> subbedChannels = getSubscribedChannels();

        for (YouTubeChannel subbedChannel : subbedChannels) {
            if (subbedChannel.getId().equalsIgnoreCase(channelId))
                return true;
        }

        return false;
    }


    /**
     * Updates the given channel's last visit time.
     *
     * @param channelId Channel ID
     * @return last visit time, if the update was successful;  -1 otherwise.
     */
    public long updateLastVisitTime(String channelId) {
        SQLiteDatabase db = getWritableDatabase();
        long currentTime = System.currentTimeMillis();

        ContentValues values = new ContentValues();
        values.put(SubscriptionsTable.COL_LAST_VISIT_TIME, currentTime);

        int count = db.update(
                SubscriptionsTable.TABLE_NAME,
                values,
                SubscriptionsTable.COL_CHANNEL_ID + " = ?",
                new String[]{channelId});

        return (count > 0 ? currentTime : -1);
    }


    /**
     * Returns the last time the user has visited this channel.
     *
     * @param channelId
     * @throws IOException
     * @return last visit time, if the update was successful;  -1 otherwise.
     */
    public long getLastVisitTime(String channelId) {
        Cursor cursor = getReadableDatabase().query(
                SubscriptionsTable.TABLE_NAME,
                new String[]{SubscriptionsTable.COL_LAST_VISIT_TIME},
                SubscriptionsTable.COL_CHANNEL_ID + " = ?",
                new String[]{channelId}, null, null, null);

        if (cursor.moveToNext()) {
            int colLastVisitTIme = cursor.getColumnIndexOrThrow(SubscriptionsTable.COL_LAST_VISIT_TIME);
            return cursor.getLong(colLastVisitTIme);
        }

        return -1;
    }

}
