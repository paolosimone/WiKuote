package com.paolosimone.wikuote.notification;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.paolosimone.wikuote.R;
import com.paolosimone.wikuote.activity.MainActivity;
import com.paolosimone.wikuote.api.WikiQuoteProvider;
import com.paolosimone.wikuote.model.Quote;

import java.io.IOException;

/**
 * Background service which task is to retrieve the quote of the day and generate a notification.
 */
public class QuoteOfTheDayService extends IntentService {

    private static final int NOTIFICATION_ID = 117;
    private Context context;

    public QuoteOfTheDayService() {
        super("QuoteOfTheDayService");
    }

    @Override
    public void onCreate(){
        super.onCreate();
        context = getApplicationContext();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Quote qotd;
        try {
            qotd = WikiQuoteProvider.getInstance().getQuoteOfTheDay();
        } catch (IOException e) {
            return;
        }

        showNotification(qotd);
    }

    private void showNotification(Quote qotd){
        Intent intent = new Intent(context, MainActivity.class);
        int requestId = (int) System.currentTimeMillis();
        PendingIntent pendingIntent = PendingIntent.getActivity(context, requestId,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.tab_quote_of_the_day))
                .setContentText(qotd.getText())
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(qotd.getText()))
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}
