package dev.dect.wear.watchface.dsh008.v2.communication;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONObject;

import dev.dect.dsh008.v2.CalendarUserSettings;
import dev.dect.wear.watchface.dsh008.v2.data.Constants;
import dev.dect.wear.watchface.dsh008.v2.utils.Utils;

public class DataLayerSender {
    private static final String TAG = DataLayerSender.class.getSimpleName();

    public static boolean IS_THE_ONE_CHANGING = false;

    private final Context CONTEXT;

    public DataLayerSender(Context ctx) {
        this.CONTEXT = ctx;
    }

    public void requestCalendarRefresh() {
        final PutDataMapRequest dataRequest = PutDataMapRequest.create(Constants.DataKey.PATH);

        send(dataRequest, Constants.DataKey.Action.REFRESH_CALENDAR);
    }

    public void requestSettingsSync() {
        final CalendarUserSettings calendarUserSettings = Utils.getCurrentCalendarUserSettings(CONTEXT);

        final JSONObject settings = new JSONObject();

        try {
            settings.put(CalendarUserSettings.K_CALENDAR_IS_TO_DRAW_BORDERS_ONLY, calendarUserSettings.isToDrawBordersOnly());
            settings.put(CalendarUserSettings.K_CALENDAR_IS_TO_SHOW_ALL_DAY_EVENT, calendarUserSettings.isToShowAllDayEvent());
            settings.put(CalendarUserSettings.K_CALENDAR_IS_TO_SHOW_HIDDEN_EVENTS_NUMBER, calendarUserSettings.isToShowHiddenEventNumber());
            settings.put(CalendarUserSettings.K_CALENDAR_IS_TO_SHOW_ROUNDED_CORNERS, calendarUserSettings.isToShowRoundedCorners());
            settings.put(CalendarUserSettings.K_CALENDAR_IS_TO_FORCE_PALETTE, calendarUserSettings.isToForceUserPalette());
            settings.put(CalendarUserSettings.K_CALENDAR_IS_TO_FORCE_UPPERCASE, calendarUserSettings.isToForceUppercase());
            settings.put(CalendarUserSettings.K_CALENDAR_INFO_TEXT_COLOR, calendarUserSettings.getCalendarInfoTextColor());
            settings.put(CalendarUserSettings.K_CALENDAR_BACKGROUND_COLOR, calendarUserSettings.getCalendarBackgroundColor());
            settings.put(CalendarUserSettings.K_CALENDAR_EVENTS_COLORS, calendarUserSettings.getCalendarEventColors());
        } catch (Exception e) {
            Log.e(TAG, "requestSettingsSync: " + e.getMessage());
        }

        final PutDataMapRequest dataRequest = PutDataMapRequest.create(Constants.DataKey.PATH);

        dataRequest.getDataMap().putString(Constants.DataKey.Action.SET_SETTINGS, settings.toString());

        send(dataRequest, Constants.DataKey.Action.SET_SETTINGS);
    }

    public void requestSettingsPaletteSync() {
        final CalendarUserSettings calendarUserSettings = Utils.getCurrentCalendarUserSettings(CONTEXT);

        final JSONObject settings = new JSONObject();

        try {
            settings.put(CalendarUserSettings.K_CALENDAR_EVENTS_COLORS, calendarUserSettings.getCalendarEventColors());
            settings.put(CalendarUserSettings.K_CALENDAR_IS_TO_FORCE_PALETTE, calendarUserSettings.isToForceUserPalette());
        } catch (Exception e) {
            Log.e(TAG, "requestSettingsPaletteSync: " + e.getMessage());
        }

        final PutDataMapRequest dataRequest = PutDataMapRequest.create(Constants.DataKey.PATH);

        dataRequest.getDataMap().putString(Constants.DataKey.Action.SET_PALETTE, settings.toString());

        send(dataRequest, Constants.DataKey.Action.SET_PALETTE);
    }

    private void send(PutDataMapRequest dataRequest, String action) {
        dataRequest.getDataMap().putString(Constants.DataKey.ACTION, action + "_" + System.currentTimeMillis());

        dataRequest.setUrgent();

        IS_THE_ONE_CHANGING = true;

        Wearable.getDataClient(CONTEXT).putDataItem(dataRequest.asPutDataRequest());
    }
}
