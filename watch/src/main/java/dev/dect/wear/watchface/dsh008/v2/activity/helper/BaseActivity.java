package dev.dect.wear.watchface.dsh008.v2.activity.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;

import com.google.android.wearable.input.RotaryEncoderHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

import dev.dect.wear.watchface.dsh008.v2.R;
import dev.dect.wear.watchface.dsh008.v2.data.Constants;

@SuppressLint("SimpleDateFormat")
public class BaseActivity extends Activity {
    /**
     * Basic/base activity to be extended
     * It provides a clock, title and bezel functions to reduce repetition in most activities
     */

    private final BroadcastReceiver CLOCK_RECEIVER = new BroadcastReceiver() { //Broadcast receiver for every time the minute changes
        @Override
        public void onReceive(Context context, Intent intent) {
            showTime();
        }
    };

    private TextView CLOCK_EL;

    private ScrollView SCROLLER_EL;

    private NestedScrollView NESTED_SCROLLER_EL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVariables();

        init();
    }

    @Override
    public void onResume() {
        final IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);

        registerReceiver(CLOCK_RECEIVER, filter);

        //Updates the clock when the user return to the activity
        showTime();

        super.onResume();
    }

    @Override
    public void onPause() {
        unregisterReceiver(CLOCK_RECEIVER);

        super.onPause();
    }

    //Adds the scroll by bezel rotation to the ScrollView/NestedScrollView if it exists in the activity
    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if(SCROLLER_EL != null) {
            //Determines the amount to scroll based on the rotation direction
            final int scrollTo = (int) (SCROLLER_EL.getY() + (RotaryEncoderHelper.getRotaryAxisValue(event) < 0 ? Constants.BEZEL_SCROLL_BY : (Constants.BEZEL_SCROLL_BY * -1)));

            SCROLLER_EL.smoothScrollBy(0, scrollTo);

            return true;
        } else if(NESTED_SCROLLER_EL != null) {
            //Determines the amount to scroll based on the rotation direction
            final int scrollTo = (int) (NESTED_SCROLLER_EL.getY() + (RotaryEncoderHelper.getRotaryAxisValue(event) < 0 ? Constants.BEZEL_SCROLL_BY : (Constants.BEZEL_SCROLL_BY * -1)));

            NESTED_SCROLLER_EL.smoothScrollBy(0, scrollTo);

            return true;
        }

        return false;
    }

    private void initVariables() {
        CLOCK_EL = findViewById(R.id.activityClock);

        SCROLLER_EL = findViewById(R.id.activityScrollview);

        NESTED_SCROLLER_EL = findViewById(R.id.activityNestedScrollview);
    }

    private void init() {
        //Updates the clock when the activity is created
        showTime();
    }

    private void showTime() {
        if(CLOCK_EL != null) {
            CLOCK_EL.setText(new SimpleDateFormat("HH:mm").format(new Date()));
        }
    }

    public void setActivityTitle(int resId) {
        ((TextView) findViewById(R.id.activityTitle)).setText(resId);
    }
}
