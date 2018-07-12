package sample.mpandroidchartstest;

import android.text.format.DateFormat;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class DateUtil {

    enum DateScale {
        WEEK(7),
        MONTH(30),
        QUARTER(91),
        YEAR(365);

        private int id;

        DateScale(int id) {
            this.id = id;
        }
        public int getId() {
            return this.id;
        }
    }

    /**
     * 今日の日付をマップで出力
     *
     * @param weekName 曜日名の配列
     * @return hashMap
     */
    public static HashMap<String, String> getTodayMap(String[] weekName) {
        Calendar cal = Calendar.getInstance();

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("year", Integer.toString(cal.get(Calendar.YEAR)));
        hashMap.put("month", Integer.toString(cal.get(Calendar.MONTH) + 1));
        hashMap.put("day", Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));
        hashMap.put("week", weekName[cal.get(Calendar.DAY_OF_WEEK)]);

        return hashMap;
    }

    /**
     * 今日の日付を指定フォーマット出力
     */
    public static String getTodayFormat(String format) {
        return getCalendarFormat(format, Calendar.getInstance());
    }

    /**
     * フォーマット指定でCalendar文字列を返す
     *
     * @param format   String
     * @param calendar Calendar
     * @return
     */
    public static String getCalendarFormat(String format, Calendar calendar) {
        return (String) DateFormat.format(format, calendar);
    }

    /**
     * 文字列 "yyyyMMdd" をCalendar型にして返す
     *
     * @param yyyyMMdd String
     * @return Calendar
     */
    public static Calendar getSettingCalendar(String yyyyMMdd) {
        final String match = "^[0-9]*$";
        if (!yyyyMMdd.matches(match) || yyyyMMdd.length() != 8) {
            yyyyMMdd = getTodayFormat("yyyyMMdd");
        }

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.clear();
        cal.set(Calendar.YEAR, Integer.parseInt(yyyyMMdd.substring(0, 4)));
        cal.set(Calendar.MONTH, Integer.parseInt(yyyyMMdd.substring(4, 6)) - 1);
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(yyyyMMdd.substring(6, 8)));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal;
    }

    /**
     * Calendarから月を返す
     *
     * @param calendar Calendar
     * @return int
     */
    public static int getCalendarMonth(Calendar calendar) {
        return calendar.get(Calendar.MONTH);
    }

    /**
     * Caledarから日を返す
     *
     * @param calendar Caledar
     * @return int
     */
    public static int getCalendarDay(Calendar calendar) {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 指定日の月末を返す
     *
     * @param yyyyMMdd String
     * @return Calendar
     */
    public static Calendar getEndOfMonth(String yyyyMMdd) {
        Calendar calendar = getSettingCalendar(yyyyMMdd);
        int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, lastDay);
        return calendar;
    }

    /**
     * yyyyMMddでCaledarを比較する
     *
     * @param calendar1 Calendar_1
     * @param calendar2 Calendar_2
     * @return boolean
     */
    public static boolean compareCalendar(Calendar calendar1, Calendar calendar2) {
        String cal1 = getCalendarFormat("yyyyMMdd", calendar1);
        String cal2 = getCalendarFormat("yyyyMMdd", calendar2);
        if (cal1.equals(cal2)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * HH24MIの時間文字列をHH24とMIに分割しListで出力する
     *
     * @param hh24mm 時分の文字列
     * @return List(0):hour / List(1):min
     */
    public static List<Integer> changeStringTimeToIntTime(String hh24mm) {
        List<Integer> list = new ArrayList<>();

        // 時と分のデリミタがあれば除去
        String time = hh24mm.replaceAll(":", "");

        try {
            list.add(Integer.parseInt(time.substring(0, 2)));
            list.add(Integer.parseInt(time.substring(2)));
        } catch (Exception ignored) {
            list = null;
        }
        return list;
    }

    /**
     * 指定24時間フォーマットを12時間フォーマットで返す
     *
     * @param hour 時
     * @param min  分
     * @return String
     */
    public static String changeH24toH12(int hour, int min) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);

        // AM/PMは後付がスタンダード
        return getCalendarFormat("hh:mm a", calendar);
    }

    /**
     * タイムゾーン一覧を取得
     *
     * @return
     */
    public static List<String> getTimezoneItems() {
        String[] ids = TimeZone.getAvailableIDs();
        List items = new ArrayList<>();
        for (String item : ids) {
            items.add(item.toString());
        }
        return items;
    }

    /**
     * 端末に設定されているタイムゾーンを返す
     *
     * @return
     */
    public static String getCurrentTimezone() {
        Calendar calendar = Calendar.getInstance();
        TimeZone zone = calendar.getTimeZone();

        return zone.getID();
    }

    /**
     * タイムスタンプを返す
     * @param yyyymmdd
     * @param pattern
     * @return long
     */
    public static long getTimestamp(String yyyymmdd, String pattern) {
        long timestamp = 0l;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            Date date = sdf.parse(yyyymmdd);
            timestamp = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timestamp;
    }

    /**
     * 当該月の最初の日を返す
     * @param yyyymmdd
     * @return
     */
    public static int getFirstDay(String yyyymmdd) {
        Calendar cal = getSettingCalendar(yyyymmdd);
        return cal.getActualMinimum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 当該月の最終日(day)を返す
     * @param yyyymmdd
     * @return
     */
    public static int getLastDay(String yyyymmdd) {
        Calendar cal = getSettingCalendar(yyyymmdd);
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }


    public static int getDiffDays(Calendar from, Calendar to) {
        float diff = Math.abs(from.getTimeInMillis() - to.getTimeInMillis());
        int interval = 1000 * 60 * 60 * 24;
        return (int) (diff / interval);
    }

    public static int getDiffMonths(Calendar from, Calendar to) {
        int count = 0;
        while (from.before(to)) {
            from.add(Calendar.MONTH, 1);
            count++;
        }

        return count;
    }
}
