package com.lagou.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class dateUtil {

    public static String getStringDate(Date date) {
         // Date currentTime = new Date();
          SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
          String dateString = formatter.format(date);
          return dateString;
    }
}
