package com.example.campusin.application.comment;

import com.example.campusin.domain.board.BoardType;
import com.example.campusin.domain.comment.*;
import com.example.campusin.domain.comment.dto.request.CommentCreateRequest;
import com.example.campusin.domain.comment.dto.response.CommentCreateResponse;
import com.example.campusin.domain.comment.dto.response.CommentsOnPostResponse;
import com.example.campusin.domain.post.Post;
import com.example.campusin.domain.user.User;
import com.example.campusin.infra.board.BoardRepository;
import com.example.campusin.infra.comment.CommentReportRepository;
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
    private final BoardRepository boardRepository;
    private final CommentReportRepository commentReportRepository;
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

        if(post.getBoard().getBoardType().equals(BoardType.Question)){
            comment.setIsAnswer(true);
        }

        commentRepository.save(comment);

        return CommentCreateResponse.convertComment(comment);
    }

    @Transactional(readOnly = true)
    public Page<CommentsOnPostResponse> searchCommentByPost(Long postId, Pageable pageable) {
        checkPostExist(postId);
        return commentRepository.findByPost(postId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Comment> getAllComments(Pageable pageable) {
        return commentRepository.findAll(pageable);
    }

    @Transactional
    public void deleteComment(Long userId, Long commentId){
        getCurrentUser(userId);

        Comment comment = getComment(commentId);
        comment.updateDelete();
    }

    @Transactional
    public void updateIsAdopted(Long commentId, Long userId){
        Comment comment = getComment(commentId);
        if (comment.getPost().getUser().getId().equals(userId)) {
            comment.setIsAdopted(true);
        } else {
            throw new IllegalArgumentException("해당 유저는 답변 채택 권한이 없습니다.");
        }
    }

    @Transactional
    public void blockComment(Long commentId) {
        Comment comment = getComment(commentId);
        comment.setContent("신고 완료 처리 된 댓글입니다.");
        commentRepository.save(comment);
    }

    @Transactional
    public void unblockComment(Long commentId) {
        Comment comment = getComment(commentId);
        comment.setReports(null);
        commentReportRepository.deleteByCommentId(commentId);
        commentRepository.save(comment);
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
