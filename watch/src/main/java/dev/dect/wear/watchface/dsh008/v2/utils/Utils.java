package dev.dect.wear.watchface.dsh008.v2.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.wear.remote.interactions.RemoteActivityHelper;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.Executors;

import dev.dect.dsh008.v2.CalendarDefaultUserSettings;
import dev.dect.dsh008.v2.CalendarUserSettings;
import dev.dect.wear.watchface.dsh008.v2.R;
import dev.dect.wear.watchface.dsh008.v2.data.Constants;

public class Utils {
    /**
     * Utils class with random methods
     */

    private final static String TAG = "Utils";

    //Tries to get the phone node reference and set it to "nodeRef" array
    public static void getPhoneNodeAsync(Context ctx, Node[] nodeRef) {
        new Thread(() -> {
            try {
                final Task<List<Node>> nodeListTask = Wearable.getNodeClient(ctx).getConnectedNodes();

                nodeRef[0] = Tasks.await(nodeListTask).get(0);
            } catch (Exception e) {
                Log.e(TAG, "getPhoneNodeAsync: " + e.getMessage());
            }
        }).start();
    }

    public static void openLinkOnPhone(Context ctx, Node[] nodeRef, String link) {
        try {
            if (!link.startsWith("https")) {
                if (link.startsWith("http:")) {
                    link = link.replaceFirst("http:", "https:");
                } else {
                    link = "https://" + link;
                }
            }

            final Intent i = new Intent(Intent.ACTION_VIEW);

            i.addCategory(Intent.CATEGORY_BROWSABLE);

            i.setData(Uri.parse(link));

            final RemoteActivityHelper remoteActivityHelper = new RemoteActivityHelper(ctx, Executors.newSingleThreadExecutor());

            remoteActivityHelper.startRemoteActivity(i, nodeRef[0].getId());

            Toast.makeText(ctx, ctx.getString(R.string.toast_info_check_phone), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(ctx, ctx.getString(R.string.toast_error_generic), Toast.LENGTH_SHORT).show();

            Log.e(TAG, "openLinkOnPhone: " + e.getMessage());
        }
    }

    public static CalendarUserSettings getCurrentCalendarUserSettings(Context ctx) {
        final SharedPreferences sp = ctx.getSharedPreferences(Constants.Sp.SP, Context.MODE_PRIVATE);

        final CalendarUserSettings calendarUserSettings = new CalendarUserSettings();

        calendarUserSettings.setCalendarEventColors(sp.getString(Constants.Sp.CALENDAR_EVENTS_COLORS, CalendarDefaultUserSettings.CALENDAR_EVENTS_COLORS));
        calendarUserSettings.setCalendarBackgroundColor(sp.getInt(Constants.Sp.CALENDAR_BACKGROUND_COLOR, CalendarDefaultUserSettings.CALENDAR_BACKGROUND_COLOR));
        calendarUserSettings.setCalendarInfoTextColor(sp.getInt(Constants.Sp.CALENDAR_INFO_TEXT_COLOR, CalendarDefaultUserSettings.CALENDAR_INFO_TEXT_COLOR));
        calendarUserSettings.setIsToForceUserPalette(sp.getBoolean(Constants.Sp.CALENDAR_IS_TO_FORCE_PALETTE, CalendarDefaultUserSettings.CALENDAR_IS_TO_FORCE_PALETTE));
        calendarUserSettings.setIsToDrawBordersOnly(sp.getBoolean(Constants.Sp.CALENDAR_IS_TO_DRAW_BORDERS_ONLY, CalendarDefaultUserSettings.CALENDAR_IS_TO_DRAW_BORDERS_ONLY));
        calendarUserSettings.setIsToForceUppercase(sp.getBoolean(Constants.Sp.CALENDAR_IS_TO_FORCE_UPPERCASE, CalendarDefaultUserSettings.CALENDAR_IS_TO_FORCE_UPPERCASE));
        calendarUserSettings.setIsToShowAllDayEvent(sp.getBoolean(Constants.Sp.CALENDAR_IS_TO_SHOW_ALL_DAY_EVENT, CalendarDefaultUserSettings.CALENDAR_IS_TO_SHOW_ALL_DAY_EVENT));
        calendarUserSettings.setIsToShowHiddenEventNumber(sp.getBoolean(Constants.Sp.CALENDAR_IS_TO_SHOW_HIDDEN_EVENTS_NUMBER, CalendarDefaultUserSettings.CALENDAR_IS_TO_SHOW_HIDDEN_EVENTS_NUMBER));
        calendarUserSettings.setIsToShowRoundedCorners(sp.getBoolean(Constants.Sp.CALENDAR_IS_TO_SHOW_ROUNDED_CORNERS, CalendarDefaultUserSettings.CALENDAR_IS_TO_SHOW_ROUNDED_CORNERS));
        calendarUserSettings.setIsToShowLastUpdateTimeOnTile(sp.getBoolean(Constants.Sp.TILE_IS_TO_SHOW_LAST_UPDATE_TIME, CalendarDefaultUserSettings.TILE_IS_TO_SHOW_LAST_UPDATE_TIME));
        calendarUserSettings.setIsToShowMinuteHandOnTile(sp.getBoolean(Constants.Sp.TILE_IS_TO_SHOW_MINUTE_HAND, CalendarDefaultUserSettings.TILE_IS_TO_SHOW_MINUTE_HAND));

        return calendarUserSettings;
    }
}
