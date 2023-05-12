package com.example.campusin.domain.message;

import com.example.campusin.domain.basetime.BaseTimeEntity;
import com.example.campusin.domain.user.User;
import io.jsonwebtoken.lang.Assert;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "message")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Message extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "message_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "message_room_id", referencedColumnName = "message_room_id")
    private MessageRoom messageRoom;

    @ManyToOne
    @JoinColumn(name = "writer_id", referencedColumnName = "user_id")
    private User writer;

    @Column(name = "content", nullable = false, length = 300)
    private String content;

    @Builder
    public Message(MessageRoom messageRoom, User writer, String content) {
        Assert.notNull(messageRoom, "messageRoom은 null이 아니여야 합니다.");
        Assert.notNull(writer, "writer은 null이 아니여야 합니다.");
        Assert.notNull(content, "메세지 내용은 비어있을 수 없습니다.");

        this.messageRoom = messageRoom;
        this.writer = writer;
        this.content = content;
    }
}
