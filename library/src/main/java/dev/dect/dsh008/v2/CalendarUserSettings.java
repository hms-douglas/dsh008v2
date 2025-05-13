package dev.dect.dsh008.v2;

public class CalendarUserSettings {
    public static final String K_TILE_IS_TO_SHOW_LAST_UPDATE_TIME = "cus0",
                               K_TILE_IS_TO_SHOW_MINUTE_HAND = "cus1",
                               K_CALENDAR_IS_TO_DRAW_BORDERS_ONLY = "cus2",
                               K_CALENDAR_IS_TO_SHOW_ALL_DAY_EVENT = "cus3",
                               K_CALENDAR_IS_TO_SHOW_HIDDEN_EVENTS_NUMBER = "cus4",
                               K_CALENDAR_IS_TO_SHOW_ROUNDED_CORNERS = "cus5",
                               K_CALENDAR_IS_TO_FORCE_PALETTE = "cus6",
                               K_CALENDAR_IS_TO_FORCE_UPPERCASE = "cus7",
                               K_CALENDAR_INFO_TEXT_COLOR = "cus8",
                               K_CALENDAR_BACKGROUND_COLOR = "cus9",
                               K_CALENDAR_EVENTS_COLORS = "cus10";

    private boolean TILE_IS_TO_SHOW_LAST_UPDATE_TIME,
                    TILE_IS_TO_SHOW_MINUTE_HAND,
                    CALENDAR_IS_TO_DRAW_BORDERS_ONLY,
                    CALENDAR_IS_TO_SHOW_ALL_DAY_EVENT,
                    CALENDAR_IS_TO_SHOW_HIDDEN_EVENTS_NUMBER,
                    CALENDAR_IS_TO_SHOW_ROUNDED_CORNERS,
                    CALENDAR_IS_TO_FORCE_PALETTE,
                    CALENDAR_IS_TO_FORCE_UPPERCASE;

    private int CALENDAR_INFO_TEXT_COLOR,
                CALENDAR_BACKGROUND_COLOR;

    private String CALENDAR_EVENTS_COLORS;

    public CalendarUserSettings() {
        this.TILE_IS_TO_SHOW_LAST_UPDATE_TIME = CalendarDefaultUserSettings.TILE_IS_TO_SHOW_LAST_UPDATE_TIME;
        this.TILE_IS_TO_SHOW_MINUTE_HAND = CalendarDefaultUserSettings.TILE_IS_TO_SHOW_MINUTE_HAND;
        this.CALENDAR_IS_TO_DRAW_BORDERS_ONLY = CalendarDefaultUserSettings.CALENDAR_IS_TO_DRAW_BORDERS_ONLY;
        this.CALENDAR_IS_TO_SHOW_ALL_DAY_EVENT = CalendarDefaultUserSettings.CALENDAR_IS_TO_SHOW_ALL_DAY_EVENT;
        this.CALENDAR_IS_TO_SHOW_HIDDEN_EVENTS_NUMBER = CalendarDefaultUserSettings.CALENDAR_IS_TO_SHOW_HIDDEN_EVENTS_NUMBER;
        this.CALENDAR_IS_TO_SHOW_ROUNDED_CORNERS = CalendarDefaultUserSettings.CALENDAR_IS_TO_SHOW_ROUNDED_CORNERS;
        this.CALENDAR_IS_TO_FORCE_PALETTE = CalendarDefaultUserSettings.CALENDAR_IS_TO_FORCE_PALETTE;
        this.CALENDAR_IS_TO_FORCE_UPPERCASE = CalendarDefaultUserSettings.CALENDAR_IS_TO_FORCE_UPPERCASE;

        this.CALENDAR_INFO_TEXT_COLOR = CalendarDefaultUserSettings.CALENDAR_INFO_TEXT_COLOR;
        this.CALENDAR_BACKGROUND_COLOR = CalendarDefaultUserSettings.CALENDAR_BACKGROUND_COLOR;

        this.CALENDAR_EVENTS_COLORS = CalendarDefaultUserSettings.CALENDAR_EVENTS_COLORS;
    }

    public boolean isToShowLastUpdateTimeOnTile() {
        return TILE_IS_TO_SHOW_LAST_UPDATE_TIME;
    }

    public void setIsToShowLastUpdateTimeOnTile(boolean b) {
        this.TILE_IS_TO_SHOW_LAST_UPDATE_TIME = b;
    }

    public boolean isToShowMinuteHandOnTile() {
        return TILE_IS_TO_SHOW_MINUTE_HAND;
    }

    public void setIsToShowMinuteHandOnTile(boolean b) {
        this.TILE_IS_TO_SHOW_MINUTE_HAND = b;
    }

    public boolean isToDrawBordersOnly() {
        return CALENDAR_IS_TO_DRAW_BORDERS_ONLY;
    }

    public void setIsToDrawBordersOnly(boolean b) {
        this.CALENDAR_IS_TO_DRAW_BORDERS_ONLY = b;
    }

    public boolean isToShowAllDayEvent() {
        return CALENDAR_IS_TO_SHOW_ALL_DAY_EVENT;
    }

    public void setIsToShowAllDayEvent(boolean b) {
        this.CALENDAR_IS_TO_SHOW_ALL_DAY_EVENT = b;
    }

    public boolean isToShowHiddenEventNumber() {
        return CALENDAR_IS_TO_SHOW_HIDDEN_EVENTS_NUMBER;
    }

    public void setIsToShowHiddenEventNumber(boolean b) {
        this.CALENDAR_IS_TO_SHOW_HIDDEN_EVENTS_NUMBER = b;
    }

    public boolean isToShowRoundedCorners() {
        return CALENDAR_IS_TO_SHOW_ROUNDED_CORNERS;
    }

    public void setIsToShowRoundedCorners(boolean b) {
        this.CALENDAR_IS_TO_SHOW_ROUNDED_CORNERS = b;
    }

    public boolean isToForceUserPalette() {
        return CALENDAR_IS_TO_FORCE_PALETTE;
    }

    public void setIsToForceUserPalette(boolean b) {
        this.CALENDAR_IS_TO_FORCE_PALETTE = b;
    }

    public boolean isToForceUppercase() {
        return CALENDAR_IS_TO_FORCE_UPPERCASE;
    }

    public void setIsToForceUppercase(boolean b) {
        this.CALENDAR_IS_TO_FORCE_UPPERCASE = b;
    }

    public int getCalendarInfoTextColor() {
        return CALENDAR_INFO_TEXT_COLOR;
    }

    public void setCalendarInfoTextColor(int color) {
        this.CALENDAR_INFO_TEXT_COLOR = color;
    }

    public String getCalendarEventColors() {
        return CALENDAR_EVENTS_COLORS;
    }

    public void setCalendarEventColors(String color) {
        this.CALENDAR_EVENTS_COLORS = color;
    }

    public int getCalendarBackgroundColor() {
        return CALENDAR_BACKGROUND_COLOR;
    }

    public void setCalendarBackgroundColor(int color) {
        this.CALENDAR_BACKGROUND_COLOR = color;
    }
}
