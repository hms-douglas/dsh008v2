package dev.dect.wear.watchface.dsh008.v2.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Switch;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;

import dev.dect.dsh008.v2.CalendarDefaultUserSettings;
import dev.dect.wear.watchface.dsh008.v2.R;
import dev.dect.wear.watchface.dsh008.v2.activity.helper.BaseActivity;
import dev.dect.wear.watchface.dsh008.v2.activity.input.InputColorActivity;
import dev.dect.wear.watchface.dsh008.v2.adapter.ColorAdapter;
import dev.dect.wear.watchface.dsh008.v2.data.Constants;
import dev.dect.wear.watchface.dsh008.v2.complication.helper.ComplicationUpdater;
import dev.dect.wear.watchface.dsh008.v2.popup.DialogPopup;

@SuppressLint({"ApplySharedPref", "UseSwitchCompatOrMaterialCode"})
public class SettingsEventColorsActivity extends BaseActivity {
    /**
     * Activity responsible for managing the user color palette
     */

    private final String TAG = SettingsEventColorsActivity.class.getSimpleName();

    private final int RESULT_ID_COLOR_INPUT = 0;

    @SuppressLint("StaticFieldLeak")
    private static SettingsEventColorsActivity ACTIVITY;

    private SharedPreferences SP;

    private JSONArray COLORS;

    private RecyclerView RECYCLER_VIEW;

    private ColorAdapter COLOR_ADAPTER;

    private Switch SWITCH_FORCE_PALETTE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_settings_event_colors);

        super.onCreate(savedInstanceState);

        setActivityTitle(R.string.event_colors);

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

    public static SettingsEventColorsActivity getInstance() {
        return ACTIVITY;
    }

    private void initVariable() {
        ACTIVITY = this;

        SP = getSharedPreferences(Constants.Sp.SP, MODE_PRIVATE);

        try {
            //Getting the colors set by the user
            COLORS = new JSONArray(SP.getString(Constants.Sp.CALENDAR_EVENTS_COLORS, CalendarDefaultUserSettings.CALENDAR_EVENTS_COLORS));
        } catch (Exception ignore) {
            COLORS = new JSONArray();
        }

        RECYCLER_VIEW = findViewById(R.id.recyclerView);

        COLOR_ADAPTER = new ColorAdapter(COLORS);

        SWITCH_FORCE_PALETTE = findViewById(R.id.switchForcePalette);
    }

    private void initListeners() {
        findViewById(R.id.btnAdd).setOnClickListener((v) -> {
            //Creates the color picker intent
            final Intent i = new Intent(this, InputColorActivity.class) ;

            if(COLORS.length() > 0) { //In case there is colors set by the user
                try {
                    //I set the color picker initial color to be the last color the user added
                    i.putExtra(InputColorActivity.EXTRA_INT_COLOR, COLORS.getInt(COLORS.length() - 1));
                } catch(Exception e) {
                    Log.e(TAG, "initListeners: " + e.getMessage());
                }
            }

            //Start the color picker
            startActivityForResult(i, RESULT_ID_COLOR_INPUT);
        });

        COLOR_ADAPTER.setListener((colors, index, color) -> {
            //Whenever the adapter changes (color added or removed) I save the color palette
            SP.edit().putString(Constants.Sp.CALENDAR_EVENTS_COLORS, colors.toString()).commit();

            //Request all complications to update, so the new palette can be used
            ComplicationUpdater.all(this);
        });

        SWITCH_FORCE_PALETTE.setOnCheckedChangeListener((v, b) -> onSwitchChangeSetting(Constants.Sp.CALENDAR_IS_TO_FORCE_PALETTE, b));

        findViewById(R.id.btnPreviewCalendar).setOnClickListener((v) -> PreviewActivity.showPreview(this, PreviewActivity.TYPE_CALENDAR));

        findViewById(R.id.btnRemoveAll).setOnClickListener((v) -> {
            new DialogPopup(
            this,
                R.string.more_remove_colors,
                DialogPopup.NO_TEXT,
                this::removeAllColors,
                null
            ).show();
        });
    }

    private void init() {
        showColors();

        SWITCH_FORCE_PALETTE.setChecked(SP.getBoolean(Constants.Sp.CALENDAR_IS_TO_FORCE_PALETTE, CalendarDefaultUserSettings.CALENDAR_IS_TO_FORCE_PALETTE));
    }

    //Render all the colors set in the palette
    private void showColors() {
        RECYCLER_VIEW.setNestedScrollingEnabled(false);

        RECYCLER_VIEW.setLayoutManager(new GridLayoutManager(this, 4));

        RECYCLER_VIEW.setAdapter(COLOR_ADAPTER);
    }

    public void updateUI() {
        startActivity(new Intent(getApplicationContext(), SettingsEventColorsActivity.class));

        finish();

        overridePendingTransition(0, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RESULT_ID_COLOR_INPUT && resultCode == RESULT_OK) { //When the user picks a color from the color picker
            //Adds the new color to the color array
            COLORS.put(data.getIntExtra(InputColorActivity.RESULT_INTENT_INT_COLOR, getColor(R.color.main)));

            //Saves the color palette
            SP.edit().putString(Constants.Sp.CALENDAR_EVENTS_COLORS, COLORS.toString()).commit();

            //Update the adapter to show the new color added
            COLOR_ADAPTER.notifyItemInserted(COLORS.length() - 1);
        }
    }

    //When a switch toggles
    private void onSwitchChangeSetting(String key, boolean b) {
        //Update the settings based on the key set
        SP.edit().putBoolean(key, b).commit();
    }

    private void removeAllColors() {
        final int length = COLORS.length();

        if(length > 0) {
            while(COLORS.length() > 0) {
                COLORS.remove(0);
            }

            SP.edit().remove(Constants.Sp.CALENDAR_EVENTS_COLORS).commit();

            //Update the adapter to show the new color added
            COLOR_ADAPTER.notifyItemRangeRemoved(0, length);

            Toast.makeText(this, getString(R.string.toast_success_done), Toast.LENGTH_SHORT).show();
        }
    }
}