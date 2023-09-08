package com.example.campusin.application.comment;

import com.example.campusin.domain.comment.Comment;
import com.example.campusin.domain.comment.CommentLikeId;
import com.example.campusin.domain.comment.CommentReport;
import com.example.campusin.domain.comment.CommentReportId;
import com.example.campusin.domain.user.User;
import com.example.campusin.infra.comment.CommentReportRepository;
import com.example.campusin.infra.comment.CommentRepository;
import com.example.campusin.infra.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by kok8454@gmail.com on 2023-09-09
 * Github : http://github.com/perArdua
 */

@Service
@Transactional
@RequiredArgsConstructor
public class CommentReportService {

    private final UserRepository userRepository;
    private final CommentReportRepository commentReportRepository;
    private final CommentRepository commentRepository;


    public boolean createReport(Long userId, Long commentId){
        CommentReportId commentReportId = new CommentReportId(userId, commentId);
        if(isPresentReport(commentReportId)){
            return false;
        }
        User currentUser = getCurrentUser(userId);
        Comment comment = getComment(commentId);
        commentReportRepository.save(new CommentReport(currentUser, comment));

        return true;
    }

    public boolean deleteReport(Long userId, Long commentId){
        CommentReportId commentReportId = new CommentReportId(userId, commentId);
        if(!isPresentReport(commentReportId)){
            return false;
        }

        Comment comment = getComment(commentId);
        comment.getReports().removeIf(commentReport -> commentReport.getId().equals(commentReportId));

        CommentReport commentReport = commentReportRepository.findById(commentReportId).orElseThrow(() -> new IllegalArgumentException("COMMENT REPORT NOT FOUND"));
        commentReport.setComment(null);
        commentReport.setUser(null);

        commentReportRepository.deleteById(commentReportId);
        return true;
    }

    private Comment getComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("COMMENT NOT FOUND"));
    }

    private User getCurrentUser(Long userId){
        return userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("USER NOT FOUND"));
    }

    private boolean isPresentReport(CommentReportId commentReportId){
        return commentReportRepository.existsById(commentReportId);
    }

}
