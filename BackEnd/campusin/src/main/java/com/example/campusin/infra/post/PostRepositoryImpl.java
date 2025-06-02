package com.example.campusin.infra.post;

import com.example.campusin.domain.post.Post;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collections;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;

import static com.example.campusin.domain.board.QBoard.board;
import static com.example.campusin.domain.post.QPost.post;
import static com.example.campusin.domain.user.QUser.user;

@Repository
public class PostRepositoryImpl implements PostRepositoryCustom {

    private static final String NORMALIZE_TEMPLATE = "replace(upper({0}), ' ', '')";
    private final JPAQueryFactory queryFactory;

    public PostRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Post> searchPosts(String keyword, Pageable pageable) {
        if (isBlank(keyword)) {
            return emptyPage(pageable);
        }

        String norm = normalize(keyword);
        BooleanExpression keywordCond = titleContains(norm)
                .or(contentContains(keyword));
        return fetchPage(keywordCond, pageable);
    }

    private Page<Post> fetchPage(BooleanExpression cond, Pageable pageable) {
        List<Post> contents = queryFactory
                .selectFrom(post)
                .join(post.board, board).fetchJoin()
                .join(post.user, user).fetchJoin()
                .where(post.deletedAt.isNull(), cond)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(post.count())
                .from(post)
                .where(post.deletedAt.isNull(), cond)
                .fetchOne();

        return new PageImpl<>(contents, pageable, total != null ? total : 0);
    }

    private BooleanExpression titleContains(String norm) {
        return post.normalizedTitle.like("%" + norm + "%");
    }

    private BooleanExpression contentContains(String norm) {
        return Expressions.stringTemplate(NORMALIZE_TEMPLATE, post.content)
                .like("%" + norm + "%");
    }

    private String normalize(String keyword) {
        return keyword.replace(" ", "").toUpperCase();
    }

    private boolean isBlank(String keyword) {
        return keyword == null || keyword.trim().isEmpty();
    }

    private Page<Post> emptyPage(Pageable pageable) {
        return new PageImpl<>(Collections.emptyList(), pageable, 0);
    }
}