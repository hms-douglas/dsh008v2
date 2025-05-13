package dev.dect.wear.watchface.dsh008.v2.activity;

import static android.content.pm.PackageManager.PERMISSION_DENIED;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import dev.dect.dsh008.v2.CalendarDefaultUserSettings;
import dev.dect.wear.watchface.dsh008.v2.R;
import dev.dect.wear.watchface.dsh008.v2.communication.DataLayerSender;
import dev.dect.wear.watchface.dsh008.v2.data.Constants;
import dev.dect.wear.watchface.dsh008.v2.popup.ColorPickerPopup;
import dev.dect.wear.watchface.dsh008.v2.popup.DialogPopup;
import dev.dect.wear.watchface.dsh008.v2.popup.HandPopup;
import dev.dect.wear.watchface.dsh008.v2.utils.Utils;
import dev.dect.wear.watchface.dsh008.v2.widget.CalendarWidget;

/** @noinspection deprecation*/
@SuppressLint({"UseSwitchCompatOrMaterialCode", "ApplySharedPref"})
public class SettingsActivity extends Activity {
    private final int REQUEST_CALENDAR_PERMISSION_CODE = 0;

    private final String TAG = SettingsActivity.class.getSimpleName();

    private SharedPreferences SP;

    private Switch SWITCH_ALL_DAY_EVENT,
                   SWITCH_ROUNDED_CORNERS,
                   SWITCH_AMOUNT_HIDDEN,
                   SWITCH_BORDER_ONLY,
                   SWITCH_FORCE_UPPERCASE,
                   SWITCH_SHOW_SECOND;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        Utils.updateStatusBarColor(this);

        initVariables();

        initListeners();

