package dev.dect.wear.watchface.dsh008.v2.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.view.View;
import android.widget.RemoteViews;

import java.util.Objects;

import dev.dect.dsh008.v2.CalendarDrawer;
import dev.dect.wear.watchface.dsh008.v2.R;
import dev.dect.wear.watchface.dsh008.v2.data.Constants;
import dev.dect.wear.watchface.dsh008.v2.receiver.AlarmReceiver;
import dev.dect.wear.watchface.dsh008.v2.utils.Utils;

public class CalendarWidget extends AppWidgetProvider {
    private static final String ACTION_SELF_UPDATE = "action.SELF_UPDATE";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if(Objects.equals(intent.getAction(), ACTION_SELF_UPDATE)) {
            requestUpdateAllFromType(context);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        updateWidgets(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);

        if(!isAdded(context)) {
            AlarmReceiver.cancelAll(context);
        }
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);

        if(!isAdded(context)) {
            AlarmReceiver.cancelAll(context);
        }
    }

    public static void requestUpdateAllFromType(Context ctx) {
        updateWidgets(ctx);
    }

    public static void updateWidgets(Context ctx) {
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(ctx);

        final int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(ctx, CalendarWidget.class));

        final CalendarDrawer calendarDrawer = getCalendarDrawer(ctx);

        calendarDrawer.drawCalendar((int) (ctx.getResources().getDisplayMetrics().widthPixels / 1.5f));

        final Bitmap calendar = calendarDrawer.addBorder(false).getDrawAsBitmap();

        for(int appWidgetId : appWidgetIds) {
            renderUI(ctx, appWidgetId, appWidgetManager, calendar);
        }

        AlarmReceiver.registerAlarm(ctx, calendarDrawer.getNextEndingCalendarEvent());
    }

    public static CalendarDrawer getCalendarDrawer(Context ctx) {
        return new CalendarDrawer(
            ctx,
            Utils.getCurrentCalendarUserSettings(ctx)
        );
    }

    private static void renderUI(Context ctx, int appWidgetId, AppWidgetManager appWidgetManager, Bitmap calendar) {
        final RemoteViews views = getRemoteView(ctx, calendar);

        final Intent intent = new Intent(ctx, CalendarWidget.class);

        intent.setAction(ACTION_SELF_UPDATE);

        views.setOnClickPendingIntent(R.id.container, PendingIntent.getBroadcast(ctx, 0, intent, PendingIntent.FLAG_IMMUTABLE));

        appWidgetManager.updateAppWidget(
            appWidgetId,
            views
        );
    }

    public static RemoteViews getRemoteView(Context ctx, Bitmap calendar) {
        final RemoteViews views = new RemoteViews(ctx.getPackageName(), R.layout.widget_calendar);

        views.setImageViewBitmap(R.id.calendar, calendar);

        setHandStyle(ctx, views);

        return views;
    }

    private static void setHandStyle(Context ctx, RemoteViews views) {
        final SharedPreferences sp = ctx.getSharedPreferences(Constants.Sp.SP, Context.MODE_PRIVATE);

        final int[] hands = Utils.getHandsResIdByHandStyleId(sp.getInt(Constants.Sp.WIDGET_HAND_STYLE, Constants.DefaultSettings.WIDGET_HAND_STYLE));

        views.setIcon(R.id.hour, "setHourHand", Icon.createWithResource(ctx, hands[0]));
        views.setIcon(R.id.minute, "setMinuteHand", Icon.createWithResource(ctx, hands[1]));

        views.setColorStateList(R.id.hour, "setHourHandTintList", ColorStateList.valueOf(sp.getInt(Constants.Sp.WIDGET_HOUR_COLOR, Constants.DefaultSettings.WIDGET_HOUR_COLOR)));
        views.setColorStateList(R.id.minute, "setMinuteHandTintList", ColorStateList.valueOf(sp.getInt(Constants.Sp.WIDGET_MINUTE_COLOR, Constants.DefaultSettings.WIDGET_MINUTE_COLOR)));

        if(sp.getBoolean(Constants.Sp.WIDGET_SHOW_SECOND, Constants.DefaultSettings.WIDGET_SHOW_SECOND)) {
            views.setViewVisibility(R.id.second, View.VISIBLE);

            views.setIcon(R.id.second, "setSecondHand", Icon.createWithResource(ctx, hands[2]));
            views.setColorStateList(R.id.second, "setSecondHandTintList", ColorStateList.valueOf(sp.getInt(Constants.Sp.WIDGET_SECOND_COLOR, Constants.DefaultSettings.WIDGET_SECOND_COLOR)));
        } else {
            views.setViewVisibility(R.id.second, View.GONE);
        }
    }

    public static boolean isAdded(Context ctx) {
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(ctx);

        return appWidgetManager.getAppWidgetIds(new ComponentName(ctx, CalendarWidget.class)).length != 0;
    }
}
