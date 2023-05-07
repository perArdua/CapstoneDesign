package com.example.campusin.domain.post.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by kok8454@gmail.com on 2023-05-07
 * Github : http://github.com/perArdua
 */
@Getter
@Setter
@NoArgsConstructor
public class PostCreateRequest {

    @NotBlank
    private String title;
    private String content;
    @NotNull
    private List<String> photos = new ArrayList<>();

    @Builder
    public PostCreateRequest(String title, String content, List<String> photos) {
        this.title = title;
        this.content = content;
        if (Objects.nonNull(photos)) {
            this.photos = photos;
        }
    }
}
