package io.github.david82oficial.dto;

import io.github.david82oficial.domain.Follower;
import lombok.Data;

@Data
public class FollowerResponseDTO {

    private Long id;
    private String name;

    public FollowerResponseDTO(){}

    public FollowerResponseDTO(Follower follower){
        this(follower.getId(), follower.getFollower().getName());
    }

    public FollowerResponseDTO(Long id, String name){
        this.id = id;
        this.name = name;
    }
}
