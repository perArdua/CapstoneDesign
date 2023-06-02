package com.example.campusin.domain.studygroup;

import com.example.campusin.domain.basetime.BaseTimeEntity;
import com.example.campusin.domain.user.User;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE studygroupmember SET deleted_at = CURRENT_TIMESTAMP where studygroupmember_id = ?")
@Table(name = "studygroupmember")
@NoArgsConstructor
@Entity
@Data
public class StudyGroupMember extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "studygroupmember_id")
    private Long id;

    @Column(name = "is_leader", nullable = false)
    private Boolean isLeader;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studygroup_id", referencedColumnName = "studygroup_id")
    private StudyGroup studyGroupId;

    @Builder
    public StudyGroupMember(Boolean isLeader, User user, StudyGroup studyGroupId) {
        this.isLeader = isLeader;
        this.user = user;
        this.studyGroupId = studyGroupId;
    }

}
