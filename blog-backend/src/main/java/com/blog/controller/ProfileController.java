package com.blog.controller;

import com.blog.common.Result;
import com.blog.entity.Profile;
import com.blog.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 个人主页控制器 —— 查看和更新博客主人的个人介绍信息
 */
@RestController
@RequestMapping("/api")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    // 获取个人主页信息（ID 固定为 1，只有一条记录）
    @GetMapping("/profile")
    public Result<Profile> get() {
        Profile p = profileService.getById(1L);
        return Result.success(p == null ? new Profile() : p); // 如果没有数据则返回空对象
    }

    // 更新个人主页信息（后台管理用）
    @PutMapping("/admin/profile")
    public Result<?> update(@RequestBody Profile profile) {
        profile.setId(1L); // 固定更新 ID 为 1 的记录
        profileService.saveOrUpdate(profile);
        return Result.success();
    }
}
