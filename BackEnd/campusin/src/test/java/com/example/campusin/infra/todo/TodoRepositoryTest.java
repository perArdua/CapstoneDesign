package com.example.campusin.infra.todo;

import com.example.campusin.domain.todo.Todo;
import com.example.campusin.domain.user.User;
import com.example.campusin.infra.TestEntityFactory;
import com.example.campusin.support.DataJpaTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TodoRepository")
class TodoRepositoryTest extends DataJpaTestSupport {

    @Autowired
    TodoRepository todoRepository;

    @Test
    @DisplayName("사용자별 Todo 리스트를 페이징 조회한다")
    void findAllMyTodoList() {
        // given
        User owner = TestEntityFactory.persistUser(em, "todo-owner");
        User other = TestEntityFactory.persistUser(em, "todo-other");
        todoRepository.save(Todo.builder().user(owner).title("mine1").completed(false).build());
        todoRepository.save(Todo.builder().user(owner).title("mine2").completed(true).build());
        todoRepository.save(Todo.builder().user(other).title("other").completed(false).build());

        // when
        Page<Todo> result = todoRepository.findAllMyTodoList(owner.getId(), PageRequest.of(0, 10));

        // then
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).allMatch(todo -> todo.getUser().getId().equals(owner.getId()));
    }
}
