package com.hannah.springssedemo.repository;


import com.hannah.springssedemo.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UserRepository {

    private final Map<String, User> users = new ConcurrentHashMap<>(
            Map.of(
                    "user1", new User("user1", "password1", "User 1"),
                    "user2", new User("user2", "password2", "User 2"),
                    "user3", new User("user3", "password3", "User 3"),
                    "user4", new User("user4", "password4", "User 4"),
                    "user5", new User("user5", "password5", "User 5")
            )
    );

    public void save(User user) {
        users.put(user.getUserId(), user);
    }

    public User findById(String userId) {
        return users.get(userId);
    }

    public void deleteById(String userId) {
        users.remove(userId);
    }
}
