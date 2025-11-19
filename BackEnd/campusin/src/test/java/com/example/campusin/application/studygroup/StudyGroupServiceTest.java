package com.example.campusin.application.studygroup;

import com.example.campusin.application.studygroup.exception.StudyGroupAlreadyMemberException;
import com.example.campusin.application.studygroup.exception.StudyGroupFullException;
import com.example.campusin.application.user.exception.UserNotFoundException;
import com.example.campusin.domain.statistics.Statistics;
import com.example.campusin.domain.studygroup.StudyGroup;
import com.example.campusin.domain.studygroup.StudyGroupMember;
import com.example.campusin.domain.studygroup.dto.request.StudyGroupCreateRequest;
import com.example.campusin.domain.studygroup.dto.response.StudyGroupIdResponse;
import com.example.campusin.domain.studygroup.dto.response.StudyGroupResponse;
import com.example.campusin.domain.studygroup.dto.response.StudyGroupTimeResponse;
import com.example.campusin.domain.user.User;
import com.example.campusin.infra.statistics.StatisticsRepository;
import com.example.campusin.infra.studygroup.StudyGroupMemberRepository;
import com.example.campusin.infra.studygroup.StudyGroupRepository;
import com.example.campusin.infra.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("StudyGroupService")
class StudyGroupServiceTest {

    @Mock
    StudyGroupRepository studyGroupRepository;
    @Mock
    StudyGroupMemberRepository studyGroupMemberRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    StatisticsRepository statisticsRepository;

    @InjectMocks
    StudyGroupService studyGroupService;

    @Nested
    @DisplayName("createStudyGroup 메서드는")
    class Describe_createStudyGroup {

