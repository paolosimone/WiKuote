package com.paolosimone.wikuote.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.paolosimone.wikuote.R;

/**
 * Created by Paolo Simone on 29/04/2016.
 */
public class WebViewFragment extends Fragment implements Titled {

    private static final String URL = "url";
    private static final String TITLE= "title";

    private String url;
    private String title;
    private WebView webView;
    private ProgressBar progressBar;

    public static WebViewFragment newInstance(String url, String title) {
        WebViewFragment frag = new WebViewFragment();
        Bundle args = new Bundle();
        args.putString(URL, url);
        args.putString(TITLE, title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        url = retrieveInput(URL, savedInstanceState);
        title = retrieveInput(TITLE, savedInstanceState);
        if (webView!=null) webView.restoreState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webview, container, false);
        webView = (WebView) view.findViewById(R.id.webview);
        progressBar = (ProgressBar) view.findViewById(R.id.loading_spinner);
        setupWebView(webView);
        webView.loadUrl(url);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle state){
        super.onSaveInstanceState(state);
        state.putString(URL, url);
        state.putString(TITLE, title);
        webView.saveState(state);
    }

    @Override
    public String getTitle(Context context) {
        return (title!=null) ? title : retrieveInput(TITLE, null);
    }

    private String retrieveInput(String key, Bundle savedInstanceState){
        return (savedInstanceState!=null) ? savedInstanceState.getString(key) : getArguments().getString(key);
    }

    private void setupWebView(WebView webView) {
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String newUrl) {
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                startActivity(intent);
                return true;
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress){
                if (newProgress > 80)
                    progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

}
