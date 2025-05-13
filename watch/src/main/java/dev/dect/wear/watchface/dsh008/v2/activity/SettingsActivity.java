package dev.dect.wear.watchface.dsh008.v2.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.wearable.Node;

import dev.dect.dsh008.v2.CalendarDefaultUserSettings;
import dev.dect.dsh008.v2.CalendarUtils;
import dev.dect.wear.watchface.dsh008.v2.R;
import dev.dect.wear.watchface.dsh008.v2.activity.helper.BaseActivity;
import dev.dect.wear.watchface.dsh008.v2.activity.helper.PermissionActivity;
import dev.dect.wear.watchface.dsh008.v2.activity.input.InputColorActivity;
import dev.dect.wear.watchface.dsh008.v2.data.Constants;
import dev.dect.wear.watchface.dsh008.v2.complication.helper.ComplicationUpdater;
import dev.dect.wear.watchface.dsh008.v2.popup.DialogPopup;
import dev.dect.wear.watchface.dsh008.v2.utils.Utils;

@SuppressLint({"ApplySharedPref", "UseSwitchCompatOrMaterialCode"})
public class SettingsActivity extends BaseActivity {
    /**
     * Activity (main) responsible for managing all settings related to the complication/tile/app
     *
     * Does not include the "watch face" settings, those can be initially set in "raw/watchface.xml" file,
     * and can be changed by the using using the wear os internal editor, I do not have access to them (at least I have no idea on how to get them using the WFF)
     */

    private final int RESULT_ID_COLOR_BACKGROUND = 0,
                      RESULT_ID_COLOR_INFO = 1;

    private final Node[] PHONE_NODE = new Node[1];

    @SuppressLint("StaticFieldLeak")
    private static SettingsActivity ACTIVITY;

    private SharedPreferences SP;

