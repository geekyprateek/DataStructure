package com.techfeed.maverick.techfeed;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by maverick on 24-Nov-14.
 */
public class FeedTask extends AsyncTask<String,Double,JSONObject> {
        public static InputStream is = null;
        private boolean isRefreshing = false;
        public DefaultHttpClient httpClient = null;
        public ArrayList json_list = null;
        public FeedTaskCompletionListener mListener;
        public ProgressDialog progressBar = null;
        public Context mContext = null;
        private static final String ns = null;
        private int lastIdNo = 0;


    public interface FeedTaskCompletionListener {

            public void onFeedTaskComplete (ArrayList FeedList,int lastIdNo);

        }

        public void setOnFeedCompletionListener(MainActivity mainActivity) {
            mListener = (FeedTaskCompletionListener) mainActivity;
        }

        public FeedTask(Context mContext,int lastIdNo,boolean isRefreshing) {
            this.isRefreshing = isRefreshing;
            this.lastIdNo = lastIdNo;
            this.mContext = mContext;
        }


        @Override
        protected JSONObject doInBackground(String... urls) {
            JSONObject js = null;
            String json = null;
            String filename = "data.txt";
            File data = new File(mContext.getFilesDir()+"/"+filename);
            StringBuilder sb = new StringBuilder();

            if(!data.exists()) {

                try {
                /* create Apache HttpClient */
                    HttpClient httpclient = new DefaultHttpClient();

                /* HttpGet Method */
                    HttpGet httpGet = new HttpGet(urls[0]);


                /* optional request header */
                    httpGet.setHeader("Content-Type", "application/json");


                /* optional request header */
                    httpGet.setHeader("Accept", "application/json");

                /* Make http request call */
                    HttpResponse httpResponse = httpclient.execute(httpGet);


                    int statusCode = httpResponse.getStatusLine().getStatusCode();

                /* 200 represents HTTP OK */
                    if (statusCode ==  200) {

                    /* receive response as inputStream */
                        is = httpResponse.getEntity().getContent();

                        sb.append(convertInputStreamToString(is));

                        is.close();
                    }else if(progressBar.isShowing() && (sb.toString() == null)) {
                        progressBar.dismiss();
                        return null;
                    } //"Failed to fetch data!"

                } catch (Exception e) {
                    Log.d("naresh", e.getLocalizedMessage());
                } finally {
                    if(progressBar.isShowing() && (sb.toString() == null)) {
                        progressBar.dismiss();
                        return null;
                    }
                }

                try {
                    FileOutputStream os = mContext.openFileOutput(filename,Context.MODE_PRIVATE);
                    os.write(sb.toString().getBytes());
                    os.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    FileInputStream is = mContext.openFileInput(filename);
                    int c;
                    BufferedReader reader = null;
                    /*while((c = is.read())!=-1)
                        sb.append((char)c);*/
                    try {
                        reader = new BufferedReader(new InputStreamReader(
                                is, "iso-8859-1"), 8);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    String line = null;
                    assert reader != null;
                    try {
                        String idNo = "";
                        Boolean postsFound = false;
                        int currIdNo = 0;
                        while ((line = reader.readLine()) != null) {
                            if(!postsFound)
                                postsFound = line.contains('"' + "posts" + '"');

                            if(postsFound && line.contains('"' + "id" + '"') && line.lastIndexOf(":")!=-1) {
                                currIdNo = Integer.parseInt(line.substring(line.indexOf(":") + 2,line.lastIndexOf(",")));
                                if(lastIdNo!=0 && currIdNo <= lastIdNo) {
                                    return null;
                                } else if(lastIdNo <= currIdNo){
                                    lastIdNo = currIdNo;
                                    postsFound = false;
                                }
                            }
                            sb.append(line + "\n");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    is.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            json = sb.toString();

            try {
                js = new JSONObject(json);
            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }
            return js;
        }


   public String convertInputStreamToString(InputStream is) {

       StringBuilder sb = new StringBuilder();

       BufferedReader reader = null;
       try {
           reader = new BufferedReader(new InputStreamReader(
                   is, "iso-8859-1"), 8);
       } catch (UnsupportedEncodingException e) {
           e.printStackTrace();
       }
       String line = null;
       assert reader != null;
       try {
           String idNo = "";
           Boolean postsFound = false;
           int currIdNo = 0;
           while ((line = reader.readLine()) != null) {
               if(!postsFound)
                   postsFound = line.contains('"' + "posts" + '"');

               if(postsFound && line.contains('"' + "id" + '"') && line.lastIndexOf(":")!=-1) {
                   currIdNo = Integer.parseInt(line.substring(line.indexOf(":") + 2,line.lastIndexOf(",")));
                   if(lastIdNo!=0 && currIdNo <= lastIdNo) {
                       return null;
                   } else if(lastIdNo <= currIdNo){
                       lastIdNo = currIdNo;
                       postsFound = false;
                   }
               }
               sb.append(line + "\n");
           }
       } catch (IOException e) {
           e.printStackTrace();
       }

       return sb.toString();
   }

    @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = new ProgressDialog(mContext);
            progressBar.setCancelable(false);
            progressBar.setMessage("Feeds Downloading ...");
            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        if(!isRefreshing)
            progressBar.show();
        }

    @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
        if(jsonObject==null)
        {
            if(!isRefreshing)
                progressBar.dismiss();
            mListener.onFeedTaskComplete(null,lastIdNo);
        } else {
            parseJson(jsonObject);
            mListener.onFeedTaskComplete(json_list,lastIdNo);
        }
        }
        public void parseJson(JSONObject json) {
            try {

                // parsing json object
                if (json.getString("status").equalsIgnoreCase("ok")) {
                    JSONArray posts = json.getJSONArray("posts");
                    //JSONArray posts = json.getJSONArray("items");

                    json_list = new ArrayList();

                    for (int i = 0; i < posts.length(); i++) {
                        JSONObject post = (JSONObject) posts.getJSONObject(i);
                        FeedItem item = new FeedItem();
                        item.setTitle(post.getString("title"));
                        item.setDate(post.getString("date"));
                        item.setId(post.getString("id"));
                        item.setUrl(post.getString("url"));
                        item.setContent(post.getString("content"));
                        //item.setAttachmentUrl(post.getString("media"));
                        JSONArray attachments = post.getJSONArray("attachments");
                        if (null != attachments && attachments.length() > 0) {
                            JSONObject attachment = attachments.getJSONObject(0);
                            if (attachment != null)
                                item.setAttachmentUrl(attachment.getString("url"));
                        }

                        json_list.add(item);
                    }
                    if(!isRefreshing)
                        progressBar.dismiss();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(!isRefreshing)
                    progressBar.dismiss();
            }
        }
    }
