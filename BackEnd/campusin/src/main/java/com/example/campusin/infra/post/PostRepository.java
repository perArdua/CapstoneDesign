package com.example.campusin.infra.post;

import com.example.campusin.domain.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Created by kok8454@gmail.com on 2023-03-19
 * Github : http://github.com/perArdua
 */

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query(
            value = "select p from Post p join fetch p.board b join fetch p.user u where p.board.id = :boardId",
            countQuery = "select count(p) from Post p where p.board.id = :boardId"
    )
    Page<Post> findPostsByBoardId(@Param("boardId") Long boardId, Pageable pageable);

    @Query(
            value = "select p from Post p join fetch p.board join fetch p.user where p.user.id = :userId",
            countQuery = "select count(p) from Post p where p.user.id = :userId"
    )
    Page<Post> findPostsByUserId(@Param("userId") Long userId, Pageable pageable);


    @Query(
            value = "select p from Post p join fetch p.board join fetch p.user where p.id IN (:postIds)",
            countQuery = "select count(p) from Post p where p.id IN (:postIds)"
    )
    Page<Post> findPostsThatUserCommentedAt(@Param("postIds") List<Long> postIds, Pageable pageable);

    @Query("select p from Post p join fetch p.board join fetch p.user left join fetch p.photos where p.id = :postId")
    Optional<Post> findPostById(@Param("postId") Long postId);


    @Query(value = "select p from Post p join fetch p.board join fetch p.user where upper(function('replace', p.title, ' ', '')) like concat('%', upper(function('replace', :keyword, ' ', '')), '%') or upper(function('replace', p.content, ' ', '')) like concat('%', upper(function('replace', :keyword, ' ', '')), '%')",
            countQuery = "select count(p) from Post p where upper(function('replace', p.title, ' ', '')) like concat('%', upper(function('replace', :keyword, ' ', '')), '%') or upper(function('replace', p.content, ' ', '')) like concat('%', upper(function('replace', :keyword, ' ', '')), '%')")
    Page<Post> searchPosts(@Param("keyword") String keyword, Pageable pageable);

    @Query(value = "select p from Post p join fetch p.board join fetch p.user where p.board.id = :boardId and (upper(function('replace', p.title, ' ', '')) like concat('%', upper(function('replace', :keyword, ' ', '')), '%') or upper(function('replace', p.content, ' ', '')) like concat('%', upper(function('replace', :keyword, ' ', '')), '%'))",
            countQuery = "select count(p) from Post p where p.board.id = :boardId and (upper(function('replace', p.title, ' ', '')) like concat('%', upper(function('replace', :keyword, ' ', '')), '%') or upper(function('replace', p.content, ' ', '')) like concat('%', upper(function('replace', :keyword, ' ', '')), '%'))")
    Page<Post> searchPostsAtBoard(@Param("keyword") String keyword, @Param("boardId") Long boardId,
                                  Pageable pageable);
}