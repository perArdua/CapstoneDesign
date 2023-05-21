package com.example.campusin.domain.photo.response;

import com.example.campusin.domain.photo.Photo;
import lombok.Builder;
import lombok.Getter;

/**
 * Created by kok8454@gmail.com on 2023-05-21
 * Github : http://github.com/perArdua
 */
@Getter
public class PhotoResponse {

    private Long photoId;
    private String content;

    @Builder
    public PhotoResponse(Long photoId, String content) {
        this.photoId = photoId;
        this.content = content;

    }

    @Builder
    public PhotoResponse(Photo entity){
        this(
                entity.getId(),
                entity.getContent()
        );
    }
}
