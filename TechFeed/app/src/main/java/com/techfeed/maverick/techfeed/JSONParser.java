package com.techfeed.maverick.techfeed;

/**
 * Created by maverick on 23-Nov-14.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class JSONParser {
    static InputStream is = null;
    DefaultHttpClient httpClient = null;
    public ArrayList json_list = null;

    // constructor
    public JSONParser(Context mContext, ArrayList json_list) {
        this.json_list = json_list;
    }

    public void getJSONFromUrl(String url) {
        // Making HTTP request
/*        try {*/
        // defaultHttpClient
/*        httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        HttpResponse httpResponse = null;*/

//            HttpEntity httpEntity = httpResponse.getEntity();
//            is = httpEntity.getContent();
/*        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        // try parse the string to a JSON object
        /*try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }*/
        // return JSON String
        //return jObj;
    }
}
