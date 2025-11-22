package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.Set;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Set<Comment> findByItem(Item item);
}
