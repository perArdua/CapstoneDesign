package com.example.campusin.domain.studygroup;

import com.example.campusin.domain.basetime.BaseTimeEntity;
import com.example.campusin.domain.user.User;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE studygroup SET deleted_at = CURRENT_TIMESTAMP where studygroup_id = ?")
@Table(name = "studygroup")
@Data
@NoArgsConstructor
@Entity
public class StudyGroup extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "studygroup_id")
    private Long id;

    @Column(name = "studygroup_name", nullable = false, length = 50)
    private String studygroupName;

    private int LimitedMemberSize;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @Builder
    public StudyGroup(User user, String studygroupName, int LimitedMemberSize) {
        this.user = user;
        this.studygroupName = studygroupName;
        this.LimitedMemberSize = LimitedMemberSize;
    }

}
