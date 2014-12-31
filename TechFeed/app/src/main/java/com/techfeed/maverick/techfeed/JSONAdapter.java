package com.techfeed.maverick.techfeed;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.lang.annotation.Target;
import java.util.ArrayList;

/**
 * Created by maverick on 23-Nov-14.
 */
public class JSONAdapter extends BaseAdapter {
        private ArrayList listData;
        private LayoutInflater layoutInflater;
        private Context mContext;
        public FavSaveListener favSave;
        int mPosition = 0;

        public JSONAdapter(Context context, ArrayList listData) {
            this.listData = listData;
            layoutInflater = LayoutInflater.from(context);
            mContext = context;
        }

        public void updateList(ArrayList json_list) {
            listData.clear();
            listData.addAll(json_list);
            notifyDataSetChanged();
        }

    public void setFavSaveListener(MainActivity mainActivity) {
        favSave = (FavSaveListener) mainActivity;
    }

    public interface FavSaveListener {
           void onFavSave(int mPosition, String id);
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
            FavHolder favHolder = new FavHolder();
            mPosition = position;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.json_list_item, null);
                holder = new ViewHolder();
                holder.headlineView = (TextView) convertView.findViewById(R.id.title);
                holder.reportedDateView = (TextView) convertView.findViewById(R.id.date);
                holder.imageView = (ImageView) convertView.findViewById(R.id.thumbImage);
                holder.fav = (ImageView) convertView.findViewById(R.id.fav);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            FeedItem newsItem = (FeedItem) getItem(position);
            holder.headlineView.setText(newsItem.getTitle());
            holder.reportedDateView.setText(newsItem.getDate());

            favHolder.position = position;
            favHolder.id = newsItem.getId();

            holder.fav.setOnClickListener(favClick);
            holder.fav.setTag(favHolder);

            if(newsItem.getIsFav()) {
                holder.fav.setImageDrawable(mContext.getResources().getDrawable(android.R.drawable.star_big_on));
            } else {
                holder.fav.setImageDrawable(mContext.getResources().getDrawable(android.R.drawable.star_big_off));
            }

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

    View.OnClickListener favClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            view.setClickable(false);
            view.setEnabled(false);
            view.setOnClickListener(null);
            FavHolder fav = new FavHolder();
            fav = (FavHolder) view.getTag();
            favSave.onFavSave(fav.position,fav.id);

        }
    };

        static class ViewHolder {
            TextView headlineView;
            TextView reportedDateView;
            ImageView imageView;
            ImageView fav;
        }
        static class FavHolder {
            int position = 0;
            String id = "";
        }
}
