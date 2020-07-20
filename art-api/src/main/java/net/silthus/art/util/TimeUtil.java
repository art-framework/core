/*
 * Copyright 2020 ART-Framework Contributors (https://github.com/Silthus/art-framework)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.silthus.art.util;

import com.google.common.base.Strings;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TimeUtil {

    public static final String TIME_DESC = "Use the 'time' notation: 1d20s10 --> 1 day 20 seconds 10 ticks. " +
            "Available values are: y(ear), m(onth), d(ay), h(our), m(inute), s(econds)";

    private TimeUtil() {

    }

    public static double secondsToMinutes(double seconds) {

        return ((int) ((seconds / 60.0) * 100)) / 100.0;
    }

    public static double millisToMinutes(long millis) {

        return secondsToMinutes(millisToSeconds(millis));
    }

    public static long yearsToMillis(long years) {

        return daysToMillis(years * 365L);
    }

    public static long weeksToMillis(long weeks) {

        return daysToMillis(weeks * 7);
    }

    public static long daysToMillis(long days) {

        return hoursToMillis(days * 24L);
    }

    public static long hoursToMillis(long hours) {

        return minutesToMillis(hours * 60L);
    }

    public static long minutesToMillis(long minutes) {

        return secondsToMillis(minutes * 60.0);
    }

    public static double millisToSeconds(long millis) {

        return ((int) (((double) millis / 1000.0) * 100.0)) / 100.0;
    }

    public static long secondsToMillis(double seconds) {

        return (long) (seconds * 1000);
    }

    public static String getFormattedTime(double seconds) {

        if (seconds > 60.0) {
            return secondsToMinutes(seconds) + "min";
        } else {
            return (((int) (seconds * 100)) / 100.0) + "s";
        }
    }

    public static String getAccurrateFormatedTime(long millis) {

        if (millis < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder(64);
        if (days > 0) {
            sb.append(days);
            sb.append(days > 1 ? " days " : " day ");
        }
        if (hours > 0) {
            sb.append(hours);
            sb.append(hours > 1 ? " hours " : " hour ");
        }
        if (minutes > 0) {
            sb.append(minutes);
            sb.append(minutes > 1 ? " minutes " : " minute ");
        }
        if (seconds > 0) {
            sb.append(seconds);
            sb.append(seconds > 1 ? " seconds " : " second ");
        }

        return (sb.toString());
    }

    public static String getAccurrateShortFormatedTime(long millis) {

        if (millis < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder(64);
        if (days > 0) {
            sb.append(days);
            sb.append("d");
        }
        if (hours > 0) {
            sb.append(hours);
            sb.append("h");
        }
        if (minutes > 0) {
            sb.append(minutes);
            sb.append("min");
        }
        if (seconds > 0) {
            sb.append(seconds);
            sb.append("s");
        }

        return (sb.toString());
    }

    // regexr.com/577cp
    private static final Pattern TIME_PATTERN = Pattern.compile("^((?<years>\\d+)y)?((?<months>\\d+)m)?((?<weeks>\\d+)w)?((?<days>\\d+)d)?((?<hours>\\d+)h)?((?<minutes>\\d+)m)?((?<seconds>\\d+)s)?(?<milliseconds>\\d+)?$");

    /**
     * Parses a given input string to ticks.
     * 20 ticks is one second.
     *
     * @param input to parse
     * @return ticks
     */
    public static long parseTimeAsMilliseconds(String input) {

        if (Objects.isNull(input)) return 0;
        Matcher matcher = TIME_PATTERN.matcher(input);
        if (!matcher.matches()) return 0;
        long result = 0;
        String years = matcher.group("years");
        if (!Strings.isNullOrEmpty(years)) {
            result += yearsToMillis(Long.parseLong(years));
        }
        String weeks = matcher.group("weeks");
        if (!Strings.isNullOrEmpty(weeks)) {
            result += weeksToMillis(Long.parseLong(weeks));
        }
        String days = matcher.group("days");
        if (!Strings.isNullOrEmpty(days)) {
            result += daysToMillis(Long.parseLong(days));
        }
        String hours = matcher.group("hours");
        if (!Strings.isNullOrEmpty(hours)) {
            result += hoursToMillis(Long.parseLong(hours));
        }
        String minutes = matcher.group("minutes");
        if (!Strings.isNullOrEmpty(minutes)) {
            result += minutesToMillis(Long.parseLong(minutes));
        }
        String seconds = matcher.group("seconds");
        if (!Strings.isNullOrEmpty(seconds)) {
            result += secondsToMillis(Long.parseLong(seconds));
        }
        String milliseconds = matcher.group("milliseconds");
        if (!Objects.isNull(milliseconds)) {
            result += Long.parseLong(milliseconds);
        }
        return result;
    }

    public static double parseTimeAsSeconds(String input) {
        return millisToSeconds(parseTimeAsMilliseconds(input));
    }
}