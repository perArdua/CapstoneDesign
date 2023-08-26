package com.example.campusin.application.rank;

import com.example.campusin.domain.rank.Rank;
import com.example.campusin.domain.rank.dto.request.RankCreateRequest;
import com.example.campusin.domain.rank.dto.response.RankIdResponse;
import com.example.campusin.domain.rank.dto.response.RankListResponse;
import com.example.campusin.domain.rank.dto.response.RankResponse;
import com.example.campusin.domain.statistics.Statistics;
import com.example.campusin.domain.studygroup.StudyGroup;
import com.example.campusin.domain.studygroup.StudyGroupMember;
import com.example.campusin.domain.timer.Timer;
import com.example.campusin.domain.user.User;
import com.example.campusin.infra.rank.RankRepository;
import com.example.campusin.infra.statistics.StatisticsRepository;
import com.example.campusin.infra.studygroup.StudyGroupRepository;
import com.example.campusin.infra.timer.TimerRepository;
import com.example.campusin.infra.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class RankService {

    private final RankRepository rankRepository;
    private final StatisticsRepository statisticsRepository;
    private final UserRepository userRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final TimerRepository timerRepository;

    @Transactional
    public RankIdResponse createRank(Long userId, RankCreateRequest request){
        User user = findUser(userId);
        Statistics statistics = statisticsRepository.findByUserAndDate(user, request.getLocalDate());

        // 이미 해당 날짜에 대한 Rank가 존재한다면 해당 Rank의 Id를 반환
        if(rankRepository.findByUserAndStatistics(user, statistics) != null){
            return new RankIdResponse(rankRepository.findByUserAndStatistics(user, statistics).getId());
        }

        // 해당 날짜 기준 전주의 statistics totalElapsedTime, totalNumberOfQuestions를 구한다.
        LocalDate localDate = statistics.getDate();
        LocalDate startDate = localDate.minusDays(localDate.getDayOfWeek().getValue() - 1);
        LocalDate endDate = startDate.plusDays(6);
        int week = startDate.getDayOfMonth() / 7 + 1;

        List<Timer> timerList = timerRepository.findAllByUserAndModifiedAtBetween(user, startDate, endDate);
        Long totalStudyTime = timerList.stream().mapToLong(Timer::getElapsedTime).sum();
        Long totalQuestion = statisticsRepository.countQuestionsByUserAndModifiedAtBetween(user, startDate, endDate);
        Rank rank = Rank.builder()
                .user(user)
                .statistics(statistics)
                .totalNumberOfQuestions(totalQuestion)
                .totalElapsedTime(totalStudyTime)
                .week(week)
                .userName(user.getNickname())
                .build();

        return new RankIdResponse(rankRepository.save(rank).getId());
    }

    // 스터디그룹 내 랭킹 create
//    @Transactional
//    public RankIdResponse createStudyGroupRanking(Long studygroupId, RankCreateRequest request){
//        StudyGroup studyGroup = findStudyGroup(studygroupId);
//        Statistics statistics = statisticsRepository.findByDate(request.getLocalDate());
//
////        if(rankRepository.findByStudyGroupAndStatistics(studyGroup, statistics) != null){
////            return new RankIdResponse(rankRepository.findByStudyGroupAndStatistics(studyGroup, statistics).getId());
////        }
//
//        for(StudyGroupMember member : studyGroup.getMembers()){
//            User user = member.getUser();
//            LocalDate localDate = statistics.getDate();
//            LocalDate startDate = localDate.minusDays(localDate.getDayOfWeek().getValue() - 1);
//            LocalDate endDate = startDate.plusDays(6);
//            int week = startDate.getDayOfMonth() / 7 + 1;
//
//            List<Timer> timerList = timerRepository.findAllByUserAndModifiedAtBetween(user, startDate, endDate);
//            Long totalStudyTime = timerList.stream().mapToLong(Timer::getElapsedTime).sum();
//            Long totalQuestion = statisticsRepository.countQuestionsByUserAndModifiedAtBetween(user, startDate, endDate);
//
//            Rank rank = Rank.builder()
//                    .user(user)
//                    .statistics(statistics)
//                    .totalNumberOfQuestions(totalQuestion)
//                    .totalElapsedTime(totalStudyTime)
//                    .week(week)
//                    .userName(user.getNickname())
//                    .studyGroup(studyGroup)
//                    .build();
//
//            rankRepository.save(rank);
//        }
//
//        return new RankIdResponse(rankRepository.findByStudyGroupAndStatistics(studyGroup, statistics).getId());
//
//    }

    // 주차별 개인 공부시간 Rank 조회
    @Transactional
    public RankResponse getPersonalStudyTimeRank(Long userId, Long rankId){
        User user = findUser(userId);
        Rank ranking = findRank(rankId);
        Statistics statistics = findStatistics(ranking.getStatistics().getId());

        LocalDate localDate = statistics.getDate();
        LocalDate startDate = localDate.minusDays(localDate.getDayOfWeek().getValue() - 1);
        LocalDate endDate = startDate.plusDays(7);
        int week = startDate.getDayOfMonth() / 7 + 1;

        List<Timer> timerList = timerRepository.findAllByUserAndModifiedAtBetween(user, startDate, endDate);

        Long totalStudyTime = timerList.stream().mapToLong(Timer::getElapsedTime).sum();
        Long rank = rankRepository.countByStatisticsAndTotalStudyTimeGreaterThan(statistics, totalStudyTime) + 1;

        ranking.updateStudyRanking(rank);
        ranking.updateTotalElapsedTime(totalStudyTime);
        rankRepository.save(ranking);

        return new RankResponse(rank, week);
    }


    // 주차별 개인 질의응답 rank 조회
    @Transactional
    public RankResponse getPersonalQuestionRank(Long userId, Long rankId){
        User user = findUser(userId);
        Rank ranking = findRank(rankId);
        Statistics statistics = findStatistics(ranking.getStatistics().getId());

        LocalDate localDate = statistics.getDate();
        LocalDate startDate = localDate.minusDays(localDate.getDayOfWeek().getValue() - 1);
        LocalDate endDate = startDate.plusDays(6);
        int week = startDate.getDayOfMonth() / 7 + 1;

        Long totalQuestion = statisticsRepository.countQuestionsByUserAndModifiedAtBetween(user, startDate, endDate);
        Long rank = rankRepository.countByStatisticsAndTotalQuestionGreaterThan(statistics, totalQuestion) + 1;

        ranking.updateQuestionRanking(rank);
        ranking.updateTotalNumberOfQuestions(totalQuestion);
        rankRepository.save(ranking);

        return new RankResponse(rank, week);
    }

    // 주차별 스터디 그룹내 개인 공부시간 rank 조회
    @Transactional
    public RankListResponse getStudyGroupPersonalStudyTimeRank(Long userId, Long rankId){
        User user = findUser(userId);
        Rank ranking = findRank(rankId);
        StudyGroup studyGroup = findStudyGroup(ranking.getStudyGroup().getId());
        Statistics statistics = statisticsRepository.findByUserAndDate(user, ranking.getStatistics().getDate());

        int week = getWeek(statistics.getDate());

        Long totalStudyTime = getTotalStudyTime(user, statistics);
        Long rank = rankRepository.countByStatisticsAndTotalStudyTimeGreaterThanInStudyGroup(studyGroup, statistics, totalStudyTime) + 1;

        return new RankListResponse(rank, user.getUsername());
    }

    // 주차별 user들 rank 순위 목록 조회
    @Transactional
    public Page<RankListResponse> getAllStudyTimeRankList(Pageable pageable){
        return rankRepository.findAll(pageable).map(RankListResponse::new);
    }

    private int getWeek(LocalDate localDate){
        LocalDate startDate = localDate.minusDays(localDate.getDayOfWeek().getValue() - 1);
        int week = startDate.getDayOfMonth() / 7 + 1;
        return week;
    }

    private Long getTotalStudyTime(User user, Statistics statistics){
        LocalDate localDate = statistics.getDate();
        LocalDate startDate = localDate.minusDays(localDate.getDayOfWeek().getValue() - 1);
        LocalDate endDate = startDate.plusDays(6);
        int week = startDate.getDayOfMonth() / 7 + 1;

        List<Timer> timerList = timerRepository.findAllByUserAndModifiedAtBetween(user, startDate, endDate);

        Long totalStudyTime = timerList.stream().mapToLong(Timer::getElapsedTime).sum();
        return totalStudyTime;
    }

    private Rank findRank(Long rankId) {
        return rankRepository.findById(rankId).orElseThrow(() -> new IllegalArgumentException("RANK NOT FOUND"));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("USER NOT FOUND"));
    }

    private Statistics findStatistics(Long statisticsId) {
        return statisticsRepository.findById(statisticsId)
                .orElseThrow(() -> new IllegalArgumentException("STATISTICS NOT FOUND"));
    }

    private StudyGroup findStudyGroup(Long studyGroupId) {
        return studyGroupRepository.findById(studyGroupId)
                .orElseThrow(() -> new IllegalArgumentException("STUDYGROUP NOT FOUND"));
    }

}
