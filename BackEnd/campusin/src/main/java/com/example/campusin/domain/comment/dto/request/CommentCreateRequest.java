package com.example.campusin.domain.comment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@Schema(name = "댓글 생성 요청", description = "댓글 생성 요청")
public class CommentCreateRequest{

    @Schema(description = "부모 댓글 id(대댓글 달성시 parent id 기입)", example = "null")
    private Long parentId;

    @Schema(description = "댓글 내용", example = "댓글 내용")
    @NotBlank
    private  String content;

}
