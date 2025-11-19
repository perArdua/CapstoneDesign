package com.example.campusin.support;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("H2Functions.dateFormat")
class H2FunctionsTest {

    @Test
    @DisplayName("MySQL 패턴을 Java 패턴으로 치환해 포맷한다")
    void dateFormat_치환된_패턴으로_포맷() {
        // given
        LocalDateTime dateTime = LocalDateTime.of(2024, 5, 6, 14, 30, 45);
        Timestamp timestamp = Timestamp.valueOf(dateTime);

        // when
        String formatted = H2Functions.dateFormat(timestamp, "%Y-%m-%d %H:%i:%s");

        // then
        assertThat(formatted).isEqualTo("2024-05-06 14:30:45");
    }

    @Test
    @DisplayName("타임스탬프나 패턴이 null이면 null을 반환한다")
    void dateFormat_null_입력은_null() {
        // when // then
        assertThat(H2Functions.dateFormat(null, "%Y")).isNull();
        assertThat(H2Functions.dateFormat(new Timestamp(System.currentTimeMillis()), null)).isNull();
    }
}
