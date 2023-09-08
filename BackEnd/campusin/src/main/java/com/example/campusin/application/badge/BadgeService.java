package com.example.campusin.application.badge;

import com.example.campusin.domain.badge.Badge;
import com.example.campusin.domain.badge.request.BadgeCreateRequest;
import com.example.campusin.domain.oauth.RoleType;
import com.example.campusin.domain.post.Post;
import com.example.campusin.domain.user.User;
import com.example.campusin.infra.badge.BadgeRepository;
import com.example.campusin.infra.post.PostRepository;
import com.example.campusin.infra.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by kok8454@gmail.com on 2023-06-05
 * Github : http://github.com/perArdua
 */

@Service
@RequiredArgsConstructor
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    @Transactional
    public Badge createBadge(Long userId, BadgeCreateRequest request) {
        User isAdmin = findUser(userId);
        if (!isAdmin.getRoleType().equals(RoleType.ADMIN)) {
            throw new IllegalArgumentException("관리자가 아닙니다.");
        }
        User user = findPost(request.getPostId()).getUser();

        Badge badge = Badge.builder()
                .name(request.getName())
                .user(user)
                .build();
        return badgeRepository.save(badge);
    }

    @Transactional
    public Page<Badge> getBadges(Long userId, Pageable pageable) {
        User user = findUser(userId);
        return badgeRepository.findAllByUserId(userId, pageable);
    }

    public User findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
    }

    public Post findPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
    }
    
}
