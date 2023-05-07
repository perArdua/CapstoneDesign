package com.example.campusin.domain.post.dto.request;

import com.example.campusin.domain.photo.Photo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * Created by kok8454@gmail.com on 2023-05-07
 * Github : http://github.com/perArdua
 */

@Getter
@NoArgsConstructor
public class PostUpdateRequest {

    @NotBlank
    private String title;
    private String content;
    private List<Photo> photos;

    public PostUpdateRequest(String title, String content, List<Photo> photos) {
        this.title = title;
        this.content = content;
        this.photos = photos;
    }
}
