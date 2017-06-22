package com.ngovihung.assignment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by ngo.vi.hung on 6/21/2017.
 */

public class Utils{
    public static String readSampleFile(String fileName) {
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = null;
            InputStreamReader is = null;
            try {
                reader = new BufferedReader(is =
                        new InputStreamReader(Application.getContext().getAssets().open(fileName)));
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                return  sb.toString(); //replaceNewLineToEmpty(sb.toString());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (reader != null)
                    reader.close();
                if (is != null)
                    is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String formatDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return dateFormat.format(date);
    }
    public static int getDayInYear(Date date){
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_YEAR);
    }

    public static String getDateString(int dayOfYear){
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.YEAR, 2017);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DATE, 1);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DAY_OF_YEAR, dayOfYear);
        return formatDate(cal.getTime());
    }

    public static boolean compareSameMonth(Date d1, Date d2){
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(d1);
        cal2.setTime(d2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
    }

    public static boolean compareSameQuarter(Date d1, Date d2){
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(d1);
        cal2.setTime(d2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                getQuarter(cal1.get(Calendar.MONTH));
    }

    public static boolean getQuarter(int month){
        switch (month){
            case 2://March
            case 5://June
            case 8://September
            case 11://December
                return true;
            default:
                return false;
        }

    }

    public static int getMonthOfYear(Date d1){
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(d1);
        return cal1.get(Calendar.MONTH);
    }

    public static int getQuarterIndex(Date d1){
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(d1);
        switch (cal1.get(Calendar.MONTH)){
            case 2: //March
                return 0;
            case 5: //June
                return 1;
            case 8: //September
                return 2;
            case 11: //December
                return 3;
            default:
                return 0;
        }
    }
}
