package com.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.entity.Article;
import com.blog.mapper.ArticleMapper;
import com.blog.service.ArticleService;
import org.springframework.stereotype.Service;

/**
 * Article service implementation.
 * Extends {@link ServiceImpl} which provides the concrete implementation of
 * all {@link IService} CRUD methods using the {@link ArticleMapper}.
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Override
    public void incrementViewCount(Long id) {
        baseMapper.updateById(new Article());
    }
}
