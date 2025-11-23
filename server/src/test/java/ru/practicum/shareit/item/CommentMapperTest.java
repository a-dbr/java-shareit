package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CommentMapperTest {

    private final CommentMapper mapper = Mappers.getMapper(CommentMapper.class);

    @Test
    void fromCreateDto_shouldMapText() {
        CommentCreateDto createDto = new CommentCreateDto();
        createDto.setText("Great item!");

        Comment comment = mapper.fromCreateDto(createDto);

        assertThat(comment).isNotNull();
        assertThat(comment.getText()).isEqualTo(createDto.getText());
    }

    @Test
    void fromCreateDto_null_returnsNull() {
        Comment comment = mapper.fromCreateDto(null);
        assertThat(comment).isNull();
    }

    @Test
    void toDto_shouldMapAllFields_whenAuthorPresent() {
        Comment comment = new Comment();
        comment.setId(123L);
        comment.setText("Nice");
        User author = new User();
        author.setId(5L);
        author.setName("Alice");
        author.setEmail("alice@example.com");
        comment.setAuthor(author);
        LocalDateTime now = LocalDateTime.now();
        comment.setCreated(now);

        CommentDto dto = mapper.toDto(comment);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(comment.getId());
        assertThat(dto.getText()).isEqualTo(comment.getText());
        assertThat(dto.getAuthorName()).isEqualTo(author.getName());
        assertThat(dto.getCreated()).isEqualTo(now);
    }

    @Test
    void toDto_shouldHandleNullAuthor_andReturnNullFields() {
        Comment comment = new Comment();
        comment.setId(200L);
        comment.setText("No author");
        comment.setAuthor(null);
        LocalDateTime now = LocalDateTime.now();
        comment.setCreated(now);

        CommentDto dto = mapper.toDto(comment);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(comment.getId());
        assertThat(dto.getText()).isEqualTo(comment.getText());
        assertThat(dto.getAuthorName()).isNull();
        assertThat(dto.getCreated()).isEqualTo(now);
    }

    @Test
    void toDto_nullInput_returnsNull() {
        CommentDto dto = mapper.toDto(null);
        assertThat(dto).isNull();
    }

    @Test
    void toDto_null_returnsNull() {
        CommentDto dto = mapper.toDto(null);
        assertThat(dto).isNull();
    }
}