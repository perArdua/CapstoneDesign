package com.example.campusin.infra;

import com.example.campusin.domain.board.Board;
import com.example.campusin.domain.board.BoardType;
import com.example.campusin.domain.badge.Badge;
import com.example.campusin.domain.comment.Comment;
import com.example.campusin.domain.message.MessageRoom;
import com.example.campusin.domain.oauth.ProviderType;
import com.example.campusin.domain.oauth.RoleType;
import com.example.campusin.domain.post.Post;
import com.example.campusin.domain.studygroup.StudyGroup;
import com.example.campusin.domain.studygroup.StudyGroupMember;
import com.example.campusin.domain.tag.Tag;
import com.example.campusin.domain.tag.TagType;
import com.example.campusin.domain.user.User;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 테스트용 엔티티 생성/저장 헬퍼.
 */
public final class TestEntityFactory {

    private TestEntityFactory() {
    }

    public static User persistUser(TestEntityManager em, String loginId) {
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
                .loginId(loginId)
                .username(loginId)
                .profileImageUrl("img")
                .providerType(ProviderType.GOOGLE)
                .roleType(RoleType.USER)
                .createdAt(now)
                .modifiedAt(now)
                .nickname(loginId)
                .build();
        user.setPassword("pw");
        return em.persistAndFlush(user);
    }

    public static Board persistBoard(TestEntityManager em, BoardType type) {
        Board board = Board.builder().boardType(type).build();
        return em.persistAndFlush(board);
    }

    public static Tag persistTag(TestEntityManager em, TagType type) {
        Tag tag = Tag.builder().tagType(type).build();
        return em.persistAndFlush(tag);
    }

    public static Post persistPost(TestEntityManager em, String title, User author, Board board, Tag tag) {
        Post post = Post.builder()
                .title(title)
                .content("content")
                .user(author)
                .board(board)
                .price(0L)
                .studyGroupId(-1L)
                .tag(tag)
                .build();
        return em.persistAndFlush(post);
    }

    public static Comment persistComment(TestEntityManager em, Post post, User user, Comment parent) {
        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .parent(parent)
                .content("c")
                .isAnswer(false)
                .isAdopted(false)
                .build();
        return em.persistAndFlush(comment);
    }

    public static StudyGroup persistStudyGroup(TestEntityManager em, User owner, String name) {
        StudyGroup studyGroup = StudyGroup.builder()
                .user(owner)
                .studygroupName(name)
                .LimitedMemberSize(10)
                .CurrentMemberSize(1)
                .members(List.of())
                .build();
        return em.persistAndFlush(studyGroup);
    }

    public static StudyGroupMember persistStudyGroupMember(TestEntityManager em, StudyGroup studyGroup, User member, boolean isLeader) {
        StudyGroupMember studyGroupMember = StudyGroupMember.builder()
                .isLeader(isLeader)
                .user(member)
                .studyGroupId(studyGroup)
                .build();
        return em.persistAndFlush(studyGroupMember);
    }

    public static Badge persistBadge(TestEntityManager em, User user, String name) {
        Badge badge = Badge.builder()
                .name(name)
                .user(user)
                .build();
        return em.persistAndFlush(badge);
    }

    public static MessageRoom persistMessageRoom(TestEntityManager em, User sender, User receiver, Post createdFrom) {
        MessageRoom room = MessageRoom.builder()
                .initialSender(sender)
                .initialReceiver(receiver)
                .createdFrom(createdFrom)
                .build();
        return em.persistAndFlush(room);
    }
}
