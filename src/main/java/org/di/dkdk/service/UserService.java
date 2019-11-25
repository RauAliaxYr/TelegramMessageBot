package org.di.dkdk.service;

import org.di.dkdk.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.di.dkdk.repo.UserRepo;


import java.util.List;

@Service
public class UserService {

    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Transactional(readOnly = true)
    public User findByChatId(long id) {
        return userRepo.findByChatId(id);
    }

    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        return userRepo.findAll();

    }


    @Transactional
    public List<User> findNewUsers() {
        List<User> users = userRepo.findNewUsers();

        users.forEach(user -> user.setNotifed(true));
        userRepo.saveAll(users);
        return users;
    }

    @Transactional
    public void addUser(User user) {
        user.setAdmin(userRepo.count() == 0);
        userRepo.save(user);
    }

    @Transactional
    public void updateUser(User user) {
        userRepo.save(user);
    }


}
