package ru.practicum.shareit.item;


import lombok.Data;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.annotations.Generated;

import java.util.List;
import java.util.stream.Collectors;

@Generated
@Component
@Data
public class CommentMapper {

    public Comment dtoToComment(CommentDtoInput dtoInput) {
        System.out.println(dtoInput.getText());
        return Comment.builder()
                .text(dtoInput.getText())
                .build();
    }

    public CommentDtoOutput toCommentDto(Comment comment) {
        return CommentDtoOutput.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getUser().getName())
                .created(comment.getCreated())
                .build();
    }

    public List<CommentDtoOutput> commentDtoList(List<Comment> comments) {
        return comments.stream().map(this::toCommentDto).collect(Collectors.toList());
    }

}
