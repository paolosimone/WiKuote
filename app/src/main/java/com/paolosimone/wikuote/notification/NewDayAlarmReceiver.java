package com.paolosimone.wikuote.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Paolo Simone on 18/04/2016.
 */
public class NewDayAlarmReceiver extends BroadcastReceiver {

    public static final int REQUEST_CODE = 117;
    public static final String ACTION = "com.paolosimone.wikuote.notification.alarm";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("ALARM","Quote of the day triggered");
        Intent i = new Intent(context, QuoteOfTheDayService.class);
        context.startService(i);
    }
}
