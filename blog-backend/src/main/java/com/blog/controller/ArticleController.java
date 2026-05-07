package com.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.common.Result;
import com.blog.entity.Article;
import com.blog.entity.Comment;
import com.blog.service.ArticleService;
import com.blog.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Article CRUD controller. Handles public article listing / detail, like
 * toggling, and admin-side create / update / delete. Comment counts are
 * resolved in-memory to avoid N+1 queries.
 */
@RestController
@RequestMapping("/api")
public class ArticleController {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private CommentService commentService;

    /** Paginated article list with optional category, keyword, and status filters. */
    @GetMapping("/articles")
    public Result<?> list(@RequestParam(defaultValue = "1") Integer page,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) Long categoryId,
                          @RequestParam(required = false) String keyword,
                          @RequestParam(required = false) Integer status) {
        Page<Article> p = new Page<>(page, pageSize);
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        if (status != null) wrapper.eq(Article::getStatus, status);
        else wrapper.eq(Article::getStatus, 1);
        if (categoryId != null) {
            wrapper.eq(Article::getCategoryId, categoryId);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Article::getTitle, keyword);
        }
        wrapper.orderByDesc(Article::getIsTop).orderByDesc(Article::getCreatedTime);
        return Result.success(fillCommentCount(articleService.page(p, wrapper)));
    }

    /** Batch-fill the commentCount field on each article in the current page. */
    private Page<Article> fillCommentCount(Page<Article> result) {
        List<Article> records = result.getRecords();
        if (!records.isEmpty()) {
            Set<Long> articleIds = records.stream().map(Article::getId).collect(Collectors.toSet());
            List<Comment> comments = commentService.list(
                new LambdaQueryWrapper<Comment>()
                    .in(Comment::getArticleId, articleIds)
                    .eq(Comment::getStatus, 1)
            );
            records.forEach(a -> {
                long count = comments.stream().filter(c -> c.getArticleId().equals(a.getId())).count();
                a.setCommentCount(count);
            });
        }
        return result;
    }

    /** Single article detail with live comment count. */
    @GetMapping("/articles/{id}")
    public Result<?> detail(@PathVariable Long id) {
        Article article = articleService.getById(id);
        if (article == null) {
            return Result.error(404, "article not found");
        }
        long count = commentService.count(
            new LambdaQueryWrapper<Comment>().eq(Comment::getArticleId, id).eq(Comment::getStatus, 1)
        );
        article.setCommentCount(count);
        return Result.success(article);
    }

    /** Admin: create or update an article (insert if no id, update if id present). */
    @PostMapping("/admin/articles")
    public Result<?> save(@RequestBody Article article) {
        articleService.saveOrUpdate(article);
        return Result.success(article.getId());
    }

    /** Increment the like count on a public article. */
    @PutMapping("/articles/{id}/like")
    public Result<?> like(@PathVariable Long id) {
        Article article = articleService.getById(id);
        if (article == null) return Result.error(404, "article not found");
        Long count = article.getLikeCount();
        article.setLikeCount(count == null ? 1 : count + 1);
        articleService.updateById(article);
        return Result.success(article.getLikeCount());
    }

    /** Admin: update an existing article. */
    public Result<?> update(@RequestBody Article article) {
        articleService.updateById(article);
        return Result.success();
    }

    /** Admin: soft-delete an article (MyBatis-Plus @TableLogic). */
    @DeleteMapping("/admin/articles/{id}")
    public Result<?> delete(@PathVariable Long id) {
        articleService.removeById(id);
        return Result.success();
    }
}
