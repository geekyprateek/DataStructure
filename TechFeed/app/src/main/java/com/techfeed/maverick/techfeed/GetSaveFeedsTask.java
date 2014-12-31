package com.techfeed.maverick.techfeed;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by maverick on 01-Dec-14.
 */
public class GetSaveFeedsTask extends AsyncTask<String,Integer,ArrayList<FeedItem>> {

    Type type = null;
    Gson gson = null;
    GetSavedFeedsListener mListener;
    ArrayList<FeedItem> mSavedFeeds = null;
    Context mContext = null;

    GetSaveFeedsTask(Context context) {
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        gson = new Gson();
        type = new TypeToken<ArrayList<FeedItem>>(){}.getType();
        mSavedFeeds = new ArrayList<FeedItem>();
    }

    public void setOnGetSavedFeedsListener(SavedFeedsActivity activity){
        mListener = (GetSavedFeedsListener)activity;
    }

    interface GetSavedFeedsListener {
        void GetSavedFeeds(ArrayList<FeedItem> feedItems);
    }

    @Override
    protected ArrayList<FeedItem> doInBackground(String...savedFeeds) {
        if (savedFeeds[0].isEmpty())
            return null;
        /*String filename = "data.txt";
        FileInputStream is = null;
        try {
            is = mContext.openFileInput(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int c;
        BufferedReader reader = null;
                    *//*while((c = is.read())!=-1)
                        sb.append((char)c);*//*
        try {
            reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String line = null;
        assert reader != null;
        try {*/

        Log.v("naresh","GETSAVED doInback :savedFeeds = "+savedFeeds[0]);

        //FeedItem ff= (FeedItem)gson.fromJson(savedFeeds[0], FeedItem.class);
        //mSavedFeeds.add(ff);
            mSavedFeeds.addAll(Arrays.asList(gson.fromJson(savedFeeds[0], FeedItem[].class)));

        /*} catch (Exception e) {
            e.printStackTrace();
        }*/
        return mSavedFeeds;
    }

    @Override
    protected void onPostExecute(ArrayList<FeedItem> feedList) {
        super.onPostExecute(feedList);
        mListener.GetSavedFeeds(feedList);
    }

}
