package com.example.campusin.domain.board.dto.response;

import com.example.campusin.domain.board.Board;
import com.example.campusin.domain.board.BoardType;
import lombok.Getter;

/**
 * Created by kok8454@gmail.com on 2023-05-07
 * Github : http://github.com/perArdua
 */

@Getter
public class BoardSimpleResponse {
    Long boardId;
    BoardType boardType;

    public BoardSimpleResponse(Long boardId, BoardType boardType) {
        this.boardId = boardId;
        this.boardType = boardType;
    }

    public BoardSimpleResponse(Board board) {
        this(
                board.getId(),
                board.getBoardType()
        );
    }
}
