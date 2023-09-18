package com.example.campusin.application.rank;

import com.example.campusin.domain.rank.Ranks;
import com.example.campusin.domain.rank.dto.request.RankCreateRequest;
import com.example.campusin.domain.rank.dto.response.RankIdResponse;
import com.example.campusin.domain.rank.dto.response.RankListQuestResponse;
import com.example.campusin.domain.rank.dto.response.RankListResponse;
import com.example.campusin.domain.rank.dto.response.RankListStudyGroupResponse;
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
        if(statistics == null){
            throw new IllegalArgumentException("해당 날짜에 대한 Statistics가 존재하지 않습니다.");
        }

        //이미 해당 날짜에 대한 Rank가 존재하면 해당 Rank의 Id를 반환
        if(rankRepository.findByUserAndStatistics(user, statistics) != null){
            return new RankIdResponse(rankRepository.findByUserAndStatistics(user, statistics).getId());
        }

        LocalDate localDate = statistics.getDate();
        LocalDate startDate = localDate.minusDays(localDate.getDayOfWeek().getValue());
        LocalDate endDate = startDate.plusDays(6);

        List<Timer> timerList = timerRepository.findAllByUserAndModifiedAtBetween(user, startDate, endDate);
        Long totalStudyTime = timerList.stream().mapToLong(Timer::getElapsedTime).sum();
        Long totalQuestion = statisticsRepository.countQuestionsByUserAndModifiedAtBetween(user, startDate, endDate);

        Ranks ranks = Ranks.builder()
                .user(user)
                .statistics(statistics)
                .totalNumberOfQuestions(totalQuestion)
                .totalElapsedTime(totalStudyTime)
                .userName(user.getNickname())
                .build();

        Long rankId = rankRepository.save(ranks).getId();
        return new RankIdResponse(rankId);
    }

    // 스터디그룹내에서의 랭킹 생성
    @Transactional
    public RankIdResponse createStudyRank(Long StudyGroupId, RankCreateRequest request){
        StudyGroup studyGroup = findStudyGroup(StudyGroupId);
        Statistics statistics = statisticsRepository.findByUserAndDate(studyGroup.getUser(), request.getLocalDate());
        if(statistics == null){
            throw new IllegalArgumentException("해당 날짜에 대한 Statistics가 존재하지 않습니다.");
        }

        // 이미 해당 날짜에 대한 Rank가 존재하면서 스터디그룹도 존재하면 해당 Rank의 Id를 반환
        if(rankRepository.findByUserAndStatisticsAndStudyGroup(studyGroup.getUser(), statistics, studyGroup.getId()) != null){
            return new RankIdResponse(rankRepository.findByUserAndStatisticsAndStudyGroup(studyGroup.getUser(), statistics, studyGroup.getId()).getId());
        }

        // 해당 날짜 기준 전주의 statistics totalElapsedTime, totalNumberOfQuestions를 구한다.
        LocalDate localDate = statistics.getDate();
        LocalDate startDate = localDate.minusDays(localDate.getDayOfWeek().getValue());
        LocalDate endDate = startDate.plusDays(6);

        Long totalStudyTime = 0L;
        Long totalQuestion = 0L;

        for(StudyGroupMember studyGroupMember : studyGroup.getMembers()){
            User studyGroupMemberUser = studyGroupMember.getUser();

            List<Timer> timerList = timerRepository.findAllByUserAndModifiedAtBetween(studyGroupMemberUser, startDate, endDate);
            Long StudyTime = timerList.stream().mapToLong(Timer::getElapsedTime).sum();
            Long Question = statisticsRepository.countQuestionsByUserAndModifiedAtBetween(studyGroupMemberUser, startDate, endDate);

            totalStudyTime += StudyTime;
            totalQuestion += Question;

        }

        totalStudyTime = totalStudyTime / studyGroup.getMembers().size();
        totalQuestion = totalQuestion / studyGroup.getMembers().size();

        Ranks ranks = Ranks.builder()
                .user(studyGroup.getUser()) // 스터디 그룹장
                .statistics(statistics)
                .totalNumberOfQuestions(totalQuestion)
                .totalElapsedTime(totalStudyTime)
                .userName(studyGroup.getStudygroupName())
                .studyGroup(studyGroup)
                .build();

        Long rankId = rankRepository.save(ranks).getId();

        return new RankIdResponse(rankId);
    }

    // 주차별 스터디 그룹 간 개인 공부시간 rank 조회
    @Transactional
    public Page<RankListStudyGroupResponse> getStudyGroupPersonalStudyTimeRank(LocalDate localDate, Pageable pageable){

        Page<Ranks> ranks = rankRepository.countInStudyGroup(localDate, pageable);

        for(Long i = 0L; i < ranks.getContent().size(); i++){
            ranks.getContent().get(i.intValue()).updateStudyRanking(i+1);
            rankRepository.save(ranks.getContent().get(i.intValue()));
        }
        return ranks.map(RankListStudyGroupResponse::new);
    }

    // 주차별 user들 rank 순위 목록 조회
    @Transactional
    public Page<RankListResponse> getAllStudyTimeRankList(LocalDate localDate, Pageable pageable){

        Page<Ranks> ranks = rankRepository.findAllByOrderByTotalStudyTimeAsc(localDate, pageable);

        for(Long i = 0L; i < ranks.getContent().size(); i++){
            ranks.getContent().get(i.intValue()).updateStudyRanking(i+1);
            rankRepository.save(ranks.getContent().get(i.intValue()));
        }
        return ranks.map(RankListResponse::new);
    }

    // 개인 질의응답 rank 조회
    @Transactional
    public Page<RankListQuestResponse> getAllQuestionRankList(LocalDate localDate, Pageable pageable) {

        Page<Ranks> ranks = rankRepository.findAllByOrderByTotalNumberOfQuestionsAsc(localDate, pageable);

        for(Long i = 0L; i < ranks.getContent().size(); i++){
            ranks.getContent().get(i.intValue()).updateQuestionRanking(i+1);
            rankRepository.save(ranks.getContent().get(i.intValue()));
        }
        return ranks.map(RankListQuestResponse::new);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("USER NOT FOUND"));
    }

    private StudyGroup findStudyGroup(Long studyGroupId) {
        return studyGroupRepository.findById(studyGroupId)
                .orElseThrow(() -> new IllegalArgumentException("STUDYGROUP NOT FOUND"));
    }
}
