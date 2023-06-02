package com.example.campusin.infra.studygroup;

import com.example.campusin.domain.studygroup.StudyGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by kok8454@gmail.com on 2023-03-19
 * Github : http://github.com/perArdua
 */

@Repository
public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long> {
}
