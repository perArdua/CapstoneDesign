package com.example.campusin.domain.post;

/**
 * Created by kok8454@gmail.com on 2023-09-09
 * Github : http://github.com/perArdua
 */

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class PostReportId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "post_id")
    private Long postId;

    public PostReportId(Long userId, Long postId) {
        this.userId = userId;
        this.postId = postId;
    }
}
