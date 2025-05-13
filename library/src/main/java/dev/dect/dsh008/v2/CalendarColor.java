package dev.dect.dsh008.v2;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import org.json.JSONArray;

import java.util.Random;

public class CalendarColor {
    /**
     * Utils class to deal with color operations
     */

    private static final String TAG = CalendarColor.class.getSimpleName();

    //Used to determine which color has been used
    private static int COUNTER = -1;

    private static JSONArray USER_PALETTE;

    private static boolean FORCE_USER_PALETTE;

    public static void setUserPalette(CalendarUserSettings userSettings) {
        try {
            USER_PALETTE = new JSONArray(userSettings.getCalendarEventColors());
        } catch (Exception e) {
            USER_PALETTE = new JSONArray();

            Log.e(TAG, "setUserPalette: " + e.getMessage());
        }

        FORCE_USER_PALETTE = userSettings.isToForceUserPalette();
    }

    //Returns a new color, first from user palette, then from internal palette, then random color
    public static int generateColor(Context ctx) {
        COUNTER++;

        if(USER_PALETTE == null) {
            USER_PALETTE = new JSONArray();
        } else if(COUNTER <= (USER_PALETTE.length() - 1)) { //If there are colors set by the user, and they have not been used yet I return the next not used color
            try {
                return USER_PALETTE.getInt(COUNTER);
            } catch (Exception e) {
                Log.e(TAG, "generateColor: " + e.getMessage());
            }
        }

        final String[] palette = ctx.getResources().getStringArray(R.array.palette);

        final int index = COUNTER - USER_PALETTE.length();

        if(index > palette.length - 1) {
            //In case all colors from the palette has been used I generate a random color
            final Random random = new Random();

            return Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
        } else {
            return Color.parseColor(palette[index]);
        }
    }

    public static void resetColorCounter() {
        COUNTER = -1;
    }

    //Determines if the background color requires a dark foreground (Contrast ratio)
    private static boolean isDarkForegroundColor(int backgroundColor) {
        return ((0.299 * Color.red(backgroundColor) + 0.587 * Color.green(backgroundColor) + 0.114 * Color.blue(backgroundColor)) / 255) > 0.5;
    }

    //Returns the foreground color based on the background color (Contrast ratio)
    public static int textColorFor(Context ctx, int backgroundColor) {
        return isDarkForegroundColor(backgroundColor) ? ctx.getColor(R.color.event_color_text_dark) : ctx.getColor(R.color.event_color_text_light);
    }

    //Converts a color string to android int color
    public static int parseColor(Context ctx, String intColorString) {
        if((!FORCE_USER_PALETTE)
            && intColorString != null
            && !intColorString.isEmpty()
            && !intColorString.equals("0")
        ) {
            try {
                return Integer.parseInt(intColorString);
            } catch (Exception e) {
                Log.e(TAG, "parseColor: " + e.getMessage());
            }
        }

        //In case of error while converting the color, or in case of no color, returns a new "unique" color
        return CalendarColor.generateColor(ctx);
    }
}
