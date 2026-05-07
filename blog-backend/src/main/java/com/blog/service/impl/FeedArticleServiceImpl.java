package com.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.entity.FeedArticle;
import com.blog.mapper.FeedArticleMapper;
import com.blog.service.FeedArticleService;
import org.springframework.stereotype.Service;

/**
 * Feed article service implementation.
 * No custom methods — relies entirely on inherited CRUD from {@link ServiceImpl}.
 */
@Service
public class FeedArticleServiceImpl extends ServiceImpl<FeedArticleMapper, FeedArticle> implements FeedArticleService {
}
