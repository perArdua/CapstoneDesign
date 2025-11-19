package com.example.campusin.application.post;

import com.example.campusin.application.postsearch.PostSearchIndexer;
import com.example.campusin.application.post.exception.BoardNotFoundException;
import com.example.campusin.application.post.exception.PostNotFoundException;
import com.example.campusin.application.post.exception.TagNotFoundException;
import com.example.campusin.application.studygroup.exception.StudyGroupNotFoundException;
import com.example.campusin.application.user.exception.UserNotFoundException;
import com.example.campusin.domain.board.Board;
import com.example.campusin.domain.board.BoardType;
import com.example.campusin.domain.board.dto.response.BoardSimpleResponse;
import com.example.campusin.domain.photo.Photo;
import com.example.campusin.domain.post.*;
import com.example.campusin.domain.post.dto.request.PostCreateRequest;
import com.example.campusin.domain.post.dto.request.PostUpdateRequest;
import com.example.campusin.domain.post.dto.response.PostIdResponse;
import com.example.campusin.domain.post.dto.response.PostResponse;
import com.example.campusin.domain.post.dto.response.PostSimpleResponse;
import com.example.campusin.domain.post.dto.response.PostStudyResponse;
import com.example.campusin.domain.studygroup.StudyGroup;
import com.example.campusin.domain.tag.Tag;
import com.example.campusin.domain.tag.TagType;
import com.example.campusin.domain.tag.dto.response.TagResponse;
import com.example.campusin.domain.user.User;
import com.example.campusin.infra.board.BoardRepository;
import com.example.campusin.infra.photo.PhotoRepository;
import com.example.campusin.infra.post.PostLikeRepository;
import com.example.campusin.infra.post.PostReportRepository;
import com.example.campusin.infra.post.PostRepository;
import com.example.campusin.infra.studygroup.StudyGroupRepository;
import com.example.campusin.infra.tag.TagRepository;
import com.example.campusin.infra.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kok8454@gmail.com on 2023-03-19
 * Github : http://github.com/perArdua
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final PhotoRepository photoRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostReportRepository postReportRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final TagRepository tagRepository;
    private final PostSearchIndexer postSearchIndexer;
    public static int REPORT_HIDE_THRESHOLD = 30;

    @Transactional(readOnly = true)
    public Page<PostSimpleResponse> getPostsByBoard(Long boardId, Pageable pageable) {
        findBoard(boardId);
        findBoard(boardId);
        return postRepository.findPostsByBoardId(boardId, pageable).map(PostSimpleResponse::new);
    }

    @Transactional(readOnly = true)
    public Page<PostSimpleResponse> getPostsByTag(Long boardId, Long tagId, Pageable pageable) {
        findBoard(boardId);
        findTag(tagId);
        return postRepository.findPostsByTagId(boardId, tagId, pageable).map(PostSimpleResponse::new);
    }

    @Transactional
    public PostIdResponse createPost(Long boardId, Long tagId, Long userId, PostCreateRequest request) {
        Board board = findBoard(boardId);
        User user = findUser(userId);
        Tag tag = findTag(tagId);
        Post post = postRepository.save(Post.builder()
                .board(board)
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .price(request.getPrice())
                .studyGroupId(request.getStudyGroupId())
                .tag(tag)
                .build());
        for (String content : request.getPhotos()) {
            Photo savePhoto = new Photo(content);
            savePhoto.setPost(post);
            photoRepository.save(savePhoto);
        }
        postSearchIndexer.index(post);
        return new PostIdResponse(postRepository.save(post).getId());
    }

    @Transactional(readOnly = true)
    public PostResponse readPost(Long postId) {
        Post post = findPost(postId);
        return new PostResponse(post);
    }


    @Transactional
    public PostIdResponse updatePost(Long postId, PostUpdateRequest request) {
        Post post = findPost(postId);
        post.updatePost(request);
        return new PostIdResponse(postRepository.save(post).getId());
    }
    @Transactional
    public void deletePost(Long postId) {
        Post post = findPost(postId);
        postRepository.deleteById(postId);
    }

    @Transactional(readOnly = true)
    public Page<PostSimpleResponse> searchPosts(String keyword, Pageable pageable) {
        return postRepository.searchPosts(keyword, pageable).map(PostSimpleResponse::new);
    }

    @Transactional(readOnly = true)
    public Page<PostSimpleResponse> searchPostsAtBoard(Long boardId, String keyword, Pageable pageable) {
        return postRepository.searchPostsAtBoard(keyword, boardId, pageable)
                .map(PostSimpleResponse::new);
    }

    @Transactional
    public boolean initBoard() {
        if (boardRepository.count() == 0) {
            for (BoardType boardType : BoardType.values()) {
                boardRepository.save(Board.builder()
                        .boardType(boardType)
                        .build());
            }

            if(tagRepository.count() == 0) {
                for(TagType tagType : TagType.values()){
                    tagRepository.save(Tag.builder()
                            .tagType(tagType)
                            .build());
                }
            }
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public Page<TagResponse> getTags(Pageable pageable){
        return tagRepository.findAll(pageable).map(TagResponse::new);
    }

    @Transactional(readOnly = true)
    public Page<PostSimpleResponse> getPostsByUser(Long userId, Pageable pageable){
        findUser(userId);
        Page<Post> posts = postRepository.findPostsByUserId(userId, pageable);
        return posts.map(PostSimpleResponse::new);
    }

    @Transactional(readOnly = true)
    public Page<PostSimpleResponse> getPostsThatUserCommentedAt(Long userId, Pageable pageable){
        List<Long> postIds = postRepository.findPostIdsThatUserCommentedAt(userId);
        if(postIds.isEmpty()){
            return new PageImpl<>(new ArrayList<>());
        }
        Page<Post> posts = postRepository.findPostsThatUserCommentedAt(postIds, pageable);
        return posts.map(PostSimpleResponse::new);
    }


    @Transactional(readOnly = true)
    public Page<BoardSimpleResponse> getBoardIds(Pageable pageable) {
        return boardRepository.findAll(pageable).map(BoardSimpleResponse::new);
    }

    @Transactional
    public boolean likePost(Long userId, Long postId) {
        PostLikeId postLikeId = new PostLikeId(userId, postId);
        if(isPresentLike(postLikeId)){
            return false;
        }
        Post post = findPost(postId);
        User user = findUser(userId);
        postLikeRepository.save(new PostLike(post, user));
        return true;
    }

    @Transactional
    public boolean unlikePost(Long userId, Long postId) {
        PostLikeId postLikeId = new PostLikeId(userId, postId);
        if(!isPresentLike(postLikeId)){
            return false;
        }
        Post post = findPost(postId);
        User user = findUser(userId);
        postLikeRepository.deleteById(postLikeId);
        post.decreaseLikeCount();
        return true;
    }

    @Transactional
    public ReportResult reportPost(Long userId, Long postId, ReportType reportType) {
        PostReportId postReportId = new PostReportId(userId, postId);
        if (isPresentReport(postReportId)) {
            return ReportResult.ALREADY_REPORTED;
        }

        Post post = findPost(postId);
        User user = findUser(userId);

        PostReport report = new PostReport(post, user, reportType);
        postReportRepository.save(report);

        int totalScore = postReportRepository.sumReportScore(postId);
        if (totalScore >= REPORT_HIDE_THRESHOLD) {
            post.hide();
            return ReportResult.POST_HIDDEN;
        }

        return ReportResult.SUCCESS;
    }

    @Transactional
    public boolean unreportPost(Long userId, Long postId) {
        PostReportId postReportId = new PostReportId(userId, postId);
        if(!isPresentReport(postReportId)){
            return false;
        }
        Post post = findPost(postId);
        User user = findUser(userId);
        postReportRepository.deleteById(postReportId);
        post.decreaseReportCount();
        return true;
    }

    @Transactional
    public boolean updateBadgeStatus(Long postId, Boolean isBadgeAccepted) {
        Post post = findPost(postId);
        post.setIsBadgeAccepted(isBadgeAccepted);
        return true;
    }

    @Transactional(readOnly = true)
    public Page<PostStudyResponse> getPostsByStudyGroup(Long studyGroupId, Pageable pageable) {
        StudyGroup studyGroup = findStudyGroup(studyGroupId);
        Page<Post> posts = postRepository.findPostsByStudyGroupId(studyGroupId, pageable);
        return posts.map(PostStudyResponse::new);
    }

    @Transactional
    public void blockPost(Long postId) {
        Post post = findPost(postId);
        post.setTitle("신고 완료 처리 된 게시글입니다.");
        post.setContent("신고 완료 처리 된 게시글입니다.");
        postRepository.save(post);
    }

    @Transactional
    public void unblockPost(Long postId) {
        Post post = findPost(postId);
        post.setReportCount(0);
        postReportRepository.deleteByPostId(postId);
        postRepository.save(post);
    }

    private StudyGroup findStudyGroup(Long studyGroupId) {
        return studyGroupRepository.findById(studyGroupId).orElseThrow(
                StudyGroupNotFoundException::new
        );
    }


    private boolean isPresentLike(PostLikeId postLikeId){
        return postLikeRepository.existsById(postLikeId);
    }

    private boolean isPresentReport(PostReportId postReportId){
        return postReportRepository.existsById(postReportId);
    }

    private Board findBoard(Long boardId) {
        return boardRepository.findById(boardId).orElseThrow(
                BoardNotFoundException::new
        );
    }

    private Post findPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(
                PostNotFoundException::new
        );
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                UserNotFoundException::new
        );
    }
    private Tag findTag(Long tagId) {
        return tagRepository.findById(tagId).orElseThrow(
                TagNotFoundException::new
        );
    }
}
