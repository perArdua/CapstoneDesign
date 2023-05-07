package com.example.campusin.infra.board;

import com.example.campusin.domain.board.Board;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by kok8454@gmail.com on 2023-05-07
 * Github : http://github.com/perArdua
 */
public interface BoardRepository extends JpaRepository<Board, Long> {

}
