package com.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.entity.Profile;
import com.blog.mapper.ProfileMapper;
import com.blog.service.ProfileService;
import org.springframework.stereotype.Service;

/**
 * Profile service implementation.
 * No custom methods — relies entirely on inherited CRUD from {@link ServiceImpl}.
 */
@Service
public class ProfileServiceImpl extends ServiceImpl<ProfileMapper, Profile> implements ProfileService {
}
