package dev.dect.wear.watchface.dsh008.v2.data;

import dev.dect.dsh008.v2.CalendarUserSettings;

public class Constants {
    /**
     * Class that holds static constants used
     */

    public static class Sp {
        /*
         * All shared preferences keys
         */
        public static final String
            SP = "dsh008v2",
            CALENDAR_EVENTS_COLORS = CalendarUserSettings.K_CALENDAR_EVENTS_COLORS,
            CALENDAR_INFO_TEXT_COLOR = CalendarUserSettings.K_CALENDAR_INFO_TEXT_COLOR,
            CALENDAR_IS_TO_SHOW_ALL_DAY_EVENT = CalendarUserSettings.K_CALENDAR_IS_TO_SHOW_ALL_DAY_EVENT,
            CALENDAR_IS_TO_SHOW_HIDDEN_EVENTS_NUMBER = CalendarUserSettings.K_CALENDAR_IS_TO_SHOW_HIDDEN_EVENTS_NUMBER,
            CALENDAR_IS_TO_SHOW_ROUNDED_CORNERS = CalendarUserSettings.K_CALENDAR_IS_TO_SHOW_ROUNDED_CORNERS,
            CALENDAR_IS_TO_DRAW_BORDERS_ONLY = CalendarUserSettings.K_CALENDAR_IS_TO_DRAW_BORDERS_ONLY,
            CALENDAR_IS_TO_FORCE_PALETTE = CalendarUserSettings.K_CALENDAR_IS_TO_FORCE_PALETTE,
            CALENDAR_IS_TO_FORCE_UPPERCASE = CalendarUserSettings.K_CALENDAR_IS_TO_FORCE_UPPERCASE,
            CALENDAR_BACKGROUND_COLOR = CalendarUserSettings.K_CALENDAR_BACKGROUND_COLOR,
            TILE_IS_TO_SHOW_MINUTE_HAND = CalendarUserSettings.K_TILE_IS_TO_SHOW_MINUTE_HAND,
            TILE_IS_TO_SHOW_LAST_UPDATE_TIME = CalendarUserSettings.K_TILE_IS_TO_SHOW_LAST_UPDATE_TIME;
    }

    public static class Url {
        public static final String
            REPOSITORY = "https://github.com/hms-douglas/dsh008v2",
            REPOSITORY_DIST_PATH = REPOSITORY + "/raw/main/dist/",
            LATEST_VERSION_FILE = REPOSITORY_DIST_PATH + "watch_latest.json";

        public static class KeyTag {
            public static final String
                LATEST_VERSION_VERSION_CODE = "versionCode",
                LATEST_VERSION_LINK = "link";
        }
    }

    public static class DataKey {
        public static final String
            ACTION = "action";

        public static class Action {
            public static final String
                REFRESH_CALENDAR = "refresh",
                SET_SETTINGS = "settings",
                SET_PALETTE = "palette";
        }
    }

    public static final int
        BEZEL_SCROLL_BY = 90;
}
