package com.example.campusin.common.exception;

public class StatisticsNotFoundException extends BusinessException {
    public StatisticsNotFoundException() {
        super(ErrorCode.STATISTICS_NOT_FOUND);
    }
}
