package com.example.campusin.domain.tag.dto.response;

import com.example.campusin.domain.tag.Tag;
import com.example.campusin.domain.tag.TagType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(name = "태그 조회 응답", description = "태그 id, 태그 이름을 반환한다.")
public class TagResponse {

    @Schema(name = "태그 id", example = "1")
    Long tagId;
    @Schema(name = "태그 이름", example = "IT")
    TagType tagType;

    public TagResponse(Long tagId, TagType tagType) {
        this.tagId = tagId;
        this.tagType = tagType;
    }

    public TagResponse(Tag tag) {
       this(
               tag.getId(),
               tag.getTagType()
       );
    }

}
