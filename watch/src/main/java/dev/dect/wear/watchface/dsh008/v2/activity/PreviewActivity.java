package dev.dect.wear.watchface.dsh008.v2.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import dev.dect.dsh008.v2.CalendarDrawer;
import dev.dect.wear.watchface.dsh008.v2.R;
import dev.dect.wear.watchface.dsh008.v2.utils.Utils;

@SuppressLint({"ApplySharedPref", "UseSwitchCompatOrMaterialCode"})
public class PreviewActivity extends Activity {
    /**
     * Activity responsible for showing the image preview in full screen...
     */

    public static final String EXTRA_TYPE = "extra_type";

    public static final int TYPE_CALENDAR = 0,
                            TYPE_WATCH_FACE = 1,
                            TYPE_TILE = 2;

    private ImageView IMAGE_VIEW;

    private CalendarDrawer CALENDAR_DRAWER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_preview);

        initVariables();

        initListeners();

        init();
    }

    private void initVariables() {
        IMAGE_VIEW = findViewById(R.id.preview);

        CALENDAR_DRAWER = new CalendarDrawer(
            this,
            Utils.getCurrentCalendarUserSettings(this)
        );
    }

    private void initListeners() {
        IMAGE_VIEW.setOnClickListener((v) -> finish());
    }

    private void init() {
        switch (getIntent().getIntExtra(EXTRA_TYPE, TYPE_CALENDAR)) { //Draw the preview according to the type requested
            case TYPE_CALENDAR:
                CALENDAR_DRAWER.drawCalendar();
                break;

            case TYPE_WATCH_FACE:
                CALENDAR_DRAWER.drawWatchFacePreview();
                break;

            case TYPE_TILE:
                CALENDAR_DRAWER.drawTile();
                break;
        }

        if(CALENDAR_DRAWER.isDrawEmpty()) { //In case there is no events being rendered
            Toast.makeText(this, getText(R.string.toast_info_preview), Toast.LENGTH_SHORT).show();

            finish();
        } else {
            IMAGE_VIEW.setImageBitmap(CALENDAR_DRAWER.getDrawAsBitmap());
        }
    }

    //Starts this activity
    public static void showPreview(Context ctx, int type) {
        final Intent i = new Intent(ctx, PreviewActivity.class);

        i.putExtra(EXTRA_TYPE, type);

        ctx.startActivity(i);
    }
}