package dev.dect.wear.watchface.dsh008.v2.complication.helper;

import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester;

import dev.dect.wear.watchface.dsh008.v2.complication.CalendarComplication;

/** @noinspection rawtypes*/
public class ComplicationUpdater {
    /**
     * Utils class to deal with complication updates triggered by code
     */

    private final static String TAG = ComplicationUpdater.class.getSimpleName();

    //Request all complications to update (in this case there is only one complication | This is in case I wish to add more complications)
    public static boolean all(Context ctx) {
        boolean allSucceeded = true;

        if(!calendar(ctx)) {
            allSucceeded = false;
        }

        return allSucceeded;
    }

    //Request the update of the "CalendarComplication" complication
    private static boolean calendar(Context ctx) {
        return updaterHelper(ctx, CalendarComplication.class);
    }

    //Helper to update a complication by its class
    private static boolean updaterHelper(Context c, Class cl) {
        try {
            ComplicationDataSourceUpdateRequester.create(c, new ComponentName(c, cl)).requestUpdateAll();
        } catch (Exception e) {
            Log.e(TAG, "updaterHelper: - " + cl.getSimpleName() + " - " + e.getMessage());

            return false;
        }

        return true;
    }
}
