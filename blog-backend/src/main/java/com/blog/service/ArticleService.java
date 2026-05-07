package com.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.entity.Article;

/**
 * Article service interface.
 * Extends {@link IService} which provides built-in CRUD methods for the
 * {@link Article} entity (save, remove, update, list, page, etc.).
 */
public interface ArticleService extends IService<Article> {
    /** Increment the view counter for the given article. */
    void incrementViewCount(Long id);
}
