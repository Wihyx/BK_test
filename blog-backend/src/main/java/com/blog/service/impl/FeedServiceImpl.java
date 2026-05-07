package com.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.entity.Feed;
import com.blog.mapper.FeedMapper;
import com.blog.service.FeedService;
import org.springframework.stereotype.Service;

/**
 * RSS feed source service implementation.
 * No custom methods — relies entirely on inherited CRUD from {@link ServiceImpl}.
 */
@Service
public class FeedServiceImpl extends ServiceImpl<FeedMapper, Feed> implements FeedService {
}
