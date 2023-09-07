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

    public boolean createLike(Long userId, Long commentId){
        CommentLikeId commentLikeId = new CommentLikeId(userId, commentId);
        if(isPresentLike(commentLikeId)){
            return false;
        }
        User currentUser = getCurrentUser(userId);
        Comment comment = getComment(commentId);
        commentLikeRepository.save(new CommentLike(currentUser, comment));

        return true;
    }

    public boolean deleteLike(Long userId, Long commentId){
        CommentLikeId commentLikeId = new CommentLikeId(userId, commentId);
        if(!isPresentLike(commentLikeId)){
            return false;
        }

        Comment comment = getComment(commentId);
        comment.getLikes().removeIf(commentLike -> commentLike.getId().equals(commentLikeId));

        CommentLike commentLike = commentLikeRepository.findById(commentLikeId).orElseThrow(() -> new IllegalArgumentException("COMMENT LIKE NOT FOUND"));
        commentLike.setComment(null);
        commentLike.setUser(null);

        commentLikeRepository.deleteById(commentLikeId);
        return true;
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
