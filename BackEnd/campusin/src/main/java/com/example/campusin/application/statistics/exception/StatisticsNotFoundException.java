package com.example.campusin.application.statistics.exception;

import com.example.campusin.common.exception.core.BusinessException;
import com.example.campusin.common.exception.core.ErrorCode;

public class StatisticsNotFoundException extends BusinessException {
    public StatisticsNotFoundException() {
        super(ErrorCode.STATISTICS_NOT_FOUND);
    }
}
