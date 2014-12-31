package com.techfeed.maverick.techfeed;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import com.google.gson.Gson;

/**
 * Created by maverick on 01-Dec-14.
 */
public class SaveFeedsTask extends AsyncTask<FeedItem,Integer,String> {

    private SaveFeedsListener mlistener = null;
    StringBuilder savedFeeds = new StringBuilder();
    Gson gson = null;

    interface SaveFeedsListener {
        void onFeedsSaved(String savedFeeds);
    }

    public void setSaveFeedsListener(MainActivity mainActivity) {
        mlistener = (SaveFeedsListener)mainActivity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        gson = new Gson();
    }

    @Override
    protected String doInBackground(FeedItem... feedLists) {
        if(feedLists==null)
            return "";
        String tt= "";
        tt =  gson.toJson(feedLists[0], FeedItem.class);
        Log.v("naresh"," tt= "+tt);
        savedFeeds.append(tt);

        Log.v("naresh"," doinback savedFeeds= "+savedFeeds.toString());

        return savedFeeds.toString();
    }

    @Override
    protected void onPostExecute(String feeds) {
        super.onPostExecute(feeds);
        mlistener.onFeedsSaved(feeds);
    }

}
