package net.hneb.jxetyy.utils;

import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by zhangshuai on 2020/6/20.
 */
public class DateUtil {
    public final static String DATE_YYYYMMDDHH = "yyyyMMddHHmmss";
    public final static String DATE_YYYYMMDDHHSSS= "yyyyMMddHHmmssSSS";

    public final static String DATE_YYYY_MM_DD_HH = "yyyy-MM-dd HH:mm:ss";

    public final static String DATE_YYYY_MM_DD = "yyyy-MM-dd";

    public final static String DATE_YYYY_MM = "yyyy-MM";

    public final static String DATE_YYYY = "yyyy";

    public final static String DATE_YYYYMM = "yyyyMM";

    public final static String DATE_YYYYMMDD = "yyyyMMdd";

    public final static String DATE_HHMMSS = "HH:mm:ss";

    public final static String DATE_HHMM = "HH:mm";

    public final static String DATE_MM_DD = "MM-dd";

    public final static Integer DATE_TYPE_YEAR = 1;

    public final static Integer DATE_TYPE_MONTH = 2;

    public final static Integer DATE_TYPE_DAY = 3;
    private static String format;


    /**
     * 将时间转换成字符串
     *
     * @param date 时间 默认格式 yyyy-MM-dd
     * @return
     */
    public static Date strToDate(String date) {
        return strToDate(date, null);
    }

