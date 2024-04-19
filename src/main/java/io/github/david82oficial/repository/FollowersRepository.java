package io.github.david82oficial.repository;

import io.github.david82oficial.domain.Follower;
import io.github.david82oficial.domain.Users;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class FollowersRepository implements PanacheRepository<Follower> {
    @Transactional
    public boolean follows (Users follower, Users user){
//        Map<String, Object> params = new HashMap<>();
//        params.put("follower", follower);
//        params.put("user", user);

        //Outra forma de realizar a query
        var params = Parameters.with("follower", follower).and("user", user).map();

        PanacheQuery<Follower> query = find("follower = :follower and user = :user", params);
        Optional<Follower> result = query.firstResultOptional();

        return result.isPresent();
    }

    public List<Follower> findByUser(Long userId){
        PanacheQuery<Follower> query = find("user.id", userId);

        return query.list();
    }

    public void deleteByFollowerAndUser (Long followerId, Long userId){
        var params = Parameters
                .with("userId", userId)
                .and("followerId", followerId)
                .map();
        delete("follower.id = :followerId and user.id = :userId", params);
    }
}
