package com.example.campusin.infra.studygroup;

import com.example.campusin.domain.studygroup.StudyGroup;
import com.example.campusin.domain.user.User;
import com.example.campusin.infra.TestEntityFactory;
import com.example.campusin.support.DataJpaTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("StudyGroupRepository")
class StudyGroupRepositoryTest extends DataJpaTestSupport {

    @Autowired
    StudyGroupRepository studyGroupRepository;

    @Test
    @DisplayName("스터디 그룹을 저장하고 조회한다")
    void saveAndFind() {
        // given
        User owner = TestEntityFactory.persistUser(em, "owner");
        StudyGroup group = TestEntityFactory.persistStudyGroup(em, owner, "algos");

        // when // then
        assertThat(studyGroupRepository.findById(group.getId())).isPresent();
    }
}
