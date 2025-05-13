package dev.dect.wear.watchface.dsh008.v2.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;

import dev.dect.dsh008.v2.CalendarDefaultUserSettings;
import dev.dect.wear.watchface.dsh008.v2.R;
import dev.dect.wear.watchface.dsh008.v2.adapter.ColorAdapter;
import dev.dect.wear.watchface.dsh008.v2.communication.DataLayerSender;
import dev.dect.wear.watchface.dsh008.v2.data.Constants;
import dev.dect.wear.watchface.dsh008.v2.popup.ColorPickerPopup;
import dev.dect.wear.watchface.dsh008.v2.popup.DialogPopup;
import dev.dect.wear.watchface.dsh008.v2.utils.Utils;
import dev.dect.wear.watchface.dsh008.v2.widget.CalendarWidget;

/** @noinspection deprecation*/
@SuppressLint({"UseSwitchCompatOrMaterialCode", "ApplySharedPref"})
public class SettingsEventColorsActivity extends Activity {
    private final String TAG = SettingsEventColorsActivity.class.getSimpleName();

    private SharedPreferences SP;

    private Switch SWITCH_FORCE_PALETTE;

    private JSONArray COLORS;

    private ColorAdapter COLOR_ADAPTER;

    private RecyclerView RECYCLER_VIEW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings_event_color);

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

        SWITCH_FORCE_PALETTE = findViewById(R.id.switchForcePalette);

        RECYCLER_VIEW = findViewById(R.id.recyclerView);

        try {
            COLORS = new JSONArray(SP.getString(Constants.Sp.CALENDAR_EVENTS_COLORS, CalendarDefaultUserSettings.CALENDAR_EVENTS_COLORS));
        } catch (Exception ignore) {
            COLORS = new JSONArray();
        }

        COLOR_ADAPTER = new ColorAdapter(COLORS);
    }

    private void initListeners() {
        Utils.addAppbarEffectListener(
            findViewById(R.id.titleBar),
            findViewById(R.id.toolbar),
            findViewById(R.id.titleExpanded),
            findViewById(R.id.titleCollapsed)
        );

        SWITCH_FORCE_PALETTE.setOnCheckedChangeListener((v, b) -> onBasicSwitchChangeSetting(Constants.Sp.CALENDAR_IS_TO_FORCE_PALETTE, b, v.isPressed()));

        COLOR_ADAPTER.setListener((colors, index, color) -> {
            SP.edit().putString(Constants.Sp.CALENDAR_EVENTS_COLORS, colors.toString()).commit();

            updatePreview();
        });

        findViewById(R.id.btnMore).setOnClickListener((v) -> {
            v.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);

            final PopupMenu popupMenu = new PopupMenu(this, v, Gravity.END, 0, R.style.PopupMenu);

            final Menu menu = popupMenu.getMenu();

            popupMenu.getMenuInflater().inflate(R.menu.more_event_color, menu);

            menu.setGroupDividerEnabled(true);

            popupMenu.setOnMenuItemClickListener((item) -> {
                final int id = item.getItemId();

                if(id == R.id.remove) {
                    new DialogPopup(
                        this,
                        R.string.more_remove_colors,
                        R.string.more_remove_colors_description,
                        R.string.popup_btn_remove,
                        this::removeAllColors,
                        R.string.popup_btn_cancel,
                        null,
                        true,
                        false,
                        true
                    ).show();
                } else if(id == R.id.sync) {
                    new DialogPopup(
                        this,
                        R.string.more_sync_watch,
                        R.string.menu_settings_sync_colors_confirm_description,
                        R.string.popup_btn_sync,
                        ()-> {
                            new DataLayerSender(this).requestSettingsPaletteSync();

                            Toast.makeText(this, getString(R.string.toast_success_generic), Toast.LENGTH_SHORT).show();
                        },
                        R.string.popup_btn_cancel,
                        null,
                        true,
                        false,
                        true
                    ).show();
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

        findViewById(R.id.btnBack).setOnClickListener((v) -> {
            v.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);

            finish();
        });

        findViewById(R.id.btnAdd).setOnClickListener((v) -> {
            v.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);

            String initialColor;

            if(COLORS.length() > 0) {
                try {
                    initialColor = Utils.Converter.intColorToHex(COLORS.getInt(COLORS.length() - 1));
                } catch(Exception e) {
                    initialColor = Utils.Converter.intColorToHex(getColor(R.color.main));
                }
            } else {
                initialColor = Utils.Converter.intColorToHex(getColor(R.color.main));
            }

            new ColorPickerPopup(
                this,
                initialColor,
                false,
                (color, colorInt) -> {
                    COLORS.put(colorInt);

                    SP.edit().putString(Constants.Sp.CALENDAR_EVENTS_COLORS, COLORS.toString()).commit();

                    COLOR_ADAPTER.notifyItemInserted(COLORS.length() - 1);

                    updatePreview();
                }
            ).show();
        });
    }

    private void init() {
        SWITCH_FORCE_PALETTE.setChecked(SP.getBoolean(Constants.Sp.CALENDAR_IS_TO_FORCE_PALETTE, CalendarDefaultUserSettings.CALENDAR_IS_TO_FORCE_PALETTE));

        updatePreview();

        showColors();
    }

    private void showColors() {
        RECYCLER_VIEW.setNestedScrollingEnabled(false);

        RECYCLER_VIEW.setLayoutManager(new LinearLayoutManager(this));

        RECYCLER_VIEW.setAdapter(COLOR_ADAPTER);
    }

    private void updatePreview() {
        Utils.Preview.update(this, findViewById(R.id.previewContainer));
    }

    private void onBasicSwitchChangeSetting(String key, boolean b, boolean userInput) {
        if(userInput) {
            SP.edit().putBoolean(key, b).commit();

            updatePreview();
        }
    }

    private void removeAllColors() {
        final int length = COLORS.length();

        if(length > 0) {
            while(COLORS.length() > 0) {
                COLORS.remove(0);
            }

            SP.edit().remove(Constants.Sp.CALENDAR_EVENTS_COLORS).commit();

            COLOR_ADAPTER.notifyItemRangeRemoved(0, length);

            updatePreview();
        }
    }
}
