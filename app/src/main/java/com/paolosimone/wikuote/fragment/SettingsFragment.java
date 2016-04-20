package com.paolosimone.wikuote.fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import com.paolosimone.wikuote.R;
import com.paolosimone.wikuote.notification.NewDayAlarmReceiver;

import java.util.Calendar;
import java.util.Random;

/**
 * Created by Paolo Simone on 20/04/2016.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        setupNotificationListener();
    }

    private void setupNotificationListener(){
        SwitchPreference notificationSwitch = (SwitchPreference) getPreferenceManager()
                .findPreference(getString(R.string.key_notification_active));
        notificationSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean isSetActive = (Boolean) newValue;
                if (isSetActive){
                    setEnableReceiver(true);
                    scheduleQuoteOfTheDayNotification(getActivity());
                }
                else {
                    setEnableReceiver(false);
                    cancelQuoteOfTheDayNotification(getActivity());
                }
                return true;
            }
        });
    }

    public static void scheduleQuoteOfTheDayNotification(Context context){
        Intent intent = new Intent(context.getApplicationContext(), NewDayAlarmReceiver.class);
        intent.setAction(NewDayAlarmReceiver.ACTION);
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, NewDayAlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Random random = new Random();
        Calendar tomorrow = Calendar.getInstance();
//        tomorrow.add(Calendar.SECOND,10);
        tomorrow.add(Calendar.DAY_OF_YEAR, 1);
        tomorrow.set(Calendar.HOUR_OF_DAY,10);
        tomorrow.set(Calendar.MINUTE, random.nextInt(20));

        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC, tomorrow.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public static void cancelQuoteOfTheDayNotification(Context context){
        Intent intent = new Intent(context.getApplicationContext(), NewDayAlarmReceiver.class);
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, NewDayAlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pendingIntent);
    }

    private void setEnableReceiver(boolean enable){
        int status = enable ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        Context context = getActivity().getApplicationContext();

        ComponentName receiver = new ComponentName(context, NewDayAlarmReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, status, PackageManager.DONT_KILL_APP);
    }
}
