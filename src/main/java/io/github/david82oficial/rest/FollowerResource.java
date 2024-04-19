package io.github.david82oficial.rest;

import io.github.david82oficial.domain.Follower;
import io.github.david82oficial.dto.FollowerPerUserResponseDTO;
import io.github.david82oficial.dto.FollowerRequestDTO;
import io.github.david82oficial.dto.FollowerResponseDTO;
import io.github.david82oficial.repository.FollowersRepository;
import io.github.david82oficial.repository.UserRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.stream.Collectors;

@Path("/user/{userId}/followers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FollowerResource {

    private FollowersRepository followersRepository;
    private UserRepository userRepository;

    @Inject
    public FollowerResource(FollowersRepository followersRepository, UserRepository userRepository){

        this.followersRepository = followersRepository;
        this.userRepository = userRepository;
    }

    @PUT
    @Transactional
    public Response followUser(@PathParam("userId") Long userId, FollowerRequestDTO followerRequest){

        if(userId.equals(followerRequest.getFollowerId())){
            return Response.status(Response.Status.CONFLICT).
                    entity("You can´t follows yourself").build();
        }

        var user = userRepository.findById(userId);

        if (user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        var follower = userRepository.findById(followerRequest.getFollowerId());

        if (follower == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if(!followersRepository.follows(follower, user)){
            var entity = new Follower();
            entity.setUser(user);
            entity.setFollower(follower);

            followersRepository.persist(entity);
        }

        return Response.status(Response.Status.NO_CONTENT).build();

    }

    @GET
    public Response listFollowers (@PathParam("userId") Long userId){

        var user = userRepository.findById(userId);

        if (user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        var list = followersRepository.findByUser(userId);
        FollowerPerUserResponseDTO response = new FollowerPerUserResponseDTO();
        response.setFollowerCount(list.size());

        var followerList = list.stream().map(FollowerResponseDTO::new).collect(Collectors.toList());

        response.setContent(followerList);
        return Response.ok(response).build();
    }

    @DELETE
    @Transactional
    public Response unfollow (@PathParam("userId") Long userId, @QueryParam("followerId") Long followerId){

        var user = userRepository.findById(userId);

        if (user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        followersRepository.deleteByFollowerAndUser(followerId, userId);

        return Response.status(Response.Status.NO_CONTENT).build();
    }

}
