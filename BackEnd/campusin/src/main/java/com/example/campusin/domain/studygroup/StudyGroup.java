package com.example.campusin.domain.studygroup;

import com.example.campusin.domain.basetime.BaseTimeEntity;
import com.example.campusin.domain.user.User;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "limited_member_size", nullable = false)
    private int LimitedMemberSize;

    @Column(name = "current_member_size", nullable = false)
    private int CurrentMemberSize;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @OneToMany(mappedBy = "studyGroupId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudyGroupMember> members = new ArrayList<>();


    public void addStudyGroupMember(StudyGroupMember studyGroup) {
        this.members.add(studyGroup);
    }


    @Builder
    public StudyGroup(User user, String studygroupName, int LimitedMemberSize, int CurrentMemberSize, List<StudyGroupMember> members) {
        this.user = user;
        this.studygroupName = studygroupName;
        this.LimitedMemberSize = LimitedMemberSize;
        this.CurrentMemberSize = CurrentMemberSize;
        this.members = members;
    }


}
