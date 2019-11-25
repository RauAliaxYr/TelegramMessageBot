package org.di.dkdk.repo;

import org.di.dkdk.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface UserRepo extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.notifed = false " +
            "AND u.phone IS NOT NULL AND u.email IS NOT NULL")
    List<User> findNewUsers();

    User findByChatId (long id);
}