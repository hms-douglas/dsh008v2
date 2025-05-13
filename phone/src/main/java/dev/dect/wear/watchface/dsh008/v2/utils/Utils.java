package dev.dect.wear.watchface.dsh008.v2.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.appbar.AppBarLayout;

import java.util.Locale;

import dev.dect.dsh008.v2.CalendarDefaultUserSettings;
import dev.dect.dsh008.v2.CalendarUserSettings;
import dev.dect.wear.watchface.dsh008.v2.R;
import dev.dect.wear.watchface.dsh008.v2.data.Constants;
import dev.dect.wear.watchface.dsh008.v2.widget.CalendarWidget;

public class Utils {
    private final static String TAG = "Utils";

    public static void openLink(Context ctx, String link) {
        try {
            if(!link.startsWith("https")) {
                if (link.startsWith("http:")) {
                    link = link.replaceFirst("http:", "https:");
                } else {
                    link = "https://" + link;
                }
            }

            final Intent i = new Intent(Intent.ACTION_VIEW);

            i.addCategory(Intent.CATEGORY_BROWSABLE);

            i.setData(Uri.parse(link));

            ctx.startActivity(i);
        } catch (Exception e) {
            Toast.makeText(ctx, ctx.getString(R.string.toast_error_generic), Toast.LENGTH_SHORT).show();

            Log.e(TAG, "openLink: " + e.getMessage());
        }
    }

