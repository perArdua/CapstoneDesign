package com.example.campusin.domain.board.dto.response;

import com.example.campusin.domain.board.Board;
import com.example.campusin.domain.board.BoardType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * Created by kok8454@gmail.com on 2023-05-07
 * Github : http://github.com/perArdua
 */

@Getter
@Schema(name = "게시판 조회 응답", description = "게시판 id, 게시판 타입을 반환한다.")
public class BoardSimpleResponse {

    @Schema(name = "게시판 id", example = "1")
    Long boardId;

    @Schema(name = "게시판 타입", example = "FREE")
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