    /**
     * 将时间转换成日期格式
     *
     * @param date 时间字符串
     * @param type 时间格式 可以为null
     * @return
     */
    public static Date strToDate(String date, String type) {
        SimpleDateFormat sdf = new SimpleDateFormat(StringUtils.isEmpty(type) ? DATE_YYYY_MM_DD_HH : type);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date calDay(Date date, Integer num) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, num);
        return c.getTime();
    }

    public static String calDay(Date date, Integer num, String type) {
        Date time = calDay(date, num);
        return getDateStr(time, type);
    }

    public static Date calHour(Date date, Integer num) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.HOUR_OF_DAY, num);
        return c.getTime();
    }

    public static String calHour(Date date, Integer num, String type) {
        Date time = calHour(date, num);
        return getDateStr(time, type);
    }

    public static Date calMin(Date date, int num) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MINUTE, num);
        return c.getTime();
    }

    public static Date calSec(Date date, int second) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.SECOND, second);
        return c.getTime();
    }

    public static Date calMonth(Date date, Integer num) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MONTH, num);
        return c.getTime();
    }

    public static Date calYear(Date date, Integer num) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.YEAR, num);
        return c.getTime();
    }

    /**
     * 去掉时分秒
     *
     * @param date 时间
     * @return 去掉后的时间
     */
    public static Date getDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_YYYY_MM_DD);
        try {
            return sdf.parse(sdf.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getDateStr(Date date, String type) {
        if (date == null)
            return "";
        SimpleDateFormat sdf = new SimpleDateFormat(StringUtils.isEmpty(type) ? DATE_YYYY_MM_DD_HH : type);
        return sdf.format(date);
    }

    public static String getDateStr(Date date) {
        return getDateStr(date, null);
    }

    /**
     * 根据年份 月份 日期 类型转换成时间
     *
     * @param year  年份
     * @param month 月份
     * @param day   天
     * @param type  类型 日 ：3 ,月 ：2 ,年：1
     * @return
     */
    public static String getDateStr(Integer year, Integer month, Integer day, Integer type) {
        if (type == DATE_TYPE_DAY) {
            return year + "-" + (month < 10 ? "0" + month : month) + "-" + (day < 10 ? "0" + day : day);
        } else if (type == DATE_TYPE_MONTH) {
            return year + "-" + (month < 10 ? "0" + month : month);
        } else {
            return year.toString();
        }
    }

    /**
     * 根据年份 月份 日期 类型转换成时间
     *
     * @param year  年份
     * @param month 月份
     * @param day   天
     * @return
     */
    public static String getDateStr(Integer year, Integer month, Integer day, Integer hour, Integer minute) {
        return year + "-" + month + "-" + day + " " + hour + ":" + minute;
    }

    public static Date getDateTime(Date endDate, Integer type) {
        if (type == 3) {
            endDate = DateUtil.calDay(endDate, 1);
        } else if (type == 2)
            endDate = DateUtil.calMonth(endDate, 1);
        else
            endDate = DateUtil.calYear(endDate, 1);
        return endDate;
    }

    public static String getDateType(Integer type) {
        if (type == DATE_TYPE_DAY) {
            return "日";
        } else if (type == DATE_TYPE_MONTH)
            return "月";
        else
            return "年";
    }

    public static Integer getDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_MONTH);
    }

    public static Integer getMaxDayInMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.getActualMaximum(Calendar.DATE);
    }

    public static String getFormat(Integer dateType) {
        if (DATE_TYPE_DAY.equals(dateType)) {
            return DATE_YYYY_MM_DD;
        } else if (DATE_TYPE_MONTH.equals(dateType)) {
            return DATE_YYYY_MM;
        } else {
            return DATE_YYYY;
        }
    }

    public static Integer getHour(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.HOUR_OF_DAY);
    }

    public static Date getMinDate(Date date1, Date date2) {
        if (null == date1 && null == date2) {
            return null;
        } else if (null == date1) {
            return date2;
        } else if (null == date2) {
            return date1;
        } else {
            return date1.getTime() > date2.getTime() ? date2 : date1;
        }
    }

    public static Integer getMinute(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.MINUTE);
    }

    public static Integer getMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.MONTH);
    }

    public static Integer getYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.YEAR);
    }

    public static boolean isSameDate(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        boolean isSameYear = cal1.get(Calendar.YEAR) == cal2
                .get(Calendar.YEAR);
        boolean isSameMonth = isSameYear
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
        boolean isSameDate = isSameMonth
                && cal1.get(Calendar.DAY_OF_MONTH) == cal2
                .get(Calendar.DAY_OF_MONTH);

        return isSameDate;
    }

    /**
     *
     * 1 第一季度 2 第二季度 3 第三季度 4 第四季度
     *
     * @param date
     * @return
     */
    public static int getSeason(Date date) {
        int season = 0;
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int month = c.get(Calendar.MONTH);
        switch (month) {
            case Calendar.JANUARY:
            case Calendar.FEBRUARY:
            case Calendar.MARCH:
                season = 1;
                break;
            case Calendar.APRIL:
            case Calendar.MAY:
            case Calendar.JUNE:
                season = 2;
                break;
            case Calendar.JULY:
            case Calendar.AUGUST:
            case Calendar.SEPTEMBER:
                season = 3;
                break;
            case Calendar.OCTOBER:
            case Calendar.NOVEMBER:
            case Calendar.DECEMBER:
                season = 4;
                break;
            default:
                break;
        }
        return season;
    }

    /**
     * 将秒时间戳转换成 时长
     * @return 该毫秒数转换为 * days * hours * minutes * seconds 后的格式
     */
    public static String formatDuration(long second) {
        long days = second / 86400;            //转换天数
        second = second % 86400;            //剩余秒数
        long hours = second / 3600;            //转换小时
        second = second % 3600;                //剩余秒数
        long minutes = second /60;            //转换分钟
        second = second % 60;                //剩余秒数
        StringBuffer duration =  new StringBuffer();
        if(days > 0)
            duration.append(days).append("天");
        if(hours > 0)
            duration.append(hours).append("小时");
        if(minutes > 0)
            duration.append(minutes).append("分");
        if(second > 0)
            duration.append(second).append("秒");
        return duration.toString();
    }

    /**
     * 将时长转换成 秒时间戳
     */
    public static long durationToMss(String duration) {
        String[] splits = duration.split("天");
        int num = Integer.parseInt(splits[0]);
        long mss = 0L;
        mss += num * 86400;
        splits = splits[1].split("小时");
        num = Integer.parseInt(splits[0]);
        mss += num * 3600;
        splits = splits[1].split("分");
        num = Integer.parseInt(splits[0]);
        mss += num * 60;
        splits = splits[1].split("秒");
        num = Integer.parseInt(splits[0]);
        mss += num;
        return mss;
    }



    public static boolean inRange(Date date, String start, String end, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);//DATE_HHMM

        try {
            Date duration = df.parse(df.format(date));
            Date startTime = df.parse(start);
            Date endTime = df.parse(end);
            return duration.before(endTime) && duration.after(startTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
//        System.out.println(getMonth(new Date()));
//        long d = StrToDate("2019-06-30 12:00:02", DATE_YYYY_MM_DD_HH).getTime() - StrToDate("2019-06-30 11:00:01", DATE_YYYY_MM_DD_HH).getTime();
//        String duration = formatDuration(d / 1000);
//        System.out.println(duration);
//        System.out.println(durationToMss(duration));

//        long d = durationToMss("0天1小时0分1秒");
//        System.out.println(d);
//        String duration = formatDuration(d);
//        System.out.println(duration);
//        System.out.println(getDateStr(DateUtil.calYear(new Date(), -1), DATE_YYYY_MM_DD_HH));

//        System.out.println(inRange(new Date(), "07:15", "19:15", DateUtil.DATE_HHMM));

//        Date d1 = new Date();
//        Date d2 = new Date();

//        long d = StrToDate("2019-06-30 12:00:02", DATE_YYYY_MM_DD_HH).getTime();

//        System.out.println(d1.getTime());

//        String project = "犬细小病毒(CPV Ag 91)";
//        System.out.println(project.substring(0, project.indexOf("(")));
//
//        Object o = "12";
//        Integer i = 12;
//        System.out.println(Objects.equals(o, i));
    }
}
