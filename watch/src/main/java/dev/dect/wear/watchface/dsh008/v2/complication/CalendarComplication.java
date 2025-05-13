package dev.dect.wear.watchface.dsh008.v2.complication;

import android.annotation.SuppressLint;
import android.graphics.drawable.Icon;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.wear.watchface.complications.data.ComplicationData;
import androidx.wear.watchface.complications.data.ComplicationType;
import androidx.wear.watchface.complications.data.PhotoImageComplicationData;
import androidx.wear.watchface.complications.data.PlainComplicationText;
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceService;
import androidx.wear.watchface.complications.datasource.ComplicationRequest;

import dev.dect.dsh008.v2.CalendarDrawer;
import dev.dect.wear.watchface.dsh008.v2.R;
import dev.dect.wear.watchface.dsh008.v2.activity.helper.PermissionActivity;
import dev.dect.wear.watchface.dsh008.v2.activity.helper.UpdaterActivity;
import dev.dect.wear.watchface.dsh008.v2.receiver.AlarmReceiver;
import dev.dect.wear.watchface.dsh008.v2.utils.Utils;

@SuppressLint("ApplySharedPref")
public class CalendarComplication extends ComplicationDataSourceService {
    /**
     * Service responsible for generating/handling all the "CalendarComplication" complications
     */

    private final String TAG = CalendarComplication.class.getSimpleName();

    /*
     * When the user removes the complication
     *
     * In this case when the watch face is removed/replaced by another
     */
    @Override
    public void onComplicationDeactivated(int complicationInstanceId) {
        super.onComplicationDeactivated(complicationInstanceId);

        AlarmReceiver.cancelAll(this);
    }

    @Nullable
    @Override
    public ComplicationData getPreviewData(@NonNull ComplicationType complicationType) {
        if(complicationType.equals(ComplicationType.PHOTO_IMAGE)) {
            return getPreview();
        }

        return null;
    }

    //When the complication update is requested, do
    @Override
    public void onComplicationRequest(@NonNull ComplicationRequest complicationRequest, @NonNull ComplicationRequestListener complicationRequestListener) {
        //Checks if the permissions are granted, if not request them by launching the permission activity helper
        PermissionActivity.requestCalendarPermissionIfNecessary(this);

        try {
            if(complicationRequest.getComplicationType().equals(ComplicationType.PHOTO_IMAGE)) { //Check if the complication request is the image type, only type set, but anyway
                complicationRequestListener.onComplicationData(getComplication());
            }
        } catch(RemoteException e) {
            Log.e(TAG, "onComplicationRequest: " + e.getMessage());
        }
    }

    //Used on the internal wear os editor...
    private PhotoImageComplicationData getPreview() {
        final CalendarDrawer calendarDrawer = new CalendarDrawer(this, Utils.getCurrentCalendarUserSettings(this));

        calendarDrawer.drawCalendar();

        if(calendarDrawer.isDrawEmpty()) {
            //Returns a static image of a custom preview
            return new PhotoImageComplicationData.Builder(
                Icon.createWithResource(this, R.drawable.preview_complication),
                new PlainComplicationText.Builder(getString(R.string.complication_calendar_description)).build()
            ).build();
        } else {
            //Return the actual current calendar as preview
            return new PhotoImageComplicationData.Builder(
                calendarDrawer.getDrawAsIcon(),
                new PlainComplicationText.Builder(getString(R.string.complication_calendar_description)).build()
            ).build();
        }
    }

    //Generates and returns the calendar complication, image type
    private PhotoImageComplicationData getComplication() {
        final CalendarDrawer calendarDrawer = new CalendarDrawer(this, Utils.getCurrentCalendarUserSettings(this));

        calendarDrawer.drawCalendar();

        AlarmReceiver.registerAlarm(getApplicationContext(), calendarDrawer.getNextEndingCalendarEvent());

        final PhotoImageComplicationData.Builder builder = new PhotoImageComplicationData.Builder(
            calendarDrawer.getDrawAsIcon(), //Request the calendar rendered image as icon
            new PlainComplicationText.Builder(getString(R.string.complication_calendar_description)).build()
        );

        //Launches the "UpdaterActivity" activity when the user taps the complication
        builder.setTapAction(UpdaterActivity.getPendingIntent(this));

        return builder.build();
    }
}
