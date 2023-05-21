package com.example.campusin.infra.comment;

import com.example.campusin.domain.comment.Comment;
import com.example.campusin.domain.comment.dto.response.CommentsOnPostResponse;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import static com.example.campusin.domain.comment.QComment.comment;
public class CommentRepositoryImpl implements CommentRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public CommentRepositoryImpl(EntityManager entityManager){
        this.queryFactory = new JPAQueryFactory(entityManager);
    }
    public Page<CommentsOnPostResponse> findByPost(Long postId, Pageable pageable) {
        List<Comment> parentComments = queryFactory
                .select(comment)
                .from(comment)
                .where(
                        comment.post.id.eq(postId)
                                .and(comment.parent.isNull())
                )
                .orderBy(comment.createdAt.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 자식 댓글은 삭제되면 메시지를 보여주지 않는다.
        List<Comment> childComments = queryFactory
                .select(comment)
                .from(comment)
                .where(
                        comment.post.id.eq(postId),
                        comment.parent.isNotNull(),
                        comment.isDelete.isFalse()
                )
                .orderBy(comment.createdAt.asc())
                .fetch();

        List<CommentsOnPostResponse> parentResponse = parentComments.stream()
                .filter(c -> !(c.getChildren().size() == 0 && c.getIsDelete()))
                .map(CommentsOnPostResponse::of)
                .collect(Collectors.toList());

        List<CommentsOnPostResponse> childrenResponse = childComments.stream()
                .filter(c -> c != null)
                .map(CommentsOnPostResponse::of)
                .collect(Collectors.toList());

        parentResponse.stream()
                .forEach(parent -> {
                    parent.setChildren(childrenResponse.stream()
                            .filter(child -> child.getParentId().equals(parent.getCommentId()))
                            .collect(Collectors.toList()));
                });

        //Count Query
        long total = queryFactory
                .select(Wildcard.count)
                .from(comment)
                .where(
                        comment.post.id.eq(postId),
                        comment.parent.isNull(),
                        comment.children.size().goe(0)
                                .or(comment.children.size().eq(0)
                                        .and(comment.isDelete.isFalse()))
                )
                .fetch().get(0);

        return new PageImpl<>(parentResponse, pageable, total);
    }

}
