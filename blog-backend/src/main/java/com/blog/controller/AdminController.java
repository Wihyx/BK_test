package com.blog.controller;

import cn.hutool.core.util.StrUtil;
import com.blog.common.Result;
import com.blog.entity.User;
import com.blog.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理员控制器 —— 处理后台登录和仪表盘数据展示
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private TagService tagService;
    @Autowired
    private CommentService commentService;

    // 管理员登录接口，校验用户名和密码
    @PostMapping("/login")
    public Result<?> login(@RequestBody User user) {
        if (StrUtil.isBlank(user.getUsername()) || StrUtil.isBlank(user.getPassword())) {
            return Result.error(400, "username or password is empty");
        }
        User loginUser = userService.login(user.getUsername(), user.getPassword());
        if (loginUser == null) {
            return Result.error(400, "wrong username or password");
        }
        loginUser.setPassword(null); // 登录成功后清除密码字段，不返回给前端
        return Result.success(loginUser);
    }

    // 仪表盘数据接口，返回文章数、分类数、标签数、评论数的统计
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard() {
        Map<String, Object> data = new HashMap<>();
        data.put("articleCount", articleService.count());
        data.put("categoryCount", categoryService.count());
        data.put("tagCount", tagService.count());
        data.put("commentCount", commentService.count());
        return Result.success(data);
    }
}
