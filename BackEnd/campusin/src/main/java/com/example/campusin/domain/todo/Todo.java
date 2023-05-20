package com.example.campusin.domain.todo;

import com.example.campusin.domain.basetime.BaseTimeEntity;
import com.example.campusin.domain.todo.dto.request.TodoUpdateRequest;
import com.example.campusin.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;


@Entity
@Table(name = "Todo")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Data
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE todo SET deleted_at = CURRENT_TIMESTAMP where todo_id = ?")
public class Todo extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "todo_id")
    private Long id;
    @Column(nullable = false)
    private String title;

    // Todo 순서 ??
    @Column(nullable = false)
    private Boolean completed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @Builder
    public Todo(User user, String title, Boolean completed) {
        this.user = user;
        this.title = title;
        this.completed = completed;
    }

    public void updateTodo(TodoUpdateRequest request){
        setTitle(request.getTitle());
        setCompleted(request.getCompleted());
    }
}
