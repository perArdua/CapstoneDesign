package com.example.campusin.application.comment;

import com.example.campusin.domain.comment.Comment;
import com.example.campusin.domain.comment.CommentLike;
import com.example.campusin.domain.comment.CommentLikeId;
import com.example.campusin.domain.user.User;
import com.example.campusin.infra.comment.CommentLikeRepository;
import com.example.campusin.infra.comment.CommentRepository;
import com.example.campusin.infra.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentLikeService {

    private final UserRepository userRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;

    public void createLike(Long userId, Long commentId){
        CommentLikeId commentLikeId = new CommentLikeId(userId, commentId);
        if(isPresentLike(commentLikeId)){
            throw new IllegalArgumentException("ALREADY LIKED EXISTS");
        }
        User currentUser = getCurrentUser(userId);
        Comment comment = getComment(commentId);
        commentLikeRepository.save(new CommentLike(currentUser, comment));

    }

    public void deleteLike(Long userId, Long commentId){
        CommentLikeId commentLikeId = new CommentLikeId(userId, commentId);
        if(!isPresentLike(commentLikeId)){
            throw new IllegalArgumentException("NOT FOUND LIKED");
        }

        Comment comment = getComment(commentId);
        comment.getLikes().removeIf(commentLike -> commentLike.getId().equals(commentLikeId));

        CommentLike commentLike = commentLikeRepository.findById(commentLikeId).orElseThrow(() -> new IllegalArgumentException("COMMENT LIKE NOT FOUND"));
        commentLike.setComment(null);
        commentLike.setUser(null);

        commentLikeRepository.deleteById(commentLikeId);
        System.out.println("삭제되었습니다");
    }

    private Comment getComment(Long commentId){
        return commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("COMMENT NOT FOUND"));
    }

    private User getCurrentUser(Long userId){
        return userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("USER NOT FOUND"));
    }

    private boolean isPresentLike(CommentLikeId commentLikeId){
        return commentLikeRepository.existsById(commentLikeId);
    }

}
