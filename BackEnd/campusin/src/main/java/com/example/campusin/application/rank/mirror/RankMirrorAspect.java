package com.example.campusin.application.rank.mirror;

import com.example.campusin.domain.rank.dto.response.RankListResponse;
import com.example.campusin.mirror.MirrorEngine;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;

import static com.example.campusin.common.utils.WeekUtil.getWeekStartDate;

@Aspect
@Component
@RequiredArgsConstructor
public class RankMirrorAspect {

    private final MirrorEngine mirrorEngine;
    private final RankShadowRunner rankShadowRunner;
    private final RankComparator rankComparator;

    @Pointcut("execution(* com.example.campusin.application.rank.RankService.getAllStudyTimeRankList*(..))")
    public void rankPointcut() {}

    @Around("rankPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        LocalDate localDate = (LocalDate) args[0];
        Pageable pageable = (Pageable) args[1];

        @SuppressWarnings("unchecked")
        Page<RankListResponse> primary = (Page<RankListResponse>) joinPoint.proceed();

        LocalDate weekStart = getWeekStartDate(localDate);
        mirrorEngine.submit(
                "rank.studyTime",
                Map.of("date", localDate.toString(), "page", pageable.getPageNumber(), "size", pageable.getPageSize()),
                () -> primary,
                () -> rankShadowRunner.run(weekStart, pageable),
                rankComparator
        );

        return primary;
    }
}
