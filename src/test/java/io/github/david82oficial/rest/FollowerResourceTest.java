package io.github.david82oficial.rest;

import io.github.david82oficial.domain.Follower;
import io.github.david82oficial.domain.Users;
import io.github.david82oficial.dto.FollowerRequestDTO;
import io.github.david82oficial.repository.FollowersRepository;
import io.github.david82oficial.repository.UserRepository;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
class FollowerResourceTest {

    @Inject
    UserRepository userRepository;
    @Inject
    FollowersRepository followerRepository;

    Long userId;
    Long followerId;

    @BeforeEach
    @Transactional
    void setUp() {
        //usuario padrão dos testes
        var user = new Users();
        user.setAge(30);
        user.setName("Fulano");
        userRepository.persist(user);
        userId = user.getId();

        // o seguidor
        var follower = new Users();
        follower.setAge(31);
        follower.setName("Cicrano");
        userRepository.persist(follower);
        followerId = follower.getId();

        //cria um follower
        var followerEntity = new Follower();
        followerEntity.setFollower(follower);
        followerEntity.setUser(user);
        followerRepository.persist(followerEntity);
    }

    @Test
    @DisplayName("should return 409 when Follower Id is equal to User id")
    public void sameUserAsFollowerTest(){

        var body = new FollowerRequestDTO();
        body.setFollowerId(userId);
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId", userId)
                .when()
                .put()
                .then()
                .statusCode(Response.Status.CONFLICT.getStatusCode())
                .body(Matchers.is("You can´t follows yourself"));
    }

    @Test
    @DisplayName("should return 404 on follow a user when User id doen't exist")
    public void userNotFoundWhenTryingToFollowTest(){

        var body = new FollowerRequestDTO();
        body.setFollowerId(userId);

        var inexistentUserId = 999;

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId", inexistentUserId)
                .when()
                .put()
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("should follow a user")
    public void followUserTest(){

        var body = new FollowerRequestDTO();
        body.setFollowerId(followerId);

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId", userId)
                .when()
                .put()
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    @DisplayName("should return 404 on list user followers and User id doen't exist")
    public void userNotFoundWhenListingFollowersTest(){
        var inexistentUserId = 999;

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .pathParam("userId", inexistentUserId)
                .when()
                .get()
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("should list a user's followers")
    public void listFollowersTest(){
        var response =
                RestAssured
                        .given()
                        .contentType(ContentType.JSON)
                        .pathParam("userId", userId)
                        .when()
                        .get()
                        .then()
                        .extract().response();

        var followersCount = response.jsonPath().get("followersCount");
        var followersContent = response.jsonPath().getList("content");

        Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.statusCode());
        Assertions.assertEquals(null, followersCount);
        Assertions.assertEquals(1, followersContent.size());

    }

    @Test
    @DisplayName("should return 404 on unfollow user and User id doen't exist")
    public void userNotFoundWhenUnfollowingAUserTest(){
        var inexistentUserId = 999;

        RestAssured
                .given()
                .pathParam("userId", inexistentUserId)
                .queryParam("followerId", followerId)
                .when()
                .delete()
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("should Unfollow an user")
    public void unfollowUserTest(){

        RestAssured
                .given()
                .pathParam("userId", userId)
                .queryParam("followerId", followerId)
                .when()
                .delete()
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }


}