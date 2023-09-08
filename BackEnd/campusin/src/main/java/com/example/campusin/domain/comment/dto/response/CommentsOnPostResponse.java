package com.example.campusin.domain.comment.dto.response;

import com.example.campusin.domain.comment.Comment;
import com.example.campusin.domain.user.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.querydsl.core.annotations.QueryProjection;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ApiModel(value = "댓글 응답", description = "댓글 응답")
public class CommentsOnPostResponse extends CommentResponse{

    @ApiModelProperty(value = "대댓글 리스트", example = "[]")
    private List<CommentsOnPostResponse> children = new ArrayList<>();

    @QueryProjection
    public CommentsOnPostResponse(Long userId, Long parentId, Long commentId, Integer like, Integer report, String name, String content, Boolean isAdopted, Long boardId, Long postId) {
        super(userId, parentId, commentId, name, content, like, report, isAdopted, boardId, postId);
    }

    public void setChildren(List<CommentsOnPostResponse> children){
        this.children = children;
    }

    public static CommentsOnPostResponse of(Comment comment) {
        User user = comment.getUser();
        Comment parent = comment.getParent();
        String name = user.getNickname();
        String content = comment.getIsDelete() || user == null ? "삭제된 댓글입니다." : comment.getContent();

        return new CommentsOnPostResponse(
                user.getId(),
                parent != null ? parent.getId() : null,
                comment.getId(),
                comment.getLikes().size(),
                comment.getReports().size(),
                name,
                content,
                comment.getIsAdopted(),
                comment.getPost().getBoard().getId(),
                comment.getPost().getId()

        );
    }

    public int getChildrenSize() {
        return children.size();
    }
}
