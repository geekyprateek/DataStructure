package com.techfeed.maverick.techfeed;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity
        implements AbsListView.OnScrollListener,SwipeRefreshLayout.OnRefreshListener,
            NavigationDrawerFragment.NavigationDrawerCallbacks,FeedTask.FeedTaskCompletionListener,
                AdapterView.OnItemClickListener, MenuItem.OnActionExpandListener,
                    android.support.v7.widget.SearchView.OnQueryTextListener,
                        JSONAdapter.FavSaveListener,SaveFeedsTask.SaveFeedsListener {

    //URL to get JSON Array
    private static String url = "http://javatechig.com/api/get_category_posts?dev=1&slug=android";
    //private static String url = "http://api.flickr.com/services/feeds/photos_public.gne";
    //private static String url = "http://blog.stackoverflow.com/feed";
    //JSON Node Names
    private static final String TAG_USER = "user";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_EMAIL = "email";
    public static JSONAdapter jsonAdapter = null;
    public ArrayList jsonList = null;
    public ArrayList favList = null;
    public android.support.v7.widget.SearchView searchView = null;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private Context mContext;
    public FeedTask feedTask = null;
    public SwipeRefreshLayout swipe = null;
    private int lastIdNo = 0;
    private boolean isRefreshing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mContext = this;
        jsonList = new ArrayList();
        favList = new ArrayList();
        jsonAdapter = new JSONAdapter(mContext,new ArrayList());
        jsonAdapter.setFavSaveListener(this);

        Log.v("naresh", "OnCreate");
                setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments

        if(position==3){
           /* jsonAdapter.updateList(favList);
            jsonAdapter.notifyDataSetChanged();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, PlaceholderFragment.newInstance(position + 1), "main_savedFeeds")
                    .commit();*/
            startActivity(new Intent(this,SavedFeedsActivity.class));

        } else {
            jsonAdapter.updateList(jsonList);
            jsonAdapter.notifyDataSetChanged();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, PlaceholderFragment.newInstance(position + 1), "main")
                    .commit();
        }
    }



    @Override
    public void onFavSave(int position, String id) {
        FeedItem favItem = (FeedItem)jsonList.get(position);
        if(id == favItem.getId()) {
            favItem.setIsFav(true);
            jsonAdapter.updateList(jsonList);
            favList.add(favItem);

            SaveFeedsTask saveFeeds = new SaveFeedsTask();
            saveFeeds.setSaveFeedsListener(this);
            saveFeeds.execute(favItem);
            Toast.makeText(mContext, "Feed Saved : id  =" + favItem.getId(), Toast.LENGTH_SHORT).show();
        }
    }

    public void onSectionAttached(int number) {
        String[] sectionName = getResources().getStringArray(R.array.drawer_list);
        mTitle = sectionName[number-1];
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(mTitle);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_bar)));
    }

    public void setActionBarTitleView() {
        int id = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        TextView actionBarText = (TextView)findViewById(id);
        actionBarText.setHorizontallyScrolling(true);
        actionBarText.setFocusableInTouchMode(true);
        actionBarText.requestFocus();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        Fragment tmp = getFragmentManager().findFragmentByTag("detail");
        if(mNavigationDrawerFragment.isDrawerOpen() || tmp!=null && tmp.isVisible()) {
            setActionBarTitleView();
            menu.removeItem(R.id.action_search);
            menu.removeItem(R.id.action_settings);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);

            MenuItem searchItem = menu.findItem(R.id.action_search);
            MenuItem settingItem = menu.findItem(R.id.action_settings);
            settingItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            searchView = (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(searchItem);

            searchView.setOnQueryTextListener(this);
            searchItem.setOnActionExpandListener(this);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.v("naresh","Onconfig");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("naresh","OnResume");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // Getting JSON from URL
            return true;
        } else if(id == R.id.action_search) {
//            searchView.onActionViewExpanded();
//            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            invalidateOptionsMenu();
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void onFeedTaskComplete(ArrayList feedList,int lastIdNo) {
        if(this.lastIdNo <= lastIdNo)
            this.lastIdNo = lastIdNo;

        if(feedList!=null) {
            jsonList.clear();
            jsonAdapter.updateList(feedList);
            jsonList.addAll(feedList);
            updateNoItemView(false);
        } else
            Toast.makeText(mContext,"No New Posts Found...",Toast.LENGTH_SHORT).show();

        if(isRefreshing) {
            isRefreshing = false;
            swipe.setRefreshing(false);
        }
    }


    public  void updateNoItemView(Boolean show) {
        TextView noItemView = (TextView) findViewById(R.id.noitemtext);
        if(noItemView==null)
            return;
        if(show)
            noItemView.setVisibility(View.VISIBLE);
        else
            noItemView.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        if(swipe.isRefreshing()) {
            Toast.makeText(mContext,"Refreshing...",Toast.LENGTH_SHORT).show();
            return;
        }
        FragmentManager fragmentManager = getFragmentManager();
        Bundle args = new Bundle();
        FeedItem f = (FeedItem) adapterView.getAdapter().getItem(i);
        args.putString("detail",f.getContent());
        args.putString("title",f.getTitle());
        Fragment detailFragment = new DetailFragment(this);
        detailFragment.setArguments(args);
        fragmentManager.beginTransaction()
                .replace(R.id.container, detailFragment,"detail").addToBackStack("main")
                .commit();
    }

    @Override
    public void onRefresh() {
        if(isRefreshing)
            return;
        swipe.setRefreshing(true);
        isRefreshing = true;
        feedTask = new FeedTask(mContext,lastIdNo,true);
        feedTask.setOnFeedCompletionListener((MainActivity) mContext);
        feedTask.execute(url);
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if(swipe==null)
            return;

        Fragment savedFeeds = getFragmentManager().findFragmentByTag("main_savedFeeds");

        if(firstVisibleItem == 0 && savedFeeds==null) {
            swipe.setEnabled(true);
        } else {
            swipe.setEnabled(false);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {

        if(s.isEmpty()) {
            jsonAdapter.updateList(jsonList);
            jsonAdapter.notifyDataSetChanged();
            return true;
        }

        ArrayList tmp = new ArrayList();
        tmp.clear();
        int i = 0;
        while (i<jsonList.size()) {
            String t = ((FeedItem)jsonList.get(i)).getTitle().toLowerCase();
            if(t.contains(s.toLowerCase()))
                tmp.add(jsonList.get(i));
            i++;
        }
        if(tmp!=null) {
            jsonAdapter.updateList(tmp);
            jsonAdapter.notifyDataSetChanged();
        }

        return true;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem menuItem) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
        searchView.setQuery("",true);
        return true;
    }

    @Override
    public void onFeedsSaved(String savedFeeds) {
        if (savedFeeds==null)
            return;

        StringBuilder Feeds = new StringBuilder();
        Boolean addbracket = false;

        SharedPreferences pref = getSharedPreferences("SavedFeeds",MODE_PRIVATE);
        Feeds.append(pref.getString("Feeds",""));

        if(Feeds.length() == 0) {
            Feeds.append('[');
            addbracket=true;
        }

        Log.v("naresh" , "saved Feeds = "+savedFeeds);
        if (addbracket) {
            Feeds.append(savedFeeds);
        } else {
            Feeds.deleteCharAt(Feeds.length()-1);
            Feeds.append(",\n");
            Feeds.append(savedFeeds);
        }

        Feeds.append(']');

        SharedPreferences.Editor e = pref.edit();
        e.putString("Feeds", Feeds.toString());
        e.commit();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private ListView main_json_list = null;
        public SwipeRefreshLayout swipeFrag = null;
        public int sectionNo = 0;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            main_json_list = (ListView) rootView.findViewById(R.id.json_list);
            main_json_list.setAdapter(jsonAdapter);
            main_json_list.setOnItemClickListener(((MainActivity) getActivity()));
            main_json_list.setOnScrollListener(((MainActivity) getActivity()));
            main_json_list.setDividerHeight(0);

            setSwipeListLayout(rootView);

            if(jsonAdapter == null || jsonAdapter.getCount()<=0)
                ((MainActivity)getActivity()).updateNoItemView(true);
            else
                ((MainActivity)getActivity()).updateNoItemView(false);

            ((MainActivity)getActivity()).restoreActionBar();
            return rootView;
        }

        public void setSwipeListLayout(View rootView) {
            swipeFrag = (SwipeRefreshLayout) rootView;
            swipeFrag.setEnabled(false);
            swipeFrag.setColorScheme(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);
            ((MainActivity)getActivity()).swipe = (SwipeRefreshLayout) rootView;
            swipeFrag.setOnRefreshListener((MainActivity)getActivity());
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            sectionNo = getArguments().getInt(ARG_SECTION_NUMBER);
            ((MainActivity) activity).onSectionAttached(sectionNo);
            JSONAdapter tempAdapter = ((MainActivity) activity).jsonAdapter;
            if(4!=sectionNo && (tempAdapter == null || tempAdapter.getCount() <= 0)) {
                ((MainActivity) activity).feedTask = new FeedTask(activity,0,false);
                ((MainActivity) activity).feedTask.setOnFeedCompletionListener((MainActivity) activity);
                ((MainActivity) activity).lastIdNo = 0;
                ((MainActivity) activity).feedTask.execute(((MainActivity) activity).url);
            }
        }
    }
}
