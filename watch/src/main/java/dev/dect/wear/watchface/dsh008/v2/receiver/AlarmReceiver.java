package dev.dect.wear.watchface.dsh008.v2.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.Objects;

import dev.dect.dsh008.v2.CalendarEvent;
import dev.dect.wear.watchface.dsh008.v2.complication.helper.ComplicationUpdater;

public class AlarmReceiver extends BroadcastReceiver {
    /**
     * Receiver alarm events
     */

    private final static String TAG = AlarmReceiver.class.getSimpleName(),
                                ACTION_UPDATE = "calendarUpdate";

    private final static int REQUEST_CODE = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(Objects.equals(intent.getAction(), ACTION_UPDATE)) {
            ComplicationUpdater.all(context);
        }
    }

    public static void registerAlarm(Context ctx, CalendarEvent calendarEvent) {
        cancelAll(ctx);

        final AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);

        final long time =
            calendarEvent == null
            ? System.currentTimeMillis() + 3600000 //3600000 = 1 hour | In case there is no events it will refresh every 1 hour from now
            : calendarEvent.getEndTimeInMillis() + 60000; // 60000 = 1 minute | Just to make sure the event will have ended by the time it runs

        alarmManager.set(AlarmManager.RTC, time, getPendingIntent(ctx));
    }

    public static void cancelAll(Context ctx) {
        final AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);

        final PendingIntent pendingIntent = getPendingIntent(ctx);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            alarmManager.cancelAll();
        } else {
            try {
                alarmManager.cancel(pendingIntent);
            } catch (Exception e) {
                Log.e(TAG, "registerAlarm: " + e.getMessage());
            }
        }
    }

    private static PendingIntent getPendingIntent(Context ctx) {
        final Intent intent = new Intent(ctx, AlarmReceiver.class);

        intent.setAction(ACTION_UPDATE);

        return PendingIntent.getBroadcast(ctx, REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE);
    }
}
