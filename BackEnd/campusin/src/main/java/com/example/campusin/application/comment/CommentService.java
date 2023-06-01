package com.example.campusin.application.comment;

import com.example.campusin.domain.comment.*;
import com.example.campusin.domain.comment.dto.request.CommentCreateRequest;
import com.example.campusin.domain.comment.dto.response.CommentCreateResponse;
import com.example.campusin.domain.comment.dto.response.CommentsOnPostResponse;
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

    @Transactional
    public CommentCreateResponse createComment(Long userId, CommentCreateRequest commentCreateRequest, Long postId) {

        User currentUser = getCurrentUser(userId);
        Post post = getPost(postId);
        Comment parent = commentCreateRequest.getParentId() != null ? commentRepository.findById(commentCreateRequest.getParentId()).orElseThrow(() -> new IllegalArgumentException("COMMENT NOT FOUND")) : null;

        Comment comment = Comment.builder()
                .user(currentUser)
                .post(post)
                .parent(parent)
                .content(commentCreateRequest.getContent())
                .build();

        commentRepository.save(comment);

        return CommentCreateResponse.convertComment(comment);
    }

    @Transactional(readOnly = true)
    public Page<CommentsOnPostResponse> searchCommentByPost(Long postId, Pageable pageable) {
        checkPostExist(postId);
        return commentRepository.findByPost(postId, pageable);
    }

    @Transactional
    public void deleteComment(Long userId, Long commentId){
        getCurrentUser(userId);

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

    private User getCurrentUser(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("USER NOT FOUND"));
    }

    private void checkPostExist(Long postId){
        if(!postRepository.existsById(postId)) {
            throw new IllegalArgumentException("해당 게시물이 없습니다.");
        }
    }

}
