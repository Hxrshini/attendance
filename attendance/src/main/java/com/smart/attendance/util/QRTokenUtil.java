package com.smart.attendance.util;

import java.util.UUID;

public class QRTokenUtil {

    private QRTokenUtil() {}

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}