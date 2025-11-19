package com.example.campusin.support;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * H2에서 MySQL DATE_FORMAT을 흉내 내기 위한 함수.
 * H2 INIT 스크립트로 알리아스를 등록해 사용한다.
 */
public final class H2Functions {

    private H2Functions() {
    }

    public static String dateFormat(Timestamp timestamp, String mysqlPattern) {
        if (timestamp == null || mysqlPattern == null) {
            return null;
        }
        // MySQL 패턴을 Java 패턴으로 단순 치환
        String javaPattern = mysqlPattern
                .replace("%Y", "yyyy")
                .replace("%m", "MM")
                .replace("%d", "dd")
                .replace("%H", "HH")
                .replace("%i", "mm")
                .replace("%s", "ss");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(javaPattern).withZone(ZoneId.systemDefault());
        return formatter.format(timestamp.toInstant());
    }
}