        @Test
        @DisplayName("사용자가 없으면 UserNotFoundException을 던진다")
        void 사용자_없음() {
            // given
            StudyGroupCreateRequest request = createRequest("모각코", 5);
            given(userRepository.findById(1L)).willReturn(Optional.empty());

            // when // then
            assertThatThrownBy(() -> studyGroupService.createStudyGroup(1L, request))
                    .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        @DisplayName("스터디그룹을 생성하고 리더 멤버를 추가한 뒤 ID를 반환한다")
        void 스터디그룹을_생성한다() {
            // given
            Long userId = 2L;
            StudyGroupCreateRequest request = createRequest("백엔드", 10);
            User leader = new User();
            leader.setId(userId);

            StudyGroup savedGroup = StudyGroup.builder()
                    .studygroupName(request.getStudygroupName())
                    .LimitedMemberSize(request.getLimitedMemberSize())
                    .CurrentMemberSize(0)
                    .user(leader)
                    .members(new ArrayList<>())
                    .build();
            savedGroup.setId(30L);

            given(userRepository.findById(userId)).willReturn(Optional.of(leader));
            given(studyGroupRepository.save(any(StudyGroup.class)))
                    .willReturn(savedGroup);

            given(studyGroupMemberRepository.save(any(StudyGroupMember.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // when
            StudyGroupIdResponse response = studyGroupService.createStudyGroup(userId, request);

            // then
            assertThat(response.getId()).isEqualTo(30L);
            ArgumentCaptor<StudyGroupMember> memberCaptor = ArgumentCaptor.forClass(StudyGroupMember.class);
            then(studyGroupMemberRepository).should().save(memberCaptor.capture());
            assertThat(memberCaptor.getValue().getIsLeader()).isTrue();
        }
    }

    @Nested
    @DisplayName("joinStudyGroup 메서드는")
    class Describe_joinStudyGroup {

        @Test
        @DisplayName("정원이 가득 찼으면 StudyGroupFullException을 던진다")
        void 정원이_가득참() {
            // given
            Long studyGroupId = 5L;
            StudyGroup studyGroup = createStudyGroupWithMembers(2, 2, 1L, 2L);

            given(userRepository.findById(10L)).willReturn(Optional.of(new User()));
            given(studyGroupRepository.findById(studyGroupId)).willReturn(Optional.of(studyGroup));

            // when // then
            assertThatThrownBy(() -> studyGroupService.joinStudyGroup(10L, studyGroupId))
                    .isInstanceOf(StudyGroupFullException.class);
        }

        @Test
        @DisplayName("이미 가입한 경우 StudyGroupAlreadyMemberException을 던진다")
        void 이미_가입함() {
            // given
            Long userId = 3L;
            Long studyGroupId = 6L;
            User member = new User();
            member.setId(userId);
            StudyGroup studyGroup = createStudyGroupWithMembers(3, 1, userId);

            given(userRepository.findById(userId)).willReturn(Optional.of(member));
            given(studyGroupRepository.findById(studyGroupId)).willReturn(Optional.of(studyGroup));

            // when // then
            assertThatThrownBy(() -> studyGroupService.joinStudyGroup(userId, studyGroupId))
                    .isInstanceOf(StudyGroupAlreadyMemberException.class);
        }

        @Test
        @DisplayName("정상 가입 시 멤버를 추가하고 현재 인원을 1 증가시킨다")
        void 멤버_추가() {
            // given
            Long userId = 4L;
            Long studyGroupId = 7L;
            User member = new User();
            member.setId(userId);

            StudyGroup studyGroup = createStudyGroupWithMembers(3, 1, 99L);
            studyGroup.setId(studyGroupId);
            studyGroup.setCurrentMemberSize(1);

            given(userRepository.findById(userId)).willReturn(Optional.of(member));
            given(studyGroupRepository.findById(studyGroupId)).willReturn(Optional.of(studyGroup));
            given(studyGroupMemberRepository.save(any(StudyGroupMember.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // when
            StudyGroupResponse response = studyGroupService.joinStudyGroup(userId, studyGroupId);

            // then
            assertThat(studyGroup.getCurrentMemberSize()).isEqualTo(2);
            assertThat(response).isNotNull();
            then(studyGroupMemberRepository).should().save(any(StudyGroupMember.class));
        }
    }

    @Nested
    @DisplayName("deleteStudyGroup 메서드는")
    class Describe_deleteStudyGroup {

        @Test
        @DisplayName("리더가 삭제하면 구성원을 모두 삭제하고 스터디그룹을 제거한다")
        void 리더가_삭제() {
            // given
            Long userId = 11L;
            Long studyGroupId = 22L;
            User leader = new User();
            leader.setId(userId);

            StudyGroupMember leaderMember = StudyGroupMember.builder()
                    .isLeader(true)
                    .user(leader)
                    .build();
            List<StudyGroupMember> members = new ArrayList<>();
            members.add(leaderMember);
            members.add(StudyGroupMember.builder().isLeader(false).user(new User()).build());

            StudyGroup studyGroup = new StudyGroup();
            studyGroup.setMembers(members);

            given(userRepository.findById(userId)).willReturn(Optional.of(leader));
            given(studyGroupRepository.findById(studyGroupId)).willReturn(Optional.of(studyGroup));
            given(studyGroupMemberRepository.findByUserAndStudyGroupId(leader, studyGroup))
                    .willReturn(Optional.of(leaderMember));

            // when
            studyGroupService.deleteStudyGroup(userId, studyGroupId);

            // then
            then(studyGroupMemberRepository).should(times(2)).delete(any(StudyGroupMember.class));
            then(studyGroupRepository).should().delete(studyGroup);
        }

        @Test
        @DisplayName("리더가 아닌 멤버는 자신의 멤버십만 삭제한다")
        void 일반멤버_삭제() {
            // given
            Long userId = 12L;
            Long studyGroupId = 23L;
            User memberUser = new User();
            memberUser.setId(userId);

            StudyGroupMember member = StudyGroupMember.builder()
                    .isLeader(false)
                    .user(memberUser)
                    .build();

            StudyGroup studyGroup = new StudyGroup();
            studyGroup.setCurrentMemberSize(3);
            studyGroup.setMembers(new ArrayList<>(List.of(member)));

            given(userRepository.findById(userId)).willReturn(Optional.of(memberUser));
            given(studyGroupRepository.findById(studyGroupId)).willReturn(Optional.of(studyGroup));
            given(studyGroupMemberRepository.findByUserAndStudyGroupId(memberUser, studyGroup))
                    .willReturn(Optional.of(member));

            // when
            studyGroupService.deleteStudyGroup(userId, studyGroupId);

            // then
            assertThat(studyGroup.getCurrentMemberSize()).isEqualTo(2);
            then(studyGroupMemberRepository).should().delete(member);
            then(studyGroupRepository).should(never()).delete(studyGroup);
        }
    }

    @Nested
    @DisplayName("getStudyGroupMemberStudyTime 메서드는")
    class Describe_getStudyGroupMemberStudyTime {

        @Test
        @DisplayName("멤버의 누적 시간을 합산해 내림차순으로 반환한다")
        void 멤버_시간을_집계한다() {
            // given
            Long studyGroupId = 40L;
            LocalDate startDate = LocalDate.of(2024, 1, 1);
            LocalDate endDate = startDate.plusDays(1);
            Pageable pageable = PageRequest.of(0, 10);

            User alice = new User();
            alice.setId(1L);
            alice.setNickname("alice");

            User bob = new User();
            bob.setId(2L);
            bob.setNickname("bob");

            StudyGroupMember aliceMember = StudyGroupMember.builder().user(alice).isLeader(true).build();
            StudyGroupMember bobMember = StudyGroupMember.builder().user(bob).isLeader(false).build();

            StudyGroup studyGroup = new StudyGroup();
            studyGroup.setMembers(new ArrayList<>(List.of(aliceMember, bobMember)));

            given(studyGroupRepository.findById(studyGroupId)).willReturn(Optional.of(studyGroup));
            given(statisticsRepository.findByUserAndDate(alice, startDate.toString()))
                    .willReturn(Statistics.builder().elapsedTime(5L).build());
            given(statisticsRepository.findByUserAndDate(alice, endDate.toString()))
                    .willReturn(Statistics.builder().elapsedTime(3L).build());

            given(statisticsRepository.findByUserAndDate(bob, startDate.toString()))
                    .willReturn(Statistics.builder().elapsedTime(2L).build());
            given(statisticsRepository.findByUserAndDate(bob, endDate.toString()))
                    .willReturn(null);

            // when
            Page<StudyGroupTimeResponse> result = studyGroupService.getStudyGroupMemberStudyTime(
                    studyGroupId, startDate, endDate, pageable);

            // then
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent().get(0).getStudyGroupMemberName()).isEqualTo("alice");
            assertThat(result.getContent().get(0).getElapsedTime()).isEqualTo(8L);
            assertThat(result.getContent().get(1).getStudyGroupMemberName()).isEqualTo("bob");
            assertThat(result.getContent().get(1).getElapsedTime()).isEqualTo(2L);
        }
    }

    private StudyGroupCreateRequest createRequest(String name, int limit) {
        StudyGroupCreateRequest request = new StudyGroupCreateRequest();
        request.setStudygroupName(name);
        request.setLimitedMemberSize(limit);
        return request;
    }

    private StudyGroup createStudyGroupWithMembers(int limitedSize, int currentSize, Long... memberUserIds) {
        StudyGroup studyGroup = new StudyGroup();
        studyGroup.setLimitedMemberSize(limitedSize);
        studyGroup.setCurrentMemberSize(currentSize);

        List<StudyGroupMember> members = new ArrayList<>();
        for (Long id : memberUserIds) {
            User user = new User();
            user.setId(id);
            StudyGroupMember member = StudyGroupMember.builder()
                    .user(user)
                    .studyGroupId(studyGroup)
                    .isLeader(false)
                    .build();
            members.add(member);
        }
        studyGroup.setMembers(members);
        return studyGroup;
    }
}
