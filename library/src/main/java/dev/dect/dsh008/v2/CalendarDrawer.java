package dev.dect.dsh008.v2;

import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;
import android.util.Range;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.wear.provider.WearableCalendarContract;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class CalendarDrawer {
    /**
     * Utils class responsible for getting all events and drawing them on canvas
     *
     * Consider the time from 0 to 24, no am/pm:
     */

    private final static String TAG = Calendar.class.getSimpleName();

    private final Context CONTEXT;

    private final List<CalendarEvent> EVENTS;

    /*
     * Lists containing the ranges (degrees) from all events
     *
     * E.g.
     *   - RANGES_FIRST_RING = [0, 47, 55, 180] / first ring, range 0deg to 47deg used
     *   - RANGES_SECOND_RING = [2, 33] / second ring, range 3deg to 33deg used
     */
    private final List<Integer> RANGES_FIRST_RING,
                                RANGES_SECOND_RING,
                                RANGES_THIRD_RING;

    private final Paint ARC_PAINT,
                        ARC_TEXT_PAINT,
                        ARC_BORDER_PAINT;

    private final CalendarUserSettings USER_SETTINGS;

    private int CANVAS_SIZE,
                HALF_CANVAS_SIZE;

    private float ARC_WIDTH,
                  HALF_ARC_WIDTH,
                  ARC_BORDER_WIDTH,
                  SPACE_BETWEEN_ARCS,
                  FONT_SIZE;
    
    private long TODAY_MIN_MILLIS,
                 TODAY_MAX_MILLIS;

    private int NOT_SHOWING_EVENTS;

    private Bitmap BITMAP;

    private Canvas CANVAS;

    public CalendarDrawer(Context ctx, CalendarUserSettings userSettings) {
        this.CONTEXT = ctx;
        
        this.EVENTS = new ArrayList<>();
        this.RANGES_FIRST_RING = new ArrayList<>();
        this.RANGES_SECOND_RING = new ArrayList<>();
        this.RANGES_THIRD_RING = new ArrayList<>();

        //Sets the basic arc style, so I only have to change the color for each "ring"
        this.ARC_PAINT = new Paint();
        this.ARC_PAINT.setStyle(Paint.Style.STROKE);

        //Sets the border arc style
        this.ARC_BORDER_PAINT = new Paint();
        this.ARC_BORDER_PAINT.setStyle(Paint.Style.STROKE);
        this.ARC_BORDER_PAINT.setBlendMode(BlendMode.DST_OUT);

        //Sets the basic arc text style, so I only have to change the color for each "ring" text (the event name)
        this.ARC_TEXT_PAINT = new Paint();
        this.ARC_TEXT_PAINT.setTextAlign(Paint.Align.CENTER);
        this.ARC_TEXT_PAINT.setFakeBoldText(true);
        this.ARC_TEXT_PAINT.setLetterSpacing(0.1f);

        this.USER_SETTINGS = userSettings;
    }

    //Used for the complication (watch face)
    public CalendarDrawer drawCalendar() {
        setSize(450); //450x450 is the watch face default width and height. It is not set according to the screen resolution because WFF scales it according to the watch face setting, which is set statically to 450x450 (WFF limitations)

        renderCalendar();

        return this;
    }

    //Used for widget and phone preview
    public CalendarDrawer drawCalendar(int size) {
        setSize(size);

        renderCalendar();

        return this;
    }

    //Used for the tile and tile preview
    public CalendarDrawer drawTile() {
        setSize(CONTEXT.getResources().getDisplayMetrics().widthPixels);

        renderCalendar();

        if(USER_SETTINGS.isToShowLastUpdateTimeOnTile()) {
            renderTimeNow(); //So the user can now when the image was drawn
        }

        //As tiles does not support "hands" I draw them
        renderHands(
            Calendar.getInstance(),
            true,
            USER_SETTINGS.isToShowMinuteHandOnTile(),
            false
        );

        return this;
    }

    //Used for watch preview only
    public CalendarDrawer drawWatchFacePreview() {
        setSize(CONTEXT.getResources().getDisplayMetrics().widthPixels);

        renderCalendar();

        final Calendar calendar = Calendar.getInstance();

        //Default time used on watch face preview, lol
        calendar.set(Calendar.HOUR_OF_DAY, 10);
        calendar.set(Calendar.MINUTE, 8);
        calendar.set(Calendar.SECOND, 32);

        renderHands(calendar, true, true, true);

        return this;
    }

    public CalendarDrawer addBorder(boolean keepDrawSize) {
        return addBorder((int) SPACE_BETWEEN_ARCS, keepDrawSize);
    }

    public CalendarDrawer addBorder(int borderSizePx, boolean keepDrawSize) {
        Bitmap bitmap;

        Canvas canvas;

        final Paint paint = new Paint();

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(USER_SETTINGS.getCalendarBackgroundColor());

        if(keepDrawSize) {
            bitmap = Bitmap.createBitmap(CANVAS_SIZE, CANVAS_SIZE, Bitmap.Config.ARGB_8888);

            canvas = new Canvas(bitmap);

            canvas.drawCircle(HALF_CANVAS_SIZE, HALF_CANVAS_SIZE, HALF_CANVAS_SIZE, paint);

            canvas.drawBitmap(
                BITMAP,
                new Rect(0, 0, CANVAS_SIZE, CANVAS_SIZE),
                new Rect(borderSizePx, borderSizePx, CANVAS_SIZE - borderSizePx, CANVAS_SIZE - borderSizePx),
                new Paint()
            );
        } else {
            final int borderHelper = borderSizePx * 2;

            bitmap = Bitmap.createBitmap(CANVAS_SIZE + borderHelper, CANVAS_SIZE + borderHelper, Bitmap.Config.ARGB_8888);

            canvas = new Canvas(bitmap);

            canvas.drawCircle(HALF_CANVAS_SIZE + borderSizePx, HALF_CANVAS_SIZE + borderSizePx, HALF_CANVAS_SIZE + borderSizePx, paint);

            canvas.drawBitmap(BITMAP, borderSizePx, borderSizePx, new Paint());
        }

        BITMAP = bitmap;
        CANVAS = canvas;

        return this;
    }

    //true in case there is no event rendered
    public boolean isDrawEmpty() {
        return EVENTS.isEmpty();
    }

    //Used on complication/watch face
    public Icon getDrawAsIcon() {
        return Icon.createWithBitmap(BITMAP);
    }

    //Used on widget
    public Bitmap getDrawAsBitmap() {
        return BITMAP;
    }

    //Used on tile
    public byte[] getDrawAsByteArray() {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        BITMAP.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

        return  byteArrayOutputStream.toByteArray();
    }

    public CalendarEvent getNextEndingCalendarEvent() {
        if(EVENTS.isEmpty()) {
            return null;
        }

        CalendarEvent calendarEvent = null;

        final List<CalendarEvent> events = new ArrayList<>(EVENTS);

        events.sort(Comparator.comparing(CalendarEvent::getEndTimeInMillis));

        for(CalendarEvent event : events) {
            if(!event.isAllDay()) {
                calendarEvent = event;

                break;
            }
        }

        return calendarEvent;
    }

    private void setSize(int size) {
        if(size <= 0) {
            size = 450;
        }

        this.CANVAS_SIZE = size;
        this.HALF_CANVAS_SIZE = CANVAS_SIZE / 2; //Used to reduce repeating calculation

        this.ARC_WIDTH = CalendarUtils.percentageFromDimension(CANVAS_SIZE, 5.194f); //The width of the "ring" | Assuming a circular/square screen, not rectangle
        this.HALF_ARC_WIDTH = ARC_WIDTH / 2; //Used to reduce repeating calculation
        this.ARC_BORDER_WIDTH = ARC_WIDTH - 4;

        this.SPACE_BETWEEN_ARCS = CalendarUtils.percentageFromDimension(CANVAS_SIZE, 0.472f); //The space between the "rings" | Assuming a circular/square screen, not rectangle
        this.FONT_SIZE = ARC_WIDTH - CalendarUtils.percentageFromDimension(CANVAS_SIZE, 0.694f); //The font of the events name, not the complications | Assuming a circular/square screen, not rectangle

        this.ARC_PAINT.setStrokeWidth(ARC_WIDTH);

        this.ARC_BORDER_PAINT.setStrokeWidth(ARC_BORDER_WIDTH);

        this.ARC_TEXT_PAINT.setTextSize(FONT_SIZE);
    }

    private void renderCalendar() {
        initVariables();

        initCanvas();

        loadEvents();

        renderEvents();

        renderBackground();
    }

    private void initVariables() {
        NOT_SHOWING_EVENTS = 0;

        final Calendar calendar = Calendar.getInstance();

        //Set the today's time for the minimum
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        TODAY_MIN_MILLIS = calendar.getTimeInMillis();

        //Set the today's time for the maximum
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        TODAY_MAX_MILLIS = calendar.getTimeInMillis();

        if(USER_SETTINGS.isToShowRoundedCorners()) {
            ARC_PAINT.setStrokeCap(Paint.Cap.ROUND);
            ARC_BORDER_PAINT.setStrokeCap(Paint.Cap.ROUND);
        } else {
            ARC_PAINT.setStrokeCap(Paint.Cap.BUTT);
            ARC_BORDER_PAINT.setStrokeCap(Paint.Cap.BUTT);
        }
    }

    private void initCanvas() {
        BITMAP = Bitmap.createBitmap(CANVAS_SIZE, CANVAS_SIZE, Bitmap.Config.ARGB_8888);

        CANVAS = new Canvas(BITMAP);
    }

    private void loadEvents() {
        EVENTS.clear();

        //Resets the color counter so it can reuse the colors
        CalendarColor.resetColorCounter();

        //Add the user color palette
        CalendarColor.setUserPalette(USER_SETTINGS);

        //Time range to fetch the events from
        final long[] startEndTimeInMillis = getStartEndSearchTimeInMillis();

        Uri.Builder builder =
            CONTEXT.getPackageManager().hasSystemFeature(PackageManager.FEATURE_WATCH) //Whether is a watch using this library
            ? WearableCalendarContract.Instances.CONTENT_URI.buildUpon() //WearableCalendarContract syncs automatically to wearable devices
            : CalendarContract.Instances.CONTENT_URI.buildUpon(); //CalendarContract does not syncs automatically to wearable devices, but is recommended to phones

        ContentUris.appendId(builder, startEndTimeInMillis[0]);
        ContentUris.appendId(builder, startEndTimeInMillis[1]);

        Cursor cursor;
        
        try {
            cursor = CONTEXT.getContentResolver().query(builder.build(), null, null, null, null);
        } catch (Exception e) {
            Log.e(TAG, "loadEvents1: " + e.getMessage());

            //No events loaded, nothing will be rendered...

            return;
        }

        if(cursor != null) {
            //Just to separate the types of events to "filter"
            final ArrayList<CalendarEvent> allDayEventsList = new ArrayList<>(),
                                           otherEventsList = new ArrayList<>();

            final Calendar calendar = Calendar.getInstance();

            final long nowInMillis = calendar.getTimeInMillis();

            final int todayDate = calendar.get(Calendar.DATE);

            calendar.add(Calendar.HOUR_OF_DAY, 12);

            final long nowPlus12hInMillis = calendar.getTimeInMillis();

            while(cursor.moveToNext()) {
                try {
                    final long eventEndingTime = cursor.getLong(cursor.getColumnIndexOrThrow(CalendarContract.Instances.END));

                    final String eventName = USER_SETTINGS.isToForceUppercase() ? cursor.getString(cursor.getColumnIndexOrThrow(CalendarContract.Instances.TITLE)).toUpperCase(Locale.ROOT) : cursor.getString(cursor.getColumnIndexOrThrow(CalendarContract.Instances.TITLE));

                    if(cursor.getInt(cursor.getColumnIndexOrThrow(CalendarContract.Instances.ALL_DAY)) == 1) { //If it is an all day event
                        final Calendar calendarHelper = Calendar.getInstance();

                        calendarHelper.setTime(new Date(eventEndingTime));

                        if(calendarHelper.get(Calendar.DATE) == todayDate) { //If it is an all day event from today. I get events from yesterday and tomorrow as well, that's the reason of this if
                            if(USER_SETTINGS.isToShowAllDayEvent()) {
                                allDayEventsList.add(
                                    new CalendarEvent(
                                        CONTEXT,
                                        eventName,
                                        cursor.getLong(cursor.getColumnIndexOrThrow(CalendarContract.Instances.BEGIN)),
                                        eventEndingTime,
                                        TODAY_MIN_MILLIS,
                                        true,
                                        cursor.getString(cursor.getColumnIndexOrThrow(CalendarContract.Instances.EVENT_COLOR))
                                    )
                                );
                            } else {
                                //Today event, but the user does not want to render it
                                NOT_SHOWING_EVENTS++;
                            }
                        }
                    } else if(eventEndingTime > nowInMillis) { //If it is not an all day event, and it has not ended yet. Events that has ended will not be listed/rendered.
                        final long eventBeginningTime = cursor.getLong(cursor.getColumnIndexOrThrow(CalendarContract.Instances.BEGIN));

                        final CalendarEvent event = new CalendarEvent(
                            CONTEXT,
                            eventName,
                            eventBeginningTime,
                            eventEndingTime,
                            TODAY_MIN_MILLIS,
                            false,
                            cursor.getString(cursor.getColumnIndexOrThrow(CalendarContract.Instances.EVENT_COLOR))
                        );

                        if(eventBeginningTime < TODAY_MIN_MILLIS) { //If the event started yesterday
                            /*
                             * Set the start time to the min of today (00:00)
                             *
                             * E.g.
                             *   - Event from 21 (9 night, yesterday) until 2 (morning, today);
                             *   - It will set from 00:00 until 02:00
                             */

                            event.setCutStartTimeInMillis(TODAY_MIN_MILLIS);
                        } else if(eventBeginningTime > nowPlus12hInMillis) { //If the event starts after the hour hand in the next 12h period
                            /*
                             *   ----------------
                             *   -     12       -    (0/12/24)
                             *   - 9    *-->  3 - (9/21)   (3/15)
                             *   -      6       -      (6/18)
                             *   ----------------
                             * E.g.
                             *   - The clock now is at 3;
                             *   - The events starts at 16 and ends at 17;
                             *   - It would render as from 4 to 5;
                             *   - This "if" fixes it, it does not add the event to the list. Therefore this event won't render;
                             *   - (In future I might add a flag and render this event with some opacity or another indicator, anyway...)
                             */

                            NOT_SHOWING_EVENTS++;

                            continue;
                        }

                        if(event.getEndTimeInMillis() > nowPlus12hInMillis) { //If the event starts before the current hour hand and ends after the hour hand in the next 12h period
                            /*
                             *   ----------------
                             *   -     12       -    (0/12/24)
                             *   - 9    *-->  3 - (9/21)   (3/15)
                             *   -      6       -      (6/18)
                             *   ----------------
                             *
                             * E.g.
                             *   - The clock now is at 3;
                             *   - The events starts at 13 and ends at 17;
                             *   - It would render as from 1 to 5, as if it was an ongoing event, which is not yet;
                             *   - This "if" fixes it, it will now set as 1 to 3, and on the next complication update it will increase the end time, until it reaches its max
                             */

                            if(USER_SETTINGS.isToShowRoundedCorners()) {
                                /*
                                 * 540000 = 9 min
                                 *
                                 * The "Paint.Cap.ROUND" draws over the max angle, this will prevent the event from being always under the hour hand indicator
                                 */
                                event.setCutEndTimeInMillis(nowPlus12hInMillis - 540000);
                            } else {
                                /*
                                 * 240000 = 4 min
                                 *
                                 * This will prevent the event from being always under the hour hand indicator
                                 */
                                event.setCutEndTimeInMillis(nowPlus12hInMillis - 240000);
                            }
                        }

                        if(event.getEndTimeInMillis() > TODAY_MAX_MILLIS) { //If the event ends tomorrow
                            /*
                             * Set the start time to the max of today (24:00)
                             *
                             * E.g.
                             *   - Event from 21 (9 night, today) until 2 (morning, tomorrow);
                             *   - It will set from 21:00 until 24:00
                             */

                            event.setCutEndTimeInMillis(TODAY_MAX_MILLIS);
                        }

                        otherEventsList.add(event);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "loadEvents2: " + e.getMessage());
                }
            }

            cursor.close();

            //Sort all the (other) events listed for the current time period based on their start time
            otherEventsList.sort(Comparator.comparing(CalendarEvent::getStartTimeInMillis));

            //Adds all events to the main list
            EVENTS.addAll(allDayEventsList);
            EVENTS.addAll(otherEventsList);
        }
    }

    private long[] getStartEndSearchTimeInMillis() {
        final Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        //86400000 = 24 hours
        return new long[]{calendar.getTimeInMillis(), calendar.getTimeInMillis() + 86400000};
    }

    private void renderBackground() {
        final Paint p = new Paint();

        p.setColor(USER_SETTINGS.getCalendarBackgroundColor());
        p.setStyle(Paint.Style.FILL);
        p.setBlendMode(BlendMode.DST_OVER);

        CANVAS.drawCircle(HALF_CANVAS_SIZE, HALF_CANVAS_SIZE, HALF_CANVAS_SIZE, p);
    }

    private void renderEvents() {
        if(EVENTS.isEmpty()) {
            if(!renderNotShowing()) { //If there is no events not being rendered
                renderNoEvents();
            }
        } else {
            RANGES_FIRST_RING.clear();
            RANGES_SECOND_RING.clear();
            RANGES_THIRD_RING.clear();

            for(CalendarEvent calendarEvent : EVENTS) {
                if(calendarEvent.isAllDay()) {
                    renderAllDayEventIfPossible(calendarEvent);
                } else {
                    renderPartialEventIfPossible(calendarEvent);
                }
            }

            renderNotShowing();
        }
    }

    private void renderNoEvents() {
        final Paint p = new Paint();

        p.setColor(USER_SETTINGS.getCalendarInfoTextColor());
        p.setTextSize(FONT_SIZE + 3);
        p.setTextAlign(Paint.Align.CENTER);
        p.setFakeBoldText(true);
        p.setShadowLayer(3, 0, 0, Color.BLACK);

        //Draws a "no event" text at the middle of the bottom of the calendar
        CANVAS.drawText(CONTEXT.getString(R.string.no_events), HALF_CANVAS_SIZE, HALF_CANVAS_SIZE + (HALF_CANVAS_SIZE / 2f) + FONT_SIZE, p);
    }

    private boolean renderNotShowing() {
        if(NOT_SHOWING_EVENTS > 0 && USER_SETTINGS.isToShowHiddenEventNumber()) {
            final Paint paint = new Paint();

            paint.setColor(USER_SETTINGS.getCalendarInfoTextColor());
            paint.setTextSize(FONT_SIZE + 2);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setFakeBoldText(true);
            paint.setShadowLayer(3, 0, 0, Color.BLACK);

            //Draws the amount of event not being rendered (E.g.: +3) at the middle of the bottom of the calendar
            CANVAS.drawText(NOT_SHOWING_EVENTS < 10 ? "+" + NOT_SHOWING_EVENTS : String.valueOf(NOT_SHOWING_EVENTS), HALF_CANVAS_SIZE, HALF_CANVAS_SIZE + (HALF_CANVAS_SIZE / 2f) - FONT_SIZE, paint);

            return true;
        }

        return false;
    }

    private void renderAllDayEventIfPossible(CalendarEvent calendarEvent) {
        int ring;

        if(RANGES_FIRST_RING.isEmpty()) { //If the first ring is not being used yet
            RANGES_FIRST_RING.add(0);   //Start range being used
            RANGES_FIRST_RING.add(360); //End range being used

            ring = 0; //First ring, used to calculate the radius
        } else if(RANGES_SECOND_RING.isEmpty()) {
            RANGES_SECOND_RING.add(0);
            RANGES_SECOND_RING.add(360);

            ring = 1;
        } else if(RANGES_THIRD_RING.isEmpty()) {
            RANGES_THIRD_RING.add(0);
            RANGES_THIRD_RING.add(360);

            ring = 2;
        } else { //If all rings are occupied, it increments the not showing events as it will not render the event
            NOT_SHOWING_EVENTS++;

            return;
        }

        //Calculates the radius based on the Nth ring
        final float radius = HALF_CANVAS_SIZE - HALF_ARC_WIDTH - (ring * ARC_WIDTH) - (ring * SPACE_BETWEEN_ARCS),
                    radiusText = radius - (FONT_SIZE / 2f) + 1.5f;

        //Draws the full ring
        CANVAS.drawCircle(HALF_CANVAS_SIZE, HALF_CANVAS_SIZE, radius, getArcPaint(calendarEvent.getIntColor()));

        if(USER_SETTINGS.isToDrawBordersOnly()) {
            CANVAS.drawCircle(HALF_CANVAS_SIZE, HALF_CANVAS_SIZE, radius, ARC_BORDER_PAINT);
        }

        final Path path = new Path();

        //set the path to draw the text at the middle of the right side of the calendar, to prevent the the "notification indicator" of the watch face to cover it
        path.addArc(HALF_CANVAS_SIZE - radiusText, HALF_CANVAS_SIZE - radiusText, HALF_CANVAS_SIZE + radiusText, HALF_CANVAS_SIZE + radiusText, 180, 360);

        //Draws the text (event name) over the ring
        CANVAS.drawTextOnPath(calendarEvent.getName(), path, 0f, 0f, getTextArcPaint(calendarEvent.getIntColor()));
    }

    private void renderPartialEventIfPossible(CalendarEvent calendarEvent) {
        //Get the ring with the event range available, so rings won't overlap
        final int ring = ringForEvent(calendarEvent);

        if(ring == -1) { //The event ring range is not available in any ring
            NOT_SHOWING_EVENTS++;

            //This event won't render

            return;
        }

        //How much "angle" this range covers
        final float sweepAngle = Math.abs(calendarEvent.getEndAngle() - calendarEvent.getStartAngle());

        final float radius = HALF_CANVAS_SIZE - HALF_ARC_WIDTH - (ring * ARC_WIDTH) - (ring * SPACE_BETWEEN_ARCS);

        CANVAS.drawArc(HALF_CANVAS_SIZE - radius, HALF_CANVAS_SIZE - radius, HALF_CANVAS_SIZE + radius, HALF_CANVAS_SIZE + radius, calendarEvent.getStartAngle() - 90, sweepAngle, false, getArcPaint(calendarEvent.getIntColor()));

        if(USER_SETTINGS.isToDrawBordersOnly()) {
            if(USER_SETTINGS.isToShowRoundedCorners()) {
                CANVAS.drawArc(HALF_CANVAS_SIZE - radius, HALF_CANVAS_SIZE - radius, HALF_CANVAS_SIZE + radius, HALF_CANVAS_SIZE + radius, calendarEvent.getStartAngle() - 90, sweepAngle, false, ARC_BORDER_PAINT);
            } else {
                CANVAS.drawArc(HALF_CANVAS_SIZE - radius, HALF_CANVAS_SIZE - radius, HALF_CANVAS_SIZE + radius, HALF_CANVAS_SIZE + radius, calendarEvent.getStartAngle() - 90 + 0.8f, sweepAngle - 1.6f, false, ARC_BORDER_PAINT);
            }
        }

        //The center of the arc, in angle, to help determine the text orientation
        final int arcMiddleAngle = (int) (calendarEvent.getStartAngle() + (sweepAngle / 2));

        final Path path = new Path();

        /*
         * This if-else is to prevent the text to be upside down when it is in the bottom half of the watch screen
         */
        if(Range.create(90, 270).contains(arcMiddleAngle) || Range.create(450, 630).contains(arcMiddleAngle)) { //If the center of the text is in the bottom half of the screen
            final float radiusText = radius + (FONT_SIZE / 2.5f);

            path.addArc(HALF_CANVAS_SIZE - radiusText, HALF_CANVAS_SIZE - radiusText, HALF_CANVAS_SIZE + radiusText, HALF_CANVAS_SIZE + radiusText, calendarEvent.getStartAngle() - 90 + sweepAngle, -sweepAngle);
        } else { //If the center of the text is in the top half of the screen
            final float radiusText = radius - (FONT_SIZE / 2.5f);

            path.addArc(HALF_CANVAS_SIZE - radiusText, HALF_CANVAS_SIZE - radiusText, HALF_CANVAS_SIZE + radiusText, HALF_CANVAS_SIZE + radiusText, calendarEvent.getStartAngle() - 90, sweepAngle);
        }

        //Draws the text (event name) over the ring
        CANVAS.drawTextOnPath(calendarEvent.getName(), path, 0f, 0f, getTextArcPaint(calendarEvent.getIntColor()));

        if(calendarEvent.isCutAtEnd() || calendarEvent.isCutAtBeginnig()) { //In case the event start/end time was changed by my code, draws a scissor icon
            final Bitmap cutBitmap = getCutBitmap(ARC_TEXT_PAINT.getColor(), radius); //Scissor icon

            final Paint p = new Paint();

            p.setAntiAlias(true);

            if(calendarEvent.isCutAtEnd()) { //Draw the scissor at the end of the event, in this case the end time was changed by my code
                CANVAS.save();

                //Rotates the canvas instead of rotating the image, easier calculation
                if(USER_SETTINGS.isToShowRoundedCorners()) {
                    CANVAS.rotate(calendarEvent.getEndAngle(), HALF_CANVAS_SIZE, HALF_CANVAS_SIZE);
                } else {
                    CANVAS.rotate(calendarEvent.getEndAngle() - 4, HALF_CANVAS_SIZE, HALF_CANVAS_SIZE);
                }

                CANVAS.drawBitmap(cutBitmap, 0,0, p);

                CANVAS.restore();
            }

            if(calendarEvent.isCutAtBeginnig()) { //Draw the scissor at the beginning of the event, in this case the start time was changed by my code
                CANVAS.save();

                //Rotates the canvas instead of rotating the image, easier calculation
                if(USER_SETTINGS.isToShowRoundedCorners()) {
                    CANVAS.rotate(calendarEvent.getStartAngle(), HALF_CANVAS_SIZE, HALF_CANVAS_SIZE);
                } else {
                    CANVAS.rotate(calendarEvent.getStartAngle() + 4, HALF_CANVAS_SIZE, HALF_CANVAS_SIZE);
                }

                CANVAS.drawBitmap(cutBitmap, 0,0, p);

                CANVAS.restore();
            }
        }
    }

    private void renderTimeNow() {
        final Paint p = new Paint();

        p.setColor(USER_SETTINGS.getCalendarInfoTextColor());
        p.setTextSize(FONT_SIZE + 5);
        p.setFakeBoldText(true);
        p.setTextAlign(Paint.Align.CENTER);
        p.setFakeBoldText(true);
        p.setShadowLayer(3, 0, 0, Color.BLACK);

        //Shows the current time (user format) at the middle of the top half of the calendar
        CANVAS.drawText(DateFormat.getTimeInstance().format(System.currentTimeMillis()), HALF_CANVAS_SIZE, HALF_CANVAS_SIZE - (HALF_CANVAS_SIZE / 2f) + FONT_SIZE, p);
    }

    private void renderHands(Calendar calendar, boolean hour, boolean minute, boolean second) {
        final float secRotateAngle = calendar.get(Calendar.SECOND) * 6f, // 60sec * 6 = 360 E.g.: 10min = 60º
                    minRotateAngle = calendar.get(Calendar.MINUTE) * 6f, // 60min * 6 = 360 E.g.: 48min = 288º
                    /*
                     * 12 * 30 = 360 or 24 * 30 = 720
                     *
                     * E.g. 10:30
                     *
                     *    10h = 300º
                     *    +
                     *    30min = 15º //this is for the hour hand to not be "sticky" to the hour mark
                     *                //12 marks in the clock would be 360º / 12 = 30º, so each hour (60 min) covers a range of 30º from the 360º
                     */
                    hourRotateAngle = (calendar.get(Calendar.HOUR) * 30) + (calendar.get(Calendar.MINUTE) / 2f);

        /*
         * 22 is half the hand width, 44px
         * 275 is the hand height
         * 225 is half 450 (default watch face size, the hands were designed for a 450 screen)
         * 450 is the default watch face size (the hands were designed for a 450 screen)
         *
         * In here I scale the width and the height
         */
        final int halfWidthScaledToCanvas = (HALF_CANVAS_SIZE * 22) / 225,
                  heightScaledToCanvas = (CANVAS_SIZE * 275) / 450 ;

        final Rect handRect = new Rect(HALF_CANVAS_SIZE - halfWidthScaledToCanvas, 0, HALF_CANVAS_SIZE + halfWidthScaledToCanvas, heightScaledToCanvas);

        //Once again I rotate the canvas instead of the image, easier calculation
        CANVAS.save();

        CANVAS.rotate(secRotateAngle, HALF_CANVAS_SIZE, HALF_CANVAS_SIZE);

        if(second) {
            renderHandHelper(R.drawable.hand_rounded_1_second, "#9A9A9A", handRect); //#9A9A9A = Default second hand color
        }

        //The canvas already rotated this amount, so I subtract it
        CANVAS.rotate(minRotateAngle - secRotateAngle, HALF_CANVAS_SIZE, HALF_CANVAS_SIZE);

        if(minute) {
            renderHandHelper(R.drawable.hand_rounded_1_minute, "#FBD75B", handRect); //#FBD75B = Default minute hand color
        }

        //The canvas has now rotated this amount, so I subtract it as well
        CANVAS.rotate(hourRotateAngle - minRotateAngle, HALF_CANVAS_SIZE, HALF_CANVAS_SIZE);

        if(hour) {
            renderHandHelper(R.drawable.hand_rounded_1_hour, "#EBEBEB", handRect); //#EBEBEB = Default hour hand color
        }

        CANVAS.restore();
    }

    private void renderHandHelper(int handResId, String tintColorHex, Rect rectBound) {
        final Drawable hand = AppCompatResources.getDrawable(CONTEXT, handResId); //Get the hand

        if(hand != null) {
            hand.setTintBlendMode(BlendMode.MODULATE); //To multiply the color of the image by the color I set to tint

            hand.setTint(Color.parseColor(tintColorHex));

            hand.setBounds(rectBound);

            hand.draw(CANVAS);
        }
    }

    private int ringForEvent(CalendarEvent calendarEvent) {
        final int[] ranges = calendarEvent.getAnglesIn360();

        if(areRangesAvailableInList(ranges, RANGES_FIRST_RING)) { //Check if the angle range of the event is available in the first ring
            addRangesToList(ranges, RANGES_FIRST_RING); //Add the range to the first ring

            return 0; //First ring
        } else if(areRangesAvailableInList(ranges, RANGES_SECOND_RING)) {
            addRangesToList(ranges, RANGES_SECOND_RING);

            return 1;
        } else if(areRangesAvailableInList(ranges, RANGES_THIRD_RING)) {
            addRangesToList(ranges, RANGES_THIRD_RING);

            return 2;
        }

        return -1; //No range available in any ring
    }

    private boolean areRangesAvailableInList(int[] ranges, List<Integer> list) {
        for(int i = 0; i < list.size(); i += 2) {
            final Range<Integer> range = Range.create(Math.min(list.get(i), list.get(i + 1)), Math.max(list.get(i), list.get(i + 1)));

            for(int j = 0; j < ranges.length; j += 2) {
                if(range.contains(ranges[j]) || range.contains(ranges[j + 1])) { //If the start and end event range is being used in tis ring
                    return false;
                }
            }
        }

        return true;
    }

    private void addRangesToList(int[] ranges, List<Integer> list) {
        for(int range : ranges) {
            list.add(range);
        }
    }

    private Paint getArcPaint(int color) {
        ARC_PAINT.setColor(color);

        return ARC_PAINT;
    }

    private Paint getTextArcPaint(int backgroundColor) {
        //In case of borders only, the text color is the same of the event, otherwise the text color will be decided by the contrast ratio
        ARC_TEXT_PAINT.setColor(USER_SETTINGS.isToDrawBordersOnly() ? backgroundColor : CalendarColor.textColorFor(CONTEXT, backgroundColor));

        return ARC_TEXT_PAINT;
    }

    private Bitmap getCutBitmap(int color, float radius) {
        //Set the icon size to fit inside the border
        final int iconSize = (int) ARC_BORDER_WIDTH - 2,
                  halfIconSize = iconSize / 2;

        //I use the radius to prevent having to calculate the ring position, easier
        final int top = (int) (HALF_CANVAS_SIZE - radius + HALF_ARC_WIDTH - iconSize - 2);

        final Drawable cutDrawable = CalendarUtils.getTintedDrawable(CONTEXT, R.drawable.icon_event_cut, color);

        /*
         * Creates a bitmap of the size of the default canvas size
         *
         * This is to easy the rotation of the image according to the angle,
         * in this case I will only need to rotate this image from the center by the
         * event start/end angle, eliminating the need to use matrix if I was drawing the
         * icon at that given position.
         */
        final Bitmap bitmap = Bitmap.createBitmap(CANVAS.getWidth(), CANVAS.getHeight(), Bitmap.Config.ARGB_8888);

        final Canvas canvas = new Canvas(bitmap);

        cutDrawable.setBounds(HALF_CANVAS_SIZE - halfIconSize, top, HALF_CANVAS_SIZE + halfIconSize, top + iconSize);

        cutDrawable.draw(canvas);

        return bitmap;
    }
}
