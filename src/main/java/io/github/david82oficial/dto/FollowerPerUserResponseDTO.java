package io.github.david82oficial.dto;

import lombok.Data;

import java.util.List;

@Data
public class FollowerPerUserResponseDTO {
    private Integer followerCount;
    private List<FollowerResponseDTO> content;

}
