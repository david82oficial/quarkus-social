package io.github.david82oficial.dto;

import io.github.david82oficial.domain.Post;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostResponseDTO {
    private String text;
    private LocalDateTime datetime;

    public static PostResponseDTO fromEntity (Post post){
        var response = new PostResponseDTO();
        response.setText(post.getText());
        response.setDatetime(post.getDateTime());
        return response;
    }
}