        init();
    }

    @Override
    protected void onStop() {
        CalendarWidget.requestUpdateAllFromType(this);

        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        updatePreview();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }

    private void initVariables() {
        SP = getSharedPreferences(Constants.Sp.SP, MODE_PRIVATE);

        SWITCH_ALL_DAY_EVENT = findViewById(R.id.switchAllDay);
        SWITCH_ROUNDED_CORNERS = findViewById(R.id.switchRoundedCorners);
        SWITCH_AMOUNT_HIDDEN = findViewById(R.id.switchHiddenNumber);
        SWITCH_BORDER_ONLY = findViewById(R.id.switchBorderOnly);
        SWITCH_FORCE_UPPERCASE = findViewById(R.id.switchForceUppercase);
        SWITCH_SHOW_SECOND = findViewById(R.id.switchShowSecond);
    }

    private void initListeners() {
        Utils.addAppbarEffectListener(
            findViewById(R.id.titleBar),
            findViewById(R.id.toolbar),
            findViewById(R.id.titleExpanded),
            findViewById(R.id.titleCollapsed)
        );

        findViewById(R.id.btnEventColors).setOnClickListener((v) -> {
            startActivity(new Intent(this, SettingsEventColorsActivity.class));
        });

        SWITCH_ALL_DAY_EVENT.setOnCheckedChangeListener((v, b) -> onBasicSwitchChangeSetting(Constants.Sp.CALENDAR_IS_TO_SHOW_ALL_DAY_EVENT, b, v.isPressed()));
        SWITCH_ROUNDED_CORNERS.setOnCheckedChangeListener((v, b) -> onBasicSwitchChangeSetting(Constants.Sp.CALENDAR_IS_TO_SHOW_ROUNDED_CORNERS, b, v.isPressed()));
        SWITCH_AMOUNT_HIDDEN.setOnCheckedChangeListener((v, b) -> onBasicSwitchChangeSetting(Constants.Sp.CALENDAR_IS_TO_SHOW_HIDDEN_EVENTS_NUMBER, b, v.isPressed()));
        SWITCH_BORDER_ONLY.setOnCheckedChangeListener((v, b) -> onBasicSwitchChangeSetting(Constants.Sp.CALENDAR_IS_TO_DRAW_BORDERS_ONLY, b, v.isPressed()));
        SWITCH_FORCE_UPPERCASE.setOnCheckedChangeListener((v, b) -> onBasicSwitchChangeSetting(Constants.Sp.CALENDAR_IS_TO_FORCE_UPPERCASE, b, v.isPressed()));
        SWITCH_SHOW_SECOND.setOnCheckedChangeListener((v, b) -> onBasicSwitchChangeSetting(Constants.Sp.WIDGET_SHOW_SECOND, b, v.isPressed()));

        addClickListenerForColorInput(
            findViewById(R.id.btnBackgroundColor),
            findViewById(R.id.btnBackgroundColorColor),
            Constants.Sp.CALENDAR_BACKGROUND_COLOR,
            CalendarDefaultUserSettings.CALENDAR_BACKGROUND_COLOR,
            true
        );

        addClickListenerForColorInput(
            findViewById(R.id.btnInfoColor),
            findViewById(R.id.btnInfoColorColor),
            Constants.Sp.CALENDAR_INFO_TEXT_COLOR,
            CalendarDefaultUserSettings.CALENDAR_INFO_TEXT_COLOR,
            false
        );

        addClickListenerForColorInput(
            findViewById(R.id.btnHourColor),
            findViewById(R.id.btnHourColorColor),
            Constants.Sp.WIDGET_HOUR_COLOR,
            Constants.DefaultSettings.WIDGET_HOUR_COLOR,
            false
        );

        addClickListenerForColorInput(
            findViewById(R.id.btnMinuteColor),
            findViewById(R.id.btnMinuteColorColor),
            Constants.Sp.WIDGET_MINUTE_COLOR,
            Constants.DefaultSettings.WIDGET_MINUTE_COLOR,
            false
        );

        addClickListenerForColorInput(
            findViewById(R.id.btnSecondColor),
            findViewById(R.id.btnSecondColorColor),
            Constants.Sp.WIDGET_SECOND_COLOR,
            Constants.DefaultSettings.WIDGET_SECOND_COLOR,
            false
        );

        findViewById(R.id.btnMore).setOnClickListener((v) -> {
            v.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);

            final PopupMenu popupMenu = new PopupMenu(this, v, Gravity.END, 0, R.style.PopupMenu);

            final Menu menu = popupMenu.getMenu();

            popupMenu.getMenuInflater().inflate(R.menu.more_main, menu);

            menu.setGroupDividerEnabled(true);

            popupMenu.setOnMenuItemClickListener((item) -> {
                final int id = item.getItemId();

                if(id == R.id.reset) {
                    new DialogPopup(
                        this,
                        R.string.more_reset,
                        R.string.menu_settings_reset_app_settings_confirm_description,
                        R.string.popup_btn_reset,
                        this::resetSettings,
                        R.string.popup_btn_cancel,
                        null,
                        true,
                        false,
                        true
                    ).show();
                } else if(id == R.id.install) {
                    startActivity(new Intent(this, InstallActivity.class));
                } else if(id == R.id.sync) {
                    new DialogPopup(
                        this,
                        R.string.more_sync_watch,
                        R.string.menu_settings_sync_confirm_description,
                        R.string.popup_btn_sync,
                        ()-> {
                            new DataLayerSender(this).requestSettingsSync();

                            Toast.makeText(this, getString(R.string.toast_success_generic), Toast.LENGTH_SHORT).show();
                        },
                        R.string.popup_btn_cancel,
                        null,
                        true,
                        false,
                        true
                    ).show();
                } else if(id == R.id.update) {
                    checkForUpdate();
                } else if(id == R.id.github) {
                    Utils.openLink(this, Constants.Url.REPOSITORY);
                } else if(id == R.id.about) {
                    try {
                        startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName())));
                    } catch (Exception e) {
                        Toast.makeText(this, getString(R.string.toast_error_generic), Toast.LENGTH_SHORT).show();

                        Log.e(TAG, "initListeners: " + e.getMessage());
                    }
                }

                return true;
            });

            popupMenu.show();
        });

        findViewById(R.id.btnHandStyle).setOnClickListener((v) -> {
            new HandPopup(
                this,
                new HandPopup.OnHandPopupListener() {
                    @Override
                    public void onHandPicked(int id) {
                        SP.edit().putInt(Constants.Sp.WIDGET_HAND_STYLE, id).commit();

                        updatePreview();
                    }
                }
            ).show();
        });
    }

    private void init() {
        requestPermissions(new String[] {Manifest.permission.READ_CALENDAR}, REQUEST_CALENDAR_PERMISSION_CODE);

        SWITCH_ALL_DAY_EVENT.setChecked(SP.getBoolean(Constants.Sp.CALENDAR_IS_TO_SHOW_ALL_DAY_EVENT, CalendarDefaultUserSettings.CALENDAR_IS_TO_SHOW_ALL_DAY_EVENT));
        SWITCH_ROUNDED_CORNERS.setChecked(SP.getBoolean(Constants.Sp.CALENDAR_IS_TO_SHOW_ROUNDED_CORNERS, CalendarDefaultUserSettings.CALENDAR_IS_TO_SHOW_ROUNDED_CORNERS));
        SWITCH_AMOUNT_HIDDEN.setChecked(SP.getBoolean(Constants.Sp.CALENDAR_IS_TO_SHOW_HIDDEN_EVENTS_NUMBER, CalendarDefaultUserSettings.CALENDAR_IS_TO_SHOW_HIDDEN_EVENTS_NUMBER));
        SWITCH_BORDER_ONLY.setChecked(SP.getBoolean(Constants.Sp.CALENDAR_IS_TO_DRAW_BORDERS_ONLY, CalendarDefaultUserSettings.CALENDAR_IS_TO_DRAW_BORDERS_ONLY));
        SWITCH_FORCE_UPPERCASE.setChecked(SP.getBoolean(Constants.Sp.CALENDAR_IS_TO_FORCE_UPPERCASE, CalendarDefaultUserSettings.CALENDAR_IS_TO_FORCE_UPPERCASE));
        SWITCH_SHOW_SECOND.setChecked(SP.getBoolean(Constants.Sp.WIDGET_SHOW_SECOND, Constants.DefaultSettings.WIDGET_SHOW_SECOND));

        updateBtnColor(
            findViewById(R.id.btnInfoColorColor),
            Constants.Sp.CALENDAR_INFO_TEXT_COLOR,
            CalendarDefaultUserSettings.CALENDAR_INFO_TEXT_COLOR
        );

        updateBtnColor(
            findViewById(R.id.btnBackgroundColorColor),
            Constants.Sp.CALENDAR_BACKGROUND_COLOR,
            CalendarDefaultUserSettings.CALENDAR_BACKGROUND_COLOR
        );

        updateBtnColor(
            findViewById(R.id.btnHourColorColor),
            Constants.Sp.WIDGET_HOUR_COLOR,
            Constants.DefaultSettings.WIDGET_HOUR_COLOR
        );

        updateBtnColor(
            findViewById(R.id.btnMinuteColorColor),
            Constants.Sp.WIDGET_MINUTE_COLOR,
            Constants.DefaultSettings.WIDGET_MINUTE_COLOR
        );

        updateBtnColor(
            findViewById(R.id.btnSecondColorColor),
            Constants.Sp.WIDGET_SECOND_COLOR,
            Constants.DefaultSettings.WIDGET_SECOND_COLOR
        );

        updatePreview();
    }

    private void updatePreview() {
        Utils.Preview.update(this, findViewById(R.id.previewContainer));
    }

    private void updateBtnColor(ImageView imageView, String keySp, int defaultColor) {
        Utils.drawColorSampleOnImageView(imageView, SP.getInt(keySp, defaultColor));
    }

    private void addClickListenerForColorInput(View view, ImageView sample, String keySp, int defaultColor, boolean showAlpha) {
        view.setOnClickListener((v) -> {
            v.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);

            new ColorPickerPopup(
                this,
                Utils.Converter.intColorToHex(SP.getInt(keySp, defaultColor)),
                showAlpha,
                (colorHex, colorInt) -> {
                    SP.edit().putInt(keySp, colorInt).commit();

                    updateBtnColor(
                        sample,
                        keySp,
                        defaultColor
                    );

                    updatePreview();
                }
            ).show();
        });
    }

    private void onBasicSwitchChangeSetting(String key, boolean b, boolean userInput) {
       if(userInput) {
           SP.edit().putBoolean(key, b).commit();

           updatePreview();
       }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CALENDAR_PERMISSION_CODE) {
            if(grantResults[0] == PERMISSION_DENIED) {
                Toast.makeText(this, getString(R.string.toast_info_permission), Toast.LENGTH_SHORT).show();
            } else {
                CalendarWidget.requestUpdateAllFromType(this);

                updatePreview();
            }
        }

        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    private void resetSettings() {
        SP.edit().clear().commit();

        init();
    }

    private void checkForUpdate() {
        Toast.makeText(this, getString(R.string.toast_info_installer_checking_update), Toast.LENGTH_SHORT).show();

        new CheckUpdate((data) -> new Handler(Looper.getMainLooper()).post(() -> {
            if(data == null) {
                Toast.makeText(this, getString(R.string.toast_error_installer_error_update), Toast.LENGTH_SHORT).show();

                Log.e(TAG, "initListeners - popupMenu - update0: null");

                return;
            }

            try {
                if(getPackageManager().getPackageInfo(getPackageName(), 0).versionCode < data.getInt(Constants.Url.KeyTag.LATEST_VERSION_VERSION_CODE)) {
                    final String downloadUrl = data.getString(Constants.Url.KeyTag.LATEST_VERSION_LINK);

                    Toast.makeText(this, getString(R.string.toast_info_installer_update_downloading), Toast.LENGTH_SHORT).show();

                    Toast.makeText(this, getString(R.string.toast_info_installer_update_download_install), Toast.LENGTH_LONG).show();

                    Utils.openLink(this, downloadUrl);
                } else {
                    Toast.makeText(this, getString(R.string.toast_info_installer_is_up_to_date), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, getString(R.string.toast_error_installer_error_update), Toast.LENGTH_SHORT).show();

                Log.e(TAG, "initListeners - popupMenu - update1: " + e.getMessage());
            }
        })).start();
    }

    /** ResultOfMethodCallIgnored */
    private static class CheckUpdate extends Thread {
        private final String TAG = SettingsActivity.class.getSimpleName() + "." + CheckUpdate.class.getSimpleName();

        public interface CheckUpdateListener {
            void onResult(JSONObject json);
        }

        private final CheckUpdateListener LISTENER;

        public CheckUpdate(CheckUpdateListener l) {
            this.LISTENER = l;
        }

        @Override
        public void run() {
            String data = "";

            try {
                final URL url = new URL(Constants.Url.LATEST_VERSION_PHONE_FILE);

                final HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                final InputStream inputStream = httpURLConnection.getInputStream();

                final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line;

                while((line = bufferedReader.readLine()) != null) {
                    data += line;
                }

                this.LISTENER.onResult(new JSONObject(data));
            } catch (IOException | JSONException e) {
                this.LISTENER.onResult(null);

                Log.e(TAG, "run: " + e.getMessage());
            }
        }
    }
}