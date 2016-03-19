package com.forfun.paolosimone.wikuote;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static String QUOTE_TEXT = "quote";

    private String quote = "Insert quote here";

    public static MainActivityFragment newInstance(String quote){
        MainActivityFragment maf = new MainActivityFragment();
        Bundle args = new Bundle();

        args.putString(QUOTE_TEXT, quote);
        maf.setArguments(args);
        return maf;
    }

    public MainActivityFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            quote = getArguments().getString(QUOTE_TEXT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        TextView quoteTextView = (TextView) view.findViewById(R.id.quote_text);
        quoteTextView.setText(quote);
    }
}
