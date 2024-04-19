package io.github.david82oficial.rest;

import io.github.david82oficial.domain.Users;
import io.github.david82oficial.dto.CreateUserRequestDTO;
import io.github.david82oficial.dto.ResponseError;
import io.github.david82oficial.repository.UserRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Set;


@Path("/users")
public class UserResource {
    private UserRepository userRepository;
    private Validator validator;
    @Inject
    public UserResource(UserRepository userRepository, Validator validator){
        this.userRepository = userRepository;
        this.validator = validator;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response createUser(@Valid CreateUserRequestDTO createUserRequestDTO){
        Set<ConstraintViolation<CreateUserRequestDTO>> violation = validator.validate(createUserRequestDTO);

        if (!violation.isEmpty()){
            return ResponseError
                    .createFromValidation(violation)
                    .withStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);
        }

        Users user = new Users();
        user.setName(createUserRequestDTO.getName());
        user.setAge(createUserRequestDTO.getAge());

        userRepository.persist(user);
        return Response
                .status(Response.Status.CREATED.getStatusCode())
                .entity(user)
                .build();
    }

    @GET
    public Response listAllUsers() {
        PanacheQuery<Users> query = userRepository.findAll();
        return Response.ok(query.list()).build();
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response deleteUser(@PathParam("id") Long id){
        Users user = userRepository.findById(id);
        if (user != null){
            userRepository.delete(user);
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Response updateUser(@PathParam("id") Long id, CreateUserRequestDTO userData){
        Users user = userRepository.findById(id);

        if (user != null){
            user.setName(userData.getName());
            userRepository.persist(user);//não há a necessidade usar essa linha, pois ela atualiza
            //automaticamente

            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();

    }
}
