package dev.dect.wear.watchface.dsh008.v2.activity.helper;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;

import dev.dect.wear.watchface.dsh008.v2.R;
import dev.dect.wear.watchface.dsh008.v2.complication.helper.ComplicationUpdater;

public class UpdaterActivity extends Activity {
    /**
     * Transparent activity to update all complications
     *
     * Taps on complications can trigger only PendingIntents, which cannot be used, directly, to trigger the complication update.
     * This activity is called when the user taps the complication in order to request the update, then it finishes itself right after.
     * It also shows a message informing whether the update was successfully(* **)
     *
     * (*) "Successfully" does not mean that new events were fetched/synced (this depends on the WearableCalendarContract only)
     * (**) "Successfully" means that the complication update request was completed,
     *      events that were fetched/synced (by the WearableCalendarContract) will now be rendered
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(ComplicationUpdater.all(this)) {
            Toast.makeText(this, getString(R.string.toast_success_done), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.toast_error_generic), Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    //Generates and returns the PendingIntent from this activity
    public static PendingIntent getPendingIntent(Context ctx) {
        final Intent updaterIntent = new Intent(ctx, UpdaterActivity.class);

        return PendingIntent.getActivity(ctx, 0, updaterIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }
}
