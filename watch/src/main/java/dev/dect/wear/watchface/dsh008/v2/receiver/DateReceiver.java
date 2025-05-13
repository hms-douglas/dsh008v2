package dev.dect.wear.watchface.dsh008.v2.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Objects;

import dev.dect.wear.watchface.dsh008.v2.complication.helper.ComplicationUpdater;

public class DateReceiver extends BroadcastReceiver {
    /**
     * Receiver for when the day changes
     */

    @Override
    public void onReceive(Context context, Intent intent) {
        if(Objects.equals(intent.getAction(), Intent.ACTION_DATE_CHANGED)) {
            ComplicationUpdater.all(context);
        }
    }
}
