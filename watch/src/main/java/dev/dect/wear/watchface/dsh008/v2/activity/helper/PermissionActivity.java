package dev.dect.wear.watchface.dsh008.v2.activity.helper;

import static android.content.pm.PackageManager.PERMISSION_DENIED;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import dev.dect.wear.watchface.dsh008.v2.R;
import dev.dect.wear.watchface.dsh008.v2.complication.helper.ComplicationUpdater;

@SuppressLint("WearRecents")
public class PermissionActivity extends Activity {
    /**
     * Transparent activity responsible for requesting all permissions needed (in this case just the calendar access)
     *
     * As I cannot request the permission from the complication/tile this activity is launched to help
     */

    private final int REQUEST_CALENDAR_PERMISSION_CODE = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestPermissions(new String[] {Manifest.permission.READ_CALENDAR}, REQUEST_CALENDAR_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CALENDAR_PERMISSION_CODE) {
            if(grantResults[0] == PERMISSION_DENIED) {
                Toast.makeText(this, getString(R.string.toast_info_permission), Toast.LENGTH_SHORT).show();
            } else {
                //In case the user grants access, I request all complication to update
                ComplicationUpdater.all(this);
            }

            finish();
        }

        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    /*
     * Requests calendar access in case the user removed the access after granting
     *
     * this will launch this activity in case the calendar permission is not granted
     */

    public static void requestCalendarPermissionIfNecessary(Context ctx) {
        if(ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            final Intent i = new Intent(ctx, PermissionActivity.class);

            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            ctx.startActivity(i);
        }
    }
}
