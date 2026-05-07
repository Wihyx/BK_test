package com.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.common.Result;
import com.blog.entity.Comment;
import com.blog.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 评论控制器 —— 处理前台评论展示和提交，以及后台评论审核、删除
 */
@RestController
@RequestMapping("/api")
public class CommentController {

    @Autowired
    private CommentService commentService;

    // 前台评论列表：按文章 ID 查询已审核通过的评论，支持分页
    @GetMapping("/comments")
    public Result<?> list(@RequestParam Long articleId,
                          @RequestParam(defaultValue = "1") Integer page,
                          @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Comment> p = new Page<>(page, pageSize);
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getArticleId, articleId);   // 指定文章
        wrapper.eq(Comment::getStatus, 1);               // 只查审核通过的
        wrapper.orderByDesc(Comment::getCreatedTime);    // 按时间倒序
        return Result.success(commentService.page(p, wrapper));
    }

    // 前台提交评论：默认状态为待审核（status = 0）
    @PostMapping("/comments")
    public Result<?> save(@RequestBody Comment comment) {
        comment.setStatus(0); // 0 表示待审核，管理员通过后改为 1
        commentService.save(comment);
        return Result.success();
    }

    // 后台评论管理列表：查询所有评论（包括待审核），支持分页
    @GetMapping("/admin/comments")
    public Result<?> adminList(@RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Comment> p = new Page<>(page, pageSize);
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Comment::getCreatedTime);
        return Result.success(commentService.page(p, wrapper));
    }

    // 后台审核评论：修改评论的状态（通过 / 拒绝）
    @PutMapping("/admin/comments/{id}/status")
    public Result<?> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        Comment comment = commentService.getById(id);
        if (comment != null) {
            comment.setStatus(status);
            commentService.updateById(comment);
        }
        return Result.success();
    }

    // 后台删除评论
    @DeleteMapping("/admin/comments/{id}")
    public Result<?> delete(@PathVariable Long id) {
        commentService.removeById(id);
        return Result.success();
    }
}
