package com.example.campusin.application.studygroup;

import com.example.campusin.domain.statistics.Statistics;
import com.example.campusin.domain.studygroup.StudyGroup;
import com.example.campusin.domain.studygroup.StudyGroupMember;
import com.example.campusin.domain.studygroup.dto.request.StudyGroupTimeRequest;
import com.example.campusin.domain.studygroup.dto.request.StudyGroupCreateRequest;
import com.example.campusin.domain.studygroup.dto.request.StudyGroupJoinRequest;
import com.example.campusin.domain.studygroup.dto.response.*;
import com.example.campusin.domain.user.User;
import com.example.campusin.infra.statistics.StatisticsRepository;
import com.example.campusin.infra.studygroup.StudyGroupMemberRepository;
import com.example.campusin.infra.studygroup.StudyGroupRepository;
import com.example.campusin.infra.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.campusin.domain.studygroup.QStudyGroup.studyGroup;

/**
 * Created by kok8454@gmail.com on 2023-03-19
 * Github : http://github.com/perArdua
 */
@Service
@RequiredArgsConstructor
public class StudyGroupService {
    private final StudyGroupRepository studyGroupRepository;
    private final StudyGroupMemberRepository studyGroupMemberRepository;
    private final UserRepository userRepository;
    private final StatisticsRepository statisticsRepository;
    private static final int INITNUMBER = 1;


    @Transactional
    public StudyGroupIdResponse createStudyGroup(Long userId, StudyGroupCreateRequest request){
        User user = findUser(userId);
        StudyGroup studyGroup = studyGroupRepository.save(StudyGroup.builder()
                    .studygroupName(request.getStudygroupName())
                    .LimitedMemberSize(request.getLimitedMemberSize())
                    .user(user)
                    .build());

        studyGroupMemberRepository.save(StudyGroupMember.builder()
                .studyGroupId(studyGroup)
                .user(user)
                .isLeader(true)
                .build());

        studyGroup.setCurrentMemberSize(INITNUMBER);


        return new StudyGroupIdResponse(studyGroupRepository.save(studyGroup).getId());
    }

    @Transactional
    public StudyGroupResponse joinStudyGroup(Long userId, Long studyGroupId){
        User member = findUser(userId);
        StudyGroup studyGroup = findStudyGroup(studyGroupId);

        //만약 스터디그룹 제한인원이 꽉찼다면 예외처리
        if(studyGroup.getLimitedMemberSize() == studyGroup.getMembers().size()){
            throw new IllegalArgumentException("스터디그룹 인원이 꽉찼습니다.");
        }

        //만약 이미 스터디그룹에 속해있다면 예외처리
        if(studyGroup.getMembers().stream().anyMatch(member1 -> member1.getUser().getId().equals(userId))){
            throw new IllegalArgumentException("이미 스터디그룹에 속해있습니다.");
        }

        //스터디그룹에 멤버 추가
        StudyGroupMember studyGroupMember = studyGroupMemberRepository.save(StudyGroupMember.builder()
                .user(member)
                .studyGroupId(studyGroup)
                .isLeader(false)
                .build());

        studyGroup.addStudyGroupMember(studyGroupMember);
        studyGroup.setCurrentMemberSize(studyGroup.getCurrentMemberSize() + 1);

        return new StudyGroupResponse(studyGroupMember);
    }

    @Transactional
    public void deleteStudyGroup(Long userId, Long studyGroupId){

        User user = findUser(userId);
        StudyGroup studyGroup = findStudyGroup(studyGroupId);
        StudyGroupMember member = CheckStudyGroupMember(user, studyGroup);

        if(member.getIsLeader()){
            List<StudyGroupMember> members = studyGroup.getMembers();
            for(StudyGroupMember studyGroupMember : members){
                try{
                    studyGroupMemberRepository.delete(studyGroupMember);
                } catch (Exception e){
                    throw new IllegalArgumentException("스터디그룹 멤버 삭제 실패");
                }
            }
            studyGroupRepository.delete(studyGroup);
        } else {
            studyGroupMemberRepository.delete(member);
            studyGroup.setCurrentMemberSize(studyGroup.getCurrentMemberSize() - 1);
        }

    }

    @Transactional(readOnly = true)
    public Page<StudyGroupResponse> getMyAllStudyGroupList(Long userId, Pageable pageable) {
        findUser(userId);
        Page<StudyGroupMember> studyGroup = studyGroupMemberRepository.findStudyGroupByUserId(userId, pageable);

        return studyGroup.map(StudyGroupResponse::new);
    }

    @Transactional(readOnly = true)
    public StudyGroupDetailResponse showStudyGroup(Long studygroupId) {
        StudyGroup studyGroup = findStudyGroup(studygroupId);
        return new StudyGroupDetailResponse(studyGroup);

    }

    @Transactional(readOnly = true)
    public Page<StudyGroupTimeResponse> getStudyGroupMemberStudyTime(Long studyGroupId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        StudyGroup studyGroup = findStudyGroup(studyGroupId);
        List<StudyGroupMember> studyGroupMembers = studyGroup.getMembers();
        List<StudyGroupTimeResponse> studyGroupMemberResponses = new ArrayList<>();

        for (StudyGroupMember member: studyGroupMembers) {
            User user = member.getUser();
            Long elapsedTime = 0L;
            for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
                Statistics statistics = statisticsRepository.findByUserAndDate(user, date);
                if(statistics != null)
                {
                    elapsedTime += statistics.getElapsedTime();
                }
                else{
                    elapsedTime += 0L;
                }
            }
            studyGroupMemberResponses.add(new StudyGroupTimeResponse(member.getUser().getNickname(), elapsedTime));
        }

        return studyGroupMemberResponses.stream()
                .sorted((o1, o2) -> o2.getElapsedTime().compareTo(o1.getElapsedTime()))
                .collect(Collectors.toList())
                .stream()
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .collect(Collectors.toCollection(ArrayList::new))
                .stream()
                .collect(Collectors.collectingAndThen(Collectors.toList(), PageImpl::new));
    }

    private StudyGroup findStudyGroup(Long studyGroupId) {
        return studyGroupRepository.findById(studyGroupId).orElseThrow(
                () -> new IllegalArgumentException("STUDYGROUP NOT FOUND")
        );
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("USER NOT FOUND")
        );
    }
    private StudyGroupMember CheckStudyGroupMember(User user, StudyGroup studyGroup) {
        return studyGroupMemberRepository.findByUserAndStudyGroupId(user, studyGroup).orElseThrow(
                () -> new IllegalArgumentException("해당 사용자는 스터디 그룹의 멤버가 아닙니다.")
        );
    }

}
