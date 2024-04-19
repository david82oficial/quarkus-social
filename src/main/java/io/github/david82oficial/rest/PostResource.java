package io.github.david82oficial.rest;

import io.github.david82oficial.domain.Post;
import io.github.david82oficial.domain.Users;
import io.github.david82oficial.dto.CreatePostRequestDTO;
import io.github.david82oficial.dto.PostResponseDTO;
import io.github.david82oficial.repository.FollowersRepository;
import io.github.david82oficial.repository.PostRepository;
import io.github.david82oficial.repository.UserRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.stream.Collectors;

@Path("/user/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    private UserRepository userRepository;
    private PostRepository postRepository;
    private FollowersRepository followersRepository;

    @Inject
    public PostResource(
            UserRepository userRepository,
            PostRepository postRepository,
            FollowersRepository followersRepository){

        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.followersRepository = followersRepository;
    }

    @POST
    @Transactional
    public Response savePost(@PathParam("userId") Long userId, CreatePostRequestDTO postRequestDTO){
        Users user = userRepository.findById(userId);

        if (user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Post post = new Post();
        post.setText(postRequestDTO.getText());
        post.setUser(user);

        postRepository.persist(post);

        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    public Response listPost(@PathParam("userId") Long userId, @HeaderParam("followerId") Long followerId){
        Users user = userRepository.findById(userId);

        if (user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (followerId == null){
            return Response.status(Response.Status.BAD_REQUEST).entity("You forgot the header followerId").build();
        }

        Users follower = userRepository.findById(followerId);

        if (follower == null){
            return Response.status(Response.Status.BAD_REQUEST).entity("Follwer doesn´t exist").build();
        }

        boolean follows = followersRepository.follows(user, follower);

        if (!follows){
            return Response.status(Response.Status.FORBIDDEN).entity("You can´t these posts").build();
        }

        PanacheQuery<Post> query = postRepository
                .find("user", Sort.by("dateTime", Sort.Direction.Descending), user);

        var list = query.list();

        var postResponseList = list.stream()
                //.map(post -> PostResponseDTO.fromEntity(post))
                .map(PostResponseDTO :: fromEntity) // outra forma de fazer (Method Reference)
                .collect(Collectors.toList());

        return Response.ok(postResponseList).build();
    }

}
