package dev.dect.wear.watchface.dsh008.v2.data;

import dev.dect.dsh008.v2.CalendarUserSettings;

public class Constants {
    public static class Sp {
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
            WIDGET_SHOW_SECOND = "p0",
            WIDGET_HOUR_COLOR = "p1",
            WIDGET_MINUTE_COLOR = "p2",
            WIDGET_SECOND_COLOR = "p3",
            WIDGET_HAND_STYLE = "p4",
            INSTALLER_IS_TO_WRAP_TEXT = "p5";
    }

    public static class DefaultSettings {
        public static final int
            WIDGET_HOUR_COLOR = -1315861,
            WIDGET_MINUTE_COLOR = -272549,
            WIDGET_SECOND_COLOR = -6645094,
            WIDGET_HAND_STYLE = 0;

        public static final boolean
            WIDGET_SHOW_SECOND = false,
            INSTALLER_IS_TO_WRAP_TEXT = true;
    }

    public static class Url {
        public static final String
            REPOSITORY = "https://github.com/hms-douglas/dsh008v2",
            REPOSITORY_DIST_PATH = REPOSITORY + "/raw/main/dist/",
            LATEST_VERSION_PHONE_FILE = REPOSITORY_DIST_PATH + "phone_latest.json",
            WATCH_VERSIONS_FILE = REPOSITORY_DIST_PATH + "watch_all.json";

        public static class KeyTag {
            public static final String
                LATEST_VERSION_VERSION_NAME = "versionName",
                LATEST_VERSION_VERSION_CODE = "versionCode",
                LATEST_VERSION_LINK = "link";
        }
    }

    public static class DataKey {
        public static final String
            PATH = "/dsh008v2",
            ACTION = "action";

        public static class Action {
            public static final String
                REFRESH_CALENDAR = "refresh",
                SET_SETTINGS = "settings",
                SET_PALETTE = "palette";
        }
    }

    public static final String
        REGEX_IPV4_AND_PORT = "\\b(?:(?:2(?:[0-4][0-9]|5[0-5])|[0-1]?[0-9]?[0-9])\\.){3}(?:(?:2([0-4][0-9]|5[0-5])|[0-1]?[0-9]?[0-9]))\\b:[0-9]{1,5}",
        VERSION_FILE_SEPARATOR = "_",
        VERSION_FILE_PREFIX = "version";
}
