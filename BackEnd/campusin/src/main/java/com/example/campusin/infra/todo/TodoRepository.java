package com.example.campusin.infra.todo;

import com.example.campusin.domain.rank.Ranks;
import com.example.campusin.domain.todo.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    @Query(value = "SELECT t FROM Todo t " +
            "WHERE t.user.id = :userId " +
            "ORDER BY t.id DESC")
    Page<Todo> findAllMyTodoList(@Param("userId") Long userId, Pageable pageable);
}