    private Switch SWITCH_ALL_DAY_EVENT,
                   SWITCH_ROUNDED_CORNERS,
                   SWITCH_AMOUNT_HIDDEN,
                   SWITCH_BORDER_ONLY,
                   SWITCH_FORCE_UPPERCASE,
                   SWITCH_MINUTE_HAND,
                   SWITCH_LAST_UPDATE_TIME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_settings);

        super.onCreate(savedInstanceState);

        setActivityTitle(R.string.app_name);

        initVariable();

        initListeners();

        init();
    }

    @Override
    protected void onStop() {
        //Request all complications to update, so the style meets the settings set
        ComplicationUpdater.all(this);

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ACTIVITY = null;
    }

    /*
     * Close the app instead of sending it to background
     * Deprecated but seems to be the only one working on some models
     */
    /** @noinspection deprecation*/
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }

    //Close the app instead of sending it to background
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        finish();

        return true;
    }

    public static SettingsActivity getInstance() {
        return ACTIVITY;
    }

    private void initVariable() {
        ACTIVITY = this;

        //Tries to get the phone "link"
        Utils.getPhoneNodeAsync(this, PHONE_NODE);

        SP = getSharedPreferences(Constants.Sp.SP, MODE_PRIVATE);

        SWITCH_ALL_DAY_EVENT = findViewById(R.id.switchAllDay);
        SWITCH_ROUNDED_CORNERS = findViewById(R.id.switchRoundedCorners);
        SWITCH_AMOUNT_HIDDEN = findViewById(R.id.switchHiddenNumber);
        SWITCH_BORDER_ONLY = findViewById(R.id.switchBorderOnly);
        SWITCH_FORCE_UPPERCASE = findViewById(R.id.switchForceUppercase);
        SWITCH_MINUTE_HAND = findViewById(R.id.switchMinuteHand);
        SWITCH_LAST_UPDATE_TIME = findViewById(R.id.switchLastUpdate);
    }

    private void initListeners() {
        findViewById(R.id.btnEventColors).setOnClickListener((v) -> {
            //Launches the activity to manage the user color palette
            startActivity(new Intent(this, SettingsEventColorsActivity.class));
        });

        //Handles the switch changes
        SWITCH_ALL_DAY_EVENT.setOnCheckedChangeListener((v, b) -> onBasicSwitchChangeSetting(Constants.Sp.CALENDAR_IS_TO_SHOW_ALL_DAY_EVENT, b));
        SWITCH_ROUNDED_CORNERS.setOnCheckedChangeListener((v, b) -> onBasicSwitchChangeSetting(Constants.Sp.CALENDAR_IS_TO_SHOW_ROUNDED_CORNERS, b));
        SWITCH_AMOUNT_HIDDEN.setOnCheckedChangeListener((v, b) -> onBasicSwitchChangeSetting(Constants.Sp.CALENDAR_IS_TO_SHOW_HIDDEN_EVENTS_NUMBER, b));
        SWITCH_BORDER_ONLY.setOnCheckedChangeListener((v, b) -> onBasicSwitchChangeSetting(Constants.Sp.CALENDAR_IS_TO_DRAW_BORDERS_ONLY, b));
        SWITCH_FORCE_UPPERCASE.setOnCheckedChangeListener((v, b) -> onBasicSwitchChangeSetting(Constants.Sp.CALENDAR_IS_TO_FORCE_UPPERCASE, b));
        SWITCH_MINUTE_HAND.setOnCheckedChangeListener((v, b) -> onBasicSwitchChangeSetting(Constants.Sp.TILE_IS_TO_SHOW_MINUTE_HAND, b));
        SWITCH_LAST_UPDATE_TIME.setOnCheckedChangeListener((v, b) -> onBasicSwitchChangeSetting(Constants.Sp.TILE_IS_TO_SHOW_LAST_UPDATE_TIME, b));

        addClickListenerForColorInput(
            findViewById(R.id.btnBackgroundColor),
            Constants.Sp.CALENDAR_BACKGROUND_COLOR,
            CalendarDefaultUserSettings.CALENDAR_BACKGROUND_COLOR,
            RESULT_ID_COLOR_BACKGROUND
        );

        addClickListenerForColorInput(
            findViewById(R.id.btnInfoColor),
            Constants.Sp.CALENDAR_INFO_TEXT_COLOR,
            CalendarDefaultUserSettings.CALENDAR_INFO_TEXT_COLOR,
            RESULT_ID_COLOR_INFO
        );

        findViewById(R.id.btnPreviewCalendar).setOnClickListener((v) -> PreviewActivity.showPreview(this, PreviewActivity.TYPE_CALENDAR));
        findViewById(R.id.btnPreviewTile).setOnClickListener((v) -> PreviewActivity.showPreview(this, PreviewActivity.TYPE_TILE));

        //Launches the activity to check for updates
        findViewById(R.id.btnCheckUpdate).setOnClickListener((v) -> startActivity(new Intent(this, VersionActivity.class)));

        //Opens the github page on the users phone
        findViewById(R.id.btnGithub).setOnClickListener((v) -> Utils.openLinkOnPhone(this, PHONE_NODE, Constants.Url.REPOSITORY));

        findViewById(R.id.btnReset).setOnClickListener((v) -> {
            new DialogPopup(
            this,
                R.string.more_reset,
                DialogPopup.NO_TEXT,
                this::resetSettings,
                null
            ).show();
        });
    }

    public void init() {
        //Checks if the permissions are granted, if not request them by launching the permission activity helper
        PermissionActivity.requestCalendarPermissionIfNecessary(this);

        //Sets all switches to match the current settings
        SWITCH_ALL_DAY_EVENT.setChecked(SP.getBoolean(Constants.Sp.CALENDAR_IS_TO_SHOW_ALL_DAY_EVENT, CalendarDefaultUserSettings.CALENDAR_IS_TO_SHOW_ALL_DAY_EVENT));
        SWITCH_ROUNDED_CORNERS.setChecked(SP.getBoolean(Constants.Sp.CALENDAR_IS_TO_SHOW_ROUNDED_CORNERS, CalendarDefaultUserSettings.CALENDAR_IS_TO_SHOW_ROUNDED_CORNERS));
        SWITCH_AMOUNT_HIDDEN.setChecked(SP.getBoolean(Constants.Sp.CALENDAR_IS_TO_SHOW_HIDDEN_EVENTS_NUMBER, CalendarDefaultUserSettings.CALENDAR_IS_TO_SHOW_HIDDEN_EVENTS_NUMBER));
        SWITCH_BORDER_ONLY.setChecked(SP.getBoolean(Constants.Sp.CALENDAR_IS_TO_DRAW_BORDERS_ONLY, CalendarDefaultUserSettings.CALENDAR_IS_TO_DRAW_BORDERS_ONLY));
        SWITCH_FORCE_UPPERCASE.setChecked(SP.getBoolean(Constants.Sp.CALENDAR_IS_TO_FORCE_UPPERCASE, CalendarDefaultUserSettings.CALENDAR_IS_TO_FORCE_UPPERCASE));
        SWITCH_MINUTE_HAND.setChecked(SP.getBoolean(Constants.Sp.TILE_IS_TO_SHOW_MINUTE_HAND, CalendarDefaultUserSettings.TILE_IS_TO_SHOW_MINUTE_HAND));
        SWITCH_LAST_UPDATE_TIME.setChecked(SP.getBoolean(Constants.Sp.TILE_IS_TO_SHOW_LAST_UPDATE_TIME, CalendarDefaultUserSettings.TILE_IS_TO_SHOW_LAST_UPDATE_TIME));

        updateBackgroundColor();

        updateInfoTextColorColor();

        try {
            ((TextView) findViewById(R.id.version)).setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException ignore) {
            findViewById(R.id.version).setVisibility(View.GONE);
        }
    }

    private void updateInfoTextColorColor() {
        updateBtnColor(
            findViewById(R.id.btnInfoColorColor),
            Constants.Sp.CALENDAR_INFO_TEXT_COLOR,
            CalendarDefaultUserSettings.CALENDAR_INFO_TEXT_COLOR
        );
    }

    private void updateBackgroundColor() {
        updateBtnColor(
            findViewById(R.id.btnBackgroundColorColor),
            Constants.Sp.CALENDAR_BACKGROUND_COLOR,
            CalendarDefaultUserSettings.CALENDAR_BACKGROUND_COLOR
        );
    }

    private void updateBtnColor(View view, String keySp, int defaultColor) {
        final Drawable circle = CalendarUtils.getTintedDrawable(this, R.drawable.circle, SP.getInt(keySp, defaultColor));

        view.setBackground(circle);
    }

    private void addClickListenerForColorInput(View view, String keySp, int defaultColor, int resultId) {
        view.setOnClickListener((v) -> {
            final Intent i = new Intent(this, InputColorActivity.class) ;

            //Set the initial color picker to the current event info color text
            i.putExtra(InputColorActivity.EXTRA_INT_COLOR, SP.getInt(keySp, defaultColor));

            startActivityForResult(i, resultId);
        });
    }

    //When a switch toggles
    private void onBasicSwitchChangeSetting(String key, boolean b) {
        //Update the settings based on the key set
        SP.edit().putBoolean(key, b).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            if(requestCode == RESULT_ID_COLOR_BACKGROUND) { //When the user picks a color from the color picker for the background color
                //Saves the color set
                SP.edit().putInt(Constants.Sp.CALENDAR_BACKGROUND_COLOR, data.getIntExtra(InputColorActivity.RESULT_INTENT_INT_COLOR, CalendarDefaultUserSettings.CALENDAR_BACKGROUND_COLOR)).commit();

                //Shows/update the color in the settings activity
                updateBackgroundColor();
            } else if(requestCode == RESULT_ID_COLOR_INFO) { //When the user picks a color from the color picker for the color info text
                //Saves the color set
                SP.edit().putInt(Constants.Sp.CALENDAR_INFO_TEXT_COLOR, data.getIntExtra(InputColorActivity.RESULT_INTENT_INT_COLOR, CalendarDefaultUserSettings.CALENDAR_INFO_TEXT_COLOR)).commit();

                //Shows/update the color in the settings activity
                updateInfoTextColorColor();
            }
        }
    }

    private void resetSettings() {
        SP.edit().clear().commit();

        init();

        Toast.makeText(this, getString(R.string.toast_success_done), Toast.LENGTH_SHORT).show();
    }
}