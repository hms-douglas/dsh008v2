package dev.dect.wear.watchface.dsh008.v2.communication;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

import org.json.JSONObject;

import java.util.Objects;

import dev.dect.dsh008.v2.CalendarUserSettings;
import dev.dect.wear.watchface.dsh008.v2.activity.SettingsActivity;
import dev.dect.wear.watchface.dsh008.v2.activity.SettingsEventColorsActivity;
import dev.dect.wear.watchface.dsh008.v2.complication.helper.ComplicationUpdater;
import dev.dect.wear.watchface.dsh008.v2.data.Constants;

@SuppressLint("ApplySharedPref")
public class DataLayerReceiver extends WearableListenerService {
    /**
     * Class/Service for receiver data from phone app
     */

    private final static String TAG = DataLayerReceiver.class.getSimpleName();

    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
        for(DataEvent event : dataEventBuffer) {
            if(event.getType() == DataEvent.TYPE_CHANGED) {
                final DataMap dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();

                if(Objects.requireNonNull(dataMap.getString(Constants.DataKey.ACTION)).startsWith(Constants.DataKey.Action.REFRESH_CALENDAR)) {
                    ComplicationUpdater.all(this);
                } else if(Objects.requireNonNull(dataMap.getString(Constants.DataKey.ACTION)).startsWith(Constants.DataKey.Action.SET_SETTINGS)) {
                    final String data = dataMap.getString(Constants.DataKey.Action.SET_SETTINGS);

                    if(data != null && !data.isEmpty()) {
                        try {
                            final SharedPreferences.Editor editor = getSharedPreferences(Constants.Sp.SP, MODE_PRIVATE).edit();

                            final JSONObject jsonObject = new JSONObject(data);

                            editor.putBoolean(Constants.Sp.CALENDAR_IS_TO_DRAW_BORDERS_ONLY, jsonObject.getBoolean(CalendarUserSettings.K_CALENDAR_IS_TO_DRAW_BORDERS_ONLY));
                            editor.putBoolean(Constants.Sp.CALENDAR_IS_TO_SHOW_ALL_DAY_EVENT, jsonObject.getBoolean(CalendarUserSettings.K_CALENDAR_IS_TO_SHOW_ALL_DAY_EVENT));
                            editor.putBoolean(Constants.Sp.CALENDAR_IS_TO_SHOW_HIDDEN_EVENTS_NUMBER, jsonObject.getBoolean(CalendarUserSettings.K_CALENDAR_IS_TO_SHOW_HIDDEN_EVENTS_NUMBER));
                            editor.putBoolean(Constants.Sp.CALENDAR_IS_TO_SHOW_ROUNDED_CORNERS, jsonObject.getBoolean(CalendarUserSettings.K_CALENDAR_IS_TO_SHOW_ROUNDED_CORNERS));
                            editor.putBoolean(Constants.Sp.CALENDAR_IS_TO_FORCE_PALETTE, jsonObject.getBoolean(CalendarUserSettings.K_CALENDAR_IS_TO_FORCE_PALETTE));
                            editor.putBoolean(Constants.Sp.CALENDAR_IS_TO_FORCE_UPPERCASE, jsonObject.getBoolean(CalendarUserSettings.K_CALENDAR_IS_TO_FORCE_UPPERCASE));

                            editor.putInt(Constants.Sp.CALENDAR_INFO_TEXT_COLOR, jsonObject.getInt(CalendarUserSettings.K_CALENDAR_INFO_TEXT_COLOR));
                            editor.putInt(Constants.Sp.CALENDAR_BACKGROUND_COLOR, jsonObject.getInt(CalendarUserSettings.K_CALENDAR_BACKGROUND_COLOR));

                            editor.putString(Constants.Sp.CALENDAR_EVENTS_COLORS, jsonObject.getString(CalendarUserSettings.K_CALENDAR_EVENTS_COLORS));

                            editor.commit();

                            ComplicationUpdater.all(this);

                            if(SettingsActivity.getInstance() != null) {
                                SettingsActivity.getInstance().init();
                            }

                            if(SettingsEventColorsActivity.getInstance() != null) {
                                SettingsEventColorsActivity.getInstance().updateUI();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "onDataChanged: set_settings - " + e.getMessage());
                        }
                    }
                } else if(Objects.requireNonNull(dataMap.getString(Constants.DataKey.ACTION)).startsWith(Constants.DataKey.Action.SET_PALETTE)) {
                    final String data = dataMap.getString(Constants.DataKey.Action.SET_PALETTE);

                    if (data != null && !data.isEmpty()) {
                        try {
                            final SharedPreferences.Editor editor = getSharedPreferences(Constants.Sp.SP, MODE_PRIVATE).edit();

                            final JSONObject jsonObject = new JSONObject(data);

                            editor.putBoolean(Constants.Sp.CALENDAR_IS_TO_FORCE_PALETTE, jsonObject.getBoolean(CalendarUserSettings.K_CALENDAR_IS_TO_FORCE_PALETTE));

                            editor.putString(Constants.Sp.CALENDAR_EVENTS_COLORS, jsonObject.getString(CalendarUserSettings.K_CALENDAR_EVENTS_COLORS));

                            editor.commit();

                            ComplicationUpdater.all(this);

                            if(SettingsEventColorsActivity.getInstance() != null) {
                                SettingsEventColorsActivity.getInstance().updateUI();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "onDataChanged: set_palette - " + e.getMessage());
                        }
                    }
                }
            }
        }
    }
}
