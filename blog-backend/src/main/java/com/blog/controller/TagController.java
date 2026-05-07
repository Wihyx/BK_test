package com.blog.controller;

import com.blog.common.Result;
import com.blog.entity.Tag;
import com.blog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签控制器 —— 负责文章标签的查看、新增和删除
 */
@RestController
@RequestMapping("/api")
public class TagController {

    @Autowired
    private TagService tagService;

    // 查询所有标签列表（前台展示用）
    @GetMapping("/tags")
    public Result<List<Tag>> list() {
        return Result.success(tagService.list());
    }

    // 新增一个标签（后台管理用）
    @PostMapping("/admin/tags")
    public Result<?> save(@RequestBody Tag tag) {
        tagService.save(tag);
        return Result.success();
    }

    // 根据 ID 删除一个标签（后台管理用）
    @DeleteMapping("/admin/tags/{id}")
    public Result<?> delete(@PathVariable Long id) {
        tagService.removeById(id);
        return Result.success();
    }
}
