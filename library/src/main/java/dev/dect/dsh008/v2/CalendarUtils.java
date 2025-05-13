package dev.dect.dsh008.v2;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.appcompat.content.res.AppCompatResources;

public class CalendarUtils {
    //Change the color of a drawable
    public static Drawable getTintedDrawable(Context ctx, int resId, int color) {
        final Drawable cutDrawable = AppCompatResources.getDrawable(ctx, resId);

        if(cutDrawable != null) {
            //Set the icon color to meet the event text color
            cutDrawable.setTint(color);
        }

        return cutDrawable;
    }

    public static float percentageFromDimension(float dim, float percentage) {
        //E.g. how much is 10% of 450? 45
        return dim * (percentage / 100f);
    }
}
