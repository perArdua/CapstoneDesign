package com.example.campusin.common.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("WeekUtil")
class WeekUtilTest {

    @Test
    @DisplayName("주 시작일(일요일)을 계산한다")
    void 주_시작일을_계산한다() {
        // given
        LocalDate wednesday = LocalDate.of(2024, 12, 4); // 수요일

        // when
        LocalDate start = WeekUtil.getWeekStartDate(wednesday);

        // then
        assertThat(start.getDayOfWeek().getValue()).isEqualTo(7); // SUNDAY
        assertThat(start).isEqualTo(LocalDate.of(2024, 12, 1));
    }

    @Test
    @DisplayName("월 기준 주차를 계산한다")
    void 주차를_계산한다() {
        // given
        LocalDate sunday = LocalDate.of(2024, 12, 8);

        // when
        int weekOfMonth = WeekUtil.getWeekOfMonth(sunday);

        // then
        assertThat(weekOfMonth).isEqualTo(2);
    }
}
