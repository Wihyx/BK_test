package com.blog.controller;

import com.blog.common.Result;
import com.blog.entity.Category;
import com.blog.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类控制器 —— 负责文章分类的查看、新增、修改和删除
 */
@RestController
@RequestMapping("/api")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // 查询所有分类列表（前台展示用）
    @GetMapping("/categories")
    public Result<List<Category>> list() {
        return Result.success(categoryService.list());
    }

    // 新增一个分类（后台管理用）
    @PostMapping("/admin/categories")
    public Result<?> save(@RequestBody Category category) {
        categoryService.save(category);
        return Result.success();
    }

    // 修改已有分类（后台管理用）
    @PutMapping("/admin/categories")
    public Result<?> update(@RequestBody Category category) {
        categoryService.updateById(category);
        return Result.success();
    }

    // 根据 ID 删除一个分类（后台管理用）
    @DeleteMapping("/admin/categories/{id}")
    public Result<?> delete(@PathVariable Long id) {
        categoryService.removeById(id);
        return Result.success();
    }
}
