package com.techfeed.maverick.techfeed;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.*;

/**
 * Created by maverick on 25-Nov-14.
 */
public class DetailFragment extends Fragment {

    private WebView webview = null;
    private String title = "";
    ActionBar ab = null;
    Context mContext = null;

    public DetailFragment(){}
    public DetailFragment(Context context){
        this.mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        container.removeAllViews();
        View rootView = inflater.inflate(R.layout.detail_fragment,container,false);
        title = getArguments().getString("title");
        ab.setTitle(title);
        initWebView(rootView);
        return rootView;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(0).setEnabled(false);
        menu.findItem(1).setEnabled(false);
    }

    public void initWebView(View rootView) {
        webview = (WebView) rootView.findViewById(R.id.webview);
        WebSettings webviewSettings = webview.getSettings();
        String detail = getArguments().getString("detail");
        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
        webviewSettings.setDefaultTextEncodingName("utf-8");
        webviewSettings.setDisplayZoomControls(true);
        webviewSettings.supportZoom();
        webviewSettings.setLoadsImagesAutomatically(true);
        //       webviewSettings.setJavaScriptEnabled(true);
        webviewSettings.setBuiltInZoomControls(true);
        webviewSettings.setUseWideViewPort(false);
        webview.loadDataWithBaseURL(null, header+detail, "text/html", "utf-8", null);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ab = activity.getActionBar();
        ((Activity)mContext).invalidateOptionsMenu();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ab.setDisplayHomeAsUpEnabled(false);
        ab.setHomeButtonEnabled(false);
        ab.setDisplayShowHomeEnabled(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ((Activity)mContext).invalidateOptionsMenu();
    }
}
