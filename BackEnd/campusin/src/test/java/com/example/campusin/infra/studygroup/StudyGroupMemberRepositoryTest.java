package com.example.campusin.infra.studygroup;

import com.example.campusin.domain.studygroup.StudyGroup;
import com.example.campusin.domain.studygroup.StudyGroupMember;
import com.example.campusin.domain.user.User;
import com.example.campusin.infra.TestEntityFactory;
import com.example.campusin.support.DataJpaTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("StudyGroupMemberRepository")
class StudyGroupMemberRepositoryTest extends DataJpaTestSupport {

    @Autowired
    StudyGroupMemberRepository studyGroupMemberRepository;

    @Test
    @DisplayName("사용자와 스터디 그룹으로 멤버를 조회하고 페이징한다")
    void findByUserAndGroup() {
        // given
        User owner = TestEntityFactory.persistUser(em, "owner");
        User memberUser = TestEntityFactory.persistUser(em, "member");
        StudyGroup group = TestEntityFactory.persistStudyGroup(em, owner, "algos");

        StudyGroupMember member = studyGroupMemberRepository.save(
                StudyGroupMember.builder()
                        .isLeader(false)
                        .user(memberUser)
                        .studyGroupId(group)
                        .build());

        // when
        Optional<StudyGroupMember> found =
                studyGroupMemberRepository.findByUserAndStudyGroupId(memberUser, group);
        Page<StudyGroupMember> page =
                studyGroupMemberRepository.findStudyGroupByUserId(memberUser.getId(), PageRequest.of(0, 10));

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(member.getId());
        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).getId()).isEqualTo(member.getId());
    }
}
