package com.example.campusin.infra.studygroup;

import com.example.campusin.domain.post.Post;
import com.example.campusin.domain.studygroup.StudyGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Created by kok8454@gmail.com on 2023-03-19
 * Github : http://github.com/perArdua
 */

@Repository
public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long> {
    @Query(
            value = "select p from StudyGroup p  join fetch p.user where p.user.id = :userId",
            countQuery = "select count(p) from StudyGroup p where p.user.id = :userId"
    )
    Page<StudyGroup> findStudyGroupByUserId(@Param("userId") Long userId, Pageable pageable);
}
