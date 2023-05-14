package com.example.campusin.application.comment;

import com.example.campusin.domain.comment.*;
import com.example.campusin.domain.post.Post;
import com.example.campusin.domain.user.User;
import com.example.campusin.infra.comment.CommentRepository;
import com.example.campusin.infra.post.PostRepository;
import com.example.campusin.infra.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public CommentCreateResponse createComment(String email, CommentCreateDto commentCreateDto){

        User currentUser = getCurrentUser(email);
        Post post = getPost(commentCreateDto.getPostId());
        Comment parent = commentCreateDto.getParentId() == null ? null : getComment(commentCreateDto.getParentId());
        Comment comment = Comment.builder()
                .user(currentUser)
                .post(post)
                .parent(parent)
                .content(commentCreateDto.getContent())
                .build();

        commentRepository.save(comment);

        return CommentCreateResponse.of(currentUser, post, comment);
    }

    @Transactional
    public Page<CommentsOnPostResponse> searchCommentByPost(Long postId, Pageable pageable) {
        checkPostExist(postId);
        return commentRepository.findByPost(postId, pageable); //조회 기능 미완
    }
    public void deleteComment(String email, Long commentId){
        Comment comment = getComment(commentId);
        comment.updateDelete();
    }

    private Post getPost(Long postId){
        return postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id = " + postId));
    }

    private Comment getComment(Long commentId){
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 comment 없습니다 id = " + commentId));
    }

    private User getCurrentUser(String email){
        return userRepository.findByEmail(email);
    }

    private void checkPostExist(Long postId){
        if(postRepository.existsById(postId)) {
            throw new IllegalArgumentException("해당 게시물이 없습니다.");
        }
    }

}
