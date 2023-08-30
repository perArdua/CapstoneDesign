package com.example.campusin.domain.tag;

import com.example.campusin.domain.basetime.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "tag")
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE tag SET deleted_at = CURRENT_TIMESTAMP where tag_id = ?")
public class Tag extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "tag_id")
    private Long id;

    @Column(name = "tag_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TagType tagType;

    @Builder
    public Tag(TagType tagType) {
        this.tagType = tagType;
    }

}
