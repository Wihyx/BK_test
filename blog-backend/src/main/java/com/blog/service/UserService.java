package com.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.entity.User;

/**
 * User service interface. Extends {@link IService} which provides built-in
 * CRUD for {@link User}, with an additional login lookup method.
 */
public interface UserService extends IService<User> {
    /** Look up a user by username and password; returns null if no match. */
    User login(String username, String password);
}
