package dev.dect.wear.watchface.dsh008.v2.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Objects;

import dev.dect.wear.watchface.dsh008.v2.communication.DataLayerSender;
import dev.dect.wear.watchface.dsh008.v2.widget.CalendarWidget;

public class CalendarReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(Objects.equals(intent.getAction(), Intent.ACTION_PROVIDER_CHANGED)) {
            CalendarWidget.requestUpdateAllFromType(context);

            new DataLayerSender(context).requestCalendarRefresh();
        }
    }
}
