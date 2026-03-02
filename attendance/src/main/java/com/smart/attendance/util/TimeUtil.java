package com.smart.attendance.util;

import java.time.LocalDateTime;

public class TimeUtil {

    private TimeUtil() {}

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
}