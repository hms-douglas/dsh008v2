package dev.dect.wear.watchface.dsh008.v2.activity.input;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.madrapps.pikolo.HSLColorPicker;
import com.madrapps.pikolo.listeners.OnColorSelectionListener;

import dev.dect.dsh008.v2.CalendarColor;
import dev.dect.wear.watchface.dsh008.v2.R;

public class InputColorActivity extends Activity {
    /**
     * Activity that hold the color picker
     *
     * This activity returns the color selected (ActivityForResult) in the "RESULT_INTENT_INT_COLOR" static variable
     * The initial color can be set through intent ("EXTRA_INT_COLOR" / as android int color)
     */

    public static final String RESULT_INTENT_INT_COLOR = "result_color_int",
                               EXTRA_INT_COLOR = "extra_color_int";

    private ConstraintLayout BTN_COLOR_SAMPLE_EL,
                             ICON_SAMPLE_EL;

    private HSLColorPicker COLOR_PICKER_EL;

    private Drawable DRAWABLE_BTN_BACKGROUND,
                     DRAWABLE_BTN_ICON;

    private int COLOR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_color_picker);

        initVariable();

        initListeners();

        init();
    }

     /*
     * Required so the user can leave the screen even if their back button is set to another function
     * Deprecated but seems to be the only one working on some models
     */
    /** @noinspection deprecation*/
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        setResult(RESULT_CANCELED);

        finish();
    }

    //Required so the user can leave the screen even if their back button is set to another function
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        finish();

        setResult(RESULT_CANCELED);

        return true;
    }

    private void initVariable() {
        BTN_COLOR_SAMPLE_EL = findViewById(R.id.btnConfirm);
        ICON_SAMPLE_EL = findViewById(R.id.iconConfirm);

        COLOR_PICKER_EL = findViewById(R.id.sliderColor);

        DRAWABLE_BTN_BACKGROUND = ContextCompat.getDrawable(this, R.drawable.circle);
        DRAWABLE_BTN_ICON = ContextCompat.getDrawable(this, R.drawable.icon_confirm);

        //Gets the color set in the intent or get the app main color / Sets the initial color
        COLOR = getIntent().getIntExtra(EXTRA_INT_COLOR, getColor(R.color.main));
    }

    private void initListeners() {
        COLOR_PICKER_EL.setColorSelectionListener(new OnColorSelectionListener() {
            @Override
            public void onColorSelected(int i) { //While the user is dragging the color picker handlers
                showColor(i);
            }

            @Override
            public void onColorSelectionStart(int i) {}

            @Override
            public void onColorSelectionEnd(int i) { //When the user finishes selecting the color
                showColor(i);
                COLOR = i;
            }
        });

        //When the user click on the "done" button, returns the color picked
        BTN_COLOR_SAMPLE_EL.setOnClickListener((l) -> {
            final Intent intent = new Intent();

            intent.putExtra(RESULT_INTENT_INT_COLOR, COLOR);

            setResult(RESULT_OK, intent);

            finish();
        });
    }

    /** @noinspection KotlinInternalInJava*/
    private void init() {
        //Set the color picker initial color
        COLOR_PICKER_EL.setColor(COLOR);

        showColor(COLOR);
    }

    /*
     * Updates the color preview
     * It will show the background and the foreground (text) color
     */
    private void showColor(int color) {
        DRAWABLE_BTN_BACKGROUND.setTint(color);
        DRAWABLE_BTN_ICON.setTint(CalendarColor.textColorFor(this, color));

        BTN_COLOR_SAMPLE_EL.setBackground(DRAWABLE_BTN_BACKGROUND);
        ICON_SAMPLE_EL.setBackground(DRAWABLE_BTN_ICON);
    }
}