package com.paolosimone.wikuote.fragment;

import android.content.Context;

/**
 * Represent a titled fragment.
 * The title of the fragment will be shown in the toolbar in the main activity.
 */
public interface Titled {
    /**
     * Return the title of the fragment.
     * @param context the context in which the request is performed
     * @return the title
     */
    String getTitle(Context context);
}
