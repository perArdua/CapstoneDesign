package com.example.campusin.domain.board;

import com.example.campusin.domain.basetime.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

/**
 * Created by kok8454@gmail.com on 2023-05-07
 * Github : http://github.com/perArdua
 */

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="board")
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE board SET deleted_at = CURRENT_TIMESTAMP where id = ?")
public class Board extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "board_id")
    private Long id;

    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    private BoardType boardType;

    @Builder
    public Board(BoardType boardType) {
        this.boardType = boardType;
    }

}
