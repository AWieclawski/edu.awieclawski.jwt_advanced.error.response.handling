package edu.awieclawski.app.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateTimeUtils {

    public static final String  TIME_PATTERN = "yyyy-MM-dd hh:mm:ss.SSS";

    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(TIME_PATTERN);

    static ThreadLocal<DateFormat> localDateFormat = new ThreadLocal<DateFormat>() {
        @Override
        public SimpleDateFormat initialValue() {
            return new SimpleDateFormat(TIME_PATTERN);
        }
    };

    public static String timestampToString(Timestamp timestamp) {
        return SIMPLE_DATE_FORMAT.format(timestamp);
    }

    public static String timestampToStringMulti(Timestamp timestamp) {
        return localDateFormat.get().format(timestamp);
    }

    public static String dateToString(Date timestamp) {
        return SIMPLE_DATE_FORMAT.format(timestamp);
    }

    public static String dateToStringMulti(Date timestamp) {
        return localDateFormat.get().format(timestamp);
    }

    public static Timestamp stringToTimestamp(String timestampString) {
        try {
            Date parsedDate = SIMPLE_DATE_FORMAT.parse(timestampString);
            return new java.sql.Timestamp(parsedDate.getTime());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public static Timestamp stringToTimestampMulti(String timestampString) {
        try {
            Date parsedDate = localDateFormat.get().parse(timestampString);
            return new java.sql.Timestamp(parsedDate.getTime());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public static Date stringToDate(String timestampString) {
        try {
            return SIMPLE_DATE_FORMAT.parse(timestampString);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public static Date stringToDateMulti(String timestampString) {
        try {
            return localDateFormat.get().parse(timestampString);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

}
