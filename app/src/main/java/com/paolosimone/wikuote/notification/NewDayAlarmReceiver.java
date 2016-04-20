package com.paolosimone.wikuote.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;

import com.paolosimone.wikuote.R;
import com.paolosimone.wikuote.fragment.SettingsFragment;

import java.util.Calendar;
import java.util.Random;

/**
 * Created by Paolo Simone on 18/04/2016.
 */
public class NewDayAlarmReceiver extends BroadcastReceiver {

    public static final int REQUEST_CODE = 117;
    public static final String ACTION = "com.paolosimone.wikuote.alarm.QOTD";

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isFreshBoot = intent.getAction().equals("android.intent.action.BOOT_COMPLETED");
        boolean shouldStartAlarm = PreferenceManager
                .getDefaultSharedPreferences(context)
                .getBoolean(context.getString(R.string.key_notification_active),false);

        if (isFreshBoot && shouldStartAlarm){
            SettingsFragment.scheduleQuoteOfTheDayNotification(context);
            return;
        }

        Intent i = new Intent(context, QuoteOfTheDayService.class);
        context.startService(i);
    }
}
