package com.techfeed.maverick.techfeed;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by maverick on 01-Dec-14.
 */
public class SavedFeedsActivity extends Activity implements GetSaveFeedsTask.GetSavedFeedsListener, AdapterView.OnItemClickListener {

    ArrayList savedFeeds = null;
    GetSaveFeedsTask getSavedFeeds = null;
    StringBuilder string_savedFeeds =  new StringBuilder();
    ListView saved_list = null;
    SavedAdapter savedAdapter = null;
    private Context mContext;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        savedFeeds = new ArrayList();
        savedAdapter = new SavedAdapter(mContext,new ArrayList());
        getSavedFeeds = new GetSaveFeedsTask(this);
        getSavedFeeds.setOnGetSavedFeedsListener(this);
        setContentView(R.layout.activity_saved_feeds);

        SharedPreferences pref = getSharedPreferences("SavedFeeds", MODE_PRIVATE);
        string_savedFeeds.append(pref.getString("Feeds",""));
        getSavedFeeds.execute(string_savedFeeds.toString());

        saved_list = (ListView) findViewById(R.id.saved_list);
        saved_list.setAdapter(savedAdapter);
        saved_list.setOnItemClickListener(this);
    }

    @Override
    public void GetSavedFeeds(ArrayList<FeedItem> feedItems) {
        if(feedItems==null)
            return;
        savedFeeds.addAll(feedItems);
        savedAdapter.updateList(savedFeeds);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        FragmentManager fragmentManager = getFragmentManager();
        Bundle args = new Bundle();
        FeedItem f = (FeedItem) adapterView.getAdapter().getItem(i);
        args.putString("detail",f.getContent());
        args.putString("title",f.getTitle());
        Fragment detailFragment = new DetailFragment(this);
        detailFragment.setArguments(args);
        fragmentManager.beginTransaction()
                .replace(R.id.saved_container, detailFragment,"saved_detail")
                .commit();
    }

//    @Override
//    public void onBackPressed() {
//        if (getFragmentManager().getBackStackEntryCount() == 0) {
//            super.onBackPressed();
//        } else {
//            invalidateOptionsMenu();
//            getFragmentManager().popBackStack();
//        }
//    }

    public class SavedAdapter extends BaseAdapter {
        private ArrayList listData;
        private LayoutInflater layoutInflater;
        private Context mContext;
        int mPosition = 0;

        public SavedAdapter(Context context, ArrayList listData) {
            this.listData = listData;
            layoutInflater = LayoutInflater.from(context);
            mContext = context;
        }

        public void updateList(ArrayList saved_list) {
            listData.clear();
            listData.addAll(saved_list);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return listData.size();
        }

        @Override
        public Object getItem(int position) {
            return listData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            mPosition = position;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.saved_json_list_item, null);
                holder = new ViewHolder();
                holder.headlineView = (TextView) convertView.findViewById(R.id.title);
                holder.reportedDateView = (TextView) convertView.findViewById(R.id.date);
                holder.imageView = (ImageView) convertView.findViewById(R.id.thumbImage);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            FeedItem newsItem = (FeedItem) getItem(position);
            holder.headlineView.setText(newsItem.getTitle());
            holder.reportedDateView.setText(newsItem.getDate());

            if(newsItem.getAttachmentUrl() == null)
                holder.imageView.setVisibility(View.GONE);
            else
                holder.imageView.setVisibility(View.VISIBLE);

            Picasso.with(mContext)
                    .load(newsItem.getAttachmentUrl())
                    .noPlaceholder()
                    .error(android.R.drawable.btn_dialog)
                    .into(holder.imageView);

           /* if (holder.imageView != null) {
                new ImageDownloaderTask(holder.imageView).execute(newsItem.getAttachmentUrl());
            }*/

            return convertView;
        }

        class ViewHolder {
            TextView headlineView;
            TextView reportedDateView;
            ImageView imageView;
        }
    }

}
