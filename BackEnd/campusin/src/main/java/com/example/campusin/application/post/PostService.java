package com.example.campusin.application.post;

import com.example.campusin.domain.board.Board;
import com.example.campusin.domain.board.BoardType;
import com.example.campusin.domain.board.dto.response.BoardSimpleResponse;
import com.example.campusin.domain.photo.Photo;
import com.example.campusin.domain.post.Post;
import com.example.campusin.domain.post.dto.request.PostCreateRequest;
import com.example.campusin.domain.post.dto.request.PostUpdateRequest;
import com.example.campusin.domain.post.dto.response.PostIdResponse;
import com.example.campusin.domain.post.dto.response.PostResponse;
import com.example.campusin.domain.post.dto.response.PostSimpleResponse;
import com.example.campusin.domain.user.User;
import com.example.campusin.infra.board.BoardRepository;
import com.example.campusin.infra.photo.PhotoRepository;
import com.example.campusin.infra.post.PostRepository;
import com.example.campusin.infra.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by kok8454@gmail.com on 2023-03-19
 * Github : http://github.com/perArdua
 */
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final PhotoRepository photoRepository;

    @Transactional(readOnly = true)
    public Page<PostSimpleResponse> getPostsByBoard(Long boardId, Pageable pageable) {
        findBoard(boardId);
        Page<Post> posts = postRepository.findPostsByBoardId(boardId, pageable);
        return posts.map(PostSimpleResponse::new);
    }

    @Transactional
    public PostIdResponse createPost(Long boardId, Long userId, PostCreateRequest request) {
        Board board = findBoard(boardId);
        User user = findUser(userId);
        Post post = postRepository.save(Post.builder()
                .board(board)
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .build());
        for (String content : request.getPhotos()) {
            Photo savePhoto = new Photo(content);
            savePhoto.setPost(post);
            photoRepository.save(savePhoto);
        }
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

    public Page<PostSimpleResponse> searchPostsAtBoard(Long boardId, String keyword, Pageable pageable) {
        return postRepository.searchPostsAtBoard(keyword, boardId, pageable)
                .map(PostSimpleResponse::new);
    }

    public boolean initBoard() {
        if (boardRepository.count() == 0) {
            for (BoardType boardType : BoardType.values()) {
                boardRepository.save(Board.builder()
                        .boardType(boardType)
                        .build());
            }
            return true;
        }
        return false;
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


    public Page<BoardSimpleResponse> getBoardIds(Pageable pageable) {
        return boardRepository.findAll(pageable).map(BoardSimpleResponse::new);
    }
    private Board findBoard(Long boardId) {
        return boardRepository.findById(boardId).orElseThrow(
                () -> new IllegalArgumentException("BOARD NOT FOUND")
        );
    }

    private Post findPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("POST NOT FOUND")
        );
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("USER NOT FOUND")
        );
    }
}