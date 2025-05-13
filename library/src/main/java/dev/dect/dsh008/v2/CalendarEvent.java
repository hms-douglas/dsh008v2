package dev.dect.dsh008.v2;

import android.content.Context;

public class CalendarEvent {
    /**
     * Model class that holds a calendar event information
     */

    private final Context CONTEXT;

    private final String NAME,
                         INT_COLOR_STRING;

    private final boolean IS_ALL_DAY;

    private final long EVENTS_DAY_MIN_IN_MILLIS;

    private long START_TIME_IN_MILLIS,
                 END_TIME_IN_MILLIS;

    private int INT_COLOR = -1,
                START_ANGLE,
                END_ANGLE;

    private int[] ANGLES_IN_360;

    private boolean IS_CUT_AT_BEGINNING,
                    IS_CUT_AT_END;

    public CalendarEvent(Context ctx, String name, long startTimeInMillis, long endTimeInMillis, long eventsDayMinInMillis, boolean isAllDay, String intColorString) {
        this.CONTEXT = ctx;
        this.NAME = name;
        this.INT_COLOR_STRING = intColorString;
        this.START_TIME_IN_MILLIS = startTimeInMillis;
        this.END_TIME_IN_MILLIS = endTimeInMillis;
        this.EVENTS_DAY_MIN_IN_MILLIS = eventsDayMinInMillis;
        this.IS_CUT_AT_BEGINNING = false;
        this.IS_CUT_AT_END = false;

        if(isAllDay) {
            this.IS_ALL_DAY = true;
            this.START_ANGLE = 0;
            this.END_ANGLE = 360;
            this.ANGLES_IN_360 = new int[] {0, 360}; //All day events fits a full ring
        } else {
            this.IS_ALL_DAY = false;
            calculateAngles();
        }
    }

    public String getName() {
        return NAME;
    }

    public long getStartTimeInMillis() {
        return START_TIME_IN_MILLIS;
    }

    public long getEndTimeInMillis() {
        return END_TIME_IN_MILLIS;
    }

    public boolean isAllDay() {
        return IS_ALL_DAY;
    }

    public int getIntColor() {
        if(INT_COLOR == -1) { //In case the event color is not set
            this.INT_COLOR = CalendarColor.parseColor(CONTEXT, INT_COLOR_STRING); //Generates a color based on some rules
        }

        return INT_COLOR;
    }

    public int getStartAngle() {
        return START_ANGLE;
    }

    public int getEndAngle() {
        return END_ANGLE;
    }

    public int[] getAnglesIn360() {
        return ANGLES_IN_360;
    }

    public boolean isCutAtBeginnig() {
        return IS_CUT_AT_BEGINNING;
    }

    public boolean isCutAtEnd() {
        return IS_CUT_AT_END;
    }

    public void setCutStartTimeInMillis(long startTimeInMillis) {
        this.START_TIME_IN_MILLIS = startTimeInMillis;
        this.IS_CUT_AT_BEGINNING = true; //Means the start time is not the original/initial, it was set by code

        calculateAngles();
    }

    public void setCutEndTimeInMillis(long endTimeInMillis) {
        this.END_TIME_IN_MILLIS = endTimeInMillis;
        this.IS_CUT_AT_END = true; //Means the end time is not the original/initial, it was set by code

        calculateAngles();
    }

    private void calculateAngles() {
        this.START_ANGLE = millisTo720Angle(START_TIME_IN_MILLIS); //Converts the start time to range 0º - 720º (0 - 24 hours)
        this.END_ANGLE = millisTo720Angle(END_TIME_IN_MILLIS); //Converts the end time to range 0º - 720º (0 - 24 hours)

        if(START_ANGLE > 360) {
            /*
             * E.g.
             *   - From 370º - 700º (start - end)
             *   - To 10º - 340º    (start - end)
             */

            this.ANGLES_IN_360 = new int[] {
                START_ANGLE - 360,
                END_ANGLE - 360
            };
        } else if(END_ANGLE > 360) {
            if(START_ANGLE == 0) {
                //Just so the text stays in the correct place
                this.ANGLES_IN_360 = new int[]{0, 360};
                this.END_ANGLE = 360;

                return;
            }

            if(END_ANGLE == 719) {
                //Just so the text stays in the correct place, in this case would rotate twice
                this.ANGLES_IN_360 = new int[]{0, 360};
                this.START_ANGLE = 0;
                this.END_ANGLE = 360;

                return;
            }

            /*
             * E.g.
             *   - From 270º - 700º              (start - end)
             *   - To 270º - 360º and 0º - 340º  (start - end, and start - end)
             */
            this.ANGLES_IN_360 = new int[] {
                START_ANGLE,
                360,
                0,
                END_ANGLE - 360
            };
        } else {
            this.ANGLES_IN_360 = new int[] {
                START_ANGLE,
                END_ANGLE
            };
        }
    }

    private int millisTo720Angle(long millis) {
        return (int) (((millis - EVENTS_DAY_MIN_IN_MILLIS) * 720) / 86400000);
    }
}
