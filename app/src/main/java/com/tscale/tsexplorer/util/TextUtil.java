package com.tscale.tsexplorer.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by rd-19 on 2015/7/13.
 */
public class TextUtil {


    public static boolean isIP(String address) {
        String ip = "(2[5][0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})";
        Pattern pattern = Pattern.compile(ip);
        Matcher matcher = pattern.matcher(address);
        return matcher.matches();
    }
}