    public static void addAppbarEffectListener(AppBarLayout titleBar, View toolbar, View expanded, View collapsed) {
        titleBar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            verticalOffset = Math.abs(verticalOffset);

            final float opacity = (float) verticalOffset / ((appBarLayout.getHeight() - toolbar.getHeight()) / 2f);

            expanded.setAlpha(1 - opacity);

            collapsed.setAlpha(opacity != 1 ? opacity - 0.5f : 1);
        });
    }

    public static CalendarUserSettings getCurrentCalendarUserSettings(Context ctx) {
        final SharedPreferences sp = ctx.getSharedPreferences(Constants.Sp.SP, Context.MODE_PRIVATE);

        final CalendarUserSettings calendarUserSettings = new CalendarUserSettings();

        calendarUserSettings.setCalendarEventColors(sp.getString(Constants.Sp.CALENDAR_EVENTS_COLORS, CalendarDefaultUserSettings.CALENDAR_EVENTS_COLORS));
        calendarUserSettings.setCalendarBackgroundColor(sp.getInt(Constants.Sp.CALENDAR_BACKGROUND_COLOR, CalendarDefaultUserSettings.CALENDAR_BACKGROUND_COLOR));
        calendarUserSettings.setCalendarInfoTextColor(sp.getInt(Constants.Sp.CALENDAR_INFO_TEXT_COLOR, CalendarDefaultUserSettings.CALENDAR_INFO_TEXT_COLOR));
        calendarUserSettings.setIsToForceUserPalette(sp.getBoolean(Constants.Sp.CALENDAR_IS_TO_FORCE_PALETTE, CalendarDefaultUserSettings.CALENDAR_IS_TO_FORCE_PALETTE));
        calendarUserSettings.setIsToDrawBordersOnly(sp.getBoolean(Constants.Sp.CALENDAR_IS_TO_DRAW_BORDERS_ONLY, CalendarDefaultUserSettings.CALENDAR_IS_TO_DRAW_BORDERS_ONLY));
        calendarUserSettings.setIsToForceUppercase(sp.getBoolean(Constants.Sp.CALENDAR_IS_TO_FORCE_UPPERCASE, CalendarDefaultUserSettings.CALENDAR_IS_TO_FORCE_UPPERCASE));
        calendarUserSettings.setIsToShowAllDayEvent(sp.getBoolean(Constants.Sp.CALENDAR_IS_TO_SHOW_ALL_DAY_EVENT, CalendarDefaultUserSettings.CALENDAR_IS_TO_SHOW_ALL_DAY_EVENT));
        calendarUserSettings.setIsToShowHiddenEventNumber(sp.getBoolean(Constants.Sp.CALENDAR_IS_TO_SHOW_HIDDEN_EVENTS_NUMBER, CalendarDefaultUserSettings.CALENDAR_IS_TO_SHOW_HIDDEN_EVENTS_NUMBER));
        calendarUserSettings.setIsToShowRoundedCorners(sp.getBoolean(Constants.Sp.CALENDAR_IS_TO_SHOW_ROUNDED_CORNERS, CalendarDefaultUserSettings.CALENDAR_IS_TO_SHOW_ROUNDED_CORNERS));

        return calendarUserSettings;
    }

    public static int[] getHandsResIdByHandStyleId(int id) {
        int hour,
            minute,
            second;

        switch(id) {
            case 1:
                hour = R.drawable.hand_rounded_2_hour;
                minute = R.drawable.hand_rounded_2_minute;
                second = R.drawable.hand_rounded_2_second;
                break;

            case 2:
                hour = R.drawable.hand_rounded_3_hour;
                minute = R.drawable.hand_rounded_3_minute;
                second = R.drawable.hand_rounded_3_second;
                break;

            case 3:
                hour = R.drawable.hand_squared_1_hour;
                minute = R.drawable.hand_squared_1_minute;
                second = R.drawable.hand_squared_1_second;
                break;

            case 4:
                hour = R.drawable.hand_squared_2_hour;
                minute = R.drawable.hand_squared_2_minute;
                second = R.drawable.hand_squared_2_second;
                break;

            default:
                hour = R.drawable.hand_rounded_1_hour;
                minute = R.drawable.hand_rounded_1_minute;
                second = R.drawable.hand_rounded_1_second;
                break;
        }

        return new int[]{hour, minute, second};
    }

    public static void drawTransparencyBackgroundOnImageView(ImageView imageView, int width, int height) {
        final Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        final Canvas c = new Canvas(b);

        Utils.drawTransparencyBackgroundOnCanvas(c);

        imageView.setImageBitmap(b);
    }

    public static void drawColorSampleOnImageView(ImageView imageView, int color) {
        final int colorSize = 100;

        final Bitmap bitmap = Bitmap.createBitmap(colorSize, colorSize, Bitmap.Config.ARGB_8888);

        final Canvas canvas = new Canvas(bitmap);

        Utils.drawTransparencyBackgroundOnCanvas(canvas);

        final Paint paintColor = new Paint();

        paintColor.setStyle(Paint.Style.FILL);
        paintColor.setColor(color);

        canvas.drawRect(new RectF(0, 0, colorSize, colorSize), paintColor);

        final Bitmap cuttedBitmap = Bitmap.createBitmap(colorSize, colorSize, Bitmap.Config.ARGB_8888);

        final Canvas canvasCut = new Canvas(cuttedBitmap);

        final Rect rect = new Rect(0, 0, colorSize, colorSize);

        final Paint paintCut = new Paint();

        paintCut.setAntiAlias(true);

        canvasCut.drawARGB(0, 0, 0, 0);

        canvasCut.drawCircle(colorSize / 2f, colorSize / 2f, colorSize / 2f, paintCut);

        paintCut.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvasCut.drawBitmap(bitmap, rect, rect, paintCut);

        imageView.setImageBitmap(cuttedBitmap);
    }

    public static void drawTransparencyBackgroundOnCanvas(Canvas canvas) {
        final float height = canvas.getHeight(),
                    width = canvas.getWidth();

        final Paint paint = new Paint();

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#FDFDFD"));

        canvas.drawRect(new RectF(0, 0, width, height), paint);

        paint.setColor(Color.parseColor("#808080"));

        final float size = 16.5f,
                amountLines = height / size;

        for(float i = 0; i < amountLines; i++) {
            final float startXat = i % 2 == 0 ? 0 : size;

            for(float j = 0; j < ((width - startXat) / size); j += 2) {
                final float x = (size * j) + startXat,
                        y = size * i;

                canvas.drawRect(new RectF(x, y, x + size, y + size), paint);
            }
        }
    }

    public static void updateStatusBarColor(Activity activity) {
        boolean isLightMode = activity.getResources().getConfiguration().isNightModeActive();

        new WindowInsetsControllerCompat(activity.getWindow(), activity.getWindow().getDecorView()).setAppearanceLightStatusBars(!isLightMode);
    }

    public static class Popup {
        public static void setMaxHeight(Context ctx, ConstraintLayout view) {
            final int screenHeight = ctx.getSystemService(WindowManager.class).getCurrentWindowMetrics().getBounds().height(),
                    maxHeightPercentage = ctx.getResources().getInteger(R.integer.popup_max_height_percentage);

            view.setMaxHeight((screenHeight * maxHeightPercentage) / 100);
        }

        public static void callOutAnimation(Dialog dialog, ConstraintLayout container, ConstraintLayout view) {
            final Animation popupAnimationOut = AnimationUtils.loadAnimation(dialog.getContext(), R.anim.popup_out),
                    popupContainerOut = AnimationUtils.loadAnimation(dialog.getContext(), R.anim.popup_background_out);

            popupAnimationOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    dialog.dismiss();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            container.startAnimation(popupContainerOut);
            view.startAnimation(popupAnimationOut);
        }
    }

    public static class Converter {
        public static int hexColorToInt(String hex) {
            final int[] argb = hexColorToArgb(hex);

            return Color.argb(argb[0], argb[1], argb[2], argb[3]);
        }

        public static String intColorToHex(int i) {
            return intColorToHex(i, Color.alpha(i));
        }

        public static String intColorToHex(int i, int a) {
            final int r = Color.red(i),
                      g = Color.green(i),
                      b = Color.blue(i);

            final String rH = Integer.toHexString(r),
                    gH = Integer.toHexString(g),
                    bH = Integer.toHexString(b),
                    aH = Integer.toHexString(a);

            return "#" + Formatter.hexIndividualNumber(rH) + Formatter.hexIndividualNumber(gH) + Formatter.hexIndividualNumber(bH) + Formatter.hexIndividualNumber(aH);
        }

        public static int[] hexColorToArgb(String hex) {
            final int color = Color.parseColor(hex.substring(0, 7)),
                    alpha = Integer.valueOf(hex.substring(7), 16);

            return new int[] {alpha, Color.red(color), Color.green(color), Color.blue(color)};
        }
    }

    public static class Formatter {
        public static String hexIndividualNumber(String n) {
            return (n.length() == 1 ? "0" + n : n).toUpperCase(Locale.ROOT);
        }
    }

    public static class Preview {
        public static void update(Context ctx, FrameLayout parent) {
            final int size = parent.getWidth() == 0 ? 450 : parent.getWidth();

            parent.removeAllViews();

            final ImageView imageView = new ImageView(ctx);

            imageView.setBackgroundResource(R.drawable.circle);
            imageView.setClipToOutline(true);

            drawTransparencyBackgroundOnImageView(imageView, size, size);

            final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            );

            params.setMargins(2, 2, 2, 2);

            parent.addView(imageView, params);

            final RemoteViews remoteViews = CalendarWidget.getRemoteView(ctx, CalendarWidget.getCalendarDrawer(ctx).drawCalendar(size).addBorder(false).getDrawAsBitmap());

            final View preview = remoteViews.apply(ctx, parent);

            parent.addView(preview);
        }
    }
}
