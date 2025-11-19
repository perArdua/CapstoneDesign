package com.example.campusin.infra.board;

import com.example.campusin.domain.board.Board;
import com.example.campusin.domain.board.BoardType;
import com.example.campusin.support.DataJpaTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BoardRepository")
class BoardRepositoryTest extends DataJpaTestSupport {

    @Autowired
    BoardRepository boardRepository;

    @Test
    @DisplayName("게시판을 저장하고 조회한다")
    void saveAndFind() {
        // given
        Board board = boardRepository.save(Board.builder().boardType(BoardType.Free).build());

        // when // then
        assertThat(boardRepository.findById(board.getId()))
                .isPresent()
                .get()
                .satisfies(found -> assertThat(found.getBoardType()).isEqualTo(BoardType.Free));
    }
}
