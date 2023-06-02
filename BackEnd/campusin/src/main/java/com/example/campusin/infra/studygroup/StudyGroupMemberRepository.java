package com.example.campusin.infra.studygroup;

import com.example.campusin.domain.studygroup.StudyGroup;
import com.example.campusin.domain.studygroup.StudyGroupMember;
import com.example.campusin.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface StudyGroupMemberRepository extends JpaRepository<StudyGroupMember, Long> {

    @Query("SELECT m FROM StudyGroupMember m WHERE m.user = :user AND m.studyGroupId = :studyGroup")
    Optional<StudyGroupMember> findByUserAndStudyGroupId(@Param("user") User user, @Param("studyGroup") StudyGroup studyGroup);

    @Query(
            value = "SELECT m FROM StudyGroupMember m WHERE m.user.id = :userId",
            countQuery = "SELECT COUNT(m) FROM StudyGroupMember m WHERE m.user.id = :userId"
    )
    Page<StudyGroupMember> findStudyGroupByUserId(@Param("userId") Long userId, Pageable pageable);

}
