package kr.hhplus.be.server.infrastructure.user;

import jakarta.annotation.PostConstruct;
import kr.hhplus.be.server.domain.user.UserEntity;
import kr.hhplus.be.server.domain.user.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static kr.hhplus.be.server.domain.user.UserEntity.initializeUserEntity;

@Repository
public class FakeUserRepository implements UserRepository {

    private final Map<Long, UserEntity> userStore = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        UserEntity user1 = initializeUserEntity(1L, 1000L);
        UserEntity user2 = initializeUserEntity(2L, 1000L);
        UserEntity user3 = initializeUserEntity(3L, 1000L);
        userStore.put(1L, user1);
        userStore.put(2L, user2);
        userStore.put(3L, user3);
    }

    @Override
    public Optional<UserEntity> findById(Long id) {
        return Optional.ofNullable(userStore.get(id));
    }

    @Override
    public UserEntity save(UserEntity user) {
        userStore.put(user.getId(), user);
        return user;
    }

    @Override
    public UserEntity update(UserEntity user) {
        if (!userStore.containsKey(user.getId())) {
            throw new IllegalArgumentException("User not found with id: " + user.getId());
        }
        userStore.put(user.getId(), user);
        return user;
    }

}